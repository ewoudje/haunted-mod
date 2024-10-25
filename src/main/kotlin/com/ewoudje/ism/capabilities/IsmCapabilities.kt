package com.ewoudje.ism.capabilities

import com.ewoudje.ism.resource
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

object IsmCapabilities {
    val SHIZO_CAPABILITY = EntityCapability.createVoid(
        "schizophrenia".resource,
        ShizoCapability::class.java
    )

    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerEntity(SHIZO_CAPABILITY, EntityType.PLAYER) { a, _ -> ShizoCapability(a) }
    }
}