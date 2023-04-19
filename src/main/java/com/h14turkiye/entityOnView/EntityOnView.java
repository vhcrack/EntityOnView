package com.h14turkiye.entityOnView;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.h14turkiye.entityOnView.library.YamlWrapper;
import com.h14turkiye.entityOnView.listeners.CreatureSpawnListener;
import com.h14turkiye.entityOnView.listeners.PreCreatureSpawnListener;

public class EntityOnView extends JavaPlugin{
	private FileConfiguration config;

	@Override
	public void onEnable() {
		YamlWrapper yamlWrapper = new YamlWrapper(this, getDataFolder(), "config", true, true);    
		config = yamlWrapper.getConfig();
		
		if(config.getBoolean("usePaperPreCreatureSpawnEvent"))
			getServer().getPluginManager().registerEvents(new PreCreatureSpawnListener(this), this);
		else
			getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
}
