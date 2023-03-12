package com.nani.engine.game.sudoku;

public class SudokuBoard {
    public CellsSet cells;
    private final int id;
    private final String name;
    public SudokuBoard(CellsSet set, String name, int id) {
        cells = set;
        this.name = name;
        this.id = id;
    }
    public CellsSet getCellsSet() { return cells; }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
