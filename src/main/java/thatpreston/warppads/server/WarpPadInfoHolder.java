package thatpreston.warppads.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class WarpPadInfoHolder {
    private final HashMap<BlockPos, WarpPadInfo> warpPadMap = new HashMap<>();
    private final ArrayList<WarpPadInfo> warpPadList = new ArrayList<>();
    private final RegistryKey<World> worldKey;
    private boolean dirty;
    public WarpPadInfoHolder(CompoundNBT tag) {
        Optional<RegistryKey<World>> key = World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, tag.get("level")).result();
        this.worldKey = key.orElse(World.OVERWORLD);
        ListNBT list = tag.getList("warpPads", Constants.NBT.TAG_COMPOUND);
        for(INBT listTag : list) {
            if(listTag instanceof CompoundNBT info) {
                addWarpPad(new WarpPadInfo(info));
            }
        }
    }
    public WarpPadInfoHolder(RegistryKey<World> key) {
        this.worldKey = key;
    }
    public CompoundNBT save() {
        CompoundNBT tag = new CompoundNBT();
        World.RESOURCE_KEY_CODEC.encodeStart(NBTDynamicOps.INSTANCE, worldKey).result().ifPresent(key -> tag.put("level", key));
        ListNBT list = new ListNBT();
        for(WarpPadInfo info : warpPadList) {
            list.add(info.save());
        }
        tag.put("warpPads", list);
        return tag;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    public boolean isDirty() {
        return dirty;
    }
    public void addWarpPad(WarpPadInfo info) {
        warpPadMap.put(info.getPos(), info);
        warpPadList.add(info);
        setDirty(true);
    }
    public void removeWarpPad(BlockPos pos) {
        WarpPadInfo info = warpPadMap.remove(pos);
        warpPadList.remove(info);
        setDirty(true);
    }
    public boolean hasWarpPad(BlockPos pos) {
        return warpPadMap.containsKey(pos);
    }
    public WarpPadInfo getWarpPad(BlockPos pos) {
        return warpPadMap.get(pos);
    }
    public WarpPadInfo getNewWarpPad(BlockPos pos) {
        WarpPadInfo info = getWarpPad(pos);
        return info != null ? info : new WarpPadInfo(pos, "");
    }
    public ArrayList<WarpPadInfo> getWarpPads() {
        return warpPadList;
    }
    public RegistryKey<World> getWorldKey() {
        return worldKey;
    }
}