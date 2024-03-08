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
    private UpdateGameMethod updateGameMethod;

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
        updateGameMethod = new UpdateGameMethod(this);

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

            updateGameMethod.GameDetails();

            // Draw some text while paused
            if(mPaused){
                updateGameMethod.PauseScreenText();
            }


            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
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
                    if (updateGameMethod.isTouchWithinPauseText(touchX, touchY)) {
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
