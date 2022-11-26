package com.h14turkiye.entityOnView;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.h14turkiye.entityOnView.library.YamlWrapper;
import com.h14turkiye.entityOnView.listeners.CreatureSpawnListener;
import com.h14turkiye.entityOnView.listeners.PreCreatureSpawnListener;

public class EntityOnView extends JavaPlugin{
	private static FileConfiguration config;

	protected static Set<Material> transparentBlocks = new HashSet<>();
	protected static Boolean realistic;
	
	private Set<EntityType> cancelSpawn = new HashSet<>();
	private Set<EntityType> abortSpawn = new HashSet<>();

	@Override
	public void onEnable() {
		YamlWrapper yamlWrapper = new YamlWrapper(this, getDataFolder(), "config", true, true);    
		config = yamlWrapper.getConfig();
		
		config.getStringList("transparent-blocks").forEach(string -> transparentBlocks.add(Material.valueOf(string)));
		realistic = config.getBoolean("realistic");
		
		config.getStringList("cancel-spawn").forEach(string -> cancelSpawn.add(EntityType.valueOf(string)));
		config.getStringList("abort-spawn").forEach(string -> abortSpawn.add(EntityType.valueOf(string)));
		
		if(getServer().getName().equalsIgnoreCase("Spigot") || getServer().getName().equalsIgnoreCase("CraftBukkit")) {
			getServer().getPluginManager().registerEvents(new CreatureSpawnListener(cancelSpawn), this);
		}
		else {
			getServer().getPluginManager().registerEvents(new PreCreatureSpawnListener(cancelSpawn, abortSpawn), this);
		}
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
}
