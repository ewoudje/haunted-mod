package com.ewoudje.spooky.mixin;

import com.ewoudje.spooky.blocks.SpookyBlocks;
import com.ewoudje.spooky.world.SpookyWorldState;
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
        if (random.nextDouble() < 0.1) {
            if (SpookyWorldState.Companion.getSpookyWorldState(level)
                    .getFogState()
                    .isInFog(new Vector3d(pos.getX(), pos.getY(), pos.getZ()))) {

                level.setBlockAndUpdate(pos, SpookyBlocks.INSTANCE.getCURSED_GRASS().defaultBlockState());
            }
        }
    }

}
