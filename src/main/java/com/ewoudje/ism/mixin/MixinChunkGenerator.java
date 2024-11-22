package com.ewoudje.ism.mixin;

import com.ewoudje.ism.world.ExtraChunkGeneratorStructureState;
import com.ewoudje.ism.world.SinglePlacementData;
import com.ewoudje.ism.world.IsmWorldState;
import com.ewoudje.ism.world.structures.SingleStructurePlacement;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator {

    @WrapMethod(method = "findNearestMapStructure")
    Pair<BlockPos, Holder<Structure>> haunted$findNearestMapStructure(ServerLevel level, HolderSet<Structure> structures, BlockPos pos, int searchRadius, boolean skipKnownStructures, Operation<Pair<BlockPos, Holder<Structure>>> original) {
        var ogResult = original.call(level, structures, pos, searchRadius, skipKnownStructures);
        ChunkGeneratorStructureState state = level.getChunkSource().getGeneratorState();
        SinglePlacementData data = IsmWorldState.Companion.getIsmWorldState(level).getSinglePlacementData();

        BlockPos rPos = null;
        Holder<Structure> result = null;
        double d2 = Double.MAX_VALUE;

        if (ogResult != null) {
            rPos = ogResult.getFirst();
            result = ogResult.getSecond();
            d2 = rPos.distSqr(pos);
        }

        ((ExtraChunkGeneratorStructureState) state).setData(data);

        synchronized (data) {
            for (Holder<Structure> s : structures) {
                for (StructurePlacement p : state.getPlacementsForStructure(s)) {
                    if (p instanceof SingleStructurePlacement single) {
                        var location = data.getLocation(single);
                        if (location == null) throw new IllegalStateException();

                        var chunkPos = location.getPos();
                        if (chunkPos == null)
                            chunkPos = data.getPlacementGuess(single);

                        if (chunkPos == null)
                            continue;

                        BlockPos bPos = p.getLocatePos(chunkPos);
                        double d0 = pos.distSqr(bPos);
                        if (d0 < d2) {
                            rPos = bPos;
                            d2 = d0;
                            result = s;
                        }
                    }
                }
            }
        }

        if (rPos == null && result == null) return null;
        return Pair.of(rPos, result);
    }

    @Inject(method = "tryGenerateStructure", at = @At("RETURN"))
    void haunted$noteStructures(StructureSet.StructureSelectionEntry structureSelectionEntry, StructureManager structureManager, RegistryAccess registryAccess, RandomState random, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess chunk, ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir, @Local StructureStart structurestart) {
        ServerChunkCache source = (ServerChunkCache) ((StructureManagerAccessor) structureManager).getLevel().getChunkSource();
        SinglePlacementData data = IsmWorldState.Companion.getIsmWorldState(source.level).getSinglePlacementData();
        var state = source.getGeneratorState();
        var placements = state.getPlacementsForStructure(structureSelectionEntry.structure());

        ((ExtraChunkGeneratorStructureState) state).setData(data);

        synchronized (data) {
            for (var placement : placements) {
                if (placement instanceof SingleStructurePlacement single) {
                    var location = data.getLocation(single);
                    if (location == null) throw new IllegalStateException();

                    if (location.getPos() != null) continue;
                    var locations = location.getPositions();

                    boolean isFromHere = locations.contains(chunkPos);
                    if (!isFromHere) continue;

                    if (!structurestart.isValid()) {
                        data.removePlacement(single, chunkPos);
                        continue;
                    }

                    data.foundPlacement(single, chunkPos);
                }
            }
        }
    }
}
