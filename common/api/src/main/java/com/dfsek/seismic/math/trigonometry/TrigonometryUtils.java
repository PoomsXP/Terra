package com.dfsek.seismic.math.trigonometry;

import com.dfsek.seismic.math.floatingpoint.FloatingPointFunctions;

import java.util.concurrent.ThreadLocalRandom;


class TrigonometryUtils {
    protected static final double a1 = 0.99997726;
    protected static final double a3 = -0.33262347;
    protected static final double a5 = 0.19354346;
    protected static final double a7 = -0.11643287;
    protected static final double a9 = 0.05265332;
    protected static final double a11 = -0.0117212;
    protected static final long INT_ARRAY_BASE = 0L;
    protected static final long INT_ARRAY_SHIFT = 0L;

    static final int lookupTableSize = 65536;
    static final double radianToIndex = lookupTableSize / TrigonometryConstants.TAU;

    private static final float tauOverLookupSize = (float) (TrigonometryConstants.TAU / lookupTableSize);
    private static final int[] sinTable = new int[lookupTableSize + 1];

    static double sinLookup(int index) {
        int sign = (index & 0x8000) << 16;
        int mask = index >> 31;
        int invert = (0x8001 & mask) + (index ^ mask);
        invert &= 0x7FFF;
        return Float.intBitsToFloat(sinTable[invert] ^ sign);
    }

    static {
        for(int i = 0; i < sinTable.length; i++) {
            double angle = i * tauOverLookupSize;
            sinTable[i] = Float.floatToRawIntBits((float) StrictMath.sin(angle));
        }

        for(int degrees = 0; degrees < 360; degrees += 90) {
            double radians = Math.toRadians(degrees);
            sinTable[((int) (radians * radianToIndex)) & 0xFFFF] = Float.floatToRawIntBits((float) StrictMath.sin(radians));
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(int i = 0; i < sinTable.length; i++) {
            double value = random.nextDouble(-Math.PI, Math.PI);
            double found = TrigonometryFunctions.sin(value);
            double expected = StrictMath.sin(value);
            if(!FloatingPointFunctions.equalsWithinEpsilon(found, expected, 1E-4)) {
                throw new IllegalArgumentException(String.format("LUT error at value %f (expected: %s, found: %s)",
                    value, found, expected));
            }
        }

        for(int degrees = 0; degrees < 360; degrees += 90) {
            double value = Math.toRadians(degrees);
            double found = TrigonometryFunctions.sin(value);
            double expected = StrictMath.sin(value);
            if(!FloatingPointFunctions.equals(found, expected)) {
                throw new IllegalArgumentException(String.format("LUT error at cardinal direction %s (expected: %s, found: %s)",
                    degrees, found, expected));
            }
        }
    }
}
