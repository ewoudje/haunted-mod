package com.ewoudje.ism.mixin;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.*;
import java.util.Set;

@Mixin(ShaderInstance.class)
public class MixinQuickDebugShaders {

    @Redirect(method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceProvider;openAsReader(Lnet/minecraft/resources/ResourceLocation;)Ljava/io/BufferedReader;"))
    private BufferedReader debugReader(ResourceProvider instance, ResourceLocation location) throws IOException {
        if (System.getenv().containsKey("shadersDebug") && System.getenv().get("shadersDebug").equals(location.getNamespace())) {
            StringBuilder file = new StringBuilder();
            file.append("../src/main/resources/assets/");
            file.append(location.getNamespace());
            file.append("/");
            file.append(location.getPath());

            return new BufferedReader(new FileReader(file.toString()));
        } else return instance.openAsReader(location);
    }

    @Redirect(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceProvider;getResourceOrThrow(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/Resource;"))
    private static Resource getDebugResource(ResourceProvider instance, ResourceLocation location) throws IOException {
        if (System.getenv().containsKey("shadersDebug") && System.getenv().get("shadersDebug").equals(location.getNamespace())) {
            StringBuilder file = new StringBuilder();
            file.append("../src/main/resources/assets/");
            file.append(location.getNamespace());
            file.append("/");
            file.append(location.getPath());

            return new Resource(
                    new PackResources() {
                @Override
                public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
                    return null;
                }

                @Override
                public @Nullable IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
                    return null;
                }

                @Override
                public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {

                }

                @Override
                public Set<String> getNamespaces(PackType type) {
                    return Set.of();
                }

                @Override
                public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
                    return null;
                }

                @Override
                public PackLocationInfo location() {
                    return null;
                }

                @Override
                public void close() {

                }

                        @Override
                        public String packId() {
                            return location.getNamespace();
                        }
                    }, () -> new FileInputStream(file.toString()));
        } else return instance.getResourceOrThrow(location);
    }
}
