package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;

public class EventPreUpdate
extends Event {
	public float yaw;
    public float pitch;
    public double y;
    private boolean onGround;

    public EventPreUpdate(float yaw, float pitch, double y, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.y = y;
        this.onGround = onGround;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean ground) {
        this.onGround = ground;
    }

	public void setRotations(float[] rot, boolean b) {
		// TODO Auto-generated method stub
		
	}
}
