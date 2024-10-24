package com.ewoudje.spooky.world.fog

import com.ewoudje.spooky.SpookyAttributes
import com.ewoudje.spooky.networking.FogUpdatePacket
import com.ewoudje.spooky.resource
import com.ewoudje.spooky.world.SpookyWorldState.Companion.spookyWorldState
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.joml.Vector3d
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

object FogHandler {
    private val FOG_ATTRIBUTE_MODIFIER = AttributeModifier("fog".resource, 1.0, AttributeModifier.Operation.ADD_VALUE)
    private var tickCounter = 0

    fun tick(event: ServerTickEvent.Post) {
        tickCounter++

        val level = event.server.overworld()
        val fog = level.spookyWorldState.fogState
        val fogPos = fog.position

        if (fogPos == null) {
            if (level.spookyWorldState.isEvilGodFree && level.random.nextFloat() < 0.0001) {
                val randomPlayer = level.randomPlayer ?: return
                val spawnDir = Vector3d(level.random.nextDouble(), 0.0, level.random.nextDouble()).normalize()
                val fDir = Vector3f(-spawnDir.x.toFloat(), 0f, -spawnDir.z.toFloat())
                fog.direction = Vector3f(fDir).apply { y = 0.5f }.normalize()
                fog.velocity = fDir.mul(0.01f)
                fog.position = randomPlayer.position().toVector3d().add(spawnDir.mul(640.0))
                fog.thickness = 256.0
            }

            return
        }

        fog.position = fogPos.add(fog.velocity)

        FogUnsealedSpawner.spawner?.tick(event.server.overworld())

        if (tickCounter >= 5 && fog.consumeNetworkDirty()) {
            sendFogUpdate(level, fog)
            tickCounter = 0
        }

        level.players().forEach { p ->
            if (fog.isInFog(p.position().toVector3d())) {
                p.getAttribute(SpookyAttributes.IN_FOG)?.addOrUpdateTransientModifier(FOG_ATTRIBUTE_MODIFIER)
            } else {
                p.getAttribute(SpookyAttributes.IN_FOG)?.removeModifier(FOG_ATTRIBUTE_MODIFIER)
            }
        }

        val found = level.players().firstOrNull { p ->
            fog.distanceTo(p.position().toVector3d()) < 512 + fog.thickness
        }

        if (found == null) {
            fog.position = null
        }
    }

    private fun sendFogUpdate(level: ServerLevel, fog: FogState) {
        val pos = fog.position
        val payload = FogUpdatePacket(fog)
        PacketDistributor.sendToAllPlayers(payload)
    }
}