package com.gamecodeschool.snake.mechanics;

public interface GameStateChangeListener {
    void onGamePaused();
    void onGameResumed();
}