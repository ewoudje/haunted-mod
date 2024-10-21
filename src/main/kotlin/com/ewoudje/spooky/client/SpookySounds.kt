package com.ewoudje.spooky.client

import com.ewoudje.spooky.SpookyMod
import com.ewoudje.spooky.resource
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookySounds {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SpookyMod.ID)

    val GOD_UNSEALED by REGISTRY.register("god_unsealed") {-> SoundEvent.createFixedRangeEvent("god_unsealed".resource, 16f)}
    val SLIDING_KEY_OPEN by REGISTRY.register("sliding_open1") {-> SoundEvent.createVariableRangeEvent("sliding_open1".resource)}
    val ROTATING_SEAL by REGISTRY.register("rotating_open1") {-> SoundEvent.createVariableRangeEvent("rotating_open1".resource)}
    val SLIDING_SEAL_OPEN by REGISTRY.register("sliding_open2") {-> SoundEvent.createVariableRangeEvent("sliding_open2".resource)}

    val LAUGHS by REGISTRY.register("laughs") {-> SoundEvent.createFixedRangeEvent("laughs".resource, 16f)}
    val WHISPERS by REGISTRY.register("whispers") {-> SoundEvent.createFixedRangeEvent("whispers".resource, 16f)}
}