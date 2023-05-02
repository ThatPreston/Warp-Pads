package thatpreston.warppads;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        MenuScreens.register(WarpPads.WARP_SELECTION.get(), WarpSelectionScreen::new);
        MenuScreens.register(WarpPads.WARP_CONFIG.get(), WarpConfigScreen::new);
    }
    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(WarpPads.WARP_PARTICLE.get(), WarpParticle.Provider::new);
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(WarpPads.WARP_PAD.get(), WarpPadRenderer::new);
    }
}