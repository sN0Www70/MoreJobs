package com.snow.morejobs.util;

import com.snow.morejobs.skills.InteriorDesignerSkills;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class InteriorDesignerTickHandler {

    private static final String[] COLORS = {
            "white","orange","magenta","light_blue","yellow","lime","pink","gray",
            "light_gray","cyan","purple","blue","brown","green","red","black"
    };
    private static final Map<UUID, Map<BlockPos, Long>> lastChange = new HashMap<>();
    private static final long COOLDOWN_MS = 200;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        server.getAllLevels().forEach(world -> {
            for (ServerPlayerEntity player : world.getPlayers(p -> true)) {
                if (!InteriorDesignerSkills.isPaletteActive(player)) continue;

                BlockPos pos = player.blockPosition().below();
                Block block = world.getBlockState(pos).getBlock();
                String path = block.getRegistryName().getPath();

                if (!path.endsWith("_wool")) continue;

                long now = world.getGameTime() * 50;
                UUID uuid = player.getUUID();
                lastChange.putIfAbsent(uuid, new HashMap<>());
                Map<BlockPos, Long> mapPerPlayer = lastChange.get(uuid);
                long last = mapPerPlayer.getOrDefault(pos, 0L);

                if (now - last < COOLDOWN_MS) continue;

                int index = java.util.Arrays.asList(COLORS).indexOf(path.substring(0, path.length() - 5));
                if (index < 0) continue;
                String next = COLORS[(index + 1) % COLORS.length];
                Block newBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft", next + "_wool"));
                if (newBlock != null) {
                    world.setBlockAndUpdate(pos, newBlock.defaultBlockState());
                    mapPerPlayer.put(pos.immutable(), now);
                }
            }
        });
    }
}
