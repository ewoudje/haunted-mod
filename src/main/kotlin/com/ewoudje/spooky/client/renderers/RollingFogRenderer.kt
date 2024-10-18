package com.ewoudje.spooky.client.renderers

import com.ewoudje.spooky.client.FogTextureBuilder
import com.ewoudje.spooky.client.Shaders
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
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f
import org.joml.Vector3fc
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.times
import java.lang.Exception
import java.nio.FloatBuffer
import java.util.stream.IntStream


object RollingFogRenderer {
    private var position = Vector3f()
    private var velocity = Vector3f()
    private var direction = Vector3f()
    private var shouldRender = false
    private var fogTexture = 0
    private var fogTextureShadow = 0;
    private var fogTextureLocation = 0
    private var fogTextureShadowLocation = 0

    private var mainCameraDepth = DepthOnlyRenderTarget(MainTarget.DEFAULT_WIDTH, MainTarget.DEFAULT_HEIGHT)

    fun updateFog(position: Vector3fc, velocity: Vector3fc, direction: Vector3fc) {
        this.position.set(position)
        this.velocity.set(velocity)
        this.direction.set(direction).normalize()
        shouldRender = true
    }

    fun tick() {
        if (!shouldRender) return
        position += velocity
    }


    fun render(viewMatrix: Matrix4fc, partialTick: Float) {
        if (!shouldRender) {
            return
        }

        if (fogTexture == 0) makeTextures()

        val fogPosition = position + (velocity * partialTick)
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

    private fun setupUniforms(shader: ShaderInstance, viewMatrixX: Matrix4fc, fogPosition: Vector3f) {
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
        shader.safeGetUniform("uFogPosition").set(fogPosition)
        shader.safeGetUniform("uFogNormal").set(direction)
        shader.safeGetUniform("uFogUp").set(up)
        shader.safeGetUniform("uFarPlane").set(RenderSystem.getProjectionMatrix().perspectiveFar())
        shader.safeGetUniform("uNearPlane").set(RenderSystem.getProjectionMatrix().perspectiveNear())
        shader.safeGetUniform("uCameraPos").set(cameraPosition)

        if (fogTextureLocation == 0) fogTextureLocation = GL30.glGetUniformLocation(shader.id, "uFogTexture")
        if (fogTextureShadowLocation == 0) fogTextureShadowLocation = GL30.glGetUniformLocation(shader.id, "uFogShadowTexture")
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

            if (fogTextureShadow != 0) {
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
            fogTextureShadow = 0
        }
    }
}