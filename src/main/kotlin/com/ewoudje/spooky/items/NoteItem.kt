package com.ewoudje.spooky.items

import com.ewoudje.spooky.component
import com.ewoudje.spooky.resource
import com.mojang.realmsclient.util.task.LongRunningTask.setScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class NoteItem(val noteTexture: ResourceLocation, properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val item = player.getItemInHand(usedHand)
        if (usedHand == InteractionHand.OFF_HAND && !player.isShiftKeyDown) return InteractionResultHolder.pass(item)
        if (!level.isClientSide) return InteractionResultHolder.consume(item)

        Minecraft.getInstance().setScreen(Gui())
        return InteractionResultHolder.success(item)
    }

    inner class Gui : Screen("A Note".component) {
        override fun isPauseScreen(): Boolean = false

        override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
            val xOffset = (width - NOTE_WIDTH) / 2
            val yOffset = (height - NOTE_HEIGHT) / 2 - 20
            guiGraphics.blit(BACKGROUND_NOTE_TEXTURE, xOffset, yOffset, 0, 0, NOTE_WIDTH, NOTE_HEIGHT)
            guiGraphics.blit(noteTexture, xOffset, yOffset, 0, 0, NOTE_WIDTH, NOTE_HEIGHT)
        }
    }

    override fun hasCraftingRemainingItem(stack: ItemStack): Boolean = true
    override fun getCraftingRemainingItem(itemStack: ItemStack): ItemStack = itemStack.copy()

    companion object {
        private val NOTE_WIDTH = 130
        private val NOTE_HEIGHT = 160
        private val BACKGROUND_NOTE_TEXTURE = "textures/gui/note.png".resource
    }
}