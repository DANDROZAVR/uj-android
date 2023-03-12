package com.nani.gui.boardsView;

import android.content.Context;
import android.util.AttributeSet;

public class SudokuBoardViewSingleLobby extends SudokuBoardView {
    NotifyActivity notifyPlayers;
    public SudokuBoardViewSingleLobby(Context context) {
        super(context);
    }

    public SudokuBoardViewSingleLobby(Context context, AttributeSet attr) {
        super(context, attr);
    }
    public void setNotifyPlayers(NotifyActivity notifyPlayers) {
        this.notifyPlayers = notifyPlayers;
    }
    @Override
    protected void wining() {
        if (notifyWin != null) {
            notifyWin.push();
        }
        animationWinning();
        notifyPlayers.push();
    }
}
