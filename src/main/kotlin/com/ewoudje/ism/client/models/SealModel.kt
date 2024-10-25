package com.ewoudje.ism.client.models

import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition
import java.util.function.Supplier


object SealModel : Supplier<LayerDefinition> {
    override fun get(): LayerDefinition {
        val meshDefinition = MeshDefinition()
        val partDefinition = meshDefinition.root

        partDefinition.addSealSide("leftLid", false)
        partDefinition.addSealSide("rightLid", true)

        return LayerDefinition.create(meshDefinition, 64, 64)
    }

    private fun PartDefinition.addSealSide(name: String, yRot: Boolean): PartDefinition {
        val yTex = if (yRot) 40 else 20
        return addOrReplaceChild(
            name,
            CubeListBuilder()
                .addBox("", -18f, 0f, 0f, 36, 2, 3, 0, yTex)
                .addBox("", -17f, 0f, 3f, 34, 2, 4, 1, yTex - 3)
                .addBox("", -16f, 0f, 7f, 32, 2, 3, 2, yTex - 7)
                .addBox("", -14f, 0f, 10f, 28, 2, 3, 4, yTex - 10)
                .addBox("", -13f, 0f, 13f, 26, 2, 1, 5, yTex - 13)
                .addBox("", -10f, 0f, 14f, 20, 2, 2, 8, yTex - 14)
                .addBox("", -7f, 0f, 16f, 14, 2, 1, 11, yTex - 16)
                .addBox("", -3f, 0f, 17f, 6, 2, 1, 15, yTex - 17),
            PartPose.rotation(0.0f, if (yRot) Math.PI.toFloat() else 0.0f, 0.0f)
        )
    }

}