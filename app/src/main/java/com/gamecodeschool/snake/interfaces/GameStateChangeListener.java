package com.gamecodeschool.snake.interfaces;

public interface GameStateChangeListener {
    void onGamePaused();
    void onGameResumed();
    void toggleGameState();
}
