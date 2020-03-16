package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;
import net.minecraft.entity.Entity;

public class EventAttack
        extends Event {
    public Entity attacked;
    public boolean cancelled;
    public EventAttack(Entity ent, boolean cancelled){
        this.attacked = ent;
        this.cancelled =cancelled;
    }
}
