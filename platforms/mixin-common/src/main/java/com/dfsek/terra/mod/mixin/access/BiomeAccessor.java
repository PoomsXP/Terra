package com.dfsek.terra.mod.mixin.access;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor("weather")
    Biome.Weather getWeather();

    @Accessor("generationSettings")
    GenerationSettings getGenerationSettings();
}
