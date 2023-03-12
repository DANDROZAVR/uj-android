package com.nani.engine.game.sudoku;

import java.util.StringTokenizer;

public class Cell {
    private int value;
    private boolean editable;
    Cell(){value = -1; editable = true;}
    Cell(StringTokenizer s) {
        String item = s.nextToken();
        editable = item.charAt(0) == '+';
        value = -1;
        int newValue = item.charAt(1) - '0';
        //TODO set number's boundaries
        if (newValue < 0 || newValue > 9)
            throw new IllegalArgumentException("Value for the cell is " + newValue + ". Parsed from" + s);
        value = newValue;
    }
    public int getValue() { return value; }
    public void setValue(int value) {
        if (!editable)
            throw new RuntimeException("Cell is not editable");
        this.value = value;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public boolean isEditable(){ return editable; }
}
