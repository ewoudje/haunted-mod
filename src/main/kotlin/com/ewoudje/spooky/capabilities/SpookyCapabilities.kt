package com.ewoudje.spooky.capabilities

import com.ewoudje.spooky.resource
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

object SpookyCapabilities {
    val SHIZO_CAPABILITY = EntityCapability.createVoid(
        "schizophrenia".resource,
        ShizoCapability::class.java
    )

    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerEntity(SHIZO_CAPABILITY, EntityType.PLAYER) { a, _ -> ShizoCapability(a) }
    }
}