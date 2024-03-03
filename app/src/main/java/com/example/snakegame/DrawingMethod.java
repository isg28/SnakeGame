package com.example.snakegame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public abstract class DrawingMethod {
    protected Canvas mCanvas;
    protected SurfaceHolder mSurfaceHolder;
    protected Paint mPaint;


    public DrawingMethod(Canvas mCanvas, SurfaceHolder mSurfaceHolder, Paint mPaint, Snake mSnake, Apple mApple){
        this.mCanvas = mCanvas;
        this.mSurfaceHolder = mSurfaceHolder;
        this.mPaint = mPaint;
    }

    public DrawingMethod(){

    }

    public abstract void draw(Canvas mCanvas, Paint mPaint);

}
