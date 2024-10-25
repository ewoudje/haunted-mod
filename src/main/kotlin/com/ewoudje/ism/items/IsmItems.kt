package com.ewoudje.ism.items

import com.ewoudje.ism.IsmEffects
import com.ewoudje.ism.IsmMod
import com.ewoudje.ism.blocks.IsmBlocks
import com.ewoudje.ism.resource
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.component.ItemLore
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmItems {
    val REGISTRY = DeferredRegister.createItems(IsmMod.ID)

    val UNSEAL_NOTE by REGISTRY.register("unseal_note") {-> NoteItem(
        "textures/gui/unseal_text.png".resource,
        Properties()
            .stacksTo(1)
    )}

    val SEAL_SMELLER by REGISTRY.register("seal_smeller") {-> StructureSmeller(
        Properties()
            .stacksTo(1)
            .component(DataComponents.LORE, ItemLore(listOf(
                Component.translatable("item.ism.seal_smeller.lore1"),
                Component.translatable("item.ism.seal_smeller.lore2")
            )))
    )}

    val ENGRAVING_TOOL by REGISTRY.register("engraving_tool") {-> CraftingToolItem(
        Properties()
            .durability(4)
            .setNoRepair()
    )}

    val ENGRAVED_DIAMOND by REGISTRY.register("engraved_diamond") {-> Item(
        Properties()
    )}

    val REDSTONE_ENGRAVED_DIAMOND by REGISTRY.register("redstone_engraved_diamond") {-> Item(
        Properties()
    )}

    val SEAL_HACKER by REGISTRY.register("seal_hacker") {-> BlockItem(
        IsmBlocks.SEAL_HACKER,
        Properties()
    )}

    val SAD_SEED by REGISTRY.register("sad_seed") {-> ItemNameBlockItem(
        IsmBlocks.SAD_CROP,
        Properties()
            .stacksTo(16)
    )}

    val SAD_FRUIT by REGISTRY.register("sad_fruit") {-> Item(
        Properties()
            .food(FoodProperties.Builder()
                .nutrition(4)
                .saturationModifier(0.1f)
                .effect({ MobEffectInstance(IsmEffects.SAD,  20 * 60 * 3) }, 0.9f)
                .build())
    )}
}