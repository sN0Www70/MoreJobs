package com.snow.morejobs.blocks;

import com.snow.morejobs.tileentity.BankChestTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BankChestBlock extends Block {

    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public BankChestBlock() {
        super(AbstractBlock.Properties.of(Material.STONE)
                .strength(2.5f)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BankChestTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof BankChestTileEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (BankChestTileEntity) tileEntity, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof BankChestTileEntity) {
                ((BankChestTileEntity) tileEntity).dropContents(world, pos);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @javax.annotation.Nullable net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack stack) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof BankChestTileEntity && stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            ((BankChestTileEntity) tileEntity).load(state, stack.getTag().getCompound("BlockEntityTag"));
        }
    }

}
