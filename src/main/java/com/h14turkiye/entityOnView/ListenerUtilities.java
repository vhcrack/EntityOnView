package com.h14turkiye.entityOnView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

public class ListenerUtilities {
	
	private ListenerUtilities() {
	    throw new IllegalStateException("Utility class");
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
	public static List<Block> getLineOfSight(Set<Material> transparent, int maxDistance, Location origin, Location target) {
		if (transparent == null) {
			transparent = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
		}
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<>();
		Iterator<Block> itr = new BlockIterator(target.getWorld(), origin.clone().add(0, -2, 0).toVector(), target.clone().toVector().subtract(origin.clone().add(0, -2, 0).toVector()).normalize(), 2, maxDistance);
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

	/**
	 * Returns whether the origin can see the target location.
	 */
	public static boolean canSee(Location origin, Location target) {
		return getLineOfSight(EntityOnView.transparentBlocks, 128, origin, target).isEmpty();
	}

	/**
	 * Returns the closest qualified {@link Player} to a specific {@link Location}
	 * @param loc The {@link Location} representing the origin to search from
	 * @return The closest qualified {@link Player}, or {@code null}
	 */
	
	public static Player getNearestQualifiedPlayer(Location loc) {
		return loc.getWorld().getPlayers().stream().sorted((o1, o2) ->
				Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc))
		).filter( p -> {
			if(p.getLocation().distanceSquared(loc)< 128*128) {
				return !(shouldCancel1(p.getEyeLocation(), loc));
			}
			return false;
		}).filter(p -> !(shouldCancel2(p.getEyeLocation(), loc))).findAny().orElse(null);
	}

	public static Boolean shouldCancel1(Location origin, Location target) {
		if(!EntityOnView.realistic)
			return false;
		if(isLookingTowards(origin, target, 150, 110))
			return false;
		return true;
	}
	
	public static Boolean shouldCancel2(Location origin, Location target) {
		if (!(canSee(origin, target))) {
			return true;
		}
		return false;
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
