package com.nani.engine.game.nonogram;

public class NonoBoard {
    public FieldsSet fields;
    private final int id;
    private final String name;
    private int state;
    public NonoBoard(FieldsSet fields, String name, int id, int state) {
        this.fields = fields;
        this.name = name;
        this.id = id;
        this.state = state;
    }
    public FieldsSet getFieldsSet() { return fields; }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getState() { return state; }
}
