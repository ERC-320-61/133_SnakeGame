package com.gamecodeschool.snake.mechanics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class DebugTouchDrawer {
    private PointF lastTouchPoint;
    private Paint paint;

    public DebugTouchDrawer() {
        lastTouchPoint = null; // No touch point initially
        paint = new Paint();
        paint.setColor(0xFFFF0000); // Red color for debugging
        paint.setStyle(Paint.Style.FILL);
    }

    public void setTouchPoint(float x, float y) {
        lastTouchPoint = new PointF(x, y);
    }

    public void draw(Canvas canvas) {
        if (lastTouchPoint != null) {
            // Draw a circle at the touch point for debugging
            canvas.drawCircle(lastTouchPoint.x, lastTouchPoint.y, 50, paint); // 50px radius
        }
    }
}
