package com.nani.engine.game.sudoku;

import java.util.Collection;

public class CellFamily {
    Cell[] cells;
    private final int maxCellValue = 9;
    public CellFamily(Collection<Cell> allCels) {
        int index = 0;
        cells = new Cell[allCels.size()];
        for (Cell c : allCels) {
            cells[index++] = c;
        }
    }

    public boolean isCompleted() {
        boolean[] count = new boolean[maxCellValue + 1];
        for (Cell c : cells)
            count[c.getValue()] = true;
        for (int i = 0; i < cells.length; ++i)
            if (!count[i + 1])
                return false;
        return true;
    }
    public boolean isValid() {
        boolean[] count = new boolean[maxCellValue + 1];
        for (Cell c : cells) {
            if (c.getValue() > 0 && count[c.getValue()])
                return false;
            count[c.getValue()] = true;
        }
        return true;
    }
    public boolean isStarted() {
        for (Cell c : cells) {
            if (c.getValue() > 0 && c.isEditable())
                return true;
        }
        return false;
    }
    public int getSize() {return cells.length; }

}
