package com.ewoudje.spooky.mixin;

import com.ewoudje.spooky.world.ExtraChunkGeneratorStructureState;
import com.ewoudje.spooky.world.SinglePlacementData;
import com.ewoudje.spooky.world.structures.SingleStructurePlacement;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(ChunkGeneratorStructureState.class)
public class MixinChunkGeneratorStructureState implements ExtraChunkGeneratorStructureState {

    @Shadow @Final private Map<Structure, List<StructurePlacement>> placementsForStructure;
    @Unique private Map<SingleStructurePlacement, Set<ChunkPos>> singlePlacements;

    @Inject(method = "generatePositions", at = @At("TAIL"))
    private void haunted$generatePositions(CallbackInfo ci) {
        singlePlacements = placementsForStructure.values().stream()
                .flatMap(List::stream)
                .filter(i -> i instanceof SingleStructurePlacement)
                .collect(Collectors.toMap(
                        i -> ((SingleStructurePlacement) i),
                        i -> ((SingleStructurePlacement) i).choosePositions((ChunkGeneratorStructureState) (Object) this)));
    }

    @Override
    public void setData(@NotNull SinglePlacementData data) {
        synchronized (data) {
            for (SingleStructurePlacement p : singlePlacements.keySet()) {
                var location = data.getLocation(p);
                if (location == null) {
                    data.initialLocations(p, singlePlacements.get(p));
                    continue;
                }

                if (location.getPositions() != null) {
                    singlePlacements.replace(p, location.getPositions());
                } else {
                    singlePlacements.remove(p);
                }
            }
        }
    }

    @Override
    public @NotNull Set<ChunkPos> getPossiblePositions(@NotNull SingleStructurePlacement p) {
        var r = singlePlacements.get(p);
        if (r == null) return Collections.emptySet();
        return r;
    }
}
