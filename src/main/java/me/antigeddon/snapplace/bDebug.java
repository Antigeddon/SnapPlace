package me.antigeddon.snapplace;

import org.bukkit.entity.Player;

public class bDebug {

    public enum DebugType {
        // --- Snow Layers ---
        SNOW_FIRST_EVENT_CANCELLED,
        SNOW_ACTION,
        SNOW_CLICKED_NULL,
        SNOW_CLICKED_TYPE,
        SNOW_NO_SNOW_IN_HAND,
        SNOW_CONFIG_REPLACE,
        SNOW_CONFIG_ENABLE,
        SNOW_NOT_SNEAKING,
        SNOW_PERM,
        SNOW_FACE,
        SNOW_MAX_HEIGHT,
        SNOW_HITBOX,
        SNOW_PLACE_CANCELLED,
        SNOW_SUCCESS,

        // --- Wall & Roof Placements (bPlace) ---
        WRPLACE_FIRST_EVENT_CANCELLED,
        WRPLACE_ACTION,
        WRPLACE_NO_ITEM,
        WRPLACE_NO_PERMISSION,
        WRPLACE_FEATURE_DISABLED,
        WRPLACE_NOT_SNEAKING,
        WRPLACE_UNSUPPORTED_ITEM,
        WRPLACE_CLICKED_NULL,
        WRPLACE_CLICKABLE_BLOCK,
        WRPLACE_BLOCK_UNDER_SOLID,
        WRPLACE_CLICKED_FLUID,
        WRPLACE_TARGET_NOT_FLUID,
        WRPLACE_ENTITY_BLOCKING,
        WRPLACE_DIRECTION_SET,
        WRPLACE_PLACE_CANCELLED,
        WRPLACE_SUCCESS,

        // --- Sign (cEditSign) ---
        SIGN_PLACE_CANCELLED,
        SIGN_BREAK_CANCELLED,
        SIGN_SIGN_EVENT_CANCELLED,
        SIGN_SUCCESS,

        // --- Rails ---
        RAIL_FIRST_EVENT_CANCELLED,
        RAIL_FEATURE_DISABLED,
        RAIL_NOT_RAIL,
        RAIL_NOT_SNEAKING,
        RAIL_NO_PERMISSION,
        RAIL_ORIENTED,
        RAIL_NO_ORIENTATION,

        // --- Interactable Placements (bPlaceOnInteractable) ---
        INTERACT_EVENT_CANCELLED,
        INTERACT_WRONG_ACTION,
        INTERACT_DISABLED,
        INTERACT_NO_BLOCK,
        INTERACT_NO_FACE,
        INTERACT_NO_PERMISSION,
        INTERACT_NO_ITEM,
        INTERACT_NO_SNEAK,
        INTERACT_NO_INTERACTIVE,
        INTERACT_INVALID_BLOCK,
        INTERACT_SIGN_CONVERT,
        INTERACT_ITEM_CONVERT,
        INTERACT_INCOMPATIBLE_DOOR_BED_BUT_SIGN,
        INTERACT_INCOMPATIBLE_DOOR_BED,
        INTERACT_ITEM_DATA,
        INTERACT_TARGET_NOT_FLUID,
        INTERACT_SAME_FLUID,
        INTERACT_SNOW_REPLACE,
        INTERACT_SAME_DATA,
        INTERACT_SLAB,
        INTERACT_FLOWER,
        INTERACT_SUGAR,
        INTERACT_SUGAR_WATER,
        INTERACT_CROPS,
        INTERACT_CHEST,
        INTERACT_CACTUS,
        INTERACT_CACTUS_GROUND,
        INTERACT_BUSH,
        INTERACT_MUSHROOM,
        INTERACT_SNOW_GROUND,
        INTERACT_CAKE,
        INTERACT_TORCH,
        INTERACT_PUMPKIN,
        INTERACT_FILL_BUCKET_CANCELLED,
        INTERACT_FILL_BUCKET_SUCCESS,
        INTERACT_FILL_BUCKET_INVALID_TARGET,
        INTERACT_UNFILL_CANCELLED,
        INTERACT_BED_TOP_FACE,
        INTERACT_BED_SECOND_BLOCK,
        INTERACT_BED_SECOND_UNDER_BLOCK,
        INTERACT_BED_PLACE_CANCELLED,
        INTERACT_BED_SUCCESS,
        INTERACT_DOOR_FACE,
        INTERACT_DOOR_SECOND_PART_NOT_FLUID,
        INTERACT_DOOR_PLACE_CANCELLED,
        INTERACT_DOOR_SUCCESS,
        INTERACT_PAINTING_FACE,
        INTERACT_PAINTING_SUCCESS,
        INTERACT_RAIL,
        INTERACT_ENTITY_BLOCKING,
        INTERACT_INVALID,
        INTERACT_PLACE_CANCELLED,
        INTERACT_SUCCESS,
        INTERACT_EMPTY_HAND_FATAL,
        INTERACT_EMPTY_BUCKET_HAND,
        INTERACT_FLINT_MINUS,
        INTERACT_FLINT_BREAK,
        INTERACT_HAND_ZERO,
        INTERACT_HAND_MINUS,

        // --- Slab Merging ---
        SLAB_MERGING_EVENT_CANCELLED,
        SLAB_MERGING_WRONG_ACTION,
        SLAB_MERGING_NO_ITEM,
        SLAB_MERGING_DISABLED,
        SLAB_MERGING_NO_PERMISSION,
        SLAB_MERGING_NOT_SLAB,
        SLAB_MERGING_CLICKED_NULL,
        SLAB_MERGING_CLICKED_INTERACTABLE_NO_SNEAK,
        SLAB_MERGING_NO_PERMISSION_INTERACT,
        SLAB_MERGING_CLICKED_SNOW,
        SLAB_MERGING_FACE_UP_FOR_BETWEEN,
        SLAB_MERGING_NO_SLAB_BELOW,
        SLAB_MERGING_DATA_MISMATCH,
        SLAB_MERGING_TARGET_BLOCKED,
        SLAB_MERGING_INVALID_GEOMETRY,
        SLAB_MERGING_ENTITY_BLOCKING,
        SLAB_MERGING_PLACE_CANCELLED,
        SLAB_MERGING_SUCCESS,
        SLAB_BREAK_MERGING_DISABLED,
        SLAB_BREAK_MERGING_NO_PERMISSION,
        SLAB_BREAK_MERGING_NO_SLAB_BELOW,
        SLAB_BREAK_MERGING_EVENT,
        SLAB_BREAK_MERGING_CANCELLED_RESTORE,

        // --- Slab Between Placement ---
        SLAB_BETWEEN_EVENT_CANCELLED,
        SLAB_BETWEEN_WRONG_ACTION,
        SLAB_BETWEEN_DISABLED,
        SLAB_BETWEEN_NO_PERMISSION,
        SLAB_BETWEEN_CLICKED_NULL,
        SLAB_BETWEEN_CLICKED_SNOW,
        SLAB_BETWEEN_NO_SNEAK,
        SLAB_BETWEEN_SLAB_NULL,
        SLAB_BETWEEN_CLICKED_INTERACTABLE_NO_SNEAK,
        SLAB_BETWEEN_NO_PERMISSION_INTERACT,
        SLAB_BETWEEN_NOT_HOLDING_SLAB,
        SLAB_BETWEEN_DATA_MISMATCH,
        SLAB_BETWEEN_ENTITY_BLOCKING,
        SLAB_BETWEEN_PLACE_CANCELLED,
        SLAB_BETWEEN_SUCCESS,

        // --- Slab Pillar Fix ---
        SLAB_PILLARFIX_SCAN_START,
        SLAB_PILLARFIX_SCAN_COMPLETE,
        SLAB_PILLARFIX_RESTORE1_NODATA,
        SLAB_PILLARFIX_RESTORE1_START,
        SLAB_PILLARFIX_RESTORE2_NODATA,
        SLAB_PILLARFIX_RESTORE2_START,
        SLAB_PILLARFIX_RESTORE2_SUCCESS
    }

    public static void debug(Player player, DebugType type, String info) {
        String message;
        switch (type) {
            // --- Snow Layers ---
            case SNOW_FIRST_EVENT_CANCELLED:
                message = "§c[SnowLayers] Event was cancelled before processing §f" + info;
                break;
            case SNOW_ACTION:
                message = "§c[SnowLayers] Wrong action type §f" + info;
                break;
            case SNOW_CLICKED_NULL:
                message = "§c[SnowLayers] Clicked block is null §f" + info;
                break;
            case SNOW_CLICKED_TYPE:
                message = "§c[SnowLayers] Player is not clicking a snowlayers §f" + info;
                break;
            case SNOW_NO_SNOW_IN_HAND:
                message = "§c[SnowLayers] Player is not holding snow §f" + info;
                break;
            case SNOW_CONFIG_REPLACE:
                message = "§c[SnowLayers] Replace snow-with-itself config active §f" + info;
                break;
            case SNOW_CONFIG_ENABLE:
                message = "§c[SnowLayers] SnowLayers feature disabled in config §f" + info;
                break;
            case SNOW_NOT_SNEAKING:
                message = "§c[SnowLayers] Sneak requirement not fulfilled §f" + info;
                break;
            case SNOW_PERM:
                message = "§c[SnowLayers] Player lacks permission §f" + info;
                break;
            case SNOW_FACE:
                message = "§c[SnowLayers] Invalid block face §f" + info;
                break;
            case SNOW_MAX_HEIGHT:
                message = "§c[SnowLayers] Snowlayers already at max height §f" + info;
                break;
            case SNOW_HITBOX:
                message = "§c[SnowLayers] Entity blocking block placement §f" + info;
                break;
            case SNOW_PLACE_CANCELLED:
                message = "§c[SnowLayers] BlockPlaceEvent was cancelled §f" + info;
                break;
            case SNOW_SUCCESS:
                message = "§a[SnowLayers] SnowLayer placed successfully §f" + info;
                break;

            // --- Wall & Roof Placement ---
            case WRPLACE_FIRST_EVENT_CANCELLED:
                message = "§c[WallRoof] Event was cancelled before processing §f" + info;
                break;
            case WRPLACE_ACTION:
                message = "§c[WallRoof] Wrong action type §f" + info;
                break;
            case WRPLACE_NO_ITEM:
                message = "§c[WallRoof] Player not holding any item §f" + info;
                break;
            case WRPLACE_NO_PERMISSION:
                message = "§c[WallRoof] Player lacks permission §f" + info;
                break;
            case WRPLACE_FEATURE_DISABLED:
                message = "§c[WallRoof] Feature disabled in config §f" + info;
                break;
            case WRPLACE_NOT_SNEAKING:
                message = "§c[WallRoof] Sneaking required but not sneaking §f" + info;
                break;
            case WRPLACE_UNSUPPORTED_ITEM:
                message = "§c[WallRoof] Unsupported block type for wall/roof placement §f" + info;
                break;
            case WRPLACE_CLICKED_NULL:
                message = "§c[WallRoof] Clicked block is null §f" + info;
                break;
            case WRPLACE_CLICKABLE_BLOCK:
                message = "§c[WallRoof] Clicked a clickable/interactable block §f" + info;
                break;
            case WRPLACE_BLOCK_UNDER_SOLID:
                message = "§c[WallRoof] Block under target is solid §f" + info;
                break;
            case WRPLACE_CLICKED_FLUID:
                message = "§c[WallRoof] Clicked block is a fluid §f" + info;
                break;
            case WRPLACE_TARGET_NOT_FLUID:
                message = "§c[WallRoof] Target block is not fluid (cannot place) §f" + info;
                break;
            case WRPLACE_ENTITY_BLOCKING:
                message = "§c[WallRoof] Entity blocking target location §f" + info;
                break;
            case WRPLACE_DIRECTION_SET:
                message = "§a[WallRoof] Pumpkin direction set successfully §f" + info;
                break;
            case WRPLACE_PLACE_CANCELLED:
                message = "§c[WallRoof] BlockPlaceEvent was cancelled §f" + info;
                break;
            case WRPLACE_SUCCESS:
                message = "§a[WallRoof] Block placed successfully §f" + info;
                break;

            // --- Rails ---
            case RAIL_FIRST_EVENT_CANCELLED:
                message = "§c[Rails] Event was cancelled before processing §f" + info;
                break;
            case RAIL_FEATURE_DISABLED:
                message = "§c[Rails] Feature disabled in config §f" + info;
                break;
            case RAIL_NOT_RAIL:
                message = "§c[Rails] Block placed is not a rail §f" + info;
                break;
            case RAIL_NOT_SNEAKING:
                message = "§c[Rails] Sneaking required but not sneaking §f" + info;
                break;
            case RAIL_NO_PERMISSION:
                message = "§c[Rails] Player lacks permission §f" + info;
                break;
            case RAIL_ORIENTED:
                message = "§a[Rails] Rail orientation set §f" + info;
                break;
            case RAIL_NO_ORIENTATION:
                message = "§c[Rails] No valid adjacent rail found §f" + info;
                break;

            // --- Interactable Placements ---
            case INTERACT_EVENT_CANCELLED:
                message = "§c[Interact] Event was cancelled before processing §f" + info;
                break;
            case INTERACT_WRONG_ACTION:
                message = "§c[Interact] Wrong action type §f" + info;
                break;
            case INTERACT_DISABLED:
                message = "§c[Interact] Feature disabled in config §f" + info;
                break;
            case INTERACT_NO_BLOCK:
                message = "§c[Interact] Clicked block is null §f" + info;
                break;
            case INTERACT_NO_FACE:
                message = "§c[Interact] Clicked block face is null §f" + info;
                break;
            case INTERACT_NO_PERMISSION:
                message = "§c[Interact] Player lacks permission §f" + info;
                break;
            case INTERACT_NO_ITEM:
                message = "§c[Interact] Player is not holding an item §f" + info;
                break;
            case INTERACT_NO_SNEAK:
                message = "§c[Interact] Sneak requirement not fulfilled §f" + info;
                break;
            case INTERACT_NO_INTERACTIVE:
                message = "§c[Interact] Clicked block is not interactable §f" + info;
                break;
            case INTERACT_INVALID_BLOCK:
                message = "§c[Interact] Item in hand is not a placeable block §f" + info;
                break;
            case INTERACT_SIGN_CONVERT:
                message = "§a[Interact] Sign item converted to placeable sign block §f" + info;
                break;
            case INTERACT_ITEM_CONVERT:
                message = "§a[Interact] Item converted to placeable block §f" + info;
                break;
            case INTERACT_INCOMPATIBLE_DOOR_BED_BUT_SIGN:
                message = "§c[Interact] Incompatible item for this fragile block (door/bed) §f" + info;
                break;
            case INTERACT_INCOMPATIBLE_DOOR_BED:
                message = "§c[Interact] Incompatible item for this very fragile block §f" + info;
                break;
            case INTERACT_ITEM_DATA:
                message = "§a[Interact] Calculated placement data for item §f" + info;
                break;
            case INTERACT_TARGET_NOT_FLUID:
                message = "§c[Interact] Target block is not a fluid §f" + info;
                break;
            case INTERACT_SAME_FLUID:
                message = "§c[Interact] Cannot place same fluid in itself §f" + info;
                break;
            case INTERACT_SNOW_REPLACE:
                message = "§c[Interact] SnowLayers is disabled or not configured for replacement §f" + info;
                break;
            case INTERACT_SAME_DATA:
                message = "§c[Interact] Block with same data already at target §f" + info;
                break;
            case INTERACT_SLAB:
                message = "§c[Interact] Cannot place slab on identical slab §f" + info;
                break;
            case INTERACT_FLOWER:
                message = "§c[Interact] Invalid ground for flower placement §f" + info;
                break;
            case INTERACT_SUGAR:
                message = "§c[Interact] Invalid ground for sugar cane placement §f" + info;
                break;
            case INTERACT_SUGAR_WATER:
                message = "§c[Interact] Sugar cane requires adjacent water §f" + info;
                break;
            case INTERACT_CROPS:
                message = "§c[Interact] Crops require soil underneath §f" + info;
                break;
            case INTERACT_CHEST:
                message = "§c[Interact] Cannot create an illegal double chest §f" + info;
                break;
            case INTERACT_CACTUS:
                message = "§c[Interact] Cactus is blocked by adjacent block §f" + info;
                break;
            case INTERACT_CACTUS_GROUND:
                message = "§c[Interact] Invalid ground for cactus placement §f" + info;
                break;
            case INTERACT_BUSH:
                message = "§c[Interact] Dead bush requires sand underneath §f" + info;
                break;
            case INTERACT_MUSHROOM:
                message = "§c[Interact] Invalid ground for mushroom placement §f" + info;
                break;
            case INTERACT_SNOW_GROUND:
                message = "§c[Interact] Invalid ground for snowlayers placement §f" + info;
                break;
            case INTERACT_CAKE:
                message = "§c[Interact] Block below cannot support a cake §f" + info;
                break;
            case INTERACT_TORCH:
                message = "§c[Interact] Invalid support for torch placement §f" + info;
                break;
            case INTERACT_PUMPKIN:
                message = "§c[Interact] Block needs solid ground and wall/roof placement is disabled §f" + info;
                break;
            case INTERACT_FILL_BUCKET_CANCELLED:
                message = "§c[Interact] Bucket fill event was cancelled §f" + info;
                break;
            case INTERACT_FILL_BUCKET_SUCCESS:
                message = "§a[Interact] Bucket filled successfully §f" + info;
                break;
            case INTERACT_FILL_BUCKET_INVALID_TARGET:
                message = "§c[Interact] Invalid target to fill bucket from §f" + info;
                break;
            case INTERACT_UNFILL_CANCELLED:
                message = "§c[Interact] Bucket empty event was cancelled §f" + info;
                break;
            case INTERACT_BED_TOP_FACE:
                message = "§c[Interact] Bed must be placed on top face of a block §f" + info;
                break;
            case INTERACT_BED_SECOND_BLOCK:
                message = "§c[Interact] Second part of the bed is blocked §f" + info;
                break;
            case INTERACT_BED_SECOND_UNDER_BLOCK:
                message = "§c[Interact] Block under second part of bed is not solid §f" + info;
                break;
            case INTERACT_BED_PLACE_CANCELLED:
                message = "§c[Interact] Bed placement event was cancelled §f" + info;
                break;
            case INTERACT_BED_SUCCESS:
                message = "§a[Interact] Bed placed successfully §f" + info;
                break;
            case INTERACT_DOOR_FACE:
                message = "§c[Interact] Door must be placed on top face of a block §f" + info;
                break;
            case INTERACT_DOOR_SECOND_PART_NOT_FLUID:
                message = "§c[Interact] Top part of the door is blocked §f" + info;
                break;
            case INTERACT_DOOR_PLACE_CANCELLED:
                message = "§c[Interact] Door placement event was cancelled §f" + info;
                break;
            case INTERACT_DOOR_SUCCESS:
                message = "§a[Interact] Door placed successfully §f" + info;
                break;
            case INTERACT_PAINTING_FACE:
                message = "§c[Interact] Painting must be placed on a side face §f" + info;
                break;
            case INTERACT_PAINTING_SUCCESS:
                message = "§a[Interact] Painting spawned successfully §f" + info;
                break;
            case INTERACT_RAIL:
                message = "§c[Interact] Ignoring orientable rails (function disabled) §f" + info;
                break;
            case INTERACT_ENTITY_BLOCKING:
                message = "§c[Interact] Entity is blocking placement §f" + info;
                break;
            case INTERACT_INVALID:
                message = "§c[Interact] Invalid placement data, action cancelled §f" + info;
                break;
            case INTERACT_PLACE_CANCELLED:
                message = "§c[Interact] BlockPlaceEvent was cancelled §f" + info;
                break;
            case INTERACT_SUCCESS:
                message = "§a[Interact] Block placed successfully §f" + info;
                break;
            case INTERACT_EMPTY_HAND_FATAL:
                message = "§4[ItemRemover] Hand is empty, cannot remove item §f" + info;
                break;
            case INTERACT_EMPTY_BUCKET_HAND:
                message = "§a[Interact] Replaced water/lava bucket with empty bucket §f" + info;
                break;
            case INTERACT_FLINT_MINUS:
                message = "§a[Interact] Reduced flint and steel durability §f" + info;
                break;
            case INTERACT_FLINT_BREAK:
                message = "§a[Interact] Flint and steel broke §f" + info;
                break;
            case INTERACT_HAND_ZERO:
                message = "§a[ItemRemover] Last item in hand used, hand is now empty §f" + info;
                break;
            case INTERACT_HAND_MINUS:
                message = "§a[ItemRemover] Removed one item from hand §f" + info;
                break;

            // --- Signs Editions ---
            case SIGN_PLACE_CANCELLED:
                message = "§c[Sign] BlockPlaceEvent was cancelled §f" + info;
                break;
            case SIGN_BREAK_CANCELLED:
                message = "§c[Sign] BlockBreakEvent was cancelled §f" + info;
                break;
            case SIGN_SIGN_EVENT_CANCELLED:
                message = "§c[Sign] SignChangeEvent was cancelled §f" + info;
                break;
            case SIGN_SUCCESS:
                message = "§a[Sign] Sign edited successfully §f" + info;
                break;

            // --- Slab Merging ---
            case SLAB_MERGING_EVENT_CANCELLED:
                message = "§c[SlabMerging] Event was cancelled before processing §f" + info;
                break;
            case SLAB_MERGING_WRONG_ACTION:
                message = "§c[SlabMerging] Wrong action type §f" + info;
                break;
            case SLAB_MERGING_NO_ITEM:
                message = "§c[SlabMerging] Player has no item in hand §f" + info;
                break;
            case SLAB_MERGING_DISABLED:
                message = "§c[SlabMerging] Feature disabled in config §f" + info;
                break;
            case SLAB_MERGING_NO_PERMISSION:
                message = "§c[SlabMerging] Player lacks permission (SnapPlace.betterslabs) §f" + info;
                break;
            case SLAB_MERGING_NOT_SLAB:
                message = "§c[SlabMerging] Item in hand is not a slab §f" + info;
                break;
            case SLAB_MERGING_CLICKED_NULL:
                message = "§c[SlabMerging] Clicked block is null §f" + info;
                break;
            case SLAB_MERGING_CLICKED_INTERACTABLE_NO_SNEAK:
                message = "§c[SlabMerging] Clicked interactable without sneaking or feature disabled §f" + info;
                break;
            case SLAB_MERGING_NO_PERMISSION_INTERACT:
                message = "§c[SlabMerging] Player lacks interact permission §f" + info;
                break;
            case SLAB_MERGING_CLICKED_SNOW:
                message = "§c[SlabMerging] Clicked on snow, aborting §f" + info;
                break;
            case SLAB_MERGING_FACE_UP_FOR_BETWEEN:
                message = "§c[SlabMerging] Clicked top face, allowing default placement §f" + info;
                break;
            case SLAB_MERGING_NO_SLAB_BELOW:
                message = "§c[SlabMerging] No slab found below target location §f" + info;
                break;
            case SLAB_MERGING_DATA_MISMATCH:
                message = "§c[SlabMerging] Slabs have different data types §f" + info;
                break;
            case SLAB_MERGING_TARGET_BLOCKED:
                message = "§c[SlabMerging] Target location is blocked §f" + info;
                break;
            case SLAB_MERGING_INVALID_GEOMETRY:
                message = "§c[SlabMerging] Invalid placement geometry §f" + info;
                break;
            case SLAB_MERGING_ENTITY_BLOCKING:
                message = "§c[SlabMerging] An entity is blocking the placement §f" + info;
                break;
            case SLAB_MERGING_PLACE_CANCELLED:
                message = "§c[SlabMerging] BlockPlaceEvent was cancelled §f" + info;
                break;
            case SLAB_MERGING_SUCCESS:
                message = "§a[SlabMerging] Slab anti-merge successful §f" + info;
                break;
            case SLAB_BREAK_MERGING_DISABLED:
                message = "§c[SlabBreakMerging] Feature disabled, pillar restoration logic skipped §f" + info;
                break;
            case SLAB_BREAK_MERGING_NO_PERMISSION:
                message = "§c[SlabBreakMerging] Player lacks permission, pillar restoration logic skipped §f" + info;
                break;
            case SLAB_BREAK_MERGING_NO_SLAB_BELOW:
                message = "§c[SlabBreakMerging] Not a pillar (no slab below), restoration logic skipped §f" + info;
                break;
            case SLAB_BREAK_MERGING_EVENT:
                message = "§a[SlabBreakMerging] Pillar break detected, checking for cancellation §f" + info;
                break;
            case SLAB_BREAK_MERGING_CANCELLED_RESTORE:
                message = "§a[SlabBreakMerging] Break event was cancelled. Restoring pillar state §f" + info;
                break;

            // --- Slab Between Placement ---
            case SLAB_BETWEEN_EVENT_CANCELLED:
                message = "§c[SlabBetween] Event was cancelled before processing §f" + info;
                break;
            case SLAB_BETWEEN_WRONG_ACTION:
                message = "§c[SlabBetween] Wrong action type for this feature §f" + info;
                break;
            case SLAB_BETWEEN_DISABLED:
                message = "§c[SlabBetween] Feature is disabled in config §f" + info;
                break;
            case SLAB_BETWEEN_NO_PERMISSION:
                message = "§c[SlabBetween] Player lacks permission (SnapPlace.betterslabs) §f" + info;
                break;
            case SLAB_BETWEEN_CLICKED_NULL:
                message = "§c[SlabBetween] Clicked block is null §f" + info;
                break;
            case SLAB_BETWEEN_CLICKED_SNOW:
                message = "§c[SlabBetween] Clicked on snow, aborting §f" + info;
                break;
            case SLAB_BETWEEN_NO_SNEAK:
                message = "§c[SlabBetween] Sneak requirement not fulfilled §f" + info;
                break;
            case SLAB_BETWEEN_SLAB_NULL:
                message = "§c[SlabBetween] No valid target slab was found to form a double slab §f" + info;
                break;
            case SLAB_BETWEEN_CLICKED_INTERACTABLE_NO_SNEAK:
                message = "§c[SlabBetween] Clicked interactable without sneaking or feature disabled §f" + info;
                break;
            case SLAB_BETWEEN_NO_PERMISSION_INTERACT:
                message = "§c[SlabBetween] Player lacks interact permission §f" + info;
                break;
            case SLAB_BETWEEN_NOT_HOLDING_SLAB:
                message = "§c[SlabBetween] Player is not holding a slab §f" + info;
                break;
            case SLAB_BETWEEN_DATA_MISMATCH:
                message = "§c[SlabBetween] Slab in hand and target slab have different data types §f" + info;
                break;
            case SLAB_BETWEEN_ENTITY_BLOCKING:
                message = "§c[SlabBetween] An entity is blocking the placement §f" + info;
                break;
            case SLAB_BETWEEN_PLACE_CANCELLED:
                message = "§c[SlabBetween] BlockPlaceEvent was cancelled by another plugin §f" + info;
                break;
            case SLAB_BETWEEN_SUCCESS:
                message = "§a[SlabBetween] Slab successfully placed to form a double slab §f" + info;
                break;

            // --- Slab Pillar Fix ---
            case SLAB_PILLARFIX_SCAN_START:
                message = "§a[SlabPillarFix] Starting pillar scan §f" + info;
                break;
            case SLAB_PILLARFIX_SCAN_COMPLETE:
                message = "§a[SlabPillarFix] Pillar scan complete §f" + info;
                break;
            case SLAB_PILLARFIX_RESTORE1_NODATA:
                message = "§c[SlabPillarFix] Restore Phase 1 aborted: No data found §f" + info;
                break;
            case SLAB_PILLARFIX_RESTORE1_START:
                message = "§a[SlabPillarFix] Starting Restore Phase 1 §f" + info;
                break;
            case SLAB_PILLARFIX_RESTORE2_NODATA:
                message = "§c[SlabPillarFix] Restore Phase 2 aborted: No data found §f" + info;
                break;
            case SLAB_PILLARFIX_RESTORE2_START:
                message = "§a[SlabPillarFix] Starting Restore Phase 2 §f" + info;
                break;
            case SLAB_PILLARFIX_RESTORE2_SUCCESS:
                message = "§a[SlabPillarFix] Pillar restoration finished successfully §f" + info;
                break;

            default:
                message = "§5[Debug] Unknown debug type §f" + info;
                break;
        }

        debug(player, message);
    }

    public static void debug(Player player, String message) {

        boolean debugEnabled = bMain.getPluginConfig().getBoolean("debug.enable", false);

        if (!debugEnabled)
            return;

        boolean logToChat = bMain.getPluginConfig().getBoolean("debug.output.chat", true);
        boolean logToConsole = bMain.getPluginConfig().getBoolean("debug.output.console", true);

        if (logToChat && player != null)
            player.sendMessage(bMain.getInstance().pName() + "§c[Debug]§f " + message);

        if (logToConsole) {
            String consoleMessage = message.replaceAll("§[0-9a-fk-or]", "");
            System.out.println("[SnapPlace] [Debug] " + consoleMessage);
        }
    }
}