package com.ewoudje.spooky

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.common.BooleanAttribute
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object SpookyAttributes {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SpookyMod.ID)

    val SANITY = REGISTRY.register("player.sanity")
        { -> RangedAttribute(SpookyMod.ID + ".attribute.sanity", 40.0, 0.0, 50.0) }
    val IN_FOG = REGISTRY.register("player.in_fog")
        { -> BooleanAttribute(SpookyMod.ID + ".attribute.in_fog", false) }


    fun DeferredHolder<Attribute, out Attribute>.getVal(player: Player): Double = player.getAttributeValue(this.delegate)
    fun DeferredHolder<Attribute, out Attribute>.getInstance(player: Player): AttributeInstance = player.getAttribute(this.delegate)!!

    fun registerAttributes(event: EntityAttributeModificationEvent) {
        event.add(EntityType.PLAYER, SANITY)
        event.add(EntityType.PLAYER, IN_FOG)
    }
}