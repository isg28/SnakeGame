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
    private Thread mThread = null;
    protected long mNextFrameTime;
    private volatile boolean mPlaying = false;
    protected volatile boolean mPaused = true;

    protected SoundPool mSP;
    protected int mEat_ID = -1;
    protected int mCrashID = -1;
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    protected int mScore;
    protected Canvas mCanvas;
    protected SurfaceHolder mSurfaceHolder;
    protected Paint mPaint;
    protected Snake mSnake;
    protected Apple mApple;
    protected DrawApple drawApple;
    protected DrawSnake drawSnake;
    protected boolean mNewGame = true;
    private UpdateSnakeGame updateSnakeGame;
    private UpdateGameMethod updateGameMethod;

    public SnakeGame(Context context, Point size) {
        super(context);
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;

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

            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
        }

        InitializeObjects(context, blockSize);

    }

    public void InitializeObjects(Context context, int blockSize){
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

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

    public void newGame() {
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        mApple.spawn();

        if(mNewGame){
            mScore = 0;
        }

        mNextFrameTime = System.currentTimeMillis();
        mNewGame = false;
    }

    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                if (updateSnakeGame.updateRequired()) {
                    updateSnakeGame.update();
                }
            }

            draw();
        }
    }

    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            updateGameMethod.GameDetails();

            if(mPaused){
                updateGameMethod.PauseScreenText();
            }
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

    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        mPlaying = true;
        if (mPaused) {
            mThread = new Thread(this);
            mThread.start();
            mNewGame = false;
        }
    }
}
