package com.ewoudje.ism.client

import com.ewoudje.ism.IsmMod
import com.ewoudje.ism.resource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyVisions {
    private var currentVision: Vision? = null
    private var visionUntil = Long.MIN_VALUE
    val REGISTRY_KEY = ResourceKey.createRegistryKey<Vision>(ResourceLocation.fromNamespaceAndPath(IsmMod.ID, "visions"))
    val REGISTRY = RegistryBuilder(REGISTRY_KEY)
        .onBake(::bakeVision)
        .sync(false)
        .create()

    val DEFFERED_REGISTRY = DeferredRegister.create(REGISTRY_KEY, IsmMod.ID)

    val CROWNING by DEFFERED_REGISTRY.register("crowning") { -> SimpleVision() }
    val OUTSIDE by DEFFERED_REGISTRY.register("outside") { -> SimpleVision() }

    private fun bakeVision(registry: Registry<Vision>) {
        registry.entrySet().forEach { (key, vision) ->
            vision.setup(key.location().withPath { "textures/misc/visions/$it.png" })
        }
    }

    fun showVision(vision: Vision, length: Float) {
        currentVision = vision
        visionUntil = System.currentTimeMillis() + (length * 1000).toInt()
    }

    fun registerLayer(event: RegisterGuiLayersEvent) {
        event.registerAboveAll("visions".resource) { g, d ->
            if (System.currentTimeMillis() > visionUntil) {
                currentVision = null
            }
            currentVision?.render(g, d)
        }
    }
}

interface Vision {
    fun setup(texture: ResourceLocation)
    fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker)
}

class SimpleVision(val transparency: Float = 0.5f): Vision {
    protected lateinit var texture: ResourceLocation
        private set

    override fun setup(texture: ResourceLocation) {
        if (this::texture.isInitialized) throw IllegalStateException("Already initialized")
        this.texture = texture
    }

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, transparency)
        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.disableDepthTest()
        RenderSystem.depthMask(false)
        RenderSystem.enableBlend()

        val pose = guiGraphics.pose().last().pose()
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        buffer.addVertex(pose, 0f, 0f, 0f).setUv(0f, 0f)
        buffer.addVertex(pose, 0f, guiGraphics.guiHeight().toFloat(), 0f).setUv(0f, 1f)
        buffer.addVertex(pose, guiGraphics.guiWidth().toFloat(), guiGraphics.guiHeight().toFloat(), 0f).setUv(1f, 1f)
        buffer.addVertex(pose, guiGraphics.guiWidth().toFloat(), 0f, 0f).setUv(1f, 0f)
        BufferUploader.drawWithShader(buffer.buildOrThrow())

        RenderSystem.disableBlend()
        RenderSystem.depthMask(true)
        RenderSystem.enableDepthTest()
    }
}