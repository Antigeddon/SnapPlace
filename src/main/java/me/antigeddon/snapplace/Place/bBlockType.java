package me.antigeddon.snapplace.Place;

import net.minecraft.server.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.*;

public class bBlockType {

    public static boolean isFluid(Material material) {
        switch (material) {
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case SNOW:
            case FIRE:
            case AIR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRail(Material material) {
        switch (material) {
            case RAILS:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isGroundPlant(Material material) {
        switch (material) {
            case GRASS:
            case DIRT:
            case SOIL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isInteractableBlackList(Material material) {
        switch (material) {
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case BED_BLOCK:
            case LEVER:
            case STONE_BUTTON:
            case CAKE_BLOCK:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case TRAP_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFragileInteractableButSigns(Material material) {
        switch (material) {
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case BED_BLOCK:
            case CAKE_BLOCK:
            case TRAP_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isVeryFragileInteractable(Material material) {
        switch (material) {
            case LEVER:
            case STONE_BUTTON:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
                return true;
            default:
                return false;
        }
    }

    public static boolean isBlockHaveNoHitBox(Material material) {
        if (isRail(material)) {
            return true;
        }

        switch (material) {
            case SAPLING:
            case STATIONARY_WATER:
            case WATER:
            case STATIONARY_LAVA:
            case LAVA:
            case WEB:
            case LONG_GRASS:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TORCH:
            case FIRE:
            case REDSTONE_WIRE:
            case CROPS:
            case SIGN_POST:
            case WALL_SIGN:
            case LEVER:
            case STONE_PLATE:
            case WOOD_PLATE:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case STONE_BUTTON:
            case SUGAR_CANE_BLOCK:
            case PORTAL:
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNotDoorAndBedCompatible(Material material) {
        switch (material) {
            case LEVER:
            case STONE_BUTTON:
            case TRAP_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean canSupportCake(Material material) {
        switch (material) {
            case STONE:
            case GRASS:
            case DIRT:
            case COBBLESTONE:
            case WOOD:
            case BEDROCK:
            case SAND:
            case GRAVEL:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LOG:
            case LEAVES:
            case SPONGE:
            case GLASS:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case DISPENSER:
            case SANDSTONE:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case PISTON_STICKY_BASE:
            case WEB:
            case PISTON_BASE:
            case PISTON_EXTENSION:
            case WOOL:
            case PISTON_MOVING_PIECE:
            case GOLD_BLOCK:
            case IRON_BLOCK:
            case DOUBLE_STEP:
            case STEP:
            case BRICK:
            case TNT:
            case BOOKSHELF:
            case MOSSY_COBBLESTONE:
            case OBSIDIAN:
            case MOB_SPAWNER:
            case WOOD_STAIRS:
            case CHEST:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case WORKBENCH:
            case SOIL:
            case FURNACE:
            case BURNING_FURNACE:
            case SIGN_POST:
            case WOODEN_DOOR:
            case COBBLESTONE_STAIRS:
            case WALL_SIGN:
            case STONE_PLATE:
            case IRON_DOOR_BLOCK:
            case WOOD_PLATE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case ICE:
            case SNOW_BLOCK:
            case CACTUS:
            case CLAY:
            case JUKEBOX:
            case FENCE:
            case PUMPKIN:
            case NETHERRACK:
            case SOUL_SAND:
            case GLOWSTONE:
            case JACK_O_LANTERN:
            case CAKE_BLOCK:
            case LOCKED_CHEST:
            case TRAP_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isClickable(Material material) {
        switch (material) {
            case DISPENSER:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case CHEST:
            case WORKBENCH:
            case FURNACE:
            case BURNING_FURNACE:
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case LEVER:
            case STONE_BUTTON:
            case CAKE_BLOCK:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case TRAP_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isGroundMushroomAndSnow(Material material) {
        switch (material) {
            case STONE:
            case GRASS:
            case DIRT:
            case COBBLESTONE:
            case WOOD:
            case BEDROCK:
            case SAND:
            case GRAVEL:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LOG:
            case LEAVES:
            case SPONGE:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case DISPENSER:
            case SANDSTONE:
            case NOTE_BLOCK:
            case WOOL:
            case GOLD_BLOCK:
            case IRON_BLOCK:
            case BRICK:
            case TNT:
            case BOOKSHELF:
            case MOSSY_COBBLESTONE:
            case OBSIDIAN:
            case CHEST:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case WORKBENCH:
            case FURNACE:
            case BURNING_FURNACE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case SNOW_BLOCK:
            case CLAY:
            case JUKEBOX:
            case PUMPKIN:
            case NETHERRACK:
            case SOUL_SAND:
            case GLOWSTONE:
            case JACK_O_LANTERN:
            case LOCKED_CHEST:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFragileNeedGroundWhenWallPlaced(Material material, byte data) {
        switch (material) {
            case TORCH:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
                return data == 0 || data >= 5;

            case LEVER:
                return data == 0 || data == 5 || data == 6 || data == 13 || data == 14;

            case REDSTONE_WIRE:
            case STONE_PLATE:
            case WOOD_PLATE:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case LONG_GRASS:
            case DEAD_BUSH:
            case SAPLING:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case FIRE:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case FENCE:
            case RAILS:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNotSolid(Material material) {
        switch (material) {
            case SAPLING:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case LEAVES:
            case GLASS:
            case BED_BLOCK:
            case RAILS:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case PISTON_STICKY_BASE:
            case PISTON_BASE:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case WEB:
            case LONG_GRASS:
            case DEAD_BUSH:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case STEP:
            case TNT:
            case TORCH:
            case FIRE:
            case COBBLESTONE_STAIRS:
            case WOOD_STAIRS:
            case REDSTONE_WIRE:
            case CROPS:
            case SOIL:
            case SIGN_POST:
            case WALL_SIGN:
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case LADDER:
            case LEVER:
            case STONE_PLATE:
            case WOOD_PLATE:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
            case STONE_BUTTON:
            case SNOW:
            case ICE:
            case CACTUS:
            case SUGAR_CANE_BLOCK:
            case FENCE:
            case PORTAL:
            case CAKE_BLOCK:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case TRAP_DOOR:
            case AIR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isBuildableNotCactusFriendly(Material material) {
        if (material == null) return false;

        switch (material) {
            case STONE:
            case GRASS:
            case DIRT:
            case COBBLESTONE:
            case WOOD:
            case BEDROCK:
            case SAND:
            case GRAVEL:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LOG:
            case LEAVES:
            case SPONGE:
            case GLASS:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case DISPENSER:
            case SANDSTONE:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case PISTON_STICKY_BASE:
            case WEB:
            case PISTON_BASE:
            case PISTON_EXTENSION:
            case WOOL:
            case PISTON_MOVING_PIECE:
            case GOLD_BLOCK:
            case IRON_BLOCK:
            case DOUBLE_STEP:
            case STEP:
            case BRICK:
            case TNT:
            case BOOKSHELF:
            case MOSSY_COBBLESTONE:
            case OBSIDIAN:
            case MOB_SPAWNER:
            case WOOD_STAIRS:
            case CHEST:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case WORKBENCH:
            case SOIL:
            case FURNACE:
            case BURNING_FURNACE:
            case SIGN_POST:
            case WOODEN_DOOR:
            case COBBLESTONE_STAIRS:
            case WALL_SIGN:
            case STONE_PLATE:
            case IRON_DOOR_BLOCK:
            case WOOD_PLATE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case ICE:
            case SNOW_BLOCK:
            case CACTUS:
            case CLAY:
            case JUKEBOX:
            case FENCE:
            case PUMPKIN:
            case NETHERRACK:
            case SOUL_SAND:
            case GLOWSTONE:
            case JACK_O_LANTERN:
            case CAKE_BLOCK:
            case LOCKED_CHEST:
            case TRAP_DOOR:
                return true;

            default:
                return false;
        }
    }

    public static boolean isEntityBlockingBlock(Location location, Player player, Material blockType, byte data) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        if (bBlockType.isBlockHaveNoHitBox(blockType)) {
            return false;
        }

        double minX = x;
        double minZ = z;
        double maxX = x + 1;
        double maxY = y + 1;
        double maxZ = z + 1;

        switch (blockType) {
            case CAKE_BLOCK:
                minX = x + 0.0625;
                minZ = z + 0.0625;
                maxX = x + 0.9375;
                maxZ = z + 0.9375;
                maxY = y + 0.4375;
                break;

            case LADDER:
                double thickness = 0.125;
                switch (data) {
                    case 2:
                        minZ = z + 1 - thickness;
                        maxZ = z + 1;
                        break;
                    case 3:
                        minZ = z;
                        maxZ = z + thickness;
                        break;
                    case 4:
                        minX = x + 1 - thickness;
                        maxX = x + 1;
                        break;
                    case 5:
                        minX = x;
                        maxX = x + thickness;
                        break;
                    default:
                        break;
                }
                break;

            case TRAP_DOOR:
                maxY = y + 0.1875;
                break;

            case SOIL:
                maxY = y + 0.9375;
                break;

            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
                maxY = y + 0.125;
                break;

            case STEP:
                maxY = y + 0.5;
                break;

            case BED_BLOCK:
                maxY = y + 0.5625;
                break;

            case CACTUS:
                minX = x + 0.0625;
                minZ = z + 0.0625;
                maxX = x + 0.9375;
                maxZ = z + 0.9375;
                break;

            case SNOW:
                if ((data >= 2 && data <= 8) || (data >= 10 && data <= 15)) {
                    maxY = y + 0.5;

                } else {
                    return false;
                }
                break;

            default:
                break;
        }

        AxisAlignedBB blockBB = AxisAlignedBB.a(minX, y, minZ, maxX, maxY, maxZ);

        for (org.bukkit.entity.Entity bukkitEntity : player.getNearbyEntities(6, 6, 6)) {
            if (bukkitEntity instanceof Item
                    || bukkitEntity instanceof Arrow
                    || bukkitEntity instanceof Painting
                    || bukkitEntity instanceof Snowball
                    || bukkitEntity instanceof Fireball
                    || bukkitEntity instanceof Fish
                    || bukkitEntity instanceof Egg) {
                continue;
            }

            net.minecraft.server.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
            if (nmsEntity.boundingBox.a(blockBB)) {
                return true;
            }
        }

        net.minecraft.server.Entity nmsPlayer = ((CraftEntity) player).getHandle();
        return nmsPlayer.boundingBox.a(blockBB);
    }
}