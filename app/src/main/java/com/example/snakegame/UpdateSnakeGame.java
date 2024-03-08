package com.example.snakegame;

public class UpdateSnakeGame {
    private SnakeGame snakeGame;

    public UpdateSnakeGame(SnakeGame snakeGame){
        this.snakeGame = snakeGame;
    }

    public void update(int mEat_ID) {
        snakeGame.mSnake.move();

        if (snakeGame.mSnake.checkDinner(snakeGame.mApple.getLocation())) {
            snakeGame.mApple.spawn();

            snakeGame.mScore = snakeGame.mScore + 1;

            snakeGame.mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        if (snakeGame.mSnake.detectDeath()) {
            snakeGame.mSP.play(snakeGame.mCrashID, 1, 1, 0, 0, 1);

            snakeGame.mPaused = true;
            snakeGame.mNewGame = true;
        }

    }

    public boolean updateRequired() {

        final long TARGET_FPS = 10;
        final long MILLIS_PER_SECOND = 1000;

        if(snakeGame.mNextFrameTime <= System.currentTimeMillis()){
            snakeGame.mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;
            return true;
        }

        return false;
    }

    public void update() {
        update(snakeGame.mEat_ID);
    }

}
