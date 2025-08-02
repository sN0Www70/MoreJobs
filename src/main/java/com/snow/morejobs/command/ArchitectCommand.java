package com.snow.morejobs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.morejobs.data.ArchitectShopData;
import com.snow.morejobs.data.JobDataStorage;
import com.snow.morejobs.gui.architect.ArchitectShopMenuProvider;
import com.snow.morejobs.jobs.JobType;
import com.snow.morejobs.util.EconomyUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class ArchitectCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("archishop")
                .requires(src -> src.getEntity() instanceof ServerPlayerEntity)
                .executes(ctx -> {
                    ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
                    if (!JobDataStorage.get(player).hasActive(JobType.ARCHITECT)) {
                        player.sendMessage(new StringTextComponent("❌ Tu n'es pas Architecte."), player.getUUID());
                        return 0;
                    }
                    ArchitectShopMenuProvider.open(player);
                    return 1;
                }));

        dispatcher.register(Commands.literal("archibuy")
                .requires(src -> src.getEntity() instanceof ServerPlayerEntity)
                .then(Commands.argument("slot", IntegerArgumentType.integer(0, 54))
                        .executes(ctx -> {
                            int slot = IntegerArgumentType.getInteger(ctx, "slot");
                            return executeBuy(ctx.getSource(), slot, false);
                        })
                        .then(Commands.argument("mode", StringArgumentType.word())
                                .executes(ctx -> {
                                    int slot = IntegerArgumentType.getInteger(ctx, "slot");
                                    String mode = StringArgumentType.getString(ctx, "mode");
                                    boolean rightClick = mode.equalsIgnoreCase("right");
                                    return executeBuy(ctx.getSource(), slot, rightClick);
                                }))));
    }

    private static int executeBuy(CommandSource source, int slot, boolean rightClick) {
        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

        if (!JobDataStorage.get(player).hasActive(JobType.ARCHITECT)) {
            player.sendMessage(new StringTextComponent("❌ Tu n'es pas Architecte."), player.getUUID());
            return 0;
        }

        ArchitectShopData.ShopItem item = ArchitectShopData.SHOP_ITEMS.get(slot);
        if (item == null) {
            player.sendMessage(new StringTextComponent("❌ Slot invalide."), player.getUUID());
            return 0;
        }

        int amount = rightClick ? 16 : 1;
        double total = item.price * amount;

        if (!EconomyUtils.has(player, ArchitectShopData.CURRENCY, total)) {
            player.sendMessage(new StringTextComponent("❌ Pas assez d'argent."), player.getUUID());
            return 0;
        }

        ItemStack copy = item.stack.copy();
        copy.setCount(amount);

        boolean added = player.inventory.add(copy);
        if (added) {
            EconomyUtils.withdraw(player, ArchitectShopData.CURRENCY, total);
            player.sendMessage(new StringTextComponent("✅ Achat de " + amount + " " + item.stack.getDisplayName().getString()), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("❌ Inventaire plein."), player.getUUID());
        }

        return 1;
    }
}
