/*
 * Copyright (c) justin97530 2015. This program is only for personal use and may not be distributed without prior permission.
 */

package ga.justin97530.Translink;

/**
 * Created by justin on 03/10/15.
 */

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.Directional;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class Translink extends JavaPlugin implements Listener {
    double highSpeedMultiplier = 0;
    double mediumSpeedMultiplier = 0;
    double lowSpeedMultiplier = 0;
    double haltMultiplier = 0;
    String highSpeedColor = "";
    String mediumSpeedColor = "";
    String lowSpeedColor = "";
    String haltColor = "";
    String ejectColor = "";
    String northColor = "";
    String southColor = "";
    String eastColor = "";
    String westColor = "";
    Material[] rails = {Material.RAILS, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL};

    private BlockFace isSideBlock(Material mat, Block block) {
        if(block.getRelative(BlockFace.UP).getType().equals(mat)) return BlockFace.UP;
        else if(block.getRelative(BlockFace.DOWN).getType().equals(mat)) return BlockFace.DOWN;
        else if(block.getRelative(BlockFace.EAST).getType().equals(mat)) return BlockFace.EAST;
        else if(block.getRelative(BlockFace.NORTH).getType().equals(mat)) return BlockFace.NORTH;
        else if(block.getRelative(BlockFace.WEST).getType().equals(mat)) return BlockFace.WEST;
        else if(block.getRelative(BlockFace.SOUTH).getType().equals(mat)) return BlockFace.SOUTH;
        else return BlockFace.SELF;
    }

    private boolean isRail(Material mat) {
        if(mat.equals(Material.RAILS) || mat.equals(Material.ACTIVATOR_RAIL) || mat.equals(Material.DETECTOR_RAIL) || mat.equals(Material.POWERED_RAIL)) return true;
        return false;
    }

    private String getDirection(Location to, Location from) {
        Wool
        String direction = "TURN";
        if(to.getX() < from.getX() && to.getZ() == from.getZ()) {
            direction = "WEST";
        } else if(to.getX() > from.getX() && to.getZ() == from.getZ()) {
            direction = "EAST";
        } else if(to.getX() == from.getX() && to.getZ() > from.getZ()) {
            direction = "SOUTH";
        } else if(to.getX() == from.getX() && to.getZ() < from.getZ()) {
            direction = "NORTH";
        } else if(to.getX() > from.getX() && to.getZ() > from.getZ()) {
            direction = "SOUTHEAST";
        } else if(to.getX() > from.getX() && to.getZ() < from.getZ()) {
            direction = "NORTHEAST";
        } else if(to.getX() < from.getX() && to.getZ() < from.getZ()) {
            direction = "NORTHWEST";
        } else if(to.getX() < from.getX() && to.getZ() > from.getZ()) {
            direction = "SOUTHWEST";
        }
        return direction;
    }

    private Vector newVelocity(Vector velocity, String direction, double multiplier) {
        if(direction.equals("EAST") || direction.equals("WEST")) {
            velocity.setX(velocity.getX() * multiplier);
            velocity.setY(velocity.getY() * multiplier);
        } else if(direction.equals("NORTH") || direction.equals("SOUTH")) {
            velocity.setZ(velocity.getZ() * multiplier);
            velocity.setY(velocity.getY() * multiplier);
        } else {
            velocity.multiply(multiplier * 5);
        }
        return velocity;
     }

    private Vector setDirection(Vector velocity, String direction, String newDirection) {
        if(!direction.equals(newDirection)) {
            if((newDirection.equals("NORTH") || newDirection.equals("SOUTH")) && (direction.equals("NORTH") || direction.equals("SOUTH"))) {
                velocity.setZ(velocity.getZ() * -1);
            } else if((newDirection.equals("EAST") || newDirection.equals("WEST")) && (direction.equals("EAST") || direction.equals("WEST"))) {
                velocity.setX(velocity.getX() * -1);
            }
        }
        return velocity;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        highSpeedMultiplier = this.getConfig().getDouble("highSpeedMultiplier");
        mediumSpeedMultiplier = this.getConfig().getDouble("mediumSpeedMultiplier");
        lowSpeedMultiplier = this.getConfig().getDouble("lowSpeedMultiplier");
        haltMultiplier = this.getConfig().getDouble("haltMultiplier");
        highSpeedColor = this.getConfig().getString("highSpeedColor");
        mediumSpeedColor = this.getConfig().getString("mediumSpeedColor");
        lowSpeedColor = this.getConfig().getString("lowSpeedColor");
        haltColor = this.getConfig().getString("haltColor");
        ejectColor = this.getConfig().getString("ejectColor");
        northColor = this.getConfig().getString("northColor");
        southColor = this.getConfig().getString("southColor");
        eastColor = this.getConfig().getString("eastColor");
        westColor = this.getConfig().getString("westColor");
    }

    @Override
    public void onDisable() {

    }

    Location pastTo = null;

    @EventHandler
    public void onMove(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        if(vehicle instanceof Minecart && (pastTo == null || pastTo.distance(e.getTo()) >= 1 || pastTo.distance(e.getTo()) <= -1)) {
            Location to = e.getTo();
            pastTo = e.getTo();

            Location from = e.getFrom();
            String direction = getDirection(to, from);

            Location newLoc = to.clone();
            newLoc.setY(newLoc.getY() - 1);
            Vector velocity = vehicle.getVelocity();

            Block block = newLoc.getBlock();
            if(block.getType().equals(Material.WOOL)) {
                String color = DyeColor.getByData(block.getData()).toString();
                if(color.equalsIgnoreCase(highSpeedColor)) {
                    velocity = newVelocity(velocity, direction, highSpeedMultiplier);
                } else if(color.equalsIgnoreCase(mediumSpeedColor)) {
                    velocity = newVelocity(velocity, direction, mediumSpeedMultiplier);
                } else if(color.equalsIgnoreCase(lowSpeedColor)) {
                    velocity = newVelocity(velocity, direction, lowSpeedMultiplier);
                } else if(color.equalsIgnoreCase(haltColor)) {
                    velocity = newVelocity(velocity, direction, haltMultiplier);
                } else if(color.equalsIgnoreCase(ejectColor)) {
                    vehicle.eject();
                } else if(color.equalsIgnoreCase(northColor)) {
                    velocity = setDirection(velocity, direction, "NORTH");
                } else if(color.equalsIgnoreCase(southColor)) {
                    velocity = setDirection(velocity, direction, "SOUTH");
                } else if(color.equalsIgnoreCase(eastColor)) {
                    velocity = setDirection(velocity, direction, "EAST");
                } else if(color.equalsIgnoreCase(westColor)) {
                    velocity = setDirection(velocity, direction, "WEST");
                }
            } else if(block.getType().equals(Material.IRON_BLOCK) && block.getBlockPower() > 0) {
                vehicle.remove();
            }

            vehicle.setVelocity(velocity);

            if(vehicle.isEmpty() || !(vehicle.getPassenger() instanceof Player)) return;
            Location signLoc = to.clone();
            signLoc.setY(signLoc.getY() - 2);
            if(!(signLoc.getBlock().getType().equals(Material.SIGN_POST) || signLoc.getBlock().getType().equals(Material.WALL_SIGN))) return;
            Sign sign = (Sign) signLoc.getBlock().getState();
            StringBuilder text = new StringBuilder();
            for (String s : sign.getLines()) {
                text.append(s);
            }
            vehicle.getPassenger().sendMessage(ChatColor.GOLD + ChatColor.translateAlternateColorCodes('&', text.toString()));
        }
    }

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent e) {
        Block block = e.getBlock();
        Material blockType = block.getType();
        BlockFace found = isSideBlock(Material.GOLD_BLOCK, block);
        if(e.getNewCurrent() > 0 && e.getOldCurrent() == 0 && !found.equals(BlockFace.SELF)) {
            Block upBlock = block.getRelative(found).getRelative(BlockFace.UP);
            if(isRail(upBlock.getType())) {
                Location upBlockLoc = upBlock.getLocation().add(0.5, 0.5, 0.5);
                upBlockLoc.getWorld().spawnEntity(upBlockLoc, EntityType.MINECART);
            }
        }
    }
}
