package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;
import net.minecraft.entity.Entity;

public class EventAttack extends Event {
	public Entity targetEntity;
	//public boolean attacked;
	public EventAttack(Entity targetEntity) {
		super();
		this.targetEntity = targetEntity;

	}
}
