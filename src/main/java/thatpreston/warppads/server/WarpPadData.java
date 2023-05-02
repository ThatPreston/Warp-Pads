package thatpreston.warppads.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;

public class WarpPadData extends WorldSavedData {
    private final HashMap<RegistryKey<World>, WarpPadInfoHolder> holders = new HashMap<>();
    public WarpPadData(String name) {
        super(name);
    }
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        ListNBT list = new ListNBT();
        for(WarpPadInfoHolder holder : holders.values()) {
            list.add(holder.save());
        }
        tag.put("holders", list);
        return tag;
    }
    @Override
    public void load(CompoundNBT tag) {
        ListNBT list = tag.getList("holders", Constants.NBT.TAG_COMPOUND);
        for(INBT listTag : list) {
            if(listTag instanceof CompoundNBT holder) {
                addHolder(new WarpPadInfoHolder(holder));
            }
        }
    }
    @Override
    public boolean isDirty() {
        return holders.values().stream().anyMatch(WarpPadInfoHolder::isDirty);
    }
    @Override
    public void setDirty(boolean dirty) {
        holders.values().forEach(holder -> holder.setDirty(dirty));
    }
    private void addHolder(WarpPadInfoHolder holder) {
        holders.put(holder.getWorldKey(), holder);
    }
    private WarpPadInfoHolder getHolder(RegistryKey<World> levelKey) {
        return holders.computeIfAbsent(levelKey, WarpPadInfoHolder::new);
    }
    public static WarpPadInfoHolder get(ServerWorld world) {
        ServerWorld overworld = world.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(() -> new WarpPadData("warp_pads"), "warp_pads").getHolder(world.dimension());
    }
}