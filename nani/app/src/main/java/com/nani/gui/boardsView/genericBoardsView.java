package com.nani.gui.boardsView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.nani.engine.game.nonogram.Field;
import com.nani.engine.game.sudoku.Cell;

import java.util.ArrayList;
import java.util.List;

abstract class genericBoardsView extends View {
    boolean readOnlyMode = false;
    private final List<Pair<Object, Object>> moves;
    NotifyActivity notifyWin = null, notifyTouch = null;
    NotifyActivityInt notifySize = null;
    public genericBoardsView(Context context) {
        this(context, null);
    }
    public genericBoardsView(Context context, AttributeSet attr) {
        super(context, attr);
        moves = new ArrayList<>();
    }
    public void redo() { redo(false); }
    public void redo(boolean force) {
        if ((!readOnlyMode || force) && !moves.isEmpty()) {
            Pair<Object, Object> pair = moves.get(moves.size() - 1);
            if (pair.first instanceof Cell) {
                Pair<Cell, Integer> cellPair = (Pair) pair;
                cellPair.first.setValue(cellPair.second);
            } else {
                Pair<Field, Field.Content> fieldPair = (Pair) pair;
                fieldPair.first.setContent(fieldPair.second);
            }
            moves.remove(moves.size() - 1);
            postInvalidate();
        }
    }
    public void clear() {
        while(!moves.isEmpty())
            redo(true);
    }
    public void addMove(Pair<Object, Object> p) {
        moves.add(p);
    }
    public interface NotifyActivity {
        void push();
    }
    public interface NotifyActivityInt {
        void push(int x);
    }

    protected void wining() {
        if (notifyWin != null)
            notifyWin.push();
    }
    void animationWinning() { }
    public void setNotifyWin(NotifyActivity notifyWin) { this.notifyWin = notifyWin; }
    public void setNotifySize(NotifyActivityInt notifySize) { this.notifySize = notifySize; }
    public void setNotifyTouch(NotifyActivity notifyTouch) { this.notifyTouch = notifyTouch; }
}
