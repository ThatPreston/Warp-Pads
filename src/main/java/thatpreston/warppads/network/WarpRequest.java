package thatpreston.warppads.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import thatpreston.warppads.block.WarpPadBlockEntity;

import java.util.function.Supplier;

public class WarpRequest {
    private final BlockPos fromPos;
    private final BlockPos toPos;
    public WarpRequest(BlockPos fromPos, BlockPos toPos) {
        this.fromPos = fromPos;
        this.toPos = toPos;
    }
    public static void encode(WarpRequest message, PacketBuffer data) {
        data.writeBlockPos(message.fromPos);
        data.writeBlockPos(message.toPos);
    }
    public static WarpRequest decode(PacketBuffer data) {
        return new WarpRequest(data.readBlockPos(), data.readBlockPos());
    }
    public static void handle(WarpRequest message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            WarpPadBlockEntity.handleWarpRequest(player, message.fromPos, message.toPos);
        });
        context.get().setPacketHandled(true);
    }
}