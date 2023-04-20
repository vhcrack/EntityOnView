package com.h14turkiye.entityOnView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class ListenerUtilities {
	
	private ListenerUtilities() {
	    throw new IllegalStateException("Utility class");
	  }
		
	/**
	 * Returns the closest qualified {@link Player} to a specific {@link Location}
	 * @param loc The {@link Location} representing the origin to search from
	 * @return The closest qualified {@link Player}, or {@code null}
	 */
	public static Player getNearestQualifiedPlayer(Location loc, int maxDistanceSquared) {
		return getNearestQualifiedPlayer(loc, maxDistanceSquared, null);
	}

	/**
	 * Returns the closest qualified {@link Player} to a specific {@link Location}
	 * @param loc The {@link Location} representing the origin to search from
	 * @return The closest qualified {@link Player}, or {@code null}
	 */
	public static Player getNearestQualifiedPlayer(Location loc, int maxDistanceSquared, Set<Material> transparentBlocks) {
		Stream<Player> sortedByNearestPlayers = loc.getWorld().getPlayers().stream().filter(p -> p.getLocation().distanceSquared(loc) < maxDistanceSquared).sorted((o1, o2) -> Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc)))
		.filter(p ->  ListenerUtilities.isLookingTowards(p.getEyeLocation(), loc, 150, 110))
		.filter(p -> ListenerUtilities.getLineOfSight(transparentBlocks, p.getEyeLocation(), loc).isEmpty());
		
		return sortedByNearestPlayers.findAny().orElse(null);
	}
	
	/**
	 * @see CraftLivingEntity.java
	 * 
	 * @param transparent
	 * @param origin The {@link Location} representing the origin
	 * @param target The {@link Location} representing the target
	 * @return Blocks between locations.
	 */
	public static List<Block> getLineOfSight(Set<Material> transparent, Location origin, Location target) {
		ArrayList<Block> blocks = new ArrayList<>();
		if (transparent == null) {
			return blocks;
		}
		Iterator<Block> itr = new BlockIterator(target.getWorld(), origin.clone().add(0, -2, 0).toVector(), target.clone().toVector().subtract(origin.clone().add(0, -2, 0).toVector()).normalize(), 2, 120);
		while (itr.hasNext()) {
			Block block = itr.next();
            Material material = block.getType();
            if (transparent.contains(material)) {
                continue;
            }
            blocks.add(block);
			break;
		}
		return blocks;
	}
	
	public static boolean isLookingTowards(Location origin, Location target, float yawLimit, float pitchLimit) {
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
	public static float getPitch(Vector vector) {
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
	public static float getYaw(Vector vector) {
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
