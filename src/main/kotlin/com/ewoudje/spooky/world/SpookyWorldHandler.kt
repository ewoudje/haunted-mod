package com.ewoudje.spooky.world

import com.ewoudje.spooky.world.SpookyWorldState.Companion.spookyWorldState
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.neoforged.neoforge.event.level.LevelEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

object SpookyWorldHandler {

    fun onLoad(event: LevelEvent.Load) {
        if (event.level.isClientSide) return
        val level = event.level as ServerLevel
    }

    fun serverTick(event: ServerTickEvent.Pre) {
        val overworld = event.server.overworld()
        val state = overworld.spookyWorldState

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