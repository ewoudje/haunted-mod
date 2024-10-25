package com.ewoudje.ism.mixin;

import com.ewoudje.ism.blocks.IsmBlocks;
import com.ewoudje.ism.world.IsmWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpreadingSnowyDirtBlock.class)
public class MixinSpreadingSnowyDirtBlock {

    @Inject(method = "randomTick", at = @At("TAIL"))
    void haunted$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (level.getServer().overworld() != level) return;
        if (random.nextDouble() < 0.15) {
            if (IsmWorldState.Companion.getIsmWorldState(level)
                    .getFogState()
                    .isInFog(new Vector3d(pos.getX(), pos.getY(), pos.getZ()))) {

                level.setBlockAndUpdate(pos, IsmBlocks.INSTANCE.getCURSED_GRASS().defaultBlockState());
            }
        }
    }

}
