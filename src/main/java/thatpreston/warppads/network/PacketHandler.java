package thatpreston.warppads.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("warppads", "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, WarpRequest.class, WarpRequest::encode, WarpRequest::decode, WarpRequest::handle);
        INSTANCE.registerMessage(id++, EditWarpName.class, EditWarpName::encode, EditWarpName::decode, EditWarpName::handle);
    }
}