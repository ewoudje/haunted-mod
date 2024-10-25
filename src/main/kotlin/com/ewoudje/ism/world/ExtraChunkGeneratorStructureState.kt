package com.ewoudje.ism.world

import com.ewoudje.ism.world.structures.SingleStructurePlacement
import net.minecraft.world.level.ChunkPos

interface ExtraChunkGeneratorStructureState {
    fun setData(data: SinglePlacementData)
    fun getPossiblePositions(p: SingleStructurePlacement): Set<ChunkPos>
}