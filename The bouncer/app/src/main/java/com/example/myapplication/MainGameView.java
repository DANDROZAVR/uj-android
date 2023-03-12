package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MainGameView extends View {
    private int width, height;
    private int ballX, ballY, lastGoodX, lastGoodY;
    private final Paint ballPaint;
    private final int radius = 80;
    private float xAcceleration, yAcceleration;
    private boolean fingerTouched = false;
    public MainGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.height = MeasureSpec.getSize(heightMeasureSpec);
        this.ballX = width / 2;
        this.ballY = height / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ballX, ballY, radius, ballPaint);
    }

    public void tryMoveBall(float dx, float dy) {
        if (fingerTouched) {
            xAcceleration = yAcceleration = 0;
            return;
        }
        float newDx = (float)(1 / (1 + Math.pow(Math.exp(1), -dx)) - 0.5) * 8;
        float newDy = (float)(1 / (1 + Math.pow(Math.exp(1), -dy)) - 0.5) * 8;
        dx = newDx;
        dy = newDy;
        xAcceleration -= dx;
        yAcceleration += dy;
        //System.out.println(xAcceleration + " " + yAcceleration);
        if (ballX + xAcceleration < radius || ballX + xAcceleration > width - radius) {
            xAcceleration = (float)(-xAcceleration * 0.97);
        }
        if (ballY + yAcceleration < radius || ballY + yAcceleration > height - radius) {
            yAcceleration = (float)(-yAcceleration * 0.97);
        }
        xAcceleration *= 0.95;
        yAcceleration *= 0.95;
        //ballX += xAcceleration;
        //ballY += yAcceleration;
        ballX = checkBorders((int)xAcceleration + ballX, radius, width - radius);
        ballY = checkBorders((int)yAcceleration + ballY, radius, height - radius);
        if (Math.abs(xAcceleration) < 0.01 || Math.abs(yAcceleration) < 0.01) {
            xAcceleration = yAcceleration = 0;
        }
        invalidate();
    }
    private int checkBorders(int newPos, int min, int max) {
        return Math.min(max, Math.max(min, newPos));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        int fixedX = checkBorders(x, radius, width - radius);
        int fixedY = checkBorders(y, radius, height - radius);

        xAcceleration = yAcceleration = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_MOVE: // continue pressing
            case MotionEvent.ACTION_DOWN: // start pressing
                lastGoodX = x;
                lastGoodY = y;
                fingerTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                xAcceleration = (float)(-(fixedX - lastGoodX) * 3);
                yAcceleration = (float)(-(fixedY - lastGoodY) * 3);
                fingerTouched = false;
        }
        invalidate();
        if (x == fixedX)
            lastGoodX = x;
        if (y == fixedY)
            lastGoodY = y;
        ballX = fixedX;
        ballY = fixedY;
        return true;
    }
}
