package com.nani.gui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.nani.R;
import com.nani.gui.nonogram.NonoBoardActivity;
import com.nani.gui.nonogram.NonoListActivity;
import com.nani.gui.sudoku.SudokuBoardActivity;
import com.nani.gui.sudoku.SudokuListActivity;
import com.nani.utility.Utils;

public class StartActivity extends MainActivity{
    Button startSudokuButton, startNonogramButton, listSudokuButton, listNonoButton, playTogether;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.err.println("START ACTIVITY");
        setContentView(R.layout.start_app);
        startSudokuButton = findViewById(R.id.startSudoku);
        startNonogramButton = findViewById(R.id.startNonogram);
        playTogether = findViewById(R.id.play_together);
        startSudokuButton.setOnClickListener(view -> prepareSudokuBoard(7));
        playTogether.setOnClickListener(view -> prepareLobby());
        startNonogramButton.setOnClickListener(view -> prepareNonogramBoard(22));
        listSudokuButton = findViewById(R.id.list_sudoku_button);
        listNonoButton = findViewById(R.id.list_nonograms_button);
        listSudokuButton.setOnClickListener(view -> startActivity(new Intent(this, SudokuListActivity.class)));
        listNonoButton.setOnClickListener(view -> startActivity(new Intent(this, NonoListActivity.class)));
    }
    private void prepareSudokuBoard(int sudokuId) {
        checkName();
        Intent intent = new Intent(StartActivity.this, SudokuBoardActivity.class);
        intent.putExtra(SudokuBoardActivity.sudokuId, sudokuId);
        startActivity(intent);
    }
    private void prepareNonogramBoard(int nonogramId) {
        checkName();
        Intent intent = new Intent(StartActivity.this, NonoBoardActivity.class);
        intent.putExtra(NonoBoardActivity.nonoId, nonogramId);
        startActivity(intent);
    }
    private void prepareLobby() {
        checkName();
        Intent intent = new Intent(StartActivity.this, LobbyChoosingActivity.class);
        intent.putExtra(LobbyChoosingActivity.lobbyNew, true);
        startActivity(intent);
    }
    private void checkName() {
        EditText name = findViewById(R.id.player_name);
        String val = name.getText().toString();
        if (!val.equals("")) {
            Utils.name = val;
        }
    }
}
