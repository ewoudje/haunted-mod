package com.ewoudje.spooky.capabilities

import com.ewoudje.spooky.SpookyAttributes
import com.ewoudje.spooky.SpookyAttributes.getInstance
import com.ewoudje.spooky.SpookyAttributes.getVal
import com.ewoudje.spooky.SpookyPOIs
import com.ewoudje.spooky.SpookyPOIs.chest
import com.ewoudje.spooky.resource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.neoforged.neoforge.event.tick.EntityTickEvent

class ShizoCapability(val player: Player) {
    val vulnerability: Float get() {
        val hungry = if (player.foodData.foodLevel < 10) 0.2f else 0.0f
        val satiated = if (player.foodData.saturationLevel > 0) 0.2f else 0.0f

        return hungry - satiated
    }

    val progress get() = SpookyAttributes.SHIZO_PROGRESS.getVal(player)
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

        if (progress > 0.5) {
            val chance = (progress - 0.5) * 2.0 * chestChancePerTick
            if (chance > random.nextDouble()) tripChest(level)
        }

        if (progress > 0.7) {
            val chance = (progress - 0.7) * 3.0 * creeperChancePerTick
            if (chance > random.nextDouble()) tripCreeper()
        }
    }

    fun tripChest(level: ServerLevel) {
        val chest = level.poiManager.findClosest(
            { it.value() == SpookyPOIs.chest },
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
            event.entity.getCapability(SpookyCapabilities.SHIZO_CAPABILITY)?.serverTick()
        }
    }
}