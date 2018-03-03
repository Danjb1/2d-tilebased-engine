package game.physics;

import game.Level;
import game.Logic;
import game.TileLayer;
import game.tiles.ForegroundTile;
import game.tiles.Slope;
import game.tiles.Tile;

/**
 * Class containing static methods used in Slope handling.
 *
 * @author Dan Bryce
 */
public class SlopeUtils {

    /**
     * Adjusts the given CollisionResult to account for any Slopes.
     *
     * <p>Slope tiles are marked as non-solid, but Hitboxes that enter the solid
     * part of a slope are adjusted by this step so that they sit atop the
     * slope.
     *
     * @param result
     * @param logic
     */
    public static void doSlopePostProcessing(CollisionResult result,
            Logic logic) {

        // Look for slopes at both of the Hitbox's "slope nodes" (horizontal
        // centres of the top and bottom edges).
        float slopeNodeX = result.getCentreX();

        boolean onSlope = handleFloorSlopeCollisions(
                result, logic, slopeNodeX, result.getBottom());

        if (!onSlope){
            // No need to check the top node if we already have a result from
            // the bottom node.
            handleCeilingSlopeCollisions(
                    result, logic, slopeNodeX, result.getTop());
        }

        // Need to resolve collisions again as they may be affected by slope
        // processing.
        result.resolveCollisions_Y();
    }

    /**
     * Checks for and handles Slope collision at the bottom slope node.
     *
     * @param result CollisionResult to update.
     * @param logic
     * @param nodeX
     * @param nodeY
     * @return True if slope node should be snapped onto a slope.
     */
    private static boolean handleFloorSlopeCollisions(CollisionResult result,
            Logic logic, float nodeX, float nodeY) {

        TileLayer foreground = logic.getLevel().getForeground();

        int tileX = (int) (nodeX / Tile.WIDTH);
        int tileY = (int) (nodeY / Tile.HEIGHT);
        int tileId = foreground.getTile(tileX, tileY);
        ForegroundTile tile = (ForegroundTile) logic.getTile(tileId);

        if (tile instanceof Slope){
            // Slope node is inside a Slope tile
            Slope slope = (Slope) tile;
            float distIntoTileX = nodeX - Tile.getLeft(nodeX);
            float distIntoTileY = nodeY - (tileY * Tile.HEIGHT);
            // Only snap if the Hitbox is colliding with the solid part of the
            // slope, or is supposed to be "stuck" to it.
            if (slope.isFloorSlope() &&
                    (slope.isPointInSlope(distIntoTileX, distIntoTileY) ||
                            result.shouldHitboxStickToSlope())){
                snapToFloorSlope(result, nodeX, tileY, slope);
                return true;
            }

        } else if (isTileBelowFloorSlope(result, tileX, tileY, logic)){
            // Slope node is inside the Tile *under* a slope.
            // We need to get this slope tile, and snap to it.
            tileY -= 1;
            tileId = foreground.getTile(tileX, tileY);
            Slope slope = (Slope) logic.getTile(tileId);
            snapToFloorSlope(result, nodeX, tileY, slope);
            return true;

        } else if (isTileAboveFloorSlope(result, tileX, tileY, logic) &&
                result.shouldHitboxStickToSlope()){
            // Hitbox is supposed to be "stuck" to the slope
            tileY += 1;
            tileId = foreground.getTile(tileX, tileY);
            Slope slope = (Slope) logic.getTile(tileId);
            snapToFloorSlope(result, nodeX, tileY, slope);
            return true;
        }

        return false;
    }

    /**
     * Checks for and handles Slope collision at the top slope node.
     *
     * @param result CollisionResult to update.
     * @param logic
     * @param nodeX
     * @param nodeY
     */
    private static void handleCeilingSlopeCollisions(CollisionResult result,
            Logic logic, float nodeX, float nodeY) {

        TileLayer foreground = logic.getLevel().getForeground();

        int tileX = (int) (nodeX / Tile.WIDTH);
        int tileY = (int) (nodeY / Tile.HEIGHT);
        int tileId = foreground.getTile(tileX, tileY);
        ForegroundTile tile = (ForegroundTile) logic.getTile(tileId);

        if (tile instanceof Slope){
            // Slope node is intersecting a Slope tile
            Slope slope = (Slope) tile;
            float distIntoTileX = nodeX - Tile.getLeft(nodeX);
            float distIntoTileY = nodeY - (tileY * Tile.HEIGHT);
            // Only snap if Hitbox is colliding with the solid part of the slope
            if (slope.isCeilingSlope() &&
                    slope.isPointInSlope(distIntoTileX, distIntoTileY)){
                /*
                 * Note that unlike floor slopes where we don't snap to the
                 * slope if the Hitbox is jumping, we SHOULD snap to ceiling
                 * slopes even when falling; otherwise, players can glitch INTO
                 * a ceiling slope if they press into it with enough momentum
                 * while falling.
                 */
                snapToCeilingSlope(result, nodeX, tileY, slope);
            }

        } else if (isTileAboveCeilingSlope(tileX, tileY, logic)){
            // Slope node is in the Tile *above* a slope.
            // We need to get this slope tile, and "snap" to it.
            tileY += 1;
            tileId = foreground.getTile(tileX, tileY);
            Slope slope = (Slope) logic.getTile(tileId);
            snapToCeilingSlope(result, nodeX, tileY, slope);
        }
    }

    /**
     * Updates the CollisionResult after a collision with the given floor slope.
     *
     * @param result
     * @param nodeX X-position of the "slope node".
     * @param tileY Tile co-ordinate of the Slope.
     * @param slope Slope tile with which the collision occurred.
     */
    private static void snapToFloorSlope(CollisionResult result, float nodeX,
            int tileY, Slope slope){
        float distIntoTileX = nodeX - Tile.getLeft(nodeX);
        float yOnSlope = slope.getSlopeY_At_X(distIntoTileX);
        float collisionY = (tileY * Tile.HEIGHT) + Tile.HEIGHT - yOnSlope;
        result.setCollision_Y(new Collision(
                result.getHitbox().getBottom(), collisionY, slope));
    }

    /**
     * Updates the CollisionResult after a collision with the given ceiling
     * slope.
     *
     * @param result
     * @param nodeX X-position of the "slope node".
     * @param tileY Tile co-ordinate of the Slope.
     * @param slope Slope tile with which the collision occurred.
     */
    private static void snapToCeilingSlope(CollisionResult result, float nodeX,
            int tileY, Slope slope){
        float distIntoTileX = nodeX - Tile.getLeft(nodeX);
        float yOnSlope = slope.getSlopeY_At_X(distIntoTileX);
        float collisionY = (tileY * Tile.HEIGHT) + yOnSlope;
        result.setCollision_Y(new Collision(
                result.getHitbox().getTop(), collisionY, slope));
    }

    /**
     * Determines if the given Tile is "behind" a slope.
     *
     * <p>A Tile is considered to be "behind" a slope if it is:
     * <br>1) A tile immediately to the left of a left slope.
     * <br>2) A tile immediately to the right of right slope.
     *
     * <p>We check for (1) or (2) depending on if the given CollisionResult was
     * an attempt to move left or right, respectively.
     *
     * @param result
     * @param tileX
     * @param tileY
     * @param logic
     * @return
     */
    public static boolean isTileBehindSlope(CollisionResult result,
            int tileX, int tileY, Logic logic) {

        Level level = logic.getLevel();
        TileLayer foreground = level.getForeground();

        int possibleSlopeTileX = result.getAttemptedDx() > 0 ?
                tileX - 1 : tileX + 1;
        if (!level.doesTileExist_X(possibleSlopeTileX)){
            // Don't try to check outside the Level bounds
            return false;
        }

        int tileId = foreground.getTile(possibleSlopeTileX, tileY);
        Tile possibleSlopeTile = logic.getTile(tileId);
        return possibleSlopeTile instanceof Slope;
    }

    /**
     * Determines if the given Tile is immediately below a floor slope.
     *
     * @param result
     * @param tileX
     * @param tileY
     * @param logic
     * @return
     */
    public static boolean isTileBelowFloorSlope(CollisionResult result,
            int tileX, int tileY, Logic logic) {

        Level level = logic.getLevel();
        TileLayer foreground = level.getForeground();

        int possibleSlopeTileY = tileY - 1;
        if (!level.doesTileExist_Y(possibleSlopeTileY)){
            // Don't try to check outside the Level bounds
            return false;
        }

        int tileId = foreground.getTile(tileX, possibleSlopeTileY);
        Tile possibleSlopeTile = logic.getTile(tileId);
        if (possibleSlopeTile instanceof Slope){
            Slope slope = (Slope) possibleSlopeTile;
            return slope.isFloorSlope();
        }

        return false;
    }

    /**
     * Determines if the given Tile is immediately above a floor slope.
     *
     * @param result
     * @param tileX
     * @param tileY
     * @param logic
     * @return
     */
    public static boolean isTileAboveFloorSlope(CollisionResult result,
            int tileX, int tileY, Logic logic) {

        Level level = logic.getLevel();
        TileLayer foreground = level.getForeground();

        int possibleSlopeTileY = tileY + 1;
        if (!level.doesTileExist_Y(possibleSlopeTileY)){
            // Don't try to check outside the Level bounds
            return false;
        }

        int tileId = foreground.getTile(tileX, possibleSlopeTileY);
        Tile possibleSlopeTile = logic.getTile(tileId);
        if (possibleSlopeTile instanceof Slope){
            Slope slope = (Slope) possibleSlopeTile;
            return slope.isFloorSlope();
        }

        return false;
    }

    /**
     * Determines if the given Tile is immediately above a ceiling slope.
     *
     * @param tileX
     * @param tileY
     * @param logic
     * @return
     */
    private static boolean isTileAboveCeilingSlope(int tileX, int tileY,
            Logic logic) {

        Level level = logic.getLevel();
        TileLayer foreground = level.getForeground();

        int possibleSlopeTileY = tileY + 1;
        if (!level.doesTileExist_Y(possibleSlopeTileY)){
            // Don't try to check outside the Level bounds
            return false;
        }

        int tileId = foreground.getTile(tileX, possibleSlopeTileY);
        Tile possibleSlopeTile = logic.getTile(tileId);
        if (possibleSlopeTile instanceof Slope){
            Slope slope = (Slope) possibleSlopeTile;
            return slope.isCeilingSlope();
        }

        return false;
    }

    /**
     * Determines if the given Tile is "at the bottom of" a floor slope.
     *
     * <p>This is true for tiles that are diagonally underneath a floor slope.
     *
     * @param result
     * @param tileX
     * @param tileY
     * @param logic
     * @return
     */
    public static boolean isTileAtBottomOfFloorSlope(CollisionResult result,
            int tileX, int tileY, Logic logic) {

        Level level = logic.getLevel();
        TileLayer foreground = level.getForeground();

        int possibleSlopeTileX = result.getAttemptedDx() > 0 ?
                tileX - 1 : tileX + 1;
        if (!level.doesTileExist_X(possibleSlopeTileX) ||
                !level.doesTileExist_Y(tileY - 1)){
            // Don't try to check outside the Level bounds
            return false;
        }

        int tileId = foreground.getTile(possibleSlopeTileX, tileY - 1);
        Tile possibleSlopeTile = logic.getTile(tileId);
        if (possibleSlopeTile instanceof Slope){
            Slope slope = (Slope) possibleSlopeTile;
            return slope.isFloorSlope();
        }

        return false;
    }

    /**
     * Determines if the given Tile is "at the top of" a ceiling slope.
     *
     * <p>This is true for tiles that are diagonally above a ceiling slope.
     *
     * @param result
     * @param tileX
     * @param tileY
     * @param logic
     * @return
     */
    public static boolean isTileAtTopOfCeilingSlope(CollisionResult result,
            int tileX, int tileY, Logic logic) {

        Level level = logic.getLevel();
        TileLayer foreground = level.getForeground();

        int possibleSlopeTileX = result.getAttemptedDx() > 0 ?
                tileX - 1 : tileX + 1;
        if (!level.doesTileExist_X(possibleSlopeTileX)||
                !level.doesTileExist_Y(tileY - 1)){
            // Don't try to check outside the Level bounds
            return false;
        }

        int tileId = foreground.getTile(possibleSlopeTileX, tileY + 1);
        Tile possibleSlopeTile = logic.getTile(tileId);
        if (possibleSlopeTile instanceof Slope){
            Slope slope = (Slope) possibleSlopeTile;
            return slope.isCeilingSlope();
        }

        return false;
    }

}
