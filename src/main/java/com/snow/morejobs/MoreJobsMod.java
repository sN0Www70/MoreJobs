package com.snow.morejobs;

import com.snow.morejobs.command.*;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.gui.architect.ArchitectShopContainer;
import com.snow.morejobs.gui.bartender.BartenderShopContainer;
import com.snow.morejobs.gui.checkup.CheckupScreen;
import com.snow.morejobs.gui.checkup.ModContainers;
import com.snow.morejobs.items.ModItems;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.network.NetworkHandler;
import com.snow.morejobs.skills.MadScientistSkills;
import com.snow.morejobs.util.EconomyUtils;

// Nouveau système de banque
import com.snow.morejobs.blocks.BankChestBlock;
import com.snow.morejobs.tileentity.BankChestTileEntity;
import com.snow.morejobs.container.BankChestContainer;
import com.snow.morejobs.client.gui.BankChestScreen;
import com.snow.morejobs.network.ModNetworking;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MoreJobsMod.MODID)
public class MoreJobsMod {

    public static final String MODID = "morejobs";

    private int salaryTimer = 0;

    // Registres pour le nouveau système bancaire
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    // Enregistrement du nouveau bank chest
    public static final RegistryObject<Block> BANK_CHEST = BLOCKS.register("bank_chest", BankChestBlock::new);
    public static final RegistryObject<Item> BANK_CHEST_ITEM = ITEMS.register("bank_chest",
            () -> new BlockItem(BANK_CHEST.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
    public static final RegistryObject<TileEntityType<BankChestTileEntity>> BANK_CHEST_TILE_ENTITY =
            TILE_ENTITIES.register("bank_chest", () -> TileEntityType.Builder.of(
                    BankChestTileEntity::new, BANK_CHEST.get()).build(null));
    public static final RegistryObject<ContainerType<BankChestContainer>> BANK_CHEST_CONTAINER =
            CONTAINERS.register("bank_chest", () -> IForgeContainerType.create(BankChestContainer::new));

    public MoreJobsMod() {
        // Réseau (métier & co)
        NetworkHandler.register();

        // Items (menottes, etc.)
        ModItems.register();

        // Containers (checkup, etc.)
        ModContainers.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Enregistrement des nouveaux systèmes bancaires
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Listeners lifecycle
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Containers spécifiques (architecte / barman)
        FMLJavaModLoadingContext.get().getModEventBus()
                .addGenericListener(ContainerType.class, this::registerContainers);

        // Bus Forge (serveur / jeu)
        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldTick);
    }

    private void setup(FMLCommonSetupEvent event) {
        // Init côté commun
        ModNetworking.register(); // Nouveau réseau pour le bank chest
    }

    private void clientSetup(FMLClientSetupEvent event) {
        // Enregistrement des écrans côté client
        event.enqueueWork(() -> {
            // Checkup (police)
            ScreenManager.register(ModContainers.CHECKUP.get(), CheckupScreen::new);

            // Nouveau Bank Chest
            ScreenManager.register(BANK_CHEST_CONTAINER.get(), BankChestScreen::new);
        });
    }

    private void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        // Architecte
        ArchitectShopContainer.TYPE = new ContainerType<>(
                (windowId, inv) -> new ArchitectShopContainer(windowId, inv.player));
        ArchitectShopContainer.TYPE.setRegistryName(MODID, "architect_shop");
        event.getRegistry().register(ArchitectShopContainer.TYPE);

        // Barman
        BartenderShopContainer.TYPE = new ContainerType<>(
                (windowId, inv) -> new BartenderShopContainer(windowId, inv.player));
        BartenderShopContainer.TYPE.setRegistryName(MODID, "bartender_shop");
        event.getRegistry().register(BartenderShopContainer.TYPE);
    }

    private void onCommandRegister(RegisterCommandsEvent event) {
        MoreJobsCommand.register(event.getDispatcher());
        HunterCommand.register(event.getDispatcher());
        FarmerCommand.register(event.getDispatcher());
        MinerCommand.register(event.getDispatcher());
        AlienCommand.register(event.getDispatcher());
        InteriorDesignerCommand.register(event.getDispatcher());
        GardenerCommand.register(event.getDispatcher());
        ArchitectCommand.register(event.getDispatcher());
        BartenderCommand.register(event.getDispatcher());
        FateTellerCommand.register(event.getDispatcher());
        MadScientistCommand.register(event.getDispatcher());
        MayorCommand.register(event.getDispatcher());
        PoliceOfficerCommand.register(event.getDispatcher());
    }

    private void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        salaryTimer++;
        if (salaryTimer >= 12000) { // ~10 minutes IRL (20 ticks par seconde)
            salaryTimer = 0;

            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                JobDataStorage data = JobDataStorage.get(player);

                for (String jobName : data.getActiveJobs()) {
                    JobType job = JobType.fromName(jobName);

                    int salary = job.getSalary();
                    if (salary > 0) {
                        EconomyUtils.giveMoney(player, salary);
                        player.sendMessage(
                                new StringTextComponent("Salaire : +" + salary + " Chelous pour le métier de " + job.getDisplayName()),
                                player.getUUID()
                        );
                    }

                    int xpGain = 10;
                    data.addXp(job, xpGain);
                    data.save();
                    player.sendMessage(
                            new StringTextComponent("Expérience : +" + xpGain + " XP pour le métier de " + job.getDisplayName()),
                            player.getUUID()
                    );
                }
            }
        }
    }

    private void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!(event.world instanceof ServerWorld)) return;
        if (event.phase != TickEvent.Phase.END) return;
        MadScientistSkills.tick((ServerWorld) event.world);
    }
}