package com.ewoudje.spooky.items

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class CraftingToolItem(properties: Properties) : Item(properties) {
    override fun hasCraftingRemainingItem(stack: ItemStack): Boolean = true
    override fun getCraftingRemainingItem(itemStack: ItemStack): ItemStack = itemStack.copy().apply { damageValue++ }.let {
        if (it.damageValue >= it.maxDamage) ItemStack.EMPTY else it
    }
}