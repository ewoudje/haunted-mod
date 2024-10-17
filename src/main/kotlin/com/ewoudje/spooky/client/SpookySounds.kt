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
    val SLIDING_SEAL_OPEN by REGISTRY.register("sliding_seal_open") {-> SoundEvent.createVariableRangeEvent("god_unsealed".resource)}
}