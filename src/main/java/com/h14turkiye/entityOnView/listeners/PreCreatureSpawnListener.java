package com.h14turkiye.entityOnView.listeners;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.h14turkiye.entityOnView.EntityOnView;
import com.h14turkiye.entityOnView.ListenerUtilities;

public class PreCreatureSpawnListener implements Listener{

	private Set<EntityType> cancelSpawn;
	private Set<EntityType> abortSpawn;

	public PreCreatureSpawnListener(Set<EntityType> cancelSpawn, Set<EntityType> abortSpawn) {
		this.cancelSpawn = cancelSpawn;
		this.abortSpawn = abortSpawn;
	}
	
	

	@EventHandler
	public void preSpawnEventNatural(PreCreatureSpawnEvent event) {
		if(EntityOnView.debug || event.getReason().equals(SpawnReason.NATURAL)) {
			Location location = event.getSpawnLocation();
			Player nearestQualifiedPlayer = ListenerUtilities.getNearestQualifiedPlayer(location);

			if(nearestQualifiedPlayer == null) {
				if(cancelSpawn.contains(event.getType())) {
					event.setCancelled(true);
					if(EntityOnView.debug)
					Bukkit.broadcastMessage(location.toString()+"-cancelled");
				}
				if(abortSpawn.contains(event.getType())) {
					event.setShouldAbortSpawn(true);
					if(EntityOnView.debug)
					Bukkit.broadcastMessage(location.toString()+"-aborted");
				}
			}
			else {
				if(EntityOnView.debug)
				Bukkit.broadcastMessage(location.toString()+"-non");
			}
		}
	}

	
}
