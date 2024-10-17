package com.ewoudje.spooky

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object SpookyAttributes {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SpookyMod.ID)

    val SHIZO_PROGRESS = REGISTRY.register("player.shizo_progress")
        { -> RangedAttribute(SpookyMod.ID + ".attribute.shizo_progress", 0.0, 0.0, 1.0) }

    val SHIZO_SUSCEPTIBLE = REGISTRY.register("player.shizo_susceptible")
        { -> RangedAttribute(SpookyMod.ID + ".attribute.shizo_susceptible", 0.0, 0.0, 1.0) }

    fun DeferredHolder<Attribute, out Attribute>.getVal(player: Player): Double = player.getAttributeValue(this.delegate)
    fun DeferredHolder<Attribute, out Attribute>.getInstance(player: Player): AttributeInstance = player.getAttribute(this.delegate)!!

    fun registerAttributes(event: EntityAttributeModificationEvent) {
        event.add(EntityType.PLAYER, SHIZO_PROGRESS)
        event.add(EntityType.PLAYER, SHIZO_SUSCEPTIBLE)
    }
}