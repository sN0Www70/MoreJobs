package com.snow.morejobs;

import com.snow.morejobs.command.*;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.gui.architect.ArchitectShopContainer;
import com.snow.morejobs.gui.bartender.BartenderShopContainer;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.network.NetworkHandler;
import com.snow.morejobs.util.EconomyUtils;
import com.snow.morejobs.util.JobUnlockFunctionHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod("morejobs")
public class MoreJobsMod {

    private int salaryTimer = 0;

    public MoreJobsMod() {
        NetworkHandler.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus()
                .addGenericListener(ContainerType.class, this::registerContainers);

        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
    }

    private void setup(FMLCommonSetupEvent event) {
    }

    private void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        ArchitectShopContainer.TYPE = new ContainerType<>((windowId, inv) -> new ArchitectShopContainer(windowId, inv.player));
        ArchitectShopContainer.TYPE.setRegistryName("morejobs", "architect_shop");
        event.getRegistry().register(ArchitectShopContainer.TYPE);

        BartenderShopContainer.TYPE = new ContainerType<>((windowId, inv) -> new BartenderShopContainer(windowId, inv.player));
        BartenderShopContainer.TYPE.setRegistryName("morejobs", "bartender_shop");
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
    }

    private void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            JobUnlockFunctionHandler.handle(player); // ← NEW pour débloquer métiers via tag
        }

        salaryTimer++;
        if (salaryTimer >= 12000) {
            salaryTimer = 0;

            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                JobDataStorage data = JobDataStorage.get(player);
                for (String jobName : data.getActiveJobs()) {
                    JobType job = JobType.fromName(jobName);
                    int salary = job.getSalary();
                    if (salary > 0) {
                        EconomyUtils.giveMoney(player, salary);
                        player.sendMessage(new StringTextComponent("💰 Salaire : +" + salary + " Chelous pour le métier de " + job.getDisplayName()), player.getUUID());
                    }
                }
            }
        }
    }
}
