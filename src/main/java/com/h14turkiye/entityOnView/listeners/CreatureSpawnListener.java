package com.h14turkiye.entityOnView.listeners;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.h14turkiye.entityOnView.ListenerUtilities;

public class CreatureSpawnListener implements Listener{

	private Set<EntityType> cancelSpawn;

	public CreatureSpawnListener(Set<EntityType> cancelSpawn) {
		this.cancelSpawn = cancelSpawn;
	}
	
	@EventHandler
	public void spawnEventNatural(CreatureSpawnEvent event) {
		if(event.getSpawnReason().equals(SpawnReason.NATURAL)) {
			Location location = event.getLocation();
			Player nearestQualifiedPlayer = ListenerUtilities.getNearestQualifiedPlayer(location);

			if(nearestQualifiedPlayer == null && cancelSpawn.contains(event.getEntityType())) {
				event.setCancelled(true);
			}
		}
	}

	
}
