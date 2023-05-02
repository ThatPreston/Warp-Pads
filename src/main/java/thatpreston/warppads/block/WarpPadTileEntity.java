package thatpreston.warppads.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.antlr.v4.runtime.misc.NotNull;
import thatpreston.warppads.WarpPads;
import thatpreston.warppads.WarpPadUtils;
import thatpreston.warppads.server.WarpPadData;
import thatpreston.warppads.server.WarpPadInfo;

import javax.annotation.Nullable;
import java.util.List;

public class WarpPadTileEntity extends TileEntity implements IForgeTileEntity, ITickableTileEntity {
    private final ItemStackHandler itemStackHandler = createItemStackHandler();
    private final LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.of(() -> itemStackHandler);
    private boolean warping = false;
    private int animation = 0;
    private boolean render = false;
    private float[] cachedColor;
    private BlockPos targetPos;
    public WarpPadTileEntity() {
        super(WarpPads.WARP_PAD.get());
    }
    private ItemStackHandler createItemStackHandler() {
        return new ItemStackHandler() {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof DyeItem;
            }
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }
    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }
    private static void sync(ServerWorld world, BlockPos pos) {
        BlockState state = WarpPads.WARP_PAD_BLOCK.get().defaultBlockState();
        world.sendBlockUpdated(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE);
    }
    private static void scheduleTick(World world, BlockPos pos, int delay) {
        world.getBlockTicks().scheduleTick(pos, WarpPads.WARP_PAD_BLOCK.get(), delay);
    }
    private static void addTicket(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(pos), 1, player.getId());
    }
    private void onSync(CompoundNBT tag) {
        itemStackHandler.deserializeNBT(tag.getCompound("inv"));
        warping = tag.getBoolean("warping");
        if(warping) {
            animation = 0;
            render = true;
        }
        cacheColor();
    }
    private void warpOut(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockPos targetPos) {
        warping = true;
        sync(world, pos);
        world.playSound(null, pos, WarpPads.WARP_OUT_SOUND.get(), SoundCategory.BLOCKS, 1, 1);
        addTicket(player, world, pos);
        scheduleTick(world, pos, 30);
        this.targetPos = targetPos;
    }
    private void teleport(ServerWorld world, BlockPos pos) {
        AxisAlignedBB box = WarpPadUtils.getBoxAbovePosition(WarpPadUtils.getTopCenter(pos), 3, 6);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, box);
        int players = 0;
        for(LivingEntity entity : entities) {
            double x = entity.xo - pos.getX() + targetPos.getX();
            double y = entity.yo - pos.getY() + targetPos.getY();
            double z = entity.zo - pos.getZ() + targetPos.getZ();
            entity.teleportTo(x, y, z);
            if(entity instanceof PlayerEntity) {
                players++;
            }
        }
        scheduleTick(world, pos, 10);
        if(world.getBlockEntity(targetPos) instanceof WarpPadTileEntity toPad && !toPad.isRemoved()) {
            toPad.tryWarpIn(world);
        } else if(players == 0) {
            WarpPadInfo info = WarpPadData.get(world).getWarpPad(targetPos);
            if(info != null) {
                info.setWarping(false);
            }
        }
        targetPos = null;
    }
    private void tryWarpIn(ServerWorld world) {
        WarpPadInfo info = WarpPadData.get(world).getWarpPad(worldPosition);
        if(info != null && info.isWarping()) {
            warpIn(world, worldPosition);
            info.setWarping(false);
        }
    }
    private void warpIn(ServerWorld world, BlockPos pos) {
        warping = true;
        sync(world, pos);
        world.playSound(null, pos, WarpPads.WARP_IN_SOUND.get(), SoundCategory.BLOCKS, 1, 1);
        scheduleTick(world, pos, 40);
    }
    private void setIdle(ServerWorld world, BlockPos pos) {
        warping = false;
        sync(world, pos);
    }
    public void handleScheduledTick(ServerWorld world, BlockPos pos) {
        if(targetPos != null) {
            teleport(world, pos);
        } else {
            setIdle(world, pos);
        }
    }
    public static void handleWarpRequest(ServerPlayerEntity player, BlockPos fromPos, BlockPos toPos) {
        ServerWorld world = player.getLevel();
        TileEntity fromEntity = world.getBlockEntity(fromPos);
        if(fromEntity instanceof WarpPadTileEntity fromPad && !fromPad.isWarping()) {
            WarpPadInfo info = WarpPadData.get(world).getWarpPad(toPos);
            if(info != null && !info.isWarping()) {
                info.setWarping(true);
                fromPad.warpOut(player, world, fromPos, toPos);
            }
        }
    }
    public boolean isWarping() {
        return warping;
    }
    private void stepAnimation() {
        animation++;
        if(animation > 40) {
            render = false;
        }
    }
    public int getAnimation() {
        return animation;
    }
    public boolean shouldRender() {
        return render;
    }
    @Override
    public void tick() {
        if(level.isClientSide) {
            stepAnimation();
        }
    }
    public void cacheColor() {
        ItemStack stack = itemStackHandler.getStackInSlot(0);
        if(!stack.isEmpty() && stack.getItem() instanceof DyeItem dye) {
            float[] color = dye.getDyeColor().getTextureDiffuseColors();
            cachedColor = WarpPadUtils.brightenColor(color, 0.3F);
        } else {
            cachedColor = null;
        }
    }
    public float[] getCachedColor() {
        return cachedColor;
    }
    @Override
    public void onLoad() {
        super.onLoad();
        if(level instanceof ServerWorld world) {
            tryWarpIn(world);
        }
    }
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("inv", itemStackHandler.serializeNBT());
        return super.save(tag);
    }
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        onSync(tag);
    }
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.putBoolean("warping", warping);
        tag.put("inv", itemStackHandler.serializeNBT());
        return tag;
    }
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        Vector3d pos = Vector3d.atBottomCenterOf(getBlockPos());
        return WarpPadUtils.getBoxAbovePosition(pos, 3, 7);
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlerOptional.cast();
        }
        return super.getCapability(capability);
    }
}