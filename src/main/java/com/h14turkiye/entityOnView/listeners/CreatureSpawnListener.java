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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.h14turkiye.entityOnView.EntityOnView;
import com.h14turkiye.entityOnView.ListenerUtilities;

public class CreatureSpawnListener implements Listener{

	private FileConfiguration config;
	
	private Boolean debug = false;
	
	private Set<EntityType> cancelSpawn = new HashSet<>();
	private Set<Material> transparentBlocks = new HashSet<>();
	private int maxDistance;
	

	public CreatureSpawnListener(EntityOnView plugin) {
		config = plugin.getConfig();
		debug = config.getBoolean("debug");
		maxDistance = config.getInt("maxDistance");
		 
		config.getStringList("cancel-spawn").forEach(string -> cancelSpawn.add(EntityType.valueOf(string)));
		config.getStringList("transparent-blocks").forEach(string -> transparentBlocks.add(Material.valueOf(string)));
	}
	
	@EventHandler
	public void spawnEventNatural(CreatureSpawnEvent event) {
		if(event.getSpawnReason().equals(SpawnReason.NATURAL) && cancelSpawn.contains(event.getEntityType())) {
			Location location = event.getLocation();
			Player nearestQualifiedPlayer = getNearestQualifiedPlayer(location);

			if(nearestQualifiedPlayer == null || nearestQualifiedPlayer.isGliding()) {
				event.setCancelled(true);
				if(debug)
					Bukkit.broadcast(location.toString()+"-cancelled", "op");
			}
			
		}
		
	}
	
	/**
	 * Returns whether the origin can see the target location.
	 */
	private boolean canSee(Location origin, Location target) {
		return ListenerUtilities.getLineOfSight(transparentBlocks, maxDistance, origin, target).isEmpty();
	}
	
	/**
	 * Returns the closest qualified {@link Player} to a specific {@link Location}
	 * @param loc The {@link Location} representing the origin to search from
	 * @return The closest qualified {@link Player}, or {@code null}
	 */
	private Player getNearestQualifiedPlayer(Location loc) {
		return loc.getWorld().getPlayers().stream().sorted((o1, o2) ->
				Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc))
		).filter( p -> {
			if(p.getLocation().distanceSquared(loc)< 128*128) {
				return ListenerUtilities.isLookingTowards(p.getEyeLocation(), loc, 150, 110);
			}
			return false;
		}).filter(p -> canSee(p.getEyeLocation(), loc)).findAny().orElse(null);
	}
}
