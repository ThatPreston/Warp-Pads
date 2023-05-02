package thatpreston.warppads.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import thatpreston.warppads.WarpPads;
import thatpreston.warppads.server.WarpPadInfo;

import java.util.ArrayList;
import java.util.List;

public class WarpSelectionMenu extends Container {
    public static final ITextComponent TITLE = new TranslationTextComponent("container.warppads.warp_selection");
    private final IWorldPosCallable worldPosCallable;
    private List<WarpPadInfo> warpPads;
    private BlockPos pos;
    public WarpSelectionMenu(int id, IWorldPosCallable worldPosCallable) {
        super(WarpPads.WARP_SELECTION.get(), id);
        this.worldPosCallable = worldPosCallable;
    }
    public WarpSelectionMenu(int id, IWorldPosCallable worldPosCallable, BlockPos pos, List<WarpPadInfo> warpPads) {
        this(id, worldPosCallable);
        this.pos = pos;
        this.warpPads = warpPads;
    }
    public WarpSelectionMenu(int id, PlayerInventory inventory, PacketBuffer data) {
        this(id, IWorldPosCallable.NULL);
        pos = data.readBlockPos();
        warpPads = new ArrayList<>();
        int count = data.readInt();
        for(int i = 0; i < count; i++) {
            WarpPadInfo entry = new WarpPadInfo(data);
            warpPads.add(entry);
        }
    }
    public static INamedContainerProvider getMenuProvider(BlockPos pos, List<WarpPadInfo> warpPads) {
        return new SimpleNamedContainerProvider((id, inventory, player) -> new WarpSelectionMenu(id, IWorldPosCallable.create(player.level, pos), pos, warpPads), TITLE);
    }
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(worldPosCallable, player, WarpPads.WARP_PAD_BLOCK.get());
    }
    public BlockPos getPos() {
        return pos;
    }
    public List<WarpPadInfo> getWarpPads() {
        return warpPads;
    }
}