package com.dfsek.terra.minestom.biome;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.minestom.api.BiomeFactory;
import com.dfsek.terra.minestom.config.VanillaBiomeProperties;


public class MinestomUserDefinedBiomeFactory implements BiomeFactory {
    private final DynamicRegistry<Biome> biomeRegistry = MinecraftServer.getBiomeRegistry();
    private final @NotNull Biome plainsBiome = Objects.requireNonNull(biomeRegistry.get(Key.key("minecraft:plains")));

    private static <T> T mergeNullable(T first, T second) {
        if(first == null) return second;
        return first;
    }

    @Subst("value")
    protected static String createBiomeID(ConfigPack pack, String biomeId) {
        return pack.getID().toLowerCase() + "/" + biomeId.toLowerCase(Locale.ROOT);
    }

    @Override
    public UserDefinedBiome create(ConfigPack pack, com.dfsek.terra.api.world.biome.Biome source) {
        VanillaBiomeProperties properties = source.getContext().get(VanillaBiomeProperties.class);
        RegistryKey<Biome> parentKey = ((MinestomBiome) source.getPlatformBiome()).getHandle();
        Biome parent = mergeNullable(biomeRegistry.get(parentKey), plainsBiome);
        BiomeEffects parentEffects = parent.effects();
        Key key = Key.key("terra", createBiomeID(pack, source.getID()));

        BiomeEffects.Builder effectsBuilder = BiomeEffects.builder()
            .waterColor(mergeNullable(properties.getWaterColor(), parentEffects.waterColor()))
            .foliageColor(mergeNullable(properties.getFoliageColor(), parentEffects.foliageColor()))
            .dryFoliageColor(mergeNullable(properties.getDryFoliageColor(), parentEffects.dryFoliageColor()))
            .grassColor(mergeNullable(properties.getGrassColor(), parentEffects.grassColor()))
            .grassColorModifier(mergeNullable(properties.getGrassColorModifier(), parentEffects.grassColorModifier()));

        Biome target = Biome.builder()
            .downfall(mergeNullable(properties.getDownfall(), parent.downfall()))
            .precipitation(mergeNullable(properties.getPrecipitation(), parent.hasPrecipitation()))
            .temperature(mergeNullable(properties.getTemperature(), parent.temperature()))
            .temperatureModifier(mergeNullable(properties.getTemperatureModifier(), parent.temperatureModifier()))
            .effects(effectsBuilder.build())
            .build();

        RegistryKey<Biome> registryKey = MinecraftServer.getBiomeRegistry().register(key, target);
        return new UserDefinedBiome(key, registryKey, source.getID(), target);
    }
}
