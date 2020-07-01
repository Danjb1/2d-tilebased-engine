package engine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import engine.game.GameUtils;

/**
 * Tests of the static methods in the GameUtils class.
 *
 * @author Dan Bryce
 */
public class GameUtilsTest {

    @Test
    public void testGetAngleFromSpeed() {

        // Edge cases (only one axis)
        assertEquals(0, GameUtils.getAngle(0, -1), 0);
        assertEquals(90, GameUtils.getAngle(1, 0), 0);
        assertEquals(180, GameUtils.getAngle(0, 1), 0);
        assertEquals(270, GameUtils.getAngle(-1, 0), 0);

        // Regular cases
        assertEquals(45, GameUtils.getAngle(0.5f, -0.5f), 0.01f);
        assertEquals(135, GameUtils.getAngle(0.5f, 0.5f), 0.01f);
        assertEquals(225, GameUtils.getAngle(-0.5f, 0.5f), 0.01f);
        assertEquals(315, GameUtils.getAngle(-0.5f, -0.5f), 0.01f);
    }

    @Test
    public void testNormaliseAngle() {

        // Angle is already normalised
        assertEquals(0, GameUtils.normaliseAngle(0), 0);
        assertEquals(180, GameUtils.normaliseAngle(180), 0);

        // Angle >= 360
        assertEquals(0, GameUtils.normaliseAngle(360), 0);
        assertEquals(180, GameUtils.normaliseAngle(540), 0);

        // Angle < 0
        assertEquals(359, GameUtils.normaliseAngle(-1), 0);
        assertEquals(180, GameUtils.normaliseAngle(-180), 0);
        assertEquals(0, GameUtils.normaliseAngle(-360), 0);
        assertEquals(350, GameUtils.normaliseAngle(-370), 0);
    }

    public void testGetAngleDifference() {

        // Angles are equal
        assertEquals(0, GameUtils.getAngleDifference(0, 0), 0);

        // Normal case
        assertEquals(30, GameUtils.getAngleDifference(30, 60), 0);

        // Wrap-around case
        assertEquals(60, GameUtils.getAngleDifference(330, 30), 0);
    }

}
