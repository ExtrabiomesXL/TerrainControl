package com.khorn.terraincontrol.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.exception.InvalidConfigException;
import com.khorn.terraincontrol.generator.resourcegens.AboveWaterGen;
import com.khorn.terraincontrol.generator.resourcegens.CactusGen;
import com.khorn.terraincontrol.generator.resourcegens.CustomObjectGen;
import com.khorn.terraincontrol.generator.resourcegens.CustomStructureGen;
import com.khorn.terraincontrol.generator.resourcegens.DungeonGen;
import com.khorn.terraincontrol.generator.resourcegens.GrassGen;
import com.khorn.terraincontrol.generator.resourcegens.LiquidGen;
import com.khorn.terraincontrol.generator.resourcegens.OreGen;
import com.khorn.terraincontrol.generator.resourcegens.PlantGen;
import com.khorn.terraincontrol.generator.resourcegens.ReedGen;
import com.khorn.terraincontrol.generator.resourcegens.SaplingGen;
import com.khorn.terraincontrol.generator.resourcegens.SmallLakeGen;
import com.khorn.terraincontrol.generator.resourcegens.TreeGen;
import com.khorn.terraincontrol.generator.resourcegens.UnderWaterOreGen;
import com.khorn.terraincontrol.generator.resourcegens.UndergroundLakeGen;
import com.khorn.terraincontrol.generator.resourcegens.VeinGen;
import com.khorn.terraincontrol.generator.resourcegens.VinesGen;
import com.khorn.terraincontrol.generator.resourcegens.WellGen;

public class ConfigFunctionsManager
{
    private Map<String, Class<? extends ConfigFunction<?>>> configFunctions;

    public ConfigFunctionsManager()
    {
        // Also store in this class
        this.configFunctions = new HashMap<String, Class<? extends ConfigFunction<?>>>();

        // Add vanilla resources
        registerConfigFunction("AboveWaterRes", AboveWaterGen.class);
        registerConfigFunction("Cactus", CactusGen.class);
        registerConfigFunction("CustomObject", CustomObjectGen.class);
        registerConfigFunction("CustomStructure", CustomStructureGen.class);
        registerConfigFunction("Dungeon", DungeonGen.class);
        registerConfigFunction("Grass", GrassGen.class);
        registerConfigFunction("Liquid", LiquidGen.class);
        registerConfigFunction("Ore", OreGen.class);
        registerConfigFunction("Plant", PlantGen.class);
        registerConfigFunction("Reed", ReedGen.class);
        registerConfigFunction("Sapling", SaplingGen.class);
        registerConfigFunction("SmallLake", SmallLakeGen.class);
        registerConfigFunction("Tree", TreeGen.class);
        registerConfigFunction("UndergroundLake", UndergroundLakeGen.class);
        registerConfigFunction("UnderWaterOre", UnderWaterOreGen.class);
        registerConfigFunction("Vein", VeinGen.class);
        registerConfigFunction("Vines", VinesGen.class);
        registerConfigFunction("Well", WellGen.class);
    }

    public void registerConfigFunction(String name, Class<? extends ConfigFunction<?>> value)
    {
        configFunctions.put(name.toLowerCase(), value);
    }

    /**
     * Returns a config function with the given name.
     *
     * @param name               The name of the config function.
     * @param holder             The holder of the config function. WorldConfig or BO3.
     * @param locationOfResource The location of the config function, for example
     *                           TaigaBiomeConfig.ini.
     * @param args               The args of the function.
     * @return A config function with the given name, or null of it wasn't
     *         found.
     */
    @SuppressWarnings("unchecked")
    // It's checked with if (!clazz.isAssignableFrom(holder.getClass()))
    public <T> ConfigFunction<T> getConfigFunction(String name, T holder, String locationOfResource, List<String> args)
    {
        // Check if config function exists
        if (!configFunctions.containsKey(name.toLowerCase()))
        {
            TerrainControl.log("Invalid resource " + name + " in " + locationOfResource + ": resource type not found!");
            return null;
        }

        ConfigFunction<?> configFunction;
        Class<? extends ConfigFunction<?>> clazz = configFunctions.get(name.toLowerCase());

        // Get a config function
        try
        {
            configFunction = clazz.newInstance();
        } catch (InstantiationException e)
        {
            TerrainControl.log(Level.WARNING, "Reflection error (Instantiation) while loading the resources: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e)
        {
            TerrainControl.log(Level.WARNING, "Reflection error (IllegalAccess) while loading the resources: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        // Check if config function is of the right type
        boolean matchingTypes;
        try
        {
            matchingTypes = holder.getClass().isAssignableFrom((Class<?>) clazz.getMethod("getHolderType").invoke(configFunction));
        } catch (Exception e)
        {
            TerrainControl.log(Level.WARNING, "Reflection error (" + e.getClass().getSimpleName() + ") while loading the resources: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        if (!matchingTypes)
        {
            TerrainControl.log(Level.WARNING, "Invalid resource " + name + " in " + locationOfResource + ": cannot be placed in this config file!");
            return null;
        }

        // Set the holder
        configFunction.setHolder(holder);

        // Load it
        try
        {
            configFunction.read(name, args);
        } catch (InvalidConfigException e)
        {
            TerrainControl.log(Level.WARNING, "Invalid resource " + name + " in " + locationOfResource + ": " + e.getMessage());
        }

        // Return it
        return (ConfigFunction<T>) configFunction;
    }

}
