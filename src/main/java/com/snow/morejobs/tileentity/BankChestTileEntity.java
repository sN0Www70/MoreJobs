package com.snow.morejobs.tileentity;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.container.BankChestContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BankChestTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private double interestRate = 0.0;
    private int tickCounter = 0;
    private static final int INTEREST_INTERVAL = 36000; // 30 minutes (20 ticks/sec * 60 * 30)
    private static final String CHELOU_ITEM_ID = "cheloucoin:chelou";

    private double pendingAmount = 0.0; // Pour garder les intérêts décimaux en attente

    public BankChestTileEntity() {
        super(MoreJobsMod.BANK_CHEST_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            tickCounter++;
            if (tickCounter >= INTEREST_INTERVAL) {
                applyInterest();
                tickCounter = 0;
                setChanged();
            }
        }
    }

    private void applyInterest() {
        if (interestRate <= 0) return;

        int totalItems = getTotalItemCount();
        if (totalItems == 0) return;

        double exactTotal = totalItems + pendingAmount;
        double interest = exactTotal * (interestRate / 100.0);
        pendingAmount += interest;

        int wholeToAdd = (int) pendingAmount;
        if (wholeToAdd > 0) {
            // Ajouter les items complets
            addItemsToSlots(wholeToAdd);
            pendingAmount -= wholeToAdd;

            setChanged();
            if (level != null) {
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
        }
    }

    private void addItemsToSlots(int amount) {
        int remaining = amount;

        for (int i = 0; i < 9 && remaining > 0; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if (stack.isEmpty()) {
                // Slot vide - créer un nouveau stack
                ItemStack newStack = new ItemStack(net.minecraft.item.Items.EMERALD, Math.min(remaining, 64)); // Remplace par chelou
                if (findChelouItem() != null) {
                    newStack = new ItemStack(findChelouItem(), Math.min(remaining, 64));
                }
                itemHandler.setStackInSlot(i, newStack);
                remaining -= newStack.getCount();
            } else if (stack.getItem().getRegistryName().toString().equals(CHELOU_ITEM_ID)) {
                // Slot avec des chelous - ajouter
                int canAdd = Math.min(remaining, 64 - stack.getCount());
                if (canAdd > 0) {
                    stack.setCount(stack.getCount() + canAdd);
                    remaining -= canAdd;
                }
            }
        }
    }

    private net.minecraft.item.Item findChelouItem() {
        // Chercher le premier chelou dans les slots pour avoir la référence
        for (int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().getRegistryName().toString().equals(CHELOU_ITEM_ID)) {
                return stack.getItem();
            }
        }
        return null;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (level != null && !level.isClientSide) {
                    BlockState state = level.getBlockState(worldPosition);
                    level.sendBlockUpdated(worldPosition, state, state, 3);
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                String itemId = stack.getItem().getRegistryName().toString();
                return CHELOU_ITEM_ID.equals(itemId);
            }
        };
    }

    public int getTotalItemCount() {
        int total = 0;
        for (int i = 0; i < 9; i++) {
            total += itemHandler.getStackInSlot(i).getCount();
        }
        return total;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        interestRate = nbt.getDouble("interestRate");
        tickCounter = nbt.getInt("tickCounter");
        pendingAmount = nbt.getDouble("pendingAmount");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putDouble("interestRate", interestRate);
        nbt.putInt("tickCounter", tickCounter);
        nbt.putDouble("pendingAmount", pendingAmount);
        return nbt;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        if (tag != null) {
            load(getBlockState(), tag);
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.inventory.InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double rate) {
        this.interestRate = Math.max(0, Math.min(rate, 100));
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Bank Chest");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BankChestContainer(windowId, playerInventory, this);
    }
}