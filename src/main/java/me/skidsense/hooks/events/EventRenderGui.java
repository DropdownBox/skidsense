package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRenderGui extends Event {
    private ScaledResolution resolution;
    private float partialTicks;
    
    public EventRenderGui(ScaledResolution resolution , float partialTicks) {
        this.resolution = resolution;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

}
