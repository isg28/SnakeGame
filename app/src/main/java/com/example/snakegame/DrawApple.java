package com.example.snakegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class DrawApple extends DrawingMethod {
    private Point location = new Point();
    private int mSize;
    private Bitmap mBitmapApple;

    public DrawApple(Canvas canvas, Paint paint, Apple apple){
        super();
    }
    public DrawApple(Bitmap bitmapApple, Point appleLocation, int blockSize){
        super();
        mBitmapApple = bitmapApple;
        location = appleLocation;
        mSize = blockSize;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, Apple apple){
        canvas.drawBitmap(mBitmapApple,
                location.x * mSize, location.y * mSize, paint);
    }

}
