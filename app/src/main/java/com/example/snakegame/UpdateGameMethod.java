package com.example.snakegame;

import android.graphics.Color;
import android.graphics.Typeface;

public class UpdateGameMethod {
    private SnakeGame snakeGame;

    public UpdateGameMethod(SnakeGame snakeGame){
        this.snakeGame = snakeGame;
    }

    public void GameDetails(){
        snakeGame.mCanvas.drawColor(Color.argb(255, 155, 184, 237));
        snakeGame.mPaint.setColor(Color.argb(255, 255, 255, 255));
        snakeGame.mPaint.setTextSize(120);

        snakeGame.mPaint.setColor(Color.argb(255, 25, 25, 112));
        snakeGame.mCanvas.drawText("" + snakeGame.mScore, 20, 120, snakeGame.mPaint);

        snakeGame.mPaint.setTextSize(60);
        snakeGame.mCanvas.drawText("Danica Galang & Isabel Santoyo-Garcia", 1150, 85, snakeGame.mPaint);

        if(!snakeGame.mPaused){
            snakeGame.mPaint.setColor(Color.argb(255, 25, 25, 112));
            snakeGame.mPaint.setTextSize(60);
            snakeGame.mCanvas.drawText("Pause", 20, 200, snakeGame.mPaint);

        }

        snakeGame.drawApple.draw(snakeGame.mCanvas,snakeGame.mPaint);
        snakeGame.drawSnake.draw(snakeGame.mCanvas, snakeGame.mPaint);

    }

    public void PauseScreenText(){
        snakeGame.mPaint.setColor(Color.argb(255, 96, 130, 182));
        snakeGame.mPaint.setTextSize(250);

        snakeGame.mCanvas.drawText(snakeGame.getResources().
                        getString(R.string.tap_to_play),
                200, 700, snakeGame.mPaint);
        snakeGame.mPaint.setTypeface(Typeface.DEFAULT_BOLD);

    }

    protected boolean isTouchWithinPauseText(int touchX, int touchY) {
        int pauseTextLeft = 20;
        int pauseTextTop = 140;
        int pauseTextRight = pauseTextLeft + (int) snakeGame.mPaint.measureText("Pause");
        int pauseTextBottom = pauseTextTop + 60;

        return touchX >= pauseTextLeft && touchX <= pauseTextRight &&
                touchY >= pauseTextTop && touchY <= pauseTextBottom;
    }
}
