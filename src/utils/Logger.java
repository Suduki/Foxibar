package utils;

import java.util.Arrays;

/**
 * Created by xxhedbet on 2017-03-23.
 */
public class Logger {
    public static Logger instance = new Logger();
    boolean[] flags;

    private Logger() {
        flags = new boolean[10];
        Arrays.fill(flags, false);
    }

    public void trace(Object o, String message){

    }

    public void debug(Object o, String message){

    }

    public void info(Object o, String message){
        System.out.println("[" + o.getClass().getCanonicalName() + "]" + " : " + message);
    }
}
