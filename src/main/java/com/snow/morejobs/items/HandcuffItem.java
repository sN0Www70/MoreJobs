package com.snow.morejobs.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class HandcuffItem extends Item {
    public HandcuffItem(Properties props) {
        super(props);
    }

    // tu peux remettre ta logique ici ensuite ; pour l’instant on laisse minimal
    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        return user.level.isClientSide ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }
}
