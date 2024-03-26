package com.gamecodeschool.snake.mechanics;

import android.graphics.Rect;
import android.view.MotionEvent;

public class PauseButtonTouchHandler extends TouchHandler {
    private Rect pauseButtonRect;
    private GameStateChangeListener gameStateChangeListener;

    public PauseButtonTouchHandler(Rect pauseButtonRect, GameStateChangeListener listener) {
        this.pauseButtonRect = pauseButtonRect;
        this.gameStateChangeListener = listener;
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (pauseButtonRect.contains(x, y) && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameStateChangeListener != null) {
                // Assuming you toggle the game's paused state elsewhere
                gameStateChangeListener.onGamePaused();
            }
            return true;
        }
        return false;
    }
}
