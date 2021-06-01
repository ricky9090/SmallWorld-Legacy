package com.ricky9090.smallworld.view.advui;

public class ViewUtils {

    /**
     * Diff node only, return true if has differences, false otherwise
     */
    public static boolean diffNode(STView a, STView b) {
        if (a == null && b == null) {
            return false;
        }

        // a == null && b != null
        if (a == null) {
            return true;
        }

        // a != null && b == null
        if (b == null) {
            return true;
        }

        // a != null && b != null
        if (a.getId() == b.getId()) {
            return true;
        }

        return true;
    }

    public static boolean diffString(String a, String b) {
        if (a == null && b == null) {
            return false;
        }

        if (a == null) {
            return true;
        }

        if (b == null) {
            return true;
        }

        return a.equals(b);
    }
}
