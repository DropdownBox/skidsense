package me.skidsense.management;

import java.util.ArrayList;
import java.util.List;

import me.skidsense.SplashProgress;
import me.skidsense.management.alt.Alt;


public class AltManager {
    public static List<Alt> alts;
    public static Alt lastAlt;

    public static void init() {
    	SplashProgress.setProgress(6, "AltManager Init");
        AltManager.setupAlts();
        AltManager.getAlts();
    }

    public Alt getLastAlt() {
        return lastAlt;
    }

    public void setLastAlt(Alt alt) {
        lastAlt = alt;
    }

    public static void setupAlts() {
        alts = new ArrayList<Alt>();
    }

    public static List<Alt> getAlts() {
        return alts;
    }
}

