package com.nani.gui.sudoku;

import static com.nani.gui.LobbyChoosingActivity.client;
import static com.nani.gui.sudoku.SudokuBoardActivity.sudokuId;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nani.R;
import com.nani.data.DBSudoku;
import com.nani.engine.game.sudoku.SudokuBoard;
import com.nani.gui.MainActivity;
import com.nani.gui.StartActivity;
import com.nani.gui.boardsView.SudokuBoardViewSingleLobby;
import com.nani.utility.Utils;
import com.nani.utility.network.Command;
import com.nani.utility.network.CommandInterpreter;


public class SudokuBoardSingleLobbyActivity extends MainActivity {
    public static final String playersNumberId =  "playersNumber";
    public static final String lobbyNumberId =  "lobbyNumber";

    private SudokuBoardViewSingleLobby sudokuBoardView;
    private DBSudoku db;
    private TextView winning_clar, won_cnt_single_lobby, last_won_single_lobby;
    private TextView[] number_button;
    private LinearLayout layoutButtons;
    private int actPlayersNumber, winningPlayer;
    private String lobbyNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int sudokuIdNumber = getIntent().getIntExtra(sudokuId, 0);
        actPlayersNumber = getIntent().getIntExtra(playersNumberId, 1);
        lobbyNumber = getIntent().getStringExtra(lobbyNumberId);
        db = new DBSudoku(getApplicationContext());
        SudokuBoard board = db.getSudoku(sudokuIdNumber);
        setContentView(R.layout.sudoku_game_single_lobby);
        sudokuBoardView = findViewById(R.id.sudoku_grip_single_lobby);
        won_cnt_single_lobby = findViewById(R.id.won_cnt_single_lobby);
        last_won_single_lobby = findViewById(R.id.last_won_single_lobby);
        sudokuBoardView.setNotifyPlayers(() -> {
            String request = CommandInterpreter.createWinningRequest(lobbyNumber, Utils.loadOwnerPlayer());
            client.sendRequest(request);
        });
        sudokuBoardView.setCellsSet(board.getCellsSet());
        sudokuBoardView.setNotifyWin(() -> {
            db.setWinSudoku(sudokuIdNumber);
            winning_clar.setTextSize(20);
            winning_clar.setTextColor(Color.RED);
            winning_clar.setText("Congratulations!!!");
        });
        configClient();
        configNumberButtons();
        layoutButtons = findViewById(R.id.linearLayoutSudoku_single_lobby);
        sudokuBoardView.setNotifySize((int width) -> {
            int numberButtonWidth = (width - 20) / 10;
            int padding = (width - numberButtonWidth * 9) / 2;
            layoutButtons.setPadding(padding, 20, padding, 0);
            System.out.println(width + " " + numberButtonWidth);
            for (int i = 0; i < 9; ++i) {
                ViewGroup.LayoutParams params = number_button[i].getLayoutParams();
                params.width = numberButtonWidth;
                number_button[i].setLayoutParams(params);
                number_button[i].setTextSize(34);
                number_button[i].setPadding(numberButtonWidth / 3, 3, 0, 3);
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return
                super.onTouchEvent(event);
    }
    private void configNumberButtons() {
        int numberButtonWidth = (900 - 40) / 10;
        System.out.println(numberButtonWidth);
        number_button = new TextView[9];
        for (int i = 0; i < 9; ++i) {
            int id = getResources().getIdentifier("button"+(i + 1) + "_single_lobby", "id", getPackageName());
            number_button[i] = findViewById(id);
            int finalI = i;
            number_button[i].setOnClickListener(view -> selectInsertNumber(finalI + 1));
        }
        Button redo_button = findViewById(R.id.redo_button_single_lobby);
        Button clear_button = findViewById(R.id.clear_button_single_lobby);
        Button home_button = findViewById(R.id.home_button_single_lobby);
        Button erase_button = findViewById(R.id.erase_sudoku_button_single_lobby);
        winning_clar = findViewById(R.id.winning_clar_single_lobby);
        redo_button.setOnClickListener(view -> redo());
        erase_button.setOnClickListener(view -> eraseCell());
        clear_button.setOnClickListener(view -> clear());
        home_button.setOnClickListener(view -> startActivity(new Intent(this, StartActivity.class)));
    }
    private void selectInsertNumber(int number) {
        System.err.println("BUTTON WAS PRESSED");
        System.err.println(number);
        sudokuBoardView.setInsertNumber(number);
    }
    private void redo()  {
        sudokuBoardView.redo();
    }
    private void clear() {
        sudokuBoardView.clear();
    }
    private void eraseCell() { sudokuBoardView.eraseCell(); }
    private void configClient() {
        client.setReadingCallback((String line) -> {
            Command command = CommandInterpreter.parseLine(line);
            if (command != null) {
                if (command.getCommandType() == Command.Type.PLAYER_WIN) {
                    runOnUiThread(() -> {
                        System.out.println(command.getPlayer().getNickname());
                        last_won_single_lobby.setText(command.getPlayer().getNickname());
                        ++winningPlayer;
                        won_cnt_single_lobby.setText(winningPlayer + " / " + actPlayersNumber);
                    });
                }
            }
        });
    }

}
