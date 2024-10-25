package com.ewoudje.ism.world

import com.ewoudje.ism.world.IsmWorldState.Companion.ismWorldState
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.event.level.LevelEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

object IsmWorldHandler {

    fun onLoad(event: LevelEvent.Load) {
        if (event.level.isClientSide) return
        val level = event.level as ServerLevel
    }

    fun serverTick(event: ServerTickEvent.Pre) {
        val overworld = event.server.overworld()
        val state = overworld.ismWorldState

        //checkGeneration(overworld, state)
    }
/*
    private var currentFuture;
    fun checkGeneration(level: ServerLevel, state: SpookyWorldState) {
        if (level.chunkSource.pendingTasksCount < 10) {
            val loc = state.singlePlacementData.getUndecidedLocation()
            if (loc != null)
                level.chunkSource.getChunkFuture(loc.x, loc.z, ChunkStatus.STRUCTURE_STARTS, false)
        }
    }*/

    fun register() {
        FORGE_BUS.addListener(::serverTick)
        FORGE_BUS.addListener(::onLoad)
    }
}