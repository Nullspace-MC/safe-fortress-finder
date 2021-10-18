package net.nullspace_mc.safe_fortress_finder;

import java.util.Random;

public class FortressPos {

    private static final Random rand = new Random();
    public final int x, z;

    public FortressPos(int xPos, int zPos) {
        this.x = xPos;
        this.z = zPos;
    }

    public static boolean isValidFortressPos(long seed, int xPos, int zPos) {
        int xSeedComponent = xPos >> 4;
        int zSeedComponent = zPos >> 4;
        FortressPos.rand.setSeed((long)(xSeedComponent ^ zSeedComponent << 4) ^ seed);
        FortressPos.rand.nextInt();
        return FortressPos.rand.nextInt(3) != 0 ? false : (
            xPos != (xSeedComponent << 4) + 4 + FortressPos.rand.nextInt(8) ? false :
            zPos == (zSeedComponent << 4) + 4 + FortressPos.rand.nextInt(8)
        );
    }

    public FortressPos copy() {
        return new FortressPos(this.x, this.z);
    }

    public String toString() {
        return String.format("[%d,%d]", this.x, this.z);
    }

    public boolean equals(FortressPos other) {
        return this.x == other.x && this.z == other.z;
    }

    public int hashCode() {
        return this.x ^ this.z;
    }
}
