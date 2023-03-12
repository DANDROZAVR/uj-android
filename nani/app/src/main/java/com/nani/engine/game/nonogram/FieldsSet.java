package com.nani.engine.game.nonogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class FieldsSet {
    private Field [][] fields;
    private final int rowN, colN;
    private FieldFamily[] col, rows;
    private final List<List<Integer>> rowsDescription, colsDescription;
    public FieldsSet(Field[][] fields, List<List<Integer>> rowsDescription, List<List<Integer>> colsDescription) {
        this.fields = fields;
        this.rowsDescription = rowsDescription;
        this.colsDescription = colsDescription;
        rowN = fields.length;
        colN = fields[0].length;
        divideSets();
    }
    public static FieldsSet parseTokenizer(StringTokenizer tokenizer, List<List<Integer>> rowsDescription, List<List<Integer>> colsDescription) {
        int rowN = rowsDescription.size();
        int colN = colsDescription.size();
        Field[][] fields = new Field[rowN][colN];
        for (int i = 0; i < rowN; ++i) {
            for (int j = 0; j < colN; ++j) {
                fields[i][j] = new Field(tokenizer);
            }
        }
        return new FieldsSet(fields, rowsDescription, colsDescription);
    }
    private void divideSets() {
        for (int i = 0; i < rowN; ++i)
            assert fields[i].length == colN;
        assert rowsDescription.size() == rowN;
        assert colsDescription.size() == colN;
        rows = new FieldFamily[rowN];
        col = new FieldFamily[colN];
        for (int i = 0; i < rowN; ++i) {
            Collection<Field> c = new ArrayList<>(Arrays.asList(fields[i]));
            rows[i] = new FieldFamily(c, rowsDescription.get(i));
        }
        for (int j = 0; j < colN; ++j) {
            Collection<Field> c = new ArrayList<>();
            for (int i = 0; i < colN; ++i)
                c.add(fields[i][j]);
            col[j] = new FieldFamily(c, colsDescription.get(j));
        }
    }
    public int getColumnsNumber() {
        return colN;
    }
    public int getRowsNumber() {
        return rowN;
    }
    public List<List<Integer>> getRowsDescription() {
        return rowsDescription;
    }
    public List<List<Integer>> getColsDescription() {
        return colsDescription;
    }

    public Field getField(int row, int col) {
        return fields[row][col];
    }
    public boolean isValidRow(int row) {
        return rows[row].isValid();
    }
    public boolean isValidColumn(int column) {
        return col[column].isValid();
    }
    public boolean isCompleted() {
        boolean good = true;
        for (FieldFamily f : rows)
            good &= f.isCompleted();
        for (FieldFamily f : col)
            good &= f.isCompleted();
        return good;
    }
    public String getStringState() {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < rowN; ++i)
            for (int j = 0; j < colN; ++j) {
                answer.append("|");
                if (fields[i][j].isEmpty())
                    answer.append("0"); else
                if (fields[i][j].isCrossed())
                    answer.append("1"); else
                if (fields[i][j].isFilled())
                    answer.append("2");
            }
        return answer.toString();
    }
    public boolean isValid() {
        boolean good = true;
        for (FieldFamily f : rows)
            good &= f.isValid();
        for (FieldFamily f : col)
            good &= f.isValid();
        return good;
    }
    public boolean isStarted() {
        boolean good = false;
        for (FieldFamily f : rows)
            good |= f.isStarted();
        for (FieldFamily f : col)
            good |= f.isStarted();
        return good;
    }
    public String getStringDesc() {
        StringBuilder answer = new StringBuilder();
        for (FieldFamily f : rows)
            answer.append(f.getStringDesc());
        answer.append(",");
        for (FieldFamily f : col)
            answer.append(f.getStringDesc());
        answer.append("&");
        return answer.toString();
    }
    public void setAllEmpty() {
        for (int i = 0; i < rowN; ++i)
            for (int j = 0; j < colN; ++j)
                fields[i][j].setEmpty();
    }
}

