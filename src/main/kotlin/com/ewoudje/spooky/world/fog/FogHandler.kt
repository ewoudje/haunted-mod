package com.ewoudje.spooky.world.fog

import com.ewoudje.spooky.networking.FogUpdatePacket
import com.ewoudje.spooky.world.SpookyWorldState.Companion.spookyWorldState
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor

object FogHandler {
    private var tickCounter = 0

    fun tick(event: ServerTickEvent.Post) {
        tickCounter++

        val level = event.server.overworld()
        val fog = level.spookyWorldState.fogState
        val fogPos = fog.position

        if (fogPos != null) {
            fog.position = fogPos.add(fog.velocity)
        }

        if (tickCounter >= 5 && fog.consumeNetworkDirty()) {
            sendFogUpdate(level, fog)
            tickCounter = 0
        }
    }

    private fun sendFogUpdate(level: ServerLevel, fog: FogState) {
        val pos = fog.position
        val payload = FogUpdatePacket(fog)
        if (pos != null) {
            PacketDistributor.sendToPlayersNear(
                level,
                null,
                pos.x,
                pos.y,
                pos.z,
                512.0,
                payload
            )
        } else {
            PacketDistributor.sendToAllPlayers(payload)
        }
    }
}