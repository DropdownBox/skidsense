package me.skidsense.command;

import me.skidsense.Client;

public abstract class Command {
    private String name;
    private String[] alias;
    private String syntax;
    private String help;
    private String args;



    public String getArgs() {
        return this.args;
    }
    public Command(String name, String[] alias, String syntax, String help) {
        this.name = name.toLowerCase();
        this.syntax = syntax.toLowerCase();
        this.help = help;
        this.alias = alias;
    }

    public abstract String execute(String[] var1);

    public String getName() {
        return this.name;
    }

    public String[] getAlias() {
        return this.alias;
    }

    public String getSyntax() {
        return this.syntax;
    }

    public String getHelp() {
        return this.help;
    }

    public void syntaxError(String msg) {
        Client.sendMessage(String.format("\u00a77Invalid command usage", msg));
    }

    public void syntaxError(byte errorType) {
        switch (errorType) {
            case 0: {
                this.syntaxError("bad argument");
                break;
            }
            case 1: {
                this.syntaxError("argument gay");
            }
        }
    }
}

