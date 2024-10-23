package com.ewoudje.spooky.client.particles

import com.ewoudje.spooky.SpookyMod
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyParticles {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SpookyMod.ID)

    val UNSEAL_PARTICLE by REGISTRY.register("unseal") { -> SimpleParticleType(true) }
    val VISION_PARTICLE by REGISTRY.register("visions") { -> SimpleParticleType(true) }

    fun registerProviders(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(UNSEAL_PARTICLE, ::UnsealParticleProvider)
        event.registerSpriteSet(VISION_PARTICLE, ::VisionParticleProvider)
    }
}