package com.gamecodeschool.snake.buttons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.gamecodeschool.snake.R;
import com.gamecodeschool.snake.mechanics.DirectionalControl;

public class dpad {

    // Debug flag to enable drawing of hit areas
    private boolean debugMode = true;
    private Bitmap dpadBitmap;
    private Rect upHitArea, downHitArea, leftHitArea, rightHitArea;
    private int dpadSize;
    private Point dpadPosition;

    public dpad(Context context, Point screenSize) {

        // Load the D-pad bitmap
        dpadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpad);

        // Define the size of the D-pad
        dpadSize = 200; // Adjust the size as needed
        int padding = 80; // Increase bottom padding to raise the DPad higher

        // Initialize the hit areas for each direction based on the new dpadPosition
        int buttonSize = dpadSize / 3; // Assuming the D-pad has 3 equally sized areas

        // Adjust dpadPosition to account for the increased padding, moving the DPad up
        dpadPosition = new Point(screenSize.x - dpadSize - padding, screenSize.y - dpadSize - padding);


        upHitArea = new Rect(dpadPosition.x + buttonSize, dpadPosition.y, dpadPosition.x + 2 * buttonSize, dpadPosition.y + buttonSize);
        downHitArea = new Rect(dpadPosition.x + buttonSize, dpadPosition.y + 2 * buttonSize, dpadPosition.x + 2 * buttonSize, dpadPosition.y + dpadSize);
        leftHitArea = new Rect(dpadPosition.x, dpadPosition.y + buttonSize, dpadPosition.x + buttonSize, dpadPosition.y + 2 * buttonSize);
        rightHitArea = new Rect(dpadPosition.x + 2 * buttonSize, dpadPosition.y + buttonSize, dpadPosition.x + dpadSize, dpadPosition.y + 2 * buttonSize);

        // Scale the bitmap to the dpadSize
        dpadBitmap = Bitmap.createScaledBitmap(dpadBitmap, dpadSize, dpadSize, false);
    }

    // Method to draw the DPad and its hit areas
    public void draw(Canvas canvas, Paint paint) {
        // Draw the DPad bitmap
        canvas.drawBitmap(dpadBitmap, dpadPosition.x, dpadPosition.y, paint);

        // If debug mode is enabled, draw the hit areas
        if (debugMode) {
            // Use a semi-transparent paint for the hit areas
            Paint debugPaint = new Paint();
            debugPaint.setStyle(Paint.Style.FILL);
            debugPaint.setColor(Color.argb(128, 255, 0, 0)); // Semi-transparent red

            // Draw the hit areas
            canvas.drawRect(upHitArea, debugPaint);
            canvas.drawRect(downHitArea, debugPaint);
            canvas.drawRect(leftHitArea, debugPaint);
            canvas.drawRect(rightHitArea, debugPaint);
        }
    }

    public boolean handleTouch(MotionEvent motionEvent, DirectionalControl directionalControl) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        boolean handled = false;

        if (upHitArea.contains(x, y)) {
            directionalControl.onUpPressed();
            handled = true;
        } else if (downHitArea.contains(x, y)) {
            directionalControl.onDownPressed();
            handled = true;
        } else if (leftHitArea.contains(x, y)) {
            directionalControl.onLeftPressed();
            handled = true;
        } else if (rightHitArea.contains(x, y)) {
            directionalControl.onRightPressed();
            handled = true;
        }

        return handled;
    }

}
