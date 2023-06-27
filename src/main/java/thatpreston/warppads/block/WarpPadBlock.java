package thatpreston.warppads.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import thatpreston.warppads.WarpPads;
import thatpreston.warppads.menu.WarpConfigMenu;
import thatpreston.warppads.menu.WarpSelectionMenu;
import thatpreston.warppads.server.WarpPadData;
import thatpreston.warppads.server.WarpPadInfo;
import thatpreston.warppads.server.WarpPadInfoHolder;

import java.util.List;

public class WarpPadBlock extends BaseEntityBlock {
    public WarpPadBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof WarpPadBlockEntity warpPad) {
            if(level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                WarpPadInfoHolder holder = WarpPadData.get(serverLevel);
                if(!holder.hasWarpPad(pos) || player.isCrouching()) {
                    WarpPadInfo info = holder.getNewWarpPad(pos);
                    MenuProvider provider = WarpConfigMenu.getMenuProvider(info, warpPad.getItemStackHandler());
                    NetworkHooks.openScreen(serverPlayer, provider, info::write);
                } else {
                    List<WarpPadInfo> warpPads = holder.getWarpPads();
                    MenuProvider provider = WarpSelectionMenu.getMenuProvider(pos, warpPads);
                    NetworkHooks.openScreen(serverPlayer, provider, data -> {
                        data.writeBlockPos(pos);
                        data.writeInt(warpPads.size());
                        for(WarpPadInfo entry : warpPads) {
                            entry.write(data);
                        }
                    });
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof WarpPadBlockEntity warpPad) {
                warpPad.handleScheduledTick(level, pos);
            }
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpPadBlockEntity(pos, state);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        super.onRemove(state, level, pos, newState, moving);
        if(level instanceof ServerLevel serverLevel) {
            WarpPadData.get(serverLevel).removeWarpPad(pos);
        }
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? createTickerHelper(type, WarpPads.WARP_PAD.get(), WarpPadBlockEntity::animateTick) : null;
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}