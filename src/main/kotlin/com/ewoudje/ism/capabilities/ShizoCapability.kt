package com.ewoudje.ism.capabilities

import com.ewoudje.ism.IsmAttributes
import com.ewoudje.ism.IsmAttributes.getVal
import com.ewoudje.ism.IsmPOIs
import com.ewoudje.ism.resource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.event.tick.EntityTickEvent

class ShizoCapability(val player: Player) {
    val vulnerability: Float get() {
        val hungry = if (player.foodData.foodLevel < 10) 0.2f else 0.0f
        val satiated = if (player.foodData.saturationLevel > 0) 0.2f else 0.0f

        return hungry - satiated
    }

    val sanity get() = IsmAttributes.SANITY.getVal(player)
    var chestTripped: BlockPos? = null
    var chestTrippedI = 0

    fun serverTick() {
        val level = player.level() as ServerLevel
        val random = level.getRandomSequence(RANDOM_SEQUENCE)

        if (chestTripped != null) {
            chestTrippedI--
            if (chestTrippedI <= 0) {
                level.blockEvent(chestTripped, Blocks.CHEST, 1, 0)
                chestTripped = null
            }
        }

        if (sanity < 10) {
            val chance = sanity * 0.1 * chestChancePerTick
            if (chance > random.nextDouble()) tripChest(level)
        }

        if (sanity < 5) {
            val chance = sanity * 0.2 * creeperChancePerTick
            if (chance > random.nextDouble()) tripCreeper()
        }
    }

    fun tripChest(level: ServerLevel) {
        val chest = level.poiManager.findClosest(
            { it.value() == IsmPOIs.chest },
            player.onPos,
            32,
            PoiManager.Occupancy.ANY).orElse(null)

        if (chest == null) return

        level.blockEvent(chest, Blocks.CHEST, 1, 1)
        level.playSound(
            player,
            chest,
            SoundEvents.CHEST_OPEN,
            SoundSource.BLOCKS,
            0.5F, 0.95F
        )
        chestTrippedI = 20
        chestTripped = chest
    }

    fun tripCreeper() {
        player.playNotifySound(
            SoundEvents.CREEPER_PRIMED,
            SoundSource.HOSTILE,
            1f,
            1f
        )
    }

    companion object {
        // Once every 5 min, aka 6000 ticks, when full shizo
        private const val chestChancePerTick =  1.0 / 600.0
        private const val creeperChancePerTick =  1.0 / 6000.0
        private val RANDOM_SEQUENCE = "shizo".resource

        fun tickPlayer(event: EntityTickEvent.Post) {
            if (event.entity.level().isClientSide) return
            event.entity.getCapability(IsmCapabilities.SHIZO_CAPABILITY)?.serverTick()
        }
    }
}