package com.nani.engine.game.sudoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;

public class CellsSet {
    private int size;

    private Cell[][] cells;
    private CellFamily[] columns;
    private CellFamily[] rows;
    private CellFamily[][] squares;
    public CellsSet(int size){ this.size = size; }
    public CellsSet(Cell[][] cells) {
        size = cells.length;
        this.cells = cells;
        divideSets();
    }
    public void divideSets() {
        columns = new CellFamily[size];
        for (int i = 0; i < size; ++i) {
            Collection<Cell> c = new ArrayList<Cell>();
            for (int j = 0; j < size; ++j)
                c.add(cells[i][j]);
            columns[i] = new CellFamily(c);
        }
        rows = new CellFamily[size];
        for (int j = 0; j < size; ++j) {
            Collection<Cell> c = new ArrayList<Cell>();
            for (int i = 0; i < size; ++i)
                c.add(cells[i][j]);
            rows[j] = new CellFamily(c);
        }
        squares = new CellFamily[size / 3][size / 3];
        // TODO implement dividin in other dimensions
        for (int i = 0; i < size; i += 3)
            for (int j = 0; j < size; j += 3) {
                Collection<Cell> c = new ArrayList<Cell>();
                for (int k = 0; k < 3; ++k)
                    for (int l = 0; l < 3; ++l)
                        c.add(cells[k + i][l + j]);
                 squares[i / 3][j / 3] = new CellFamily(c);
            }
    }
    public void changeSell(int row, int column, int newValue) {
        cells[row][column].setValue(newValue);
    }
    public Cell getCell(int row, int column) {
        return cells[row][column];
    }
    public void setEditable(int row, int column, boolean newEditable) {
        cells[row][column].setEditable(newEditable);
    }
    public boolean isComplete() {
        boolean ans = true;
        for (CellFamily column : columns) ans &= column.isCompleted();
        if (!ans) return false;
        for (CellFamily row : rows) ans &= row.isCompleted();
        if (!ans) return false;
        for (CellFamily[] rowsFamily : squares)
            for (CellFamily row : rowsFamily)
                ans &= row.isCompleted();
        return ans;
    }
    public boolean isStarted() {
        boolean ans = false;
        for (CellFamily column : columns) ans |= column.isStarted();
        return ans;
    }
    public boolean isValid() {
        boolean ans = true;
        for (CellFamily column : columns) ans &= column.isValid();
        if (!ans) return false;
        for (CellFamily row : rows) ans &= row.isValid();
        if (!ans) return false;
        for (CellFamily[] rowsFamily : squares)
            for (CellFamily row : rowsFamily)
                ans &= row.isValid();
        return ans;
    }
    public int getSize() { return size; }
    public static int DEFAULT_SIZE = 9; //LATER move
    public static CellsSet parseString(StringTokenizer tokenizer) {
        Cell[][] cells = new Cell[DEFAULT_SIZE][DEFAULT_SIZE];
        for (int row = 0; row < DEFAULT_SIZE; ++row)
            for (int col = 0; col < DEFAULT_SIZE; ++col) {
                //char c = coded.charAt(row * DEFAULT_SIZE + col);
                //if (c < '0' || c > '9') throw new IllegalArgumentException("Error in parsing string '" + coded + "'. It contains char" + c);
                cells[row][col] = new Cell(tokenizer);
            }
        return new CellsSet(cells);
    }
    interface onChangeListener {
        public void onChange(Cell cell);
    }
    private onChangeListener listener = null;
    public void onChange(Cell cell) {
        if (listener != null)
            listener.onChange(cell);
    }
    public String getStringState() {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j) {
                answer.append("|");
                if (cells[i][j].isEditable())
                    answer.append("+"); else
                    answer.append("-");
                answer.append(cells[i][j].getValue());
            }
        return answer.toString();
    }
    public void setOnChangeListener(onChangeListener listener) {
        this.listener = listener;
    }
}
