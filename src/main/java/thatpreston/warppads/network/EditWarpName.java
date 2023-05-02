package thatpreston.warppads.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import thatpreston.warppads.server.WarpPadData;
import thatpreston.warppads.server.WarpPadInfo;

import java.util.function.Supplier;

public class EditWarpName {
    private final BlockPos pos;
    private final String name;
    public EditWarpName(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }
    public static void encode(EditWarpName message, PacketBuffer data) {
        data.writeBlockPos(message.pos);
        data.writeUtf(message.name);
    }
    public static EditWarpName decode(PacketBuffer data) {
        return new EditWarpName(data.readBlockPos(), data.readUtf());
    }
    public static void handle(EditWarpName message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            WarpPadData.get(player.getLevel()).addWarpPad(new WarpPadInfo(message.pos, message.name));
        });
        context.get().setPacketHandled(true);
    }
}