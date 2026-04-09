package com.dfsek.terra.mod.config;

import com.dfsek.tectonic.api.config.template.ConfigTemplate;
import com.dfsek.tectonic.api.config.template.annotations.Default;
import com.dfsek.tectonic.api.config.template.annotations.Value;
import net.minecraft.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome.TemperatureModifier;
import net.minecraft.world.biome.BiomeEffects.GrassColorModifier;
import net.minecraft.world.biome.SpawnSettings;

import com.dfsek.terra.api.properties.Properties;


public class VanillaBiomeProperties implements ConfigTemplate, Properties {
    @Value("colors.grass")
    @Default
    private Integer grassColor = null;

    @Value("colors.water")
    @Default
    private Integer waterColor = null;

    @Value("colors.foliage")
    @Default
    private Integer foliageColor = null;

    @Value("colors.dry-foliage")
    @Default
    private Integer dryFoliageColor = null;

    @Value("colors.modifier")
    @Default
    private GrassColorModifier grassColorModifier = null;

    @Value("climate.precipitation")
    @Default
    private Boolean precipitation = true;

    @Value("climate.temperature")
    @Default
    private Float temperature = null;

    @Value("climate.temperature-modifier")
    @Default
    private TemperatureModifier temperatureModifier = null;

    @Value("climate.downfall")
    @Default
    private Float downfall = null;

    @Value("spawning")
    @Default
    private SpawnSettings spawnSettings = null;

    @Value("villager-type")
    @Default
    private
    RegistryKey<VillagerType> villagerType = null;

    public Integer getGrassColor() {
        return grassColor;
    }

    public Integer getWaterColor() {
        return waterColor;
    }

    public Integer getFoliageColor() {
        return foliageColor;
    }

    public Integer getDryFoliageColor() {
        return dryFoliageColor;
    }

    public GrassColorModifier getGrassColorModifier() {
        return grassColorModifier;
    }

    public Boolean getPrecipitation() {
        return precipitation;
    }

    public Float getTemperature() {
        return temperature;
    }

    public TemperatureModifier getTemperatureModifier() {
        return temperatureModifier;
    }

    public Float getDownfall() {
        return downfall;
    }

    public SpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    public RegistryKey<VillagerType> getVillagerType() {
        return villagerType;
    }
}
