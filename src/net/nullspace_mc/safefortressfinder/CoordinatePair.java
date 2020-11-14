package net.nullspace_mc.safefortressfinder;

public class CoordinatePair {
    public final int x;
    public final int z;

    public CoordinatePair(int xCoord, int zCoord) {
	this.x = xCoord;
	this.z = zCoord;
    }

    public Long toLong() {
	return Long.valueOf((long)x & 4294967295L | ((long)z & 4294967295L) << 32);
    }

    public int getHashMapBucket(int mask) {
	int h = this.toLong().hashCode();
	return (h ^ (h >>> 16)) & mask;
    }
}
