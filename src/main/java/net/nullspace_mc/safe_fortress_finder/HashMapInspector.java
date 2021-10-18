package net.nullspace_mc.safe_fortress_finder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// used for accessing certain hidden fields in a HashMap
public class HashMapInspector<K, V> {
    
    private static Field table;         // HashMap table of Nodes
    private static Class<?> nodeClass;  // HashMap Node internal class
    private static Field nodeNext;      // next Node in bin

    static {
        try {
            HashMapInspector.table = HashMap.class.getDeclaredField("table");
            HashMapInspector.table.setAccessible(true);
            HashMapInspector.nodeClass = Class.forName("java.util.HashMap$Node");
            HashMapInspector.nodeNext = nodeClass.getDeclaredField("next");
            HashMapInspector.nodeNext.setAccessible(true);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }

    // returns the underlying hash table of a given HashMap
    public static <K, V> Map.Entry<K, V>[] getTable(HashMap<K, V> map) {
        try {
            return (Map.Entry<K, V>[])table.get(map);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }

    // returns the node following a given entry
    public static <K, V> Map.Entry<K, V> getNextNode(Map.Entry<K, V> node) {
        try {
            return (Map.Entry<K, V>)nodeNext.get(node);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }
}
