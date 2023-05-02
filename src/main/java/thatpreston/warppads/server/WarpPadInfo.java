package thatpreston.warppads.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class WarpPadInfo {
    private final BlockPos pos;
    private final String name;
    private boolean warping = false;
    public WarpPadInfo(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }
    public WarpPadInfo(CompoundNBT tag) {
        pos = NBTUtil.readBlockPos(tag);
        name = tag.getString("name");
    }
    public WarpPadInfo(PacketBuffer data) {
        pos = data.readBlockPos();
        name = data.readUtf();
    }
    public CompoundNBT save() {
        CompoundNBT tag = NBTUtil.writeBlockPos(pos);
        tag.putString("name", name);
        return tag;
    }
    public void write(PacketBuffer data) {
        data.writeBlockPos(pos);
        data.writeUtf(name);
    }
    public BlockPos getPos() {
        return pos;
    }
    public String getName() {
        return name;
    }
    public boolean isWarping() {
        return warping;
    }
    public void setWarping(boolean warping) {
        this.warping = warping;
    }
}