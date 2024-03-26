package com.gamecodeschool.snake.buttons;
import android.graphics.Rect;

public class PauseButton {
    private Rect bounds;
    private Runnable onPausePressed;

    public PauseButton(Rect bounds, Runnable onPausePressed) {
        this.bounds = bounds;
        this.onPausePressed = onPausePressed;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void onPressed() {
        if (onPausePressed != null) {
            onPausePressed.run();
        }
    }
}
