package utils;

import java.util.Arrays;

/**
 *
 */
public class log {
    private static boolean[] flags = new boolean[10];

    public static void trace(Object o, String message){
        System.out.println("[" + o.getClass().getCanonicalName() + "]" + " : " + message);
    }

    public static void debug(Object o, String message){

    }

    public static void info(Object o, String message){
        System.out.println("[" + o.getClass().getCanonicalName() + "]" + " : " + message);
    }

    public static void toggleFlag(int index){
        if(index > 10 || index < 0)
            System.out.println("[utils.log] Tried to toggle a flag with index out of bounds");
        flags[index] = !flags[index];
    }

}
