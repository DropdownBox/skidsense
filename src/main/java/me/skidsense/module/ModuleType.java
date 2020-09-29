package me.skidsense.module;

public enum ModuleType {
	Fight("a"),
	Move("b"),
	Player("c"),
    World("e"),
    Visual("g");

    private final String character;

    ModuleType(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }
}

