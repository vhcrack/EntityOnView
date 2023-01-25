package com.h14turkiye.entityOnView;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.h14turkiye.entityOnView.library.YamlWrapper;
import com.h14turkiye.entityOnView.listeners.CreatureSpawnListener;

public class EntityOnView extends JavaPlugin{
	private FileConfiguration config;

	protected static Set<Material> transparentBlocks = new HashSet<>();
	protected static Boolean realistic;

	@Override
	public void onEnable() {
		YamlWrapper yamlWrapper = new YamlWrapper(this, getDataFolder(), "config", true, true);    
		config = yamlWrapper.getConfig();
		
		config.getStringList("transparent-blocks").forEach(string -> transparentBlocks.add(Material.valueOf(string)));
		realistic = config.getBoolean("realistic");
		
		getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
}
