package com.h14turkiye.entityOnView.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.h14turkiye.entityOnView.EntityOnView;
import com.h14turkiye.entityOnView.ListenerUtilities;

public class CreatureSpawnListener implements Listener{

	private EntityOnView plugin;
	private FileConfiguration config;
	
	private Boolean debug = false;
	
	private Set<EntityType> cancelSpawn = new HashSet<>();
	

	public CreatureSpawnListener(EntityOnView plugin) {
		this.plugin = plugin;
		config = plugin.getConfig();
		debug = config.getBoolean("debug");
		 
		config.getStringList("cancel-spawn").forEach(string -> cancelSpawn.add(EntityType.valueOf(string)));
	}

	@EventHandler
	public void spawnEventNatural(CreatureSpawnEvent event) {
		if(event.getSpawnReason().equals(SpawnReason.NATURAL) && cancelSpawn.contains(event.getEntityType())) {
			Location location = event.getLocation();
			Player nearestQualifiedPlayer = ListenerUtilities.getNearestQualifiedPlayer(location);

			if(nearestQualifiedPlayer == null || nearestQualifiedPlayer.isGliding()) {
				event.setCancelled(true);
				if(debug)
					Bukkit.broadcast(location.toString()+"-cancelled", "op");
			}
			
		}
		
	}
}
