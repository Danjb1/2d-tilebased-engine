package com.danjb.engine.util;

public class Directions {

    /**
     * Valid directions in the x-axis.
     */
    public enum DirectionX {

        LEFT (-1),
        NONE  (0),
        RIGHT (1);

        private int multiplier;

        private DirectionX(int multiplier) {
            this.multiplier = multiplier;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

    /**
     * Valid directions in the y-axis.
     */
    public enum DirectionY {

        UP   (-1),
        NONE  (0),
        DOWN  (1);

        private int multiplier;

        private DirectionY(int multiplier) {
            this.multiplier = multiplier;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

}
