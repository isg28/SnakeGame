package com.example.snakegame;

public class UpdateSnakeGame {
    private SnakeGame snakeGame;

    public UpdateSnakeGame(SnakeGame snakeGame){
        this.snakeGame = snakeGame;
    }

    public void update(int mEat_ID) {

        // Move the snake
        snakeGame.mSnake.move();

        // Did the head of the snake eat the apple?
        if (snakeGame.mSnake.checkDinner(snakeGame.mApple.getLocation())) {
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            snakeGame.mApple.spawn();

            // Add to  mScore
            snakeGame.mScore = snakeGame.mScore + 1;

            // Play a sound
            snakeGame.mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (snakeGame.mSnake.detectDeath()) {
            // Pause the game ready to start again
            snakeGame.mSP.play(snakeGame.mCrashID, 1, 1, 0, 0, 1);

            snakeGame.mPaused = true;
            snakeGame.mNewGame = true;
        }

    }

    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(snakeGame.mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            snakeGame.mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }

    public void update() {
        update(snakeGame.mEat_ID);
    }

}
