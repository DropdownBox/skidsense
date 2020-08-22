package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;

public class EventChatRecieve
extends Event {
    private String message;

    public EventChatRecieve(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

