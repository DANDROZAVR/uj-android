package com.nani.gui.sudoku;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.example.nani.R;
import com.nani.data.DBSudoku;
import com.nani.engine.game.sudoku.SudokuBoard;
import com.nani.gui.MainActivity;
import com.nani.gui.StartActivity;
import com.nani.gui.boardsView.SudokuBoardView;


public class SudokuBoardActivity extends MainActivity {
    public static final String sudokuId = "sudokuId";
    private SudokuBoardView sudokuBoardView;
    private SudokuBoard board;
    private DBSudoku db;
    private TextView winning_clar;
    private TextView[] number_button;
    private LinearLayout layoutButtons;
    private PopupWindow exit_popup;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int sudokuIdNumber = getIntent().getIntExtra(sudokuId, 0);
        db = new DBSudoku(getApplicationContext());
        board = db.getSudoku(sudokuIdNumber);
        setContentView(R.layout.soduku_game);
        sudokuBoardView = findViewById(R.id.sudoku_grip);
        sudokuBoardView.setCellsSet(board.getCellsSet());
        sudokuBoardView.setNotifyWin(() -> {
            db.setWinSudoku(sudokuIdNumber);
            winning_clar.setTextSize(20);
            winning_clar.setTextColor(Color.RED);
            winning_clar.setText("Congratulations!!!");
        });
        configNumberButtons();
        layoutButtons = findViewById(R.id.linearLayoutSudoku);
        sudokuBoardView.setNotifyTouch(this::clearPopup);
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
    public void clearPopup() {
        if (exit_popup != null) {
            exit_popup.dismiss();
            sudokuBoardView.setAlpha(1.0F);
            exit_popup = null;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clearPopup();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (exit_popup == null) {
            sudokuBoardView.setAlpha(0.3F);
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View popupView = layoutInflater.inflate(R.layout.sudoku_game_back_popup, null);
            exit_popup = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            exit_popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exit_popup.setOutsideTouchable(true);
            exit_popup.showAtLocation(sudokuBoardView, Gravity.CENTER, 0, 10);
            Button buttonPopupYes = popupView.findViewById(R.id.buttonExitPopupYes);
            Button buttonPopupNo = popupView.findViewById(R.id.buttonExitPopupNo);
            buttonPopupYes.setOnClickListener(view -> saveProgressAndReturn());
            buttonPopupNo.setOnClickListener(view -> NotSaveProgressAndReturn());
        }
    }

    public void saveProgressAndReturn() {
        db.saveStateSudoku(board);
        NotSaveProgressAndReturn();
    }
    public void NotSaveProgressAndReturn() {
        clearPopup();
        finish();
    }
    private void configNumberButtons() {
        int numberButtonWidth = (900 - 40) / 10;
        System.out.println(numberButtonWidth);
        number_button = new TextView[9];
        for (int i = 0; i < 9; ++i) {
            int id = getResources().getIdentifier("button"+(i + 1), "id", getPackageName());
            number_button[i] = findViewById(id);
            int finalI = i;
            number_button[i].setOnClickListener(view -> selectInsertNumber(finalI + 1));
        }
        Button redo_button = findViewById(R.id.redo_button);
        Button clear_button = findViewById(R.id.clear_button);
        Button home_button = findViewById(R.id.home_button);
        Button erase_button = findViewById(R.id.erase_sudoku_button);
        winning_clar = findViewById(R.id.winning_clar);
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

}
