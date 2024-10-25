package com.ewoudje.ism.blocks

import com.ewoudje.ism.world.IsmWorldState
import com.ewoudje.ism.world.IsmWorldState.Companion.ismWorldState
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SnowyDirtBlock
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d

class CursedGrassBlock(properties: Properties) : SnowyDirtBlock(properties) {

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (random.nextFloat() > 0.1 &&
            !level.ismWorldState.fogState.isInFog(Vector3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()))) {

            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState())
        }
    }
}