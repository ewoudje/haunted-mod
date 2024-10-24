package com.ewoudje.spooky.client

import com.ewoudje.spooky.SpookyMod
import com.ewoudje.spooky.blocks.SpookyBlocks
import com.ewoudje.spooky.client.models.SpookyModels
import com.ewoudje.spooky.client.particles.SpookyParticles
import com.ewoudje.spooky.client.renderers.RollingFogRenderer
import com.ewoudje.spooky.client.renderers.SpookyEntityRenderers
import com.ewoudje.spooky.world.fog.FogState
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.level.GrassColor
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.*
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

@Mod(SpookyMod.ID, dist = [Dist.CLIENT])
object ClientSpookyMod {

    init {
        MOD_BUS.addListener(Shaders::registerShaders)
        MOD_BUS.addListener(::buildTextures)
        MOD_BUS.addListener(SpookyModels::registerModels)
        MOD_BUS.addListener(SpookyModels::registerLayers)
        MOD_BUS.addListener(SpookyEntityRenderers::registerRenderers)
        MOD_BUS.addListener(SpookyParticles::registerProviders)
        MOD_BUS.addListener(SpookyVisions::registerLayer)
        MOD_BUS.addListener(::registerBlockColorHandlers)

        SpookySounds.REGISTRY.register(MOD_BUS)
        SpookyParticles.REGISTRY.register(MOD_BUS)
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
            SpookyBlocks.CURSED_GRASS
        )
    }

    fun buildTextures(event: TextureAtlasStitchedEvent?) {
        //RollingFogRenderer.makeTextures()
    }
}