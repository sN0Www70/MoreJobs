package com.snow.morejobs.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class CropUtils {

    public static int growCropsAround(ServerWorld world, BlockPos center, int radius) {
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -2, -radius), center.offset(radius, 2, radius))) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof CropsBlock) {
                CropsBlock crop = (CropsBlock) state.getBlock();
                if (!crop.isMaxAge(state)) {
                    world.setBlock(pos, crop.getStateForAge(crop.getMaxAge()), 3);
                    count++;
                }
            }
        }
        return count;
    }

    public static List<ItemStack> harvestCropsAround(ServerWorld world, BlockPos center, int radius) {
        List<ItemStack> drops = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -2, -radius), center.offset(radius, 2, radius))) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof CropsBlock) {
                CropsBlock crop = (CropsBlock) state.getBlock();
                if (crop.isMaxAge(state)) {
                    List<ItemStack> list = Block.getDrops(state, world, pos, null);
                    world.destroyBlock(pos, false);
                    drops.addAll(list);
                }
            }
        }
        return drops;
    }
}
