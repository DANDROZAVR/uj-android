package com.nani.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.nani.engine.game.nonogram.FieldsSet;
import com.nani.engine.game.nonogram.NonoBoard;
import com.nani.engine.game.sudoku.CellsSet;
import com.nani.engine.game.sudoku.SudokuBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DBNonogram {
    public static final String NONOGRAM_LIST_NAME = "sus_nonos";
    public static final String NONOGRAM_NAME = "name";
    public static final String NONOGRAM_ID = "_id";
    public static final String NONOGRAM_STATE = "state"; //currently 0 or 1 or 2
    public static final String NONOGRAM_DATA = "board";
    DBHelper helper;
    public DBNonogram(Context context) {
        helper = new DBHelper(context);
    }
    public NonoBoard getNonogram(int nonogramId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NONOGRAM_LIST_NAME);
        qb.appendWhere(NONOGRAM_ID + "=" + nonogramId);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = qb.query(db, null, null, null, null, null, null);
        if (c.moveToFirst())
            return getNonoBoardFromRow(c);
        return null;
    }
    private NonoBoard getNonoBoardFromRow(Cursor c) {
        @SuppressLint("Range") String data = c.getString(c.getColumnIndex(NONOGRAM_DATA));
        @SuppressLint("Range") String name = c.getString(c.getColumnIndex(NONOGRAM_NAME));
        @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(NONOGRAM_ID));
        @SuppressLint("Range") int state = c.getInt(c.getColumnIndex(NONOGRAM_STATE));
        StringTokenizer descAndData = new StringTokenizer(data, "&");
        String desc = descAndData.nextToken();
        StringTokenizer descRowsAndColsTokenizer = new StringTokenizer(desc, ",");
        List<List<Integer>> descRows = getNonogramDescription(new StringTokenizer(descRowsAndColsTokenizer.nextToken(), "|"));
        List<List<Integer>> descCols = getNonogramDescription(new StringTokenizer(descRowsAndColsTokenizer.nextToken(), "|"));
        StringTokenizer dataTokenizer = new StringTokenizer(descAndData.nextToken(), "|");
        return new NonoBoard(FieldsSet.parseTokenizer(dataTokenizer, descRows, descCols), name, id, state);
    }
    List<List<Integer>> getNonogramDescription(StringTokenizer descriptionTokenizer) {
        List<List<Integer>> description = new ArrayList<>();;
        while(descriptionTokenizer.hasMoreTokens()) {
            StringTokenizer descValuesTokenizer = new StringTokenizer(descriptionTokenizer.nextToken(), ":");
            List<Integer> descValues = new ArrayList<>();
            while (descValuesTokenizer.hasMoreTokens()) {
                String value = descValuesTokenizer.nextToken();
                if (!value.equals("@"))
                    descValues.add(Integer.parseInt(value));
            }
            description.add(descValues);
        }
        return description;
    }
    public void setWinNono(int sudokuId) {
        ContentValues values = new ContentValues();
        values.put(NONOGRAM_STATE, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(NONOGRAM_LIST_NAME, values, NONOGRAM_ID + "=" + sudokuId, null);
    }
    public Cursor getAllNonogramsCursor() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NONOGRAM_LIST_NAME);
        SQLiteDatabase db = helper.getReadableDatabase();
        return qb.query(db, null, null, null, null, null, null);
    }
    public void saveStateNonoboard(NonoBoard board) {
        String desc = board.getFieldsSet().getStringDesc();
        String data = board.getFieldsSet().getStringState();

        int id = board.getId();
        int state;
        if (board.getFieldsSet().isCompleted()) {
            state = 2;
        } else
        if (board.getFieldsSet().isStarted()) {
            state = 1;
        } else {
            state = 0;
        }
        ContentValues values = new ContentValues();
        values.put(NONOGRAM_DATA, desc + data);
        values.put(NONOGRAM_STATE, state);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.update(NONOGRAM_LIST_NAME, values, NONOGRAM_ID + "=" + id, null);
    }
}
