package com.example.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

public class SnakeGame extends SurfaceView implements Runnable{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    protected long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    protected volatile boolean mPaused = true;

    // for playing sound effects
    protected SoundPool mSP;
    protected int mEat_ID = -1;
    protected int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    protected int mScore;

    // Objects for drawing
    protected Canvas mCanvas;
    protected SurfaceHolder mSurfaceHolder;
    protected Paint mPaint;

    // A snake ssss
    protected Snake mSnake;
    // And an apple
    protected Apple mApple;

    protected DrawApple drawApple;
    protected DrawSnake drawSnake;
    protected boolean mNewGame = true;
    private UpdateSnakeGame updateSnakeGame;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        InitializeObjects(context, blockSize);

    }

    public void InitializeObjects(Context context, int blockSize){
        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        drawApple = new DrawApple(mApple.getBitmap(), mApple.getLocation(), mApple.getBlockSize());
        drawSnake = new DrawSnake(mSnake);

        updateSnakeGame = new UpdateSnakeGame(this);

    }


    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        if(mNewGame){
            mScore = 0;
        }

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
        mNewGame = false;
    }


    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateSnakeGame.updateRequired()) {
                    updateSnakeGame.update();
                }
            }

            draw();
        }
    }

    // Do all the drawing
    public void draw() {

        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            GameDetails();

            // Draw some text while paused
            if(mPaused){
                PauseScreenText();
            }


            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    public void GameDetails(){
        // Fill the screen with a color
        mCanvas.drawColor(Color.argb(255, 155, 184, 237));
        // Set the size and color of the mPaint for the text
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(120);

        // Draw the score
        mPaint.setColor(Color.argb(255, 25, 25, 112));
        mCanvas.drawText("" + mScore, 20, 120, mPaint);

        mPaint.setTextSize(60);
        mCanvas.drawText("Danica Galang & Isabel Santoyo-Garcia", 1150, 85, mPaint);

        if(!mPaused){
            mPaint.setColor(Color.argb(255, 25, 25, 112));
            mPaint.setTextSize(60);
            mCanvas.drawText("Pause", 20, 200, mPaint);

        }

        // Draw the apple and the snake
        drawApple.draw(mCanvas,mPaint);
        drawSnake.draw(mCanvas, mPaint);

    }
    public void PauseScreenText(){
        // Set the size and color of the mPaint for the text
        mPaint.setColor(Color.argb(255, 96, 130, 182));
        mPaint.setTextSize(250);

        // Draw the message
        // We will give this an international upgrade soon
        //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
        mCanvas.drawText(getResources().
                        getString(R.string.tap_to_play),
                200, 700, mPaint);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int touchX = (int) motionEvent.getX();
        int touchY = (int) motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    mPaused = false;
                    newGame();

                    // Don't want to process snake direction for this tap
                    return true;
                } else {
                    if (isTouchWithinPauseText(touchX, touchY)) {
                        mPaused = true;
                        return true;
                    } else {
                        mSnake.switchHeading(motionEvent);
                    }
                }
                break;
        }
        return true;
    }

    //Maps out a rectangular area for the "Pause" text
    private boolean isTouchWithinPauseText(int touchX, int touchY) {
        int pauseTextLeft = 20;
        int pauseTextTop = 140;
        int pauseTextRight = pauseTextLeft + (int) mPaint.measureText("Pause");
        int pauseTextBottom = pauseTextTop + 60;

        return touchX >= pauseTextLeft && touchX <= pauseTextRight &&
                touchY >= pauseTextTop && touchY <= pauseTextBottom;
    }


    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        mPlaying = true;
        if (mPaused) {
            mThread = new Thread(this);
            mThread.start();
            mNewGame = false;
        }
    }
}
