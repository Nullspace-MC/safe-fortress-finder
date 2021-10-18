package net.nullspace_mc.safe_fortress_finder;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import me.nullicorn.nedit.NBTInputStream;
import me.nullicorn.nedit.type.NBTCompound;

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

    // searches a given Fortress.dat file for fortresses within the given range and adds them to forts
    public void searchFile(File fortFile) {
        try {
            NBTInputStream fortFileStream = new NBTInputStream(new FileInputStream(fortFile));
            NBTCompound fortTags = fortFileStream.readFully().getCompound("data.Features");
            for(Object fto : fortTags.values()) {
                NBTCompound ft = (NBTCompound)fto;
                this.forts.put(new FortressPos(ft.getInt("ChunkX", 0), ft.getInt("ChunkZ", 0)), placeholder);
            }
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }

    // gets the table underlying the forts HashMap and find the first nonempty bucket
    public void cacheTable() {
        this.table = HashMapInspector.getTable(forts);
        int i; for(i = 0; i < table.length && table[i] == null; ++i);
        this.firstFortIdx = i;
    }

    // simulates saving the fortress map to/loading from disk
    public void reloadMap() {
        HashMap<String, FortressPos> nbtWrite = new HashMap<String, FortressPos>();
        for(FortressPos fp : this.forts.keySet()) {
            nbtWrite.put(fp.toString(), fp.copy());
        }
        HashMap<String, FortressPos> nbtRead = new HashMap<String, FortressPos>();
        for(Map.Entry<String, FortressPos> nbt : nbtWrite.entrySet()) {
            nbtRead.put(nbt.getKey(), nbt.getValue().copy());
        }
        this.forts = new HashMap<FortressPos, Object>();
        for(FortressPos fp : nbtRead.values()) {
            this.forts.put(fp.copy(), FortressFinder.placeholder);
        }
    }

    // simulates saving/loading the fortress map several times and checks for changes
    public void checkSafety(int n) {
        this.cacheTable();
        Map.Entry<FortressPos, Object> oldEntry, newEntry;
        FortressPos oldPos, newPos;
        for(int i = 1; i <= n; ++i) {
            oldEntry = this.table[this.firstFortIdx];
            oldPos = oldEntry.getKey();
            this.reloadMap();
            this.cacheTable();
            newEntry = this.table[this.firstFortIdx];
            newPos = newEntry.getKey();

            if(!oldPos.equals(newPos)) {
                System.out.printf("Safe fortress changed on replacement %d from (%d,%d) to (%d,%d)\n",
                    i, oldPos.x, oldPos.z, newPos.x, newPos.z
                );
            }
        }
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

    // calculates and prints the minimum hash table size for which bucket 0 has a consistent set of fortresses in it
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
