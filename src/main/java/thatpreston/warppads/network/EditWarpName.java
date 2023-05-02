package thatpreston.warppads.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
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
    public static void encode(EditWarpName message, FriendlyByteBuf data) {
        data.writeBlockPos(message.pos);
        data.writeUtf(message.name);
    }
    public static EditWarpName decode(FriendlyByteBuf data) {
        return new EditWarpName(data.readBlockPos(), data.readUtf());
    }
    public static void handle(EditWarpName message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            ServerLevel level = player.getLevel();
            WarpPadData.get(level).addWarpPad(new WarpPadInfo(message.pos, message.name));
        });
        context.get().setPacketHandled(true);
    }
}