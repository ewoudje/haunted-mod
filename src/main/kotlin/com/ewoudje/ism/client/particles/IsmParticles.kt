package com.ewoudje.ism.client.particles

import com.ewoudje.ism.IsmMod
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmParticles {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, IsmMod.ID)

    val UNSEAL_PARTICLE by REGISTRY.register("unseal") { -> SimpleParticleType(true) }
    val VISION_PARTICLE by REGISTRY.register("visions") { -> SimpleParticleType(true) }

    fun registerProviders(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(UNSEAL_PARTICLE, ::UnsealParticleProvider)
        event.registerSpriteSet(VISION_PARTICLE, ::VisionParticleProvider)
    }
}