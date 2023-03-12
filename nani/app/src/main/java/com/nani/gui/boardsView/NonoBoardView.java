package com.nani.gui.boardsView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.nani.engine.game.nonogram.Field;
import com.nani.engine.game.nonogram.FieldsSet;
import java.util.List;

public class NonoBoardView extends genericBoardsView {
    private FieldsSet fields;
    private int maxDescRowsSize, maxDescColsSize;
    private float rowsDescOffset;
    private float colsDescOffset;
    private float bigLineWidth;
    private float fieldTextOffsetLeft;
    private float fieldTextOffsetUp;
    private float fieldLetterOffsetLeft;
    private float fieldSize;
    private float mScaleFactor;
    private float borderLineWidth;
    private final Paint linePaint, recPaint, descBackground, fieldDescPaint, descError;
    private final ScaleGestureDetector mScaleDetector;
    private final float[] mLastTouchY, mLastTouchX;
    private float mOffsetX, mOffsetY;
    private Field startField;
    private boolean win = false, haveTouched = false;

    public NonoBoardView(Context context) {
        this(context, null);
    }
    public NonoBoardView(Context context, AttributeSet attr) {
        super(context, attr);
        linePaint = new Paint();
        recPaint = new Paint();
        descBackground = new Paint();
        fieldDescPaint = new Paint();
        descBackground.setColor(Color.rgb(247, 226, 200));
        mScaleFactor = 1f;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        descError = new Paint();
        descError.setColor(Color.RED);
        mLastTouchX = new float[2];
        mLastTouchY = new float[2];
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float oldScale = mScaleFactor;
            mScaleFactor = Math.max(0.52f, Math.min(5f, detector.getScaleFactor() * mScaleFactor));
            float centerX = detector.getFocusX();
            float centerY = detector.getFocusY();
            mOffsetX = -((-mOffsetX + centerX) * (mScaleFactor / oldScale) - centerX) / mScaleFactor * oldScale;
            mOffsetY = -((-mOffsetY + centerY) * (mScaleFactor /oldScale) - centerY) / mScaleFactor * oldScale;
            invalidate();
            return true;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
    private void configParam(int width, int height) {
        fieldSize = Math.min(1.0f * width / (fields.getColumnsNumber() + maxDescRowsSize), 1.0f * height / (fields.getRowsNumber() + maxDescColsSize));
        final float fieldDescTextSize = fieldSize * 0.7f;
        rowsDescOffset = maxDescRowsSize * fieldSize;
        colsDescOffset = maxDescColsSize * fieldSize;
        fieldDescPaint.setTextSize(fieldDescTextSize);
        fieldLetterOffsetLeft = (fieldSize - fieldDescPaint.measureText("0")) / 2;
        fieldTextOffsetLeft = (fieldSize - fieldDescPaint.measureText("10")) / 2;
        fieldTextOffsetUp = (fieldSize - fieldDescTextSize) / 2;
        bigLineWidth = fieldSize / 20;
        borderLineWidth = 3 * bigLineWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        assert fields != null;
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        int width = getWidth() - getPaddingRight();
        int height = getHeight() - getPaddingBottom();
        configParam(width, height);
        drawDesc(canvas);
        drawFields(canvas);
        if (win) {
            notifyWin.push();
        }

        canvas.restore();
    }
    private void drawInvalid(Canvas canvas) {
        for (int i = 0; i < fields.getRowsNumber(); ++i)
            if (!fields.isValidRow(i)) {
                canvas.drawRect(mOffsetX, mOffsetY + colsDescOffset + fieldSize * i, mOffsetX + rowsDescOffset, mOffsetY + colsDescOffset + fieldSize * (i + 1), descError);
            }
        for (int i = 0; i < fields.getColumnsNumber(); ++i)
            if (!fields.isValidColumn(i)) {
                canvas.drawRect(mOffsetX + rowsDescOffset + fieldSize * i, mOffsetY, mOffsetX + rowsDescOffset + fieldSize * (i + 1), mOffsetY + colsDescOffset, descError);
            }
    }
    private void drawFields(Canvas canvas) {
        for (int i = 0; i < fields.getRowsNumber(); ++i)
            for (int j = 0; j < fields.getColumnsNumber(); ++j) {
                Field f = fields.getField(i, j);
                if (f.isFilled()) {
                    canvas.drawRoundRect(mOffsetX + rowsDescOffset + fieldSize * j + bigLineWidth * 2, mOffsetY + colsDescOffset + fieldSize * i + bigLineWidth * 2,
                            mOffsetX + rowsDescOffset + fieldSize * (j + 1) - bigLineWidth * 2, mOffsetY + colsDescOffset + fieldSize * (i + 1) - bigLineWidth * 2,
                            fieldSize / 8, fieldSize / 8, recPaint);
                }
            }
    }
    private void drawDesc(Canvas canvas) {
        canvas.drawRect(mOffsetX, mOffsetY + colsDescOffset, mOffsetX + rowsDescOffset, mOffsetY + colsDescOffset + fieldSize * fields.getRowsNumber(), descBackground);
        canvas.drawRect(mOffsetX + rowsDescOffset, mOffsetY, mOffsetX + rowsDescOffset + fields.getColumnsNumber() * fieldSize, mOffsetY + colsDescOffset, descBackground);
        if (!fields.isValid()) {
            drawInvalid(canvas);
        }
        canvas.drawRect(mOffsetX, mOffsetY, mOffsetX + borderLineWidth, mOffsetY + colsDescOffset + fieldSize * fields.getRowsNumber(), recPaint); // left main border
        canvas.drawRect(mOffsetX + rowsDescOffset - borderLineWidth / 2 + fieldSize * fields.getColumnsNumber(), mOffsetY, mOffsetX + rowsDescOffset + borderLineWidth / 2 + fieldSize * fields.getColumnsNumber(), mOffsetY + colsDescOffset + fieldSize * fields.getRowsNumber(), recPaint); // right main border
        canvas.drawRect(mOffsetX, mOffsetY + colsDescOffset - borderLineWidth / 2 + fieldSize * fields.getRowsNumber(), mOffsetX + rowsDescOffset + fieldSize * fields.getColumnsNumber(), mOffsetY + colsDescOffset + borderLineWidth / 2 + fieldSize * fields.getRowsNumber(), recPaint);
        canvas.drawRect(mOffsetX, mOffsetY, mOffsetX + rowsDescOffset + fieldSize * fields.getColumnsNumber(), mOffsetY + borderLineWidth, recPaint);
        for (int i = 0; i < fields.getRowsNumber(); ++i) {
            canvas.drawLine(mOffsetX, mOffsetY + colsDescOffset + fieldSize * i, mOffsetX + rowsDescOffset + fields.getColumnsNumber() * fieldSize, mOffsetY + colsDescOffset + fieldSize * i, linePaint);
            List<Integer> rowDesc = fields.getRowsDescription().get(i);
            for (int j = 0; j < rowDesc.size(); ++j) {
                canvas.drawText(String.valueOf(rowDesc.get(j)),
                        mOffsetX + (rowDesc.get(j) > 9 ? fieldTextOffsetLeft : fieldLetterOffsetLeft) + rowsDescOffset - fieldSize * (rowDesc.size() - j),
                        mOffsetY + fieldTextOffsetUp + colsDescOffset + fieldSize * i - fieldDescPaint.ascent(),
                        fieldDescPaint
                );
            }
        }
        for (int i = 0; i < fields.getColumnsNumber(); ++i) {
            canvas.drawLine(mOffsetX + rowsDescOffset + fieldSize * i, mOffsetY, mOffsetX + rowsDescOffset + fieldSize * i, mOffsetY + colsDescOffset + fields.getRowsNumber() * fieldSize, linePaint);
            List<Integer> colDesc = fields.getColsDescription().get(i);
            for (int j = 0; j < colDesc.size(); ++j) {
                canvas.drawText(String.valueOf(colDesc.get(j)),
                        mOffsetX + (colDesc.get(j) > 9 ? fieldTextOffsetLeft : fieldLetterOffsetLeft) + rowsDescOffset + fieldSize * i,
                        mOffsetY + fieldTextOffsetUp + colsDescOffset - fieldSize * (colDesc.size() - j) - fieldDescPaint.ascent(),
                        fieldDescPaint
                );
            }
        }
        for (int i = 0; i < maxDescColsSize; ++i) {
            canvas.drawLine(mOffsetX + rowsDescOffset, mOffsetY + fieldSize * i, mOffsetX + rowsDescOffset + fields.getColumnsNumber() * fieldSize, mOffsetY + fieldSize * i, linePaint);
        }
        for (int i = 0; i < maxDescRowsSize; ++i) {
            canvas.drawLine(mOffsetX+ fieldSize * i, mOffsetY + colsDescOffset, mOffsetX + fieldSize * i, mOffsetY + colsDescOffset + fields.getRowsNumber() * fieldSize, linePaint);
        }
        for (int i = 0; i < fields.getRowsNumber(); i += 5) {
            canvas.drawRect(mOffsetX, mOffsetY + colsDescOffset - bigLineWidth / 2 + i * fieldSize, mOffsetX + rowsDescOffset + fieldSize * fields.getColumnsNumber(), mOffsetY + colsDescOffset + bigLineWidth / 2 + i * fieldSize, recPaint);
        }
        for (int i = 0; i < fields.getColumnsNumber(); i += 5) {
            canvas.drawRect(mOffsetX + rowsDescOffset - bigLineWidth / 2 + i * fieldSize, mOffsetY, mOffsetX + rowsDescOffset + bigLineWidth / 2 + i * fieldSize, mOffsetY + colsDescOffset + fieldSize * fields.getRowsNumber(), recPaint);
        }
    }
    public void setFieldsSet(FieldsSet fields) {
        this.fields = fields;
        maxDescRowsSize = 0;
        maxDescColsSize = 0;

        for (List<Integer> rowsDesc : fields.getRowsDescription())
            maxDescRowsSize = Math.max(maxDescRowsSize, rowsDesc.size());
        for (List<Integer> colsDesc : fields.getColsDescription())
            maxDescColsSize = Math.max(maxDescColsSize, colsDesc.size());
        postInvalidate();
    }
    private Field getFieldByCoordinates(int x, int y) {
        int row = (int)(x - (mOffsetX + rowsDescOffset) * mScaleFactor);
        if (row < 0) return null;
        row /= fieldSize * mScaleFactor;
        int col = (int)(y - (mOffsetY + colsDescOffset) * mScaleFactor);
        if (col < 0) return null;
        col /= fieldSize * mScaleFactor;
        if (row < 0 || col < 0 || col >= fields.getRowsNumber() || row >= fields.getColumnsNumber())
            return null;
        return fields.getField(col, row);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        mScaleDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started (for dragging)
                if (pointerIndex < 2) {
                    mLastTouchX[pointerIndex] = x;
                    mLastTouchY[pointerIndex] = y;
                }
                if (event.getPointerCount() == 1) {
                    startField = getFieldByCoordinates((int)x, (int)(y));
                }
                break;
            }
            case MotionEvent.ACTION_HOVER_MOVE:
            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                // Calculate the distance moved
                for (int i = 0; i < 2; ++i) {
                    pointerIndex = i;
                    float dx = event.getX(pointerIndex) - mLastTouchX[pointerIndex];
                    float dy = event.getY(pointerIndex) - mLastTouchY[pointerIndex];
                    mLastTouchX[pointerIndex] = event.getX(pointerIndex);
                    mLastTouchY[pointerIndex] = event.getY(pointerIndex);
                    if (event.getPointerCount() > 1) {
                        mOffsetX += dx / 2;
                        mOffsetY += dy / 2;
                    } else {
                        break;
                    }
                    mOffsetX = Math.min(fieldSize * fields.getColumnsNumber(), mOffsetX);
                    mOffsetX = Math.max(-fieldSize * fields.getColumnsNumber(), mOffsetX);
                    mOffsetY = Math.min(fieldSize * fields.getRowsNumber(), mOffsetY);
                    mOffsetY = Math.max(-fieldSize * fields.getRowsNumber(), mOffsetY);
                }
                if (event.getPointerCount() > 1) {
                    invalidate();
                    startField = null;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                if (event.getPointerCount() == 1) {
                    Field endField = getFieldByCoordinates((int)x, (int)(y));
                    if (endField == startField && endField != null) {
                        addMove(new Pair<>(endField, endField.getContent()));
                        haveTouched = true;
                        if (endField.isFilled()) {
                            endField.setEmpty();
                        } else {
                            endField.setFilled();
                        }
                        invalidate();
                    }
                }
                if (fields.isCompleted()) {
                    win = true;
                    wining();
                }
                break;
            }
        }
        return true;
    }
    @Override
    public void clear() {
        if (fields.isCompleted() && !haveTouched) {
            fields.setAllEmpty();
            postInvalidate();
        } else {
            super.clear();
        }
    }
}
