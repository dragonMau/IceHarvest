package curome;

import arc.util.Log;

public class Logging {
    public static final String prefix = "[IceHarvest]";
    public static void info(String msg) {
        Log.info(prefix+" "+msg);
    }
}