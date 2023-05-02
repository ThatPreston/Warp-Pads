package thatpreston.warppads;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thatpreston.warppads.block.WarpPadBlock;
import thatpreston.warppads.block.WarpPadTileEntity;
import thatpreston.warppads.menu.WarpConfigMenu;
import thatpreston.warppads.menu.WarpSelectionMenu;
import thatpreston.warppads.network.PacketHandler;

@Mod("warppads")
public class WarpPads {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "warppads");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "warppads");
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "warppads");
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "warppads");
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "warppads");
    public static final DeferredRegister<ContainerType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, "warppads");
    public static final RegistryObject<Block> WARP_PAD_BLOCK = BLOCKS.register("warp_pad", WarpPadBlock::new);
    public static final RegistryObject<Item> WARP_PAD_ITEM = ITEMS.register("warp_pad", () -> new BlockItem(WARP_PAD_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    public static final RegistryObject<TileEntityType<WarpPadTileEntity>> WARP_PAD = BLOCK_ENTITY_TYPES.register("warp_pad", () -> TileEntityType.Builder.of(WarpPadTileEntity::new, WARP_PAD_BLOCK.get()).build(null));
    public static final RegistryObject<SoundEvent> WARP_OUT_SOUND = SOUND_EVENTS.register("warp_out", () -> new SoundEvent(new ResourceLocation("warppads", "warp_out")));
    public static final RegistryObject<SoundEvent> WARP_IN_SOUND = SOUND_EVENTS.register("warp_in", () -> new SoundEvent(new ResourceLocation("warppads", "warp_in")));
    public static final RegistryObject<BasicParticleType> WARP_PARTICLE = PARTICLE_TYPES.register("warp_particle", () -> new BasicParticleType(true));
    public static final RegistryObject<ContainerType<WarpSelectionMenu>> WARP_SELECTION = MENU_TYPES.register("warp_selection", () -> IForgeContainerType.create(WarpSelectionMenu::new));
    public static final RegistryObject<ContainerType<WarpConfigMenu>> WARP_CONFIG = MENU_TYPES.register("warp_config", () -> IForgeContainerType.create(WarpConfigMenu::new));
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
        PacketHandler.registerPackets();
    }
    private void commonSetup(final FMLCommonSetupEvent event) {}
}