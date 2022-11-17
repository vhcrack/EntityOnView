package com.h14turkiye.entityOnView;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.h14turkiye.entityOnView.library.YamlWrapper;

public class Main extends JavaPlugin{
	private FileConfiguration config;

	@Override
	public void onEnable() {
		YamlWrapper YAMLWRAPPER = new YamlWrapper(this, getDataFolder(), "config", true, true);    
		config = YAMLWRAPPER.getConfig();
		YAMLWRAPPER.reload();
		
		getServer().getPluginManager().registerEvents(new PreCreatureSpawnListener(this), this);
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
}
