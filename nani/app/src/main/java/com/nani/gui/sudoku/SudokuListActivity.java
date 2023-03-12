package com.nani.gui.sudoku;

import static com.nani.data.DBSudoku.*;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.nani.R;
import com.nani.data.DBSudoku;
import com.nani.gui.MainActivity;

public class SudokuListActivity extends MainActivity {
    DBSudoku db;
    SimpleCursorAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.err.println("SUDOKU LIST ACTIVITY");
        setContentView(R.layout.list_sudoku);
        db = new DBSudoku(getApplicationContext());
        adapter = new SimpleCursorAdapter(this, R.layout.item_sudoku,
                null, new String[]{SUDOKU_NAME, SUDOKU_STATE,
        }, new int[]{R.id.list_item_name, R.id.list_item_state}, 0);
        adapter.setViewBinder(new SudokuListViewBinder());
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> prepareSudokuBoard((int)id));
        refreshList();
    }
    Cursor cursor = null;
    private void refreshList() {
        cursor = db.getAllSudokuCursor();
        adapter.changeCursor(cursor);
    }
    private void prepareSudokuBoard(int sudokuId) {
        Intent intent = new Intent(SudokuListActivity.this, SudokuBoardActivity.class);
        intent.putExtra(SudokuBoardActivity.sudokuId, sudokuId);
        startActivity(intent);
    }
    private static class SudokuListViewBinder implements SimpleCursorAdapter.ViewBinder {
        SudokuListViewBinder() {}
        @Override
        public boolean setViewValue(View view, Cursor c, int columnIndex) {
            switch (view.getId()) {
                case R.id.list_item_name:
                    String name = c.getString(columnIndex);
                    TextView label = ((TextView) view);
                    label.setTextSize(20);
                    label.setVisibility(View.VISIBLE);
                    label.setText(name);
                    break;
                case R.id.list_item_state:
                    int state = c.getInt(columnIndex);
                    label = ((TextView) view);
                    label.setTextSize(18);
                    label.setVisibility(View.VISIBLE);
                    label.setText(state == 1 ? "Solved!" : (state == 0 ? "Another challenge!" : "In process!"));
                    break;
            }
            return true;
        }
    }
}
