package thatpreston.warppads.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import thatpreston.warppads.WarpPads;
import thatpreston.warppads.network.EditWarpName;
import thatpreston.warppads.network.PacketHandler;
import thatpreston.warppads.server.WarpPadInfo;

public class WarpConfigMenu extends AbstractContainerMenu {
    public static final Component TITLE = Component.translatable("container.warppads.warp_config");
    private final ContainerLevelAccess levelAccess;
    private final SlotItemHandler dyeSlot;
    private WarpPadInfo info;
    public WarpConfigMenu(int id, Inventory inventory, ContainerLevelAccess levelAccess, IItemHandler itemHandler) {
        super(WarpPads.WARP_CONFIG.get(), id);
        this.levelAccess = levelAccess;
        dyeSlot = new SlotItemHandler(itemHandler, 0, 138, 18);
        this.addSlot(dyeSlot);
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 3; y++) {
                this.addSlot(new Slot(inventory, x + 9 * (y + 1), 8 + x * 18, 51 + y * 18));
            }
        }
        for(int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 109));
        }
    }
    public WarpConfigMenu(int id, Inventory inventory, ContainerLevelAccess levelAccess, WarpPadInfo entry, IItemHandler itemHandler) {
        this(id, inventory, levelAccess, itemHandler);
        this.info = entry;
    }
    public WarpConfigMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        this(id, inventory, ContainerLevelAccess.NULL, new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof DyeItem;
            }
        });
        info = new WarpPadInfo(data);
    }
    public static MenuProvider getMenuProvider(WarpPadInfo info, IItemHandler itemHandler) {
        return new SimpleMenuProvider((id, inventory, player) -> new WarpConfigMenu(id, inventory, ContainerLevelAccess.create(player.getLevel(), info.getPos()), info, itemHandler), TITLE);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if(index == 0) {
                if(!this.moveItemStackTo(stack, 1, 37, true)) {
                    return empty;
                }
            } else if(dyeSlot.getItemHandler().isItemValid(index, stack)) {
                if(!this.moveItemStackTo(stack, 0, 1, true)) {
                    return empty;
                }
            }
        }
        return empty;
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, WarpPads.WARP_PAD_BLOCK.get());
    }
    public WarpPadInfo getInfo() {
        return info;
    }
    public void saveName(String name) {
        if(name.length() > 0 && name != info.getName()) {
            PacketHandler.INSTANCE.sendToServer(new EditWarpName(info.getPos(), name));
        }
    }
    public boolean hasDye() {
        return !dyeSlot.getItem().isEmpty();
    }
}