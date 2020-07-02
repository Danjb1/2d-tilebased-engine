package com.danjb.engine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.danjb.engine.util.AngleUtils;

/**
 * Tests of the static methods in the AngleUtils class.
 *
 * @author Dan Bryce
 */
public class AngleUtilsTest {

    @Test
    public void testGetAngleFromSpeed() {

        // Edge cases (only one axis)
        assertEquals(0, AngleUtils.getAngle(0, -1), 0);
        assertEquals(90, AngleUtils.getAngle(1, 0), 0);
        assertEquals(180, AngleUtils.getAngle(0, 1), 0);
        assertEquals(270, AngleUtils.getAngle(-1, 0), 0);

        // Regular cases
        assertEquals(45, AngleUtils.getAngle(0.5f, -0.5f), 0.01f);
        assertEquals(135, AngleUtils.getAngle(0.5f, 0.5f), 0.01f);
        assertEquals(225, AngleUtils.getAngle(-0.5f, 0.5f), 0.01f);
        assertEquals(315, AngleUtils.getAngle(-0.5f, -0.5f), 0.01f);
    }

    @Test
    public void testNormaliseAngle() {

        // Angle is already normalised
        assertEquals(0, AngleUtils.normaliseAngle(0), 0);
        assertEquals(180, AngleUtils.normaliseAngle(180), 0);

        // Angle >= 360
        assertEquals(0, AngleUtils.normaliseAngle(360), 0);
        assertEquals(180, AngleUtils.normaliseAngle(540), 0);

        // Angle < 0
        assertEquals(359, AngleUtils.normaliseAngle(-1), 0);
        assertEquals(180, AngleUtils.normaliseAngle(-180), 0);
        assertEquals(0, AngleUtils.normaliseAngle(-360), 0);
        assertEquals(350, AngleUtils.normaliseAngle(-370), 0);
    }

    public void testGetAngleDifference() {

        // Angles are equal
        assertEquals(0, AngleUtils.getAngleDifference(0, 0), 0);

        // Normal case
        assertEquals(30, AngleUtils.getAngleDifference(30, 60), 0);

        // Wrap-around case
        assertEquals(60, AngleUtils.getAngleDifference(330, 30), 0);
    }

}
