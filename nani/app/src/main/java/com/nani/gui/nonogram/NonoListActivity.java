package com.nani.gui.nonogram;

import static com.nani.data.DBSudoku.SUDOKU_NAME;
import static com.nani.data.DBSudoku.SUDOKU_STATE;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.nani.R;
import com.nani.data.DBNonogram;
import com.nani.gui.MainActivity;

public class NonoListActivity extends MainActivity {
    DBNonogram db;
    SimpleCursorAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.err.println("NONOGRAM LIST ACTIVITY");
        setContentView(R.layout.list_nonogram);
        db = new DBNonogram(getApplicationContext());
        adapter = new SimpleCursorAdapter(this, R.layout.item_nonogram,
                null, new String[]{SUDOKU_NAME, SUDOKU_STATE,
        }, new int[]{R.id.list_item_name_nonogram, R.id.list_item_state_nonogram}, 0);
        adapter.setViewBinder(new NonoboardListViewBinder());
        listView = findViewById(R.id.list_nono);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> prepareNonogramBoard((int)id));
        refreshList();
    }
    Cursor cursor = null;
    private void refreshList() {
        cursor = db.getAllNonogramsCursor();
        adapter.changeCursor(cursor);
    }
    private void prepareNonogramBoard(int nonoId) {
        Intent intent = new Intent(NonoListActivity.this, NonoBoardActivity.class);
        intent.putExtra(NonoBoardActivity.nonoId, nonoId);
        startActivity(intent);
    }
    private static class NonoboardListViewBinder implements SimpleCursorAdapter.ViewBinder {
        NonoboardListViewBinder() {}
        @Override
        public boolean setViewValue(View view, Cursor c, int columnIndex) {
            switch (view.getId()) { // TODO: bigger grid plan
                case R.id.list_item_name_nonogram:
                    String name = c.getString(columnIndex);
                    TextView label = ((TextView) view);
                    label.setTextSize(20);
                    label.setVisibility(View.VISIBLE);
                    label.setText(name);
                    break;
                case R.id.list_item_state_nonogram:
                    int state = c.getInt(columnIndex);
                    label = ((TextView) view);
                    label.setTextSize(18);
                    label.setVisibility(View.VISIBLE);
                    label.setText(state == 2 ? "Solved!" : (state == 0 ? "Another challenge!" : "In process!"));
                    break;
            }
            return true;
        }
    }
}
