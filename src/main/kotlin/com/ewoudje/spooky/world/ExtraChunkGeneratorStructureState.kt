package com.ewoudje.spooky.world

import com.ewoudje.spooky.world.structures.SingleStructurePlacement
import net.minecraft.world.level.ChunkPos

interface ExtraChunkGeneratorStructureState {
    fun setData(data: SinglePlacementData)
    fun getPossiblePositions(p: SingleStructurePlacement): Set<ChunkPos>
}