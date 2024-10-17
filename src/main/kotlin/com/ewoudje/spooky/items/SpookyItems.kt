package com.ewoudje.spooky.items

import com.ewoudje.spooky.SpookyMod
import com.ewoudje.spooky.blocks.SpookyBlocks
import com.ewoudje.spooky.resource
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.component.ItemLore
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyItems {
    val REGISTRY = DeferredRegister.createItems(SpookyMod.ID)

    val UNSEAL_NOTE by REGISTRY.register("unseal_note") {-> NoteItem(
        "textures/gui/unseal_text.png".resource,
        Properties()
            .stacksTo(1)
    )}

    val SEAL_SMELLER by REGISTRY.register("seal_smeller") {-> StructureSmeller(
        Properties()
            .stacksTo(1)
            .component(DataComponents.LORE, ItemLore(listOf(
                Component.translatable("item.haunted.seal_smeller.lore1"),
                Component.translatable("item.haunted.seal_smeller.lore2")
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
        SpookyBlocks.SEAL_HACKER,
        Properties()
    )}
}