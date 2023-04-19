package com.h14turkiye.entityOnView.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.h14turkiye.entityOnView.EntityOnView;
import com.h14turkiye.entityOnView.ListenerUtilities;

public class PreCreatureSpawnListener implements Listener{

	private FileConfiguration config;
	
	private Boolean debug = false;
	private Boolean useTransparencyCheck = false;
	
	private Set<EntityType> cancelSpawn = new HashSet<>();
	private Set<Material> transparentBlocks = new HashSet<>();
	
	private int maxDistance;
	private int maxDistanceSquared;

	public PreCreatureSpawnListener(EntityOnView plugin) {
		config = plugin.getConfig();
		debug = config.getBoolean("debug");
		useTransparencyCheck = config.getBoolean("useTransparencyCheck");
		maxDistance = config.getInt("maxDistance");
		maxDistanceSquared = maxDistance*maxDistance;
		 
		config.getStringList("cancel-spawn").forEach(string -> cancelSpawn.add(EntityType.valueOf(string)));
		config.getStringList("transparent-blocks").forEach(string -> transparentBlocks.add(Material.valueOf(string)));
	}
	
	@EventHandler
	public void spawnEventNatural(PreCreatureSpawnEvent event) {
		if(event.getReason().equals(SpawnReason.NATURAL) && cancelSpawn.contains(event.getType())) {
			Location location = event.getSpawnLocation();
			Player nearestQualifiedPlayer;
			
			if(useTransparencyCheck)
				nearestQualifiedPlayer = ListenerUtilities.getNearestQualifiedPlayer(location, maxDistanceSquared, transparentBlocks);
			else
				nearestQualifiedPlayer = ListenerUtilities.getNearestQualifiedPlayer(location, maxDistanceSquared);

			if(nearestQualifiedPlayer == null) {
				event.setCancelled(true);
				if(debug)
					Bukkit.broadcast(location.toString()+"-cancelled", "op");
			}
			
		}
		
	}
	
}
