package me.skidsense.module.collection.move;

import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import java.awt.Color;

public class SafeWalk
extends Mod {
    public SafeWalk() {
        super("SafeWalk", new String[]{"eagle", "parkour"}, ModuleType.Move);
        this.setColor(new Color(198, 253, 191).getRGB());
    }
}

