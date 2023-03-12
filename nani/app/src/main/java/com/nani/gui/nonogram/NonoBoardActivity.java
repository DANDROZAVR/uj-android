package com.nani.gui.nonogram;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.nani.R;
import com.nani.data.DBNonogram;
import com.nani.engine.game.nonogram.NonoBoard;
import com.nani.gui.MainActivity;
import com.nani.gui.boardsView.NonoBoardView;

public class NonoBoardActivity extends MainActivity {
    public static final String nonoId = "nonoId";
    private DBNonogram db;
    private NonoBoard board;
    private NonoBoardView sudokuBoardView;
    private PopupWindow exit_popup;
    private TextView winning_clar;
    private boolean wasSaved = false;

    public NonoBoardActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int nonoIdNumber = getIntent().getIntExtra(nonoId, 0);
        db = new DBNonogram(getApplicationContext());
        board = db.getNonogram(nonoIdNumber);
        setContentView(R.layout.nanogram_game);
        sudokuBoardView = findViewById(R.id.nonoboard_grip);
            sudokuBoardView.setFieldsSet(board.getFieldsSet());
        winning_clar = findViewById(R.id.winningClarNonogram);
        Button redo_button = findViewById(R.id.redo_button_nonogram);
        Button clear_button = findViewById(R.id.clear_button_nonogram);
        redo_button.setOnClickListener(view -> redo());
        clear_button.setOnClickListener(view -> clear());
        sudokuBoardView.setNotifyWin(() -> {
            if (!wasSaved) {
                wasSaved = true;
                db.setWinNono(board.getId());
            }
            winning_clar.setTextSize(20);
            winning_clar.setTextColor(Color.RED);
            winning_clar.setText("Congratulations!!!");
        });
        sudokuBoardView.setNotifyTouch(this::clearPopup);
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
    private void redo()  {
        sudokuBoardView.redo();
    }
    private void clear() {
        sudokuBoardView.clear();
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
        if (board.getState() != 2) {
            db.saveStateNonoboard(board);
        }
        NotSaveProgressAndReturn();
    }
    public void NotSaveProgressAndReturn() {
        clearPopup();
        finish();
    }
}
