package com.ewoudje.spooky.client

import com.ewoudje.spooky.client.renderers.RollingFogRenderer
import com.ewoudje.spooky.world.fog.FogState
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import org.joml.Vector3d
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

object ClientFogHandler {
    private val FOG_DISTANCE = 512.0 * 512.0
    private var lastFog: FogState? = null
    private var whisperChance = 0.0

    fun tick() {
        val player = Minecraft.getInstance().player ?: return

        if (lastFog?.isInFog(player.position().toVector3d()) == true) {
            if (whisperChance > 0.005 && player.random.nextFloat() < whisperChance) {
                Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forAmbientAddition(SpookySounds.WHISPERS))
                whisperChance = 0.0
            } else {
                whisperChance += 0.0001
            }
        }

        RollingFogRenderer.tick()
    }

    fun updateFog(fog: FogState) {
        val player = Minecraft.getInstance().player ?: throw IllegalStateException()
        val playerPos = player.position().toVector3d()

        if (fog.position != null && playerPos.distanceSquared(fog.position) < FOG_DISTANCE + (fog.thickness * fog.thickness)) {
            if (lastFog == null) {
                player.playSound(SpookySounds.LAUGHS)
            }

            lastFog = fog
            RollingFogRenderer.updateRenderer(fog, playerPos)
        } else {
            lastFog = null
            RollingFogRenderer.shouldRender = false
        }
    }

    private fun RollingFogRenderer.updateRenderer(fog: FogState, player: Vector3d) {
        val pos = fog.position ?: throw IllegalStateException()
        shouldRender = true
        nextTick = {
            val diff = player.sub(pos, Vector3d())
            val hDepth = fog.thickness / 2
            val flatNormal = fog.getFlatNormal()

            height = fog.height
            velocity = fog.velocity
            lastPosition = position

            val distanceToInnerPlane = flatNormal.dot(diff)
            if (distanceToInnerPlane > 0) {
                position = Vector3d(pos).add(flatNormal.mul(hDepth))
                direction = fog.direction
            } else {
                position = Vector3d(pos).add(flatNormal.mul(-hDepth))
                direction = fog.direction.negate(Vector3f())
                direction.y = fog.direction.y
                direction.normalize()
            }
        }
    }
}