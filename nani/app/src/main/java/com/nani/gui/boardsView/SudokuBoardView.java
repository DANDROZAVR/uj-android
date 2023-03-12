package com.nani.gui.boardsView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;

import com.nani.engine.game.sudoku.Cell;
import com.nani.engine.game.sudoku.CellsSet;


public class SudokuBoardView extends genericBoardsView {
    private CellsSet cells;
    private Cell touchingCell;
    private final Paint cellPaint;
    private final Paint cellReadOnlyPaint;
    private final Paint touchingCellColor;
    private final Paint touchingCellRec;
    private final Paint linePaint;
    private final Paint squaresPaint;
    private int gridOffsetTop, gridOffsetLeft;
    private int cellWidth, cellHeight;
    private int cellTextOffsetLeft, cellTextOffsetTop;
    private float cellTextSize = -1;
    private int insertNumber;


    public SudokuBoardView(Context context) {
        this(context, null);
    }
    public SudokuBoardView(Context context, AttributeSet attr) {
        super(context, attr);
        cellPaint = new Paint();
        linePaint = new Paint();
        cellReadOnlyPaint = new Paint();
        squaresPaint = new Paint();
        touchingCellColor = new Paint();
        touchingCellRec = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int DEFAULT_GRIP_SIZE = 1000;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = DEFAULT_GRIP_SIZE, height = DEFAULT_GRIP_SIZE;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        if (width != height) {
            width = Math.min(width, height);
            height = Math.min(width, height);
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(DEFAULT_GRIP_SIZE, widthSize);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(DEFAULT_GRIP_SIZE, heightSize);
        }
        setMeasuredDimension(width, height);
    }
    private void calculateMetaLength(int width, int height) {
        gridOffsetTop = getPaddingTop();
        gridOffsetLeft = getPaddingLeft();
        cellWidth = (width - gridOffsetLeft * 2) / cells.getSize();
        cellHeight = (height - gridOffsetTop * 2) / cells.getSize();
        if (cellTextSize == -1) { //then default
            cellTextSize = cellHeight * 0.7f;
        }
        cellPaint.setTextSize(cellTextSize);
        cellReadOnlyPaint.setTextSize(cellTextSize);
        cellTextOffsetLeft = (int)(cellWidth - cellPaint.measureText("0")) / 2;
        cellTextOffsetTop = (int)(cellHeight - cellTextSize) / 2;
        cellPaint.setTextSize(cellTextSize);
        linePaint.setAntiAlias(true);
        int v = Color.argb(200, 173, 216, 230); // 15128749
        int v2 = Color.argb(200, 20, 20, 250);
        int v3 = Color.argb(200, 220, 220, 220);
        System.err.println(v);
        touchingCellColor.setColor(v);
        cellPaint.setColor(v2);
        touchingCellRec.setColor(v3);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth() - getPaddingRight();
        int height = getHeight() - getPaddingBottom();
        calculateMetaLength(width, height);
        assert cells != null;
        int sz = cells.getSize();
        for (int row = 0; row < sz; ++row)
            for (int col = 0; col < sz; ++col) {
                Cell cell = cells.getCell(row, col);
                int cellTopX = col * cellWidth + gridOffsetLeft;
                int cellLeftY = row * cellHeight + gridOffsetTop;
                int value = cell.getValue();
                if (value != 0) {
                    Paint actualCellPaint = cell.isEditable() ? cellPaint : cellReadOnlyPaint;
                    canvas.drawText(String.valueOf(value),
                            cellTextOffsetLeft + cellTopX + getPaddingLeft(),
                            cellTextOffsetTop + cellLeftY - cellPaint.ascent(),
                            actualCellPaint
                            );
                }
                if (cell.isEditable() && touchingCell == cell) {
                    float y = gridOffsetTop + row * cellHeight;
                    canvas.drawRect(gridOffsetLeft, y, width, y + cellHeight, touchingCellRec);
                    float x = gridOffsetLeft + col * cellWidth;
                    canvas.drawRect(x, gridOffsetTop, x + cellWidth, height, touchingCellRec);
                    canvas.drawRect(cellTopX, cellLeftY, cellTopX + cellWidth, cellLeftY + cellHeight, touchingCellColor);
                }
            }
        drawBorders(width, height, canvas);
    }
    private void drawBorders(int width, int height, Canvas canvas) {
        int sz = cells.getSize();
        for (int row = 0; row < sz + 1; ++row) {
            float y = gridOffsetTop + row * cellHeight;
            canvas.drawLine(gridOffsetLeft, y, width, y, linePaint); // not width - offset?
        }
        for (int col = 0; col < sz + 1; ++col) {
            float x = gridOffsetLeft + col * cellWidth;
            canvas.drawLine(x, gridOffsetTop, x, height, linePaint);
        }
        final float squaresLineWidth = 12f;
        for (int row = 0; row <= 9; row += 3) {
            float y = gridOffsetTop + row * cellHeight;
            canvas.drawRect(gridOffsetLeft, y - squaresLineWidth / 3, width, y + squaresLineWidth / 2, squaresPaint);
        }
        for (int col = 0; col <= 9; col += 3) {
            float x = gridOffsetLeft + col * cellWidth;
            canvas.drawRect(x - squaresLineWidth / 2, gridOffsetTop, x + squaresLineWidth / 2, height, linePaint);
        }
        if (notifySize != null) {
            notifySize.push(width);
        }
    }
    private Cell getCellByCoordinates(int x, int y) { // or NULL
        int row = (x - gridOffsetLeft) / cellWidth;
        int col = (y - gridOffsetTop) / cellHeight;
        int sz = cells.getSize();
        if (row < 0 || col < 0 || row >= sz || col >= sz)
            return null;
        return cells.getCell(col, row);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (notifyTouch != null) {
            notifyTouch.push();
        }
        int x = (int)event.getX();
        int y = (int)event.getY();
        Cell actualTouchingCell = getCellByCoordinates(x, y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                touchingCell = null;
                postInvalidate(); // call onDraw (refresh view) asc
            case MotionEvent.ACTION_MOVE: // continue pressing
            case MotionEvent.ACTION_DOWN: // start pressing
                if (touchingCell == actualTouchingCell || readOnlyMode)
                    break;
                touchingCell = actualTouchingCell;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchingCell = null;
                if (actualTouchingCell != null && actualTouchingCell.isEditable()) {
                    if (!readOnlyMode && event.getEventTime() - event.getDownTime() < 200 && insertNumber != 0) {
                        addMove(new Pair<>(actualTouchingCell, actualTouchingCell.getValue()));
                        if (insertNumber > 0) {
                            actualTouchingCell.setValue(insertNumber);
                        } else if (insertNumber == -1) {
                            actualTouchingCell.setValue(0);
                        }
                        System.out.println("changed value");
                    }
                    System.out.println(cells.isValid() + " | " + cells.isComplete());
                    if (cells.isComplete()) {
                        System.err.println("WE'VE DONE IT");
                        readOnlyMode = true;
                        wining();
                    }
                }
                invalidate();
        }
        return true;
    }

    public void setCellsSet(CellsSet cells) {
        this.cells = cells;
        System.err.println("CEllS WERE SET");
        postInvalidate();
    }
    public void setInsertNumber(int number) {
        this.insertNumber = number;
    }
    public void eraseCell() {
        insertNumber = -1;
    }
}
