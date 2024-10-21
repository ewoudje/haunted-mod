package com.ewoudje.spooky.client.renderers

import com.ewoudje.spooky.client.FogTextureBuilder
import com.ewoudje.spooky.client.Shaders
import com.ewoudje.spooky.world.fog.FogState
import com.ibm.icu.lang.UCharacter.GraphemeClusterBreak.V
import com.mojang.blaze3d.pipeline.MainTarget
import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.synth.PerlinNoise
import org.joml.*
import org.lwjgl.opengl.GL30
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.times
import java.lang.Exception
import java.nio.FloatBuffer
import java.util.stream.IntStream


object RollingFogRenderer {
    var position = Vector3d()
    var lastPosition = Vector3d()
    var nextTick: (() -> Unit)? = null
    var velocity = Vector3f()
    var direction = Vector3f()
    var shouldRender = false
    var height = 192.0
    var fogRoll = 0f
    private var fogTexture = 0

    private var mainCameraDepth = DepthOnlyRenderTarget(MainTarget.DEFAULT_WIDTH, MainTarget.DEFAULT_HEIGHT)
    fun tick() {
        if (!shouldRender) return
        if (nextTick != null) {
            nextTick!!()
            nextTick = null
        } else {
            lastPosition = position
            position = position.add(velocity.x.toDouble(), velocity.y.toDouble(), velocity.z.toDouble(), Vector3d())
        }
    }


    fun render(viewMatrix: Matrix4fc, partialTick: Float) {
        if (!shouldRender) {
            return
        }

        if (fogTexture == 0) makeTextures()

        val fogPosition = lastPosition.lerp(position, partialTick.toDouble(), Vector3d())

        fogRoll -= velocity.length() * partialTick
        val renderTarget = Minecraft.getInstance().mainRenderTarget

        grabDepthBuffer()
        setupUniforms(Shaders.ROLLING_FOG, viewMatrix, fogPosition)
        blit(renderTarget)
    }

    private fun grabDepthBuffer() {
        val mainRenderTarget = Minecraft.getInstance().mainRenderTarget
        if (mainRenderTarget.width != mainCameraDepth.width || mainRenderTarget.height != mainCameraDepth.height) {
            mainCameraDepth.resize(mainRenderTarget.width, mainRenderTarget.height, Minecraft.ON_OSX)
        }
        mainCameraDepth = mainCameraDepth.copyBufferSettings(mainRenderTarget)
        mainCameraDepth.copyDepthFrom(mainRenderTarget)
        mainRenderTarget.bindWrite(false)
    }

    private fun setupUniforms(shader: ShaderInstance, viewMatrixX: Matrix4fc, fogPosition: Vector3d) {
        val cameraPosition = Minecraft.getInstance().gameRenderer.mainCamera.position.toVector3f()
        val invertedViewMatrix = Matrix4f(viewMatrixX)
            //.translation(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
            .invert()


        val invertedProjectionMatrix = Matrix4f(RenderSystem.getProjectionMatrix()).invert()
        val up = if (direction.z > 0.99) Vector3f(1f, 0f, 0f) else Vector3f(0f, 0f, 1f)
        up.cross(direction)

        shader.setSampler("uDepth", mainCameraDepth.depthTextureId)
        shader.setSampler("uFogTexture", fogTexture)
        shader.safeGetUniform("uInverseView").set(invertedViewMatrix)
        shader.safeGetUniform("uInverseProjection").set(invertedProjectionMatrix)
        //TODO fix
        shader.safeGetUniform("uFogPosition").set(fogPosition.x.toFloat(), fogPosition.y.toFloat(), fogPosition.z.toFloat())
        shader.safeGetUniform("uFogNormal").set(direction)
        shader.safeGetUniform("uFogUp").set(up)
        shader.safeGetUniform("uFarPlane").set(RenderSystem.getProjectionMatrix().perspectiveFar())
        shader.safeGetUniform("uNearPlane").set(RenderSystem.getProjectionMatrix().perspectiveNear())
        shader.safeGetUniform("uCameraPos").set(cameraPosition)
        shader.safeGetUniform("uScroll").set(fogRoll)
        shader.safeGetUniform("uHeight").set(height.toFloat())
    }


    private fun blit(target: RenderTarget) {
        val width = target.width.toFloat()
        val height = target.height.toFloat()

        RenderSystem.depthMask(false)
        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()

        val oldShader = RenderSystem.getShader()
        RenderSystem.setShader(Shaders::ROLLING_FOG)

        val shader = RenderSystem.getShader()!!
        shader.apply()

        val bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferbuilder.addVertex(-1f, -1f, 0.0f).setUv(0f, 0f)
        bufferbuilder.addVertex(1f, -1f, 0.0f).setUv(1f, 0f)
        bufferbuilder.addVertex(1f, 1f, 0.0f).setUv(1f, 1f)
        bufferbuilder.addVertex(-1f, 1f, 0.0f).setUv(0f, 1f)
        BufferUploader.draw(bufferbuilder.buildOrThrow())

        RenderSystem.setShader { oldShader }

        RenderSystem.depthMask(true)
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    fun makeTextures() {
        try {
            if (fogTexture != 0) {
                TextureUtil.releaseTextureId(fogTexture)
            }

            val textureSize = 2048
            fun makeTexture(buffer: FloatBuffer, type: Int): Int {
                val texture = TextureUtil.generateTextureId()
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture)
                GL30.glTexImage2D(
                    GL30.GL_TEXTURE_2D,
                    0,
                    type,
                    textureSize,
                    textureSize,
                    0,
                    GL30.GL_RED,
                    GL30.GL_FLOAT,
                    buffer
                )
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_MIRRORED_REPEAT)
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_MIRRORED_REPEAT)
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_R, GL30.GL_MIRRORED_REPEAT)
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR)
                GlStateManager._bindTexture(0)
                return texture
            }


            val noise = PerlinNoise.create(RandomSource.create(128), IntStream.of(1, 2, 8, 16))
            val noise2 = PerlinNoise.create(RandomSource.create(512), IntStream.of(3, 5, 7, 10))
            val textureBuffer = FogTextureBuilder.build2DTexture(textureSize) { x, y ->
                (noise.getValue(x.toDouble(), 0.0, y.toDouble()).toFloat() +
                        noise2.getValue(x.toDouble(), 0.0, y.toDouble()).toFloat()) / 2
            }

            fogTexture = makeTexture(textureBuffer, GL30.GL_R32F)
        } catch (e: Exception) {
            e.printStackTrace()
            fogTexture = 0
        }
    }


}