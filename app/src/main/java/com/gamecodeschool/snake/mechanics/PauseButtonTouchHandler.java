package com.gamecodeschool.snake.mechanics;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.gamecodeschool.snake.interfaces.GameStateChangeListener;

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
            // Toggle the game state between paused and resumed
            gameStateChangeListener.toggleGameState();
            return true;
        }
        return false;
    }
}
