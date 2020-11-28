package me.skidsense.hooks.value;

public class Option<V>
extends Value<V> {
    public Option(String displayName, String name, V enabled) {
        super(displayName, name);
        this.setValue(enabled);  
    }
    
    public Option(String name, V enabled) {
        super(name, name);
        this.setValue(enabled);  
    }
}

