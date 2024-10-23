package com.ewoudje.spooky.world.fog

import com.ewoudje.spooky.SpookyAttributes
import com.ewoudje.spooky.networking.FogUpdatePacket
import com.ewoudje.spooky.resource
import com.ewoudje.spooky.world.SpookyWorldState.Companion.spookyWorldState
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

object FogHandler {
    private val FOG_ATTRIBUTE_MODIFIER = AttributeModifier("fog".resource, 1.0, AttributeModifier.Operation.ADD_VALUE)
    private var tickCounter = 0

    fun tick(event: ServerTickEvent.Post) {
        tickCounter++

        val level = event.server.overworld()
        val fog = level.spookyWorldState.fogState
        val fogPos = fog.position

        if (fogPos != null) {
            fog.position = fogPos.add(fog.velocity)
        }

        FogUnsealedSpawner.spawner?.tick(event.server.overworld())

        if (tickCounter >= 5 && fog.consumeNetworkDirty()) {
            sendFogUpdate(level, fog)
            tickCounter = 0
        }

        event.server.overworld().players().forEach { p ->
            if (fog.isInFog(p.position().toVector3d())) {
                p.getAttribute(SpookyAttributes.IN_FOG)?.addOrUpdateTransientModifier(FOG_ATTRIBUTE_MODIFIER)
            } else {
                p.getAttribute(SpookyAttributes.IN_FOG)?.removeModifier(FOG_ATTRIBUTE_MODIFIER)
            }
        }
    }

    private fun sendFogUpdate(level: ServerLevel, fog: FogState) {
        val pos = fog.position
        val payload = FogUpdatePacket(fog)
        /*if (pos != null) {
            PacketDistributor.sendToPlayersNear(
                level,
                null,
                pos.x,
                pos.y,
                pos.z,
                512.0 + fog.thickness,
                payload
            )
        } else {*/
            PacketDistributor.sendToAllPlayers(payload)
        //}
    }
}