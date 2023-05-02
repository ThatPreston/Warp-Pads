package thatpreston.warppads;

import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thatpreston.warppads.block.WarpPadBlock;
import thatpreston.warppads.block.WarpPadBlockEntity;
import thatpreston.warppads.menu.WarpConfigMenu;
import thatpreston.warppads.menu.WarpSelectionMenu;
import thatpreston.warppads.network.PacketHandler;

@Mod("warppads")
public class WarpPads {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "warppads");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "warppads");
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "warppads");
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "warppads");
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "warppads");
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "warppads");
    public static final RegistryObject<Block> WARP_PAD_BLOCK = BLOCKS.register("warp_pad", WarpPadBlock::new);
    public static final RegistryObject<Item> WARP_PAD_ITEM = ITEMS.register("warp_pad", () -> new BlockItem(WARP_PAD_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<WarpPadBlockEntity>> WARP_PAD = BLOCK_ENTITY_TYPES.register("warp_pad", () -> BlockEntityType.Builder.of(WarpPadBlockEntity::new, WARP_PAD_BLOCK.get()).build(null));
    public static final RegistryObject<SoundEvent> WARP_OUT_SOUND = SOUND_EVENTS.register("warp_out", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("warppads", "warp_out")));
    public static final RegistryObject<SoundEvent> WARP_IN_SOUND = SOUND_EVENTS.register("warp_in", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("warppads", "warp_in")));
    public static final RegistryObject<SimpleParticleType> WARP_PARTICLE = PARTICLE_TYPES.register("warp_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<MenuType<WarpSelectionMenu>> WARP_SELECTION = MENU_TYPES.register("warp_selection", () -> IForgeMenuType.create(WarpSelectionMenu::new));
    public static final RegistryObject<MenuType<WarpConfigMenu>> WARP_CONFIG = MENU_TYPES.register("warp_config", () -> IForgeMenuType.create(WarpConfigMenu::new));
    public WarpPads() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        PacketHandler.registerPackets();
    }
    private void commonSetup(final FMLCommonSetupEvent event) {}
    private void addCreative(final CreativeModeTabEvent.BuildContents event) {
        if(event.getTab().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            event.accept(WARP_PAD_ITEM);
        }
    }
}