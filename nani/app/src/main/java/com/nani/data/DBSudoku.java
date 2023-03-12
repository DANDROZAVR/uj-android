package com.nani.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.nani.engine.game.sudoku.CellsSet;
import com.nani.engine.game.sudoku.SudokuBoard;

import java.util.StringTokenizer;

public class DBSudoku {
    public static final String SUDOKU_LIST_NAME = "sus_dokus";
    public static final String SUDOKU_NAME = "name";
    public static final String SUDOKU_ID = "_id";
    public static final String SUDOKU_STATE = "state"; //currently 0 or 1 or 2
    public static final String SUDOKU_DATA = "board";
    DBHelper helper;
    public DBSudoku(Context context) {
        helper = new DBHelper(context);
    }
    public SudokuBoard getSudoku(int sudokuId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SUDOKU_LIST_NAME);
        qb.appendWhere(SUDOKU_ID + "=" + sudokuId);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = qb.query(db, null, null, null, null, null, null);
        if (c.moveToFirst())
            return getSudokuFromRow(c);
        return null;
    }
    public void setWinSudoku(int sudokuId) {
        ContentValues values = new ContentValues();
        values.put(SUDOKU_STATE, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(SUDOKU_LIST_NAME, values, SUDOKU_ID + "=" + sudokuId, null);
    }
    public Cursor getAllSudokuCursor() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SUDOKU_LIST_NAME);
        SQLiteDatabase db = helper.getReadableDatabase();
        return qb.query(db, null, null, null, null, null, null);
    }
    private SudokuBoard getSudokuFromRow(Cursor c) {
        String data = c.getString(c.getColumnIndex(SUDOKU_DATA));
        String name = c.getString(c.getColumnIndex(SUDOKU_NAME));
        int id = c.getInt(c.getColumnIndex(SUDOKU_ID));
        StringTokenizer tokenizer = new StringTokenizer(data, "|");
        SudokuBoard board = new SudokuBoard(CellsSet.parseString(tokenizer), name, id);
        return board;
    }
    public void saveStateSudoku(SudokuBoard board) {
        String data = board.getCellsSet().getStringState();
        int id = board.getId();
        int state;
        if (board.getCellsSet().isComplete()) {
            state = 1;
        } else
        if (board.getCellsSet().isStarted()) {
            state = 2;
        } else {
            state = 0;
        }
        ContentValues values = new ContentValues();
        values.put(SUDOKU_DATA, data);
        values.put(SUDOKU_STATE, state);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.update(SUDOKU_LIST_NAME, values, SUDOKU_ID + "=" + id, null);
    }
}
