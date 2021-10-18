package net.nullspace_mc.safe_fortress_finder;

import java.util.HashMap;
import java.util.Map;

public class FortressFinder {

    // HashMap containing fortresses with hash based on coordinates
    private HashMap<FortressPos, Object> forts;
    private static Object placeholder = new Object();

    private Map.Entry<FortressPos, Object>[] table;
    private int firstFortIdx;

    public FortressFinder() {
        this.forts = new HashMap<FortressPos, Object>();
    }

    // searches a given seed for fortresses within the given range and adds them to forts
    public void searchSeed(long seed, int range) {
        for(int x = -range; x < range; ++x) {
            for(int z = -range; z < range; ++z) {
                if(FortressPos.isValidFortressPos(seed, x, z)) {
                    this.forts.put(new FortressPos(x, z), FortressFinder.placeholder);
                }
            }
        }
    }

    // gets the table underlying the forts HashMap and find the first nonempty bucket
    public void cacheTable() {
        this.table = HashMapInspector.getTable(forts);
        int i; for(i = 0; i < table.length && table[i] == null; ++i);
        this.firstFortIdx = i;
    }

    // prints the safe fortress candidates in the first nonempty HashMap bucket
    public void printCandidates(boolean highlightFirst) {
        if(this.firstFortIdx >= table.length) {
            System.out.println("Fortress HashMap is empty!");
            return;
        }
        System.out.printf("First nonempty bucket: %d\n", this.firstFortIdx);

        // list safe fortress candidates within first nonempty bucket
        System.out.println("Safe fortress candidate chunk coordinates:");
        boolean first = true;
        for(Map.Entry<FortressPos, Object> node = table[this.firstFortIdx]; node != null; node = HashMapInspector.getNextNode(node)) {
            FortressPos fort = node.getKey();
            if(first && highlightFirst) {
                System.out.printf("\t%d,%d (Current safe fortress)\n",fort.x, fort.z);
            } else {
                System.out.printf("\t%d,%d\n",fort.x, fort.z);
            }

            first = false;
        }
    }

    public void printMinTableSize() {
        int minTableSize;
        SIZECALC:
        for(minTableSize = table.length; minTableSize > 0; minTableSize /= 2) {
            for(int i = this.firstFortIdx + (minTableSize/2); i < table.length; i += minTableSize/2) {
                if(table[i] != null) break SIZECALC;
            }
        }
        System.out.printf("Minimum table size: %d\n", minTableSize);
        System.out.printf("(At least %d fortresses must be generated.)\n", (int)(minTableSize*0.375) + 1);
    }
}
