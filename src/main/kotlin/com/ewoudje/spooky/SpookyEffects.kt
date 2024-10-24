package com.ewoudje.spooky

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyEffects {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SpookyMod.ID)

    val SAD = REGISTRY.register("sad") { ->
        SimpleEffect(
            MobEffectCategory.NEUTRAL,
            0
        ).addAttributeModifier(
            SpookyAttributes.SANITY,
            "sad_sanity_drain".resource,
            -10.0,
            AttributeModifier.Operation.ADD_VALUE
        ).addAttributeModifier(
            Attributes.LUCK,
            "lucky_sad".resource,
            1.0,
            AttributeModifier.Operation.ADD_VALUE
        ).addAttributeModifier(
            Attributes.SUBMERGED_MINING_SPEED,
            "drowning_in_tears".resource,
            1.5,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
    }

    class SimpleEffect(category: MobEffectCategory, color: Int): MobEffect(category, color)
}