package net.nullspace_mc.safefortressfinder;

public class Main {
    public static void main(String argv[]) {
	long seed;
	int range;

	seed = Main.parseSeed(argv);
	range = Main.parseRange(argv);

	FortressFinder finder = new FortressFinder(seed, range);
	finder.findFortressLocations();
	finder.findBucket0Fortresses();
    }

    static long parseSeed(String argv[]) {
	long seed = Long.MIN_VALUE;

	if(argv.length > 0) {
	    try {
		seed = Long.parseLong(argv[0]);
	    } catch(NumberFormatException e) {
		System.err.println("seed must be an integer in base 10");
		Main.printUsageAndExit(1);
	    }
	} else {
	    Main.printUsageAndExit(0);
	}

	return seed;
    }

    static int parseRange(String argv[]) {
	int range = Integer.MIN_VALUE;

	if(argv.length > 1) {
	    try {
		range = Integer.parseInt(argv[1]);
		
		if(range < 0) {
		    System.err.println("range must be a positive integer in base 10");
		    Main.printUsageAndExit(1);
		}
	    } catch(NumberFormatException e) {
		System.err.println("range must be a positive integer in base 10");
		Main.printUsageAndExit(1);
	    }
	} else {
	    Main.printUsageAndExit(1);
	}

	return range;
    }

    static void printUsageAndExit(int status) {
	System.err.println("Usage:");
	System.err.println("\tjava -jar SafeFortressFinder.jar <seed> <range>");
	System.err.println("\t\tseed:\ta Minecraft world seed");
	System.err.println("\t\trange:\tthe maximum range (in chunks) to search from (0,0)");
	
	System.exit(status);
    }
}
