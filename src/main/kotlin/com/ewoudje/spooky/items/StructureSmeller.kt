package com.ewoudje.spooky.items

import com.ewoudje.spooky.client.particles.SpookyParticles
import com.ewoudje.spooky.resource
import net.minecraft.client.particle.Particle
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus

class StructureSmeller(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (level.isClientSide) return InteractionResultHolder.consume(itemStack)
        level as ServerLevel

        val pos = level.findNearestMapStructure(SMELLY_STRUCTURES, player.onPos, 5000, false)
            ?: return InteractionResultHolder.fail(itemStack)

        renderParticles(level, player.eyePosition, Vec3(player.forward.x, 0.0, player.forward.z).normalize(), pos.toVec3())

        return InteractionResultHolder.success(itemStack)
    }

    private fun renderParticles(level: ServerLevel, playerPos: Vec3, forward: Vec3, target: Vec3) {
        val properTargetPos = Vec3(target.x + 18, playerPos.y - 0.5, target.z + 25)
        val dirTowards = (properTargetPos - (playerPos + forward)).normalize()
        var pos = playerPos + forward

        repeat(10) { i ->
            val z2One = (i + 1) / 10.0
            val lerped = forward.lerp(dirTowards, z2One + 0.3)
            pos += lerped.scale(z2One)

            if (pos.distanceToSqr(properTargetPos) < 0.6) return

            level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0)
        }
    }

    companion object {
        val SMELLY_STRUCTURES = TagKey.create(Registries.STRUCTURE, "smelly_structures".resource)
    }

}