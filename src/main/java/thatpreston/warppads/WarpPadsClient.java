package thatpreston.warppads;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thatpreston.warppads.client.gui.WarpConfigScreen;
import thatpreston.warppads.client.gui.WarpSelectionScreen;
import thatpreston.warppads.client.render.WarpPadRenderer;
import thatpreston.warppads.particle.WarpParticle;

@Mod.EventBusSubscriber(modid = "warppads", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WarpPadsClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ScreenManager.register(WarpPads.WARP_SELECTION.get(), WarpSelectionScreen::new);
        ScreenManager.register(WarpPads.WARP_CONFIG.get(), WarpConfigScreen::new);
        ClientRegistry.bindTileEntityRenderer(WarpPads.WARP_PAD.get(), WarpPadRenderer::new);
    }
    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(WarpPads.WARP_PARTICLE.get(), WarpParticle.Factory::new);
    }
}