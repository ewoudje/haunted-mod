package com.ewoudje.spooky.client

import com.ewoudje.spooky.SpookyMod
import com.ewoudje.spooky.client.models.SpookyModels
import com.ewoudje.spooky.client.particles.SpookyParticles
import com.ewoudje.spooky.client.renderers.RollingFogRenderer
import com.ewoudje.spooky.client.renderers.SpookyEntityRenderers
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.commands.CommandSourceStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(SpookyMod.ID, dist = [Dist.CLIENT])
object ClientSpookyMod {

    init {
        MOD_BUS.addListener(Shaders::registerShaders)
        MOD_BUS.addListener(::buildTextures)
        MOD_BUS.addListener(SpookyModels::registerModels)
        MOD_BUS.addListener(SpookyModels::registerLayers)
        MOD_BUS.addListener(SpookyEntityRenderers::registerRenderers)
        MOD_BUS.addListener(SpookyParticles::registerProviders)

        SpookySounds.REGISTRY.register(MOD_BUS)
        SpookyParticles.REGISTRY.register(MOD_BUS)

        FORGE_BUS.addListener(::addClientCommands)
        FORGE_BUS.addListener(::tick)
        FORGE_BUS.addListener(::render)
    }

    fun tick(event: ClientTickEvent.Post) {
        RollingFogRenderer.tick()
    }

    fun render(event: RenderLevelStageEvent) {

        if (event.stage == RenderLevelStageEvent.Stage.AFTER_LEVEL)
            RollingFogRenderer.render(event.modelViewMatrix, event.partialTick.gameTimeDeltaTicks)
    }

    fun buildTextures(event: TextureAtlasStitchedEvent?) {
        //RollingFogRenderer.makeTextures()
    }

    fun addClientCommands(event: RegisterClientCommandsEvent) {
        event.dispatcher.register(
            literal<CommandSourceStack>("cspooky").then(
                literal<CommandSourceStack>("rebuildTextures").executes { ctx ->
                    buildTextures(null)
                    1
                }).then(literal<CommandSourceStack>("fog").executes { ctx ->
                    val entity = ctx.source.entityOrException
                    RollingFogRenderer.updateFog(entity.eyePosition.toVector3f(), Vector3f(0.1f, 0f, 0f), Vector3f(0.5f, 1f, 0f).normalize())
                    1
                }))
    }
}