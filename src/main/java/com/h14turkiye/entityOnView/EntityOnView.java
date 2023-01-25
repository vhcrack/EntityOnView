package com.h14turkiye.entityOnView;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.h14turkiye.entityOnView.library.YamlWrapper;
import com.h14turkiye.entityOnView.listeners.CreatureSpawnListener;

public class EntityOnView extends JavaPlugin{
	private FileConfiguration config;

	@Override
	public void onEnable() {
		YamlWrapper yamlWrapper = new YamlWrapper(this, getDataFolder(), "config", true, true);    
		config = yamlWrapper.getConfig();
		
		getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
}
