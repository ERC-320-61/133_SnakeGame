package com.gamecodeschool.snake.interfaces;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface GameObject {
    void update();
    void draw(Canvas canvas, Paint paint);
}