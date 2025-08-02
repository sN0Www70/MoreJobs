package com.snow.morejobs.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class EconomyUtils {

    private static final ResourceLocation CURRENCY_ID = new ResourceLocation("cheloucoin", "chelou");

    public static boolean withdraw(ServerPlayerEntity player, Item currencyItem, double amount) {
        int needed = (int) amount;
        int found = countCurrency(player, currencyItem);

        if (found < needed) {
            player.sendMessage(new StringTextComponent("❌ Pas assez de monnaie !"), player.getUUID());
            return false;
        }

        int toRemove = needed;
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.getItem() == currencyItem) {
                int remove = Math.min(stack.getCount(), toRemove);
                stack.shrink(remove);
                toRemove -= remove;
                if (toRemove <= 0) break;
            }
        }

        return true;
    }

    public static boolean has(ServerPlayerEntity player, Item currencyItem, double amount) {
        return countCurrency(player, currencyItem) >= amount;
    }

    private static int countCurrency(ServerPlayerEntity player, Item currencyItem) {
        int count = 0;
        for (ItemStack stack : player.inventory.items) {
            if (stack.getItem() == currencyItem) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static void giveMoney(ServerPlayerEntity player, int amount) {
        if (amount <= 0) return;

        if (ForgeRegistries.ITEMS.containsKey(CURRENCY_ID)) {
            ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(CURRENCY_ID), amount);
            boolean added = player.inventory.add(stack);
            if (!added) {
                player.drop(stack, false);
            }
        }
    }
}
