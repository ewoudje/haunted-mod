package com.ewoudje.spooky.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SnowyDirtBlock
import net.minecraft.world.level.block.state.BlockState

class CursedGrassBlock(properties: Properties) : SnowyDirtBlock(properties) {

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (random.nextFloat() > 0.1) {
            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState())
        }
    }
}