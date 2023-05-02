package thatpreston.warppads.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import thatpreston.warppads.menu.WarpConfigMenu;
import thatpreston.warppads.menu.WarpSelectionMenu;
import thatpreston.warppads.server.WarpPadData;
import thatpreston.warppads.server.WarpPadInfo;
import thatpreston.warppads.server.WarpPadInfoHolder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WarpPadBlock extends ContainerBlock {
    public WarpPadBlock() {
        super(AbstractBlock.Properties.of(Material.STONE));
    }
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity entity = world.getBlockEntity(pos);
        if(entity instanceof WarpPadBlockEntity warpPad) {
            if(world instanceof ServerWorld serverWorld && player instanceof ServerPlayerEntity serverPlayer) {
                WarpPadInfoHolder holder = WarpPadData.get(serverWorld);
                if(!holder.hasWarpPad(pos) || player.isCrouching()) {
                    WarpPadInfo info = holder.getNewWarpPad(pos);
                    INamedContainerProvider provider = WarpConfigMenu.getMenuProvider(info, warpPad.getItemStackHandler());
                    NetworkHooks.openGui(serverPlayer, provider, info::write);
                } else {
                    List<WarpPadInfo> warpPads = holder.getWarpPads();
                    INamedContainerProvider provider = WarpSelectionMenu.getMenuProvider(pos, warpPads);
                    NetworkHooks.openGui(serverPlayer, provider, data -> {
                        data.writeBlockPos(pos);
                        data.writeInt(warpPads.size());
                        for(WarpPadInfo entry : warpPads) {
                            entry.write(data);
                        }
                    });
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(!world.isClientSide) {
            TileEntity entity = world.getBlockEntity(pos);
            if(entity instanceof WarpPadBlockEntity warpPad) {
                warpPad.handleScheduledTick(world, pos);
            }
        }
    }
    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new WarpPadBlockEntity();
    }
    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean moving) {
        super.onRemove(state, level, pos, newState, moving);
        if(level instanceof ServerWorld serverLevel) {
            WarpPadData.get(serverLevel).removeWarpPad(pos);
        }
    }
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
}