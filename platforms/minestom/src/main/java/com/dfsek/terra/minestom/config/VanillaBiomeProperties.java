package com.dfsek.terra.minestom.config;

import com.dfsek.tectonic.api.config.template.ConfigTemplate;
import com.dfsek.tectonic.api.config.template.annotations.Default;
import com.dfsek.tectonic.api.config.template.annotations.Value;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.world.biome.Biome.TemperatureModifier;
import net.minestom.server.world.biome.BiomeEffects;
import net.minestom.server.world.biome.BiomeEffects.GrassColorModifier;

import com.dfsek.terra.api.properties.Properties;


public class VanillaBiomeProperties implements ConfigTemplate, Properties {
    @Value("colors.grass")
    @Default
    private RGBLike grassColor = null;

    @Value("colors.water")
    @Default
    private RGBLike waterColor = null;

    @Value("colors.foliage")
    @Default
    private RGBLike foliageColor = null;

    @Value("colors.dry-foliage")
    @Default
    private RGBLike dryFoliageColor = null;

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

    public RGBLike getGrassColor() {
        return grassColor;
    }

    public RGBLike getWaterColor() {
        return waterColor;
    }

    public RGBLike getFoliageColor() {
        return foliageColor;
    }

    public RGBLike getDryFoliageColor() {
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
}
