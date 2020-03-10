package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRenderGui extends Event {
    private ScaledResolution resolution;

    public EventRenderGui(ScaledResolution resolution) {
        this.resolution = resolution;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }

}
