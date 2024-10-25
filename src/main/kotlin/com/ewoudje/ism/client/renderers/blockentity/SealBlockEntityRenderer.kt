package com.ewoudje.ism.client.renderers.blockentity

import com.ewoudje.ism.blockentities.SealBlockEntity
import com.ewoudje.ism.client.IsmRenderTypes
import com.ewoudje.ism.client.models.IsmModels
import com.ewoudje.ism.client.renderers.blockentity.SealHackerBlockEntityRenderer.Companion.renderPrismarineModel
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Unit
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.renderable.BakedModelRenderable
import net.neoforged.neoforge.client.model.renderable.ITextureRenderTypeLookup
import org.joml.Quaternionf

class SealBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) : BlockEntityRenderer<SealBlockEntity> {
    private val leftLid = BakedModelRenderable.of(IsmModels.SEAL_LEFT).withContext(BakedModelRenderable.Context(ModelData.EMPTY))
    private val rightLid = BakedModelRenderable.of(IsmModels.SEAL_RIGHT).withContext(BakedModelRenderable.Context(ModelData.EMPTY))

    private fun SealBlockEntity.distanceFromCenter(partialTick: Float): Float = scaledProgress(partialTick, 0.5f, 1f) * 0.61f
    private fun SealBlockEntity.rotationAngle(partialTick: Float): Float = scaledProgress(partialTick, 0.3f, 0.5f) * 135
    private fun SealBlockEntity.upwardsDistance(partialTick: Float): Float = scaledProgress(partialTick, 0.3f, 0.5f) * 0.1f
    private fun SealBlockEntity.distanceKeyDown(partialTick: Float): Float = scaledProgress(partialTick, 0.0f, 0.3f) * 0.3f

    override fun render(
        blockEntity: SealBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5f, 1.0f, 0.5f)
        renderKey(poseStack, bufferSource, packedLight, packedOverlay, blockEntity.distanceKeyDown(partialTick))

        poseStack.mulPose(Quaternionf().fromAxisAngleDeg(0f, 1f, 0f, blockEntity.rotationAngle(partialTick)))
        poseStack.mulPose(Quaternionf().fromAxisAngleDeg(1f, 0f, 0f, 90f))
        poseStack.scale(1/8f, 1/8f, 1/8f)
        val up = blockEntity.upwardsDistance(partialTick) * 8f
        poseStack.translate(blockEntity.distanceFromCenter(partialTick) * 8f, 0.0f, -up)
        leftLid.render(poseStack, bufferSource, EntityCutoutTextureProvider, packedLight, packedOverlay, partialTick, Unit.INSTANCE)
        poseStack.translate(blockEntity.distanceFromCenter(partialTick) * -16f, 0.0f, 0f)
        rightLid.render(poseStack, bufferSource, EntityCutoutTextureProvider, packedLight, packedOverlay, partialTick, Unit.INSTANCE)
        poseStack.popPose()
    }

    fun renderKey(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        down: Float,
    ) {
        if (down >= 0.3f) return
        poseStack.pushPose()
        poseStack.mulPose(Quaternionf().fromAxisAngleDeg(0f, 1f, 0f, 45f))
        poseStack.translate(0f, 0.1f - down, 0f)
        poseStack.scale(0.2f, 0.2f, 0.2f)


        val buffer = bufferSource.getBuffer(IsmRenderTypes.GEM_RENDER_TYPE)
        val pose = poseStack.last()

        buffer.setLight(packedLight)
        buffer.setOverlay(packedOverlay)

        renderPrismarineModel { x, y, z ->
            buffer
                .addVertex(pose, x, y, z)
                .setColor(
                    if (y < 0.8) 0.2f else 0.5f,
                    if (y < 0.8) 0.2f else 0.5f,
                    if (y < 0.8) 0.2f else 0.5f, 1f)
        }

        poseStack.popPose()
    }

    object EntityCutoutTextureProvider: ITextureRenderTypeLookup {
        override fun get(name: ResourceLocation): RenderType = RenderType.ENTITY_CUTOUT.apply(name)
    }

}