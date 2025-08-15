package com.snow.morejobs.container;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.tileentity.BankChestTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BankChestContainer extends Container {

    private final BankChestTileEntity tileEntity;
    private final PlayerEntity playerEntity;

    public BankChestContainer(int windowId, PlayerInventory playerInventory, BankChestTileEntity tileEntity) {
        super(MoreJobsMod.BANK_CHEST_CONTAINER.get(), windowId);
        this.tileEntity = tileEntity;
        this.playerEntity = playerInventory.player;

        // Slot du coffre (centre de l'interface)
        IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (itemHandler != null) {
            addSlot(new SlotItemHandler(itemHandler, 0, 80, 32));
        }

        // Inventaire du joueur
        // Hotbar
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        // Inventaire principal
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    // Constructeur pour le client (depuis le PacketBuffer)
    public BankChestContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    private static BankChestTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        return (BankChestTileEntity) playerInventory.player.level.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return playerIn.distanceToSqr(tileEntity.getBlockPos().getX() + 0.5,
                tileEntity.getBlockPos().getY() + 0.5,
                tileEntity.getBlockPos().getZ() + 0.5) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();

            if (index < 1) {
                // Du coffre vers l'inventaire
                if (!this.moveItemStackTo(itemStack1, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // De l'inventaire vers le coffre
                if (!this.moveItemStackTo(itemStack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    public BankChestTileEntity getTileEntity() {
        return tileEntity;
    }
}