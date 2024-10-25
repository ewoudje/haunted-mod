package com.ewoudje.ism.client.renderers

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.platform.TextureUtil
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL30


class DepthOnlyRenderTarget(width: Int, height: Int) : TextureTarget(width, height, true, Minecraft.ON_OSX) {
    override fun createBuffers(width: Int, height: Int, isOnOSX: Boolean) {
        super.createBuffers(width, height, isOnOSX)
        if (colorTextureId > -1) {
            if (frameBufferId > -1) {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId)
                GL30.glDrawBuffer(GL30.GL_NONE)
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
            }
            TextureUtil.releaseTextureId(this.colorTextureId)
            this.colorTextureId = -1
        }
    }

    fun copyBufferSettings(mainRenderTarget: RenderTarget): DepthOnlyRenderTarget {
        if (mainRenderTarget.isStencilEnabled) {
            enableStencil()
            return this
        } else if (isStencilEnabled) {
            destroyBuffers()
            return DepthOnlyRenderTarget(width, height)
        } else {
            return this
        }
    }

    override fun getColorTextureId(): Int = depthTextureId
}