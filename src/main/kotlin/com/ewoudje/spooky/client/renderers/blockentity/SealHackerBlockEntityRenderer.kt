package com.ewoudje.spooky.client.renderers.blockentity

import com.ewoudje.spooky.blockentities.SealHackerBlockEntity
import com.ewoudje.spooky.client.SpookyRenderTypes
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class SealHackerBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) : BlockEntityRenderer<SealHackerBlockEntity> {

    override fun render(
        blockEntity: SealHackerBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if (!blockEntity.isSlotted()) return

        poseStack.pushPose()
        poseStack.translate(0.5, 0.5, 0.5)
        poseStack.scale(0.2f, 0.2f, 0.2f)

        val buffer = bufferSource.getBuffer(SpookyRenderTypes.GEM_RENDER_TYPE)
        val pose = poseStack.last()

        buffer.setLight(packedLight)
        buffer.setOverlay(packedOverlay)

        renderPrismarineModel { x, y, z ->
            buffer
                .addVertex(pose, x, y, z)
                .setColor(
                    if (y < 0.8) 0.8f else 0.2f,
                    0.3f,
                    if (y > 0.8) 0.8f else 0.2f, 0.85f)
        }

        poseStack.popPose()
    }


    companion object {
        inline fun renderPrismarineModel(applyVertex: (Float, Float, Float) -> Unit) {
            applyVertex(0.0f, 1.0f, 0.0f)
            applyVertex(0.0f, 0.0f, 1.0f)
            applyVertex(1.0f, 0.0f, 0.0f)

            applyVertex(0.0f, 1.0f, 0.0f)
            applyVertex(1.0f, 0.0f, 0.0f)
            applyVertex(0.0f, 0.0f, -1.0f)

            applyVertex(0.0f, 1.0f, 0.0f)
            applyVertex(0.0f, 0.0f, -1.0f)
            applyVertex(-1.0f, 0.0f, 0.0f)

            applyVertex(0.0f, 1.0f, 0.0f)
            applyVertex(-1.0f, 0.0f, 0.0f)
            applyVertex(0.0f, 0.0f, 1.0f)
        }
    }
}