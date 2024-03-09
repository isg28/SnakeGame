package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

public class Snake implements Resettable{
    protected ArrayList<Point> segmentLocations;
    protected int mSegmentSize;
    private Point mMoveRange;
    private int halfWayPoint;
    protected enum Heading {
        UP, RIGHT, DOWN, LEFT
    }
    protected Heading heading = Heading.RIGHT;
    protected Bitmap mBitmapHeadRight;
    protected Bitmap mBitmapHeadLeft;
    protected Bitmap mBitmapHeadUp;
    protected Bitmap mBitmapHeadDown;
    protected Bitmap mBitmapBody;

    public Heading getHeading(){
        return heading;
    }


    public Snake(Context context, Point moveRange, int segmentSize) {
        segmentLocations = new ArrayList<>();

        mSegmentSize = segmentSize;
        mMoveRange = moveRange;

        mBitmapHeadRight = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);

        mBitmapHeadLeft = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);

        mBitmapHeadUp = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);

        mBitmapHeadDown = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);

        mBitmapHeadRight = Bitmap
                .createScaledBitmap(mBitmapHeadRight,
                        segmentSize, segmentSize, false);

        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);

        mBitmapHeadLeft = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, segmentSize, segmentSize, matrix, true);

        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, segmentSize, segmentSize, matrix, true);

        matrix.preRotate(180);
        mBitmapHeadDown = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, segmentSize, segmentSize, matrix, true);

        mBitmapBody = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.body);

        mBitmapBody = Bitmap
                .createScaledBitmap(mBitmapBody,
                        segmentSize, segmentSize, false);

        halfWayPoint = moveRange.x * segmentSize / 2;
    }

    @Override
    public void reset(int width, int height) {

        heading = Heading.RIGHT;

        segmentLocations.clear();

        segmentLocations.add(new Point(width / 2, height / 2));
    }


    public void move() {
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }

        Point position = segmentLocations.get(0);

        switch (heading) {
            case UP:
                position.y--;
                break;

            case RIGHT:
                position.x++;
                break;

            case DOWN:
                position.y++;
                break;

            case LEFT:
                position.x--;
                break;
        }

    }

    public boolean detectDeath() {
        boolean dead = false;

        if (segmentLocations.get(0).x == -1 ||
                segmentLocations.get(0).x > mMoveRange.x ||
                segmentLocations.get(0).y == -1 ||
                segmentLocations.get(0).y > mMoveRange.y) {

            dead = true;
        }

        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (segmentLocations.get(0).x == segmentLocations.get(i).x &&
                    segmentLocations.get(0).y == segmentLocations.get(i).y) {

                dead = true;
            }
        }
        return dead;
    }

    public boolean checkDinner(Point location) {
        if (segmentLocations.get(0).x == location.x &&
                segmentLocations.get(0).y == location.y) {

            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }


    // Handle changing direction
    public void switchHeading(MotionEvent motionEvent) {

        // Is the tap on the right hand side?
        if (motionEvent.getX() >= halfWayPoint) {
            switch (heading) {
                // Rotate right
                case UP:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.UP;
                    break;

            }
        } else {
            // Rotate left
            switch (heading) {
                case UP:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.UP;
                    break;
            }
        }
    }
}
