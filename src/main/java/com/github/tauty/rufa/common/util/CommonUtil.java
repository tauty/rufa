package com.github.tauty.rufa.common.util;

/**
 * A Utility class which has useful methods.
 */
public class CommonUtil {

    public static boolean containsNull(Object... vals) {
        for (Object val : vals) {
            if(val == null) return true;
        }
        return false;
    }

    public static boolean containsOnStartsWith(String target, String... expects) {
        for (String expect : expects) {
            if (target.startsWith(expect)) return true;
        }
        return false;
    }

    public static boolean equalsSafely(Object o1, Object o2) {
        return (o1 == null || o2 == null) ? (o1 == o2) : o1.equals(o2);
    }

    public static boolean equalsSafely(Object... vals) {
        if(vals.length == 0) return true;
        Object val0 = vals[0];
        for (int i = 1; i < vals.length; i++) {
            if(!equalsSafely(val0, vals[i])) return false;
        }
        return true;
    }

    public static int hashSum(Object... keys) {
        int hash = 0;
        for (Object key : keys) {
            hash += key == null ? 0 : key.hashCode();
        }
        return hash;
    }
}
