package com.example.snakegame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public abstract class DrawingMethod {
    protected Canvas mCanvas;
    protected SurfaceHolder mSurfaceHolder;
    protected Paint mPaint;

    // A snake ssss
    protected Snake mSnake;
    // And an apple
    protected Apple mApple;

    public DrawingMethod(Canvas mCanvas, SurfaceHolder mSurfaceHolder, Paint mPaint, Snake mSnake, Apple mApple){
        this.mCanvas = mCanvas;
        this.mSurfaceHolder = mSurfaceHolder;
        this.mPaint = mPaint;
        this.mSnake = mSnake;
        this.mApple = mApple;
    }

    public DrawingMethod(){

    }

    public abstract void draw(Canvas mCanvas, Paint mPaint, Apple mApple);

}
