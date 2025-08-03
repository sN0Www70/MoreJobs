package com.snow.morejobs.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

public class FakePlayerEntity extends FakePlayer {

    public FakePlayerEntity(ServerWorld world, GameProfile profile) {
        super(world, profile);

        this.setCustomName(new StringTextComponent(profile.getName()));
        this.setCustomNameVisible(true);
        this.setInvisible(false);
        this.setNoAi(true); // pas d’IA

        this.setInvulnerable(false); // peut être frappé
        this.setSilent(true);        // ne fait pas de bruit
        this.setCanPickUpLoot(false);
    }

    @Override
    public boolean isPushedByFluid() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean canBeLeashed(PlayerEntity player) {
        return true;
    }
}
