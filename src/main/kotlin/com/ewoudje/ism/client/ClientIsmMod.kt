package com.ewoudje.ism.client

import com.ewoudje.ism.IsmMod
import com.ewoudje.ism.blocks.IsmBlocks
import com.ewoudje.ism.client.models.IsmModels
import com.ewoudje.ism.client.particles.IsmParticles
import com.ewoudje.ism.client.renderers.RollingFogRenderer
import com.ewoudje.ism.client.renderers.SpookyEntityRenderers
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.world.level.GrassColor
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.*
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(IsmMod.ID, dist = [Dist.CLIENT])
object ClientIsmMod {

    init {
        MOD_BUS.addListener(IsmShaders::registerShaders)
        MOD_BUS.addListener(::buildTextures)
        MOD_BUS.addListener(IsmModels::registerModels)
        MOD_BUS.addListener(IsmModels::registerLayers)
        MOD_BUS.addListener(SpookyEntityRenderers::registerRenderers)
        MOD_BUS.addListener(IsmParticles::registerProviders)
        MOD_BUS.addListener(SpookyVisions::registerLayer)
        MOD_BUS.addListener(::registerBlockColorHandlers)

        IsmSounds.REGISTRY.register(MOD_BUS)
        IsmParticles.REGISTRY.register(MOD_BUS)
        SpookyVisions.DEFFERED_REGISTRY.register(MOD_BUS)

        FORGE_BUS.addListener(::tick)
        FORGE_BUS.addListener(::render)
    }

    fun tick(event: ClientTickEvent.Post) {
        // If there is no player then we aren't loaded, if we aren't loaded we don't need to tick
        Minecraft.getInstance().player ?: return

        ClientFogHandler.tick()
    }

    fun render(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_LEVEL)
            RollingFogRenderer.render(event.modelViewMatrix, event.partialTick.gameTimeDeltaTicks)
    }

    fun registerBlockColorHandlers(event: RegisterColorHandlersEvent.Block) {
        event.register(
            { a, b, c, d -> if (b != null && c != null) BiomeColors.getAverageGrassColor(b, c) else GrassColor.getDefaultColor() },
            IsmBlocks.CURSED_GRASS
        )
    }

    fun buildTextures(event: TextureAtlasStitchedEvent?) {
        //RollingFogRenderer.makeTextures()
    }
}