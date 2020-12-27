package net.nullspace_mc.safefortressfinder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class FortressFinder {
    private long worldSeed;
    private int range;

    private LinkedList<CoordinatePair> fortresses;

    private Random rand = new Random();

    public FortressFinder(long s, int r) {
	this.worldSeed = s;
	this.range = r;
    }

    /**	searches for nether fortresses in the world and puts their
     *	positions into the fortresses list
     */
    public void findFortressLocations() {
	this.fortresses = new LinkedList<CoordinatePair>();

	System.out.println("Searching for fortresses within " + this.range + " chunks of (0,0).");

	for(int x = -range; x < range; ++x) {
	    for(int z = -range; z < range; ++z) {
		if(this.isValidFortressLocation(x, z)) {
		    this.fortresses.add(new CoordinatePair(x, z));
		}
	    }
	}

	System.out.println("Found " + this.fortresses.size() + " fortresses.");
    }

    /** searches for nether fortresses that will be in a certain bucket of
     *	the hash map
     */
    public void findBucketNFortresses(int n) {
	int tableSize = 16;
	int numFortresses = this.fortresses.size();
	int maxTableSize = this.tableSizeFor(numFortresses);
	if((maxTableSize * 3) / 4 <= fortresses.size()) maxTableSize *= 2;
	LinkedList<CoordinatePair> bucketNFortresses = new LinkedList<CoordinatePair>();

	while(tableSize <= maxTableSize) {
	    System.out.println("Fortresses in bucket " + n + " with table size " + tableSize);

	    for(CoordinatePair f : this.fortresses) {
		if(f.getHashMapBucket(tableSize - 1) == n) {
		    System.out.println("(" + f.x + "," + f.z + ")");
		    bucketNFortresses.add(f);
		}
	    }

	    if(bucketNFortresses.size() == 0) {
		System.out.println("No fortresses in bucket 0");
	    }

	    this.fortresses.clear();
	    this.fortresses = bucketNFortresses;
	    bucketNFortresses = new LinkedList<CoordinatePair>();

	    tableSize *= 2;
	}
    }

    /** computes whether or not a nether fortress will spawn
     *	in the given chunk
     */
    public boolean isValidFortressLocation(int x, int z) {
	int seedComponentX = x >> 4;
	int seedComponentZ = z >> 4;
	this.rand.setSeed((long)(seedComponentX ^ seedComponentZ << 4) ^ this.worldSeed);
	this.rand.nextInt();
	return this.rand.nextInt(3) != 0 ? false : (
	    x != (seedComponentX << 4) + 4 + this.rand.nextInt(8) ? false :
		z == (seedComponentZ << 4) + 4 + this.rand.nextInt(8)
	);
    }

    public int tableSizeFor(int count) {
	int n = count - 1;
	n |= n >>> 1;
	n |= n >>> 2;
	n |= n >>> 4;
	n |= n >>> 8;
	n |= n >>> 16;
	return n + 1;
    }
}
