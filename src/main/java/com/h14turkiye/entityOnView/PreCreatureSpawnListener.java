package com.h14turkiye.entityOnView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.google.common.collect.Sets;

public class PreCreatureSpawnListener implements Listener{

	private FileConfiguration config;

	private Set<Material> transparentBlocks;
	private Set<EntityType> cancelSpawn;
	private Set<EntityType> abortSpawn;

	private int maximumDistance = 128;

	public PreCreatureSpawnListener(Main plugin) {
		config = plugin.getConfig();
		config.getStringList("transparent-blocks").forEach(string -> transparentBlocks.add(Material.valueOf(string)));
		config.getStringList("cancel-spawn").forEach(string -> cancelSpawn.add(EntityType.valueOf(string)));
		config.getStringList("abort-spawn").forEach(string -> abortSpawn.add(EntityType.valueOf(string)));
	}
	
	/**
	 * @see CraftLivingEntity.java
	 * 
	 * @param transparent
	 * @param maxDistance
	 * @param origin The {@link Location} representing the origin
	 * @param target The {@link Location} representing the target
	 * @return Blocks between locations.
	 */
	public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance, Location origin, Location target) {
		if (transparent == null) {
			transparent = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
		}
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<>();
		Iterator<Block> itr = new BlockIterator(target.getWorld(), origin.clone().toVector(), target.clone().toVector().subtract(origin.toVector()).normalize(), 0, maxDistance);
		while (itr.hasNext()) {
			Block block = itr.next();
			blocks.add(block);
			Material material = block.getType();
			if (!transparent.contains(material)) {
				break;
			}
		}
		return blocks;
	}

	/**
	 * Returns whether the origin can see the target location.
	 */
	public boolean canSee(Location origin, Location target) {
		return getLineOfSight(transparentBlocks, maximumDistance, origin, target).isEmpty();
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
			if(p.getLocation().distanceSquared(loc)< maximumDistance) {
				return !(shouldCancel(p.getEyeLocation(), loc));
			}
			return false;
		}).findAny().orElse(null);
	}

	public Boolean shouldCancel(Location origin, Location target) {
		if(config.getBoolean("realistic") && isLookingTowards(origin, target, 90, 110))
		{
			return false;
		}

		return !(canSee(origin, target));
	}

	@EventHandler
	public void preSpawnEventNatural(PreCreatureSpawnEvent event) {
		if(event.getReason().equals(SpawnReason.NATURAL)) {
			Location location = event.getSpawnLocation();
			Player nearestQualifiedPlayer = getNearestQualifiedPlayer(location);

			if(nearestQualifiedPlayer == null) {
				if(cancelSpawn.contains(event.getType())) {
					event.setCancelled(true);
				}
				if(abortSpawn.contains(event.getType())) {
					event.setShouldAbortSpawn(true);
				}
			}
		}
	}

	public boolean isLookingTowards(Location origin, Location target, float yawLimit, float pitchLimit) {
		Vector rel = target.toVector().subtract(origin.toVector()).normalize();
		float yaw = normalizeYaw(origin.getYaw());
		float yawHelp = getYaw(rel);
		if (!(Math.abs(yawHelp - yaw) < yawLimit ||
				Math.abs(yawHelp + 360 - yaw) < yawLimit ||
				Math.abs(yaw + 360 - yawHelp) < yawLimit)) {
			return false;
		}
		float pitch = origin.getPitch();
		float pitchHelp = getPitch(rel);
		if (!(Math.abs(pitchHelp - pitch) < pitchLimit)) {
			return false;
		}
		return true;
	}

	public static float normalizeYaw(float yaw) {
		yaw = yaw % 360;
		if (yaw < 0) {
			yaw += 360.0;
		}
		return yaw;
	}

	/**
	 * Gets the pitch angle value (in degrees) for a normalized vector.
	 */
	public float getPitch(Vector vector) {
		double dx = vector.getX();
		double dy = vector.getY();
		double dz = vector.getZ();
		double forward = Math.sqrt((dx * dx) + (dz * dz));
		double pitch = Math.atan2(dy, forward) * (180.0 / Math.PI);
		return (float) pitch;
	}

	/**
	 * Gets the yaw angle value (in degrees) for a normalized vector.
	 */
	public float getYaw(Vector vector) {
		double dx = vector.getX();
		double dz = vector.getZ();
		double yaw = 0;
		// Set yaw
		if (dx != 0) {
			// Set yaw start value based on dx
			if (dx < 0) {
				yaw = 1.5 * Math.PI;
			}
			else {
				yaw = 0.5 * Math.PI;
			}
			yaw -= Math.atan(dz / dx); // or atan2?
		}
		else if (dz < 0) {
			yaw = Math.PI;
		}
		return (float) (-yaw * (180.0 / Math.PI));
	}
}
