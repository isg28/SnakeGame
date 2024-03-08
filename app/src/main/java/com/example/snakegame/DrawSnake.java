package com.example.snakegame;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawSnake extends DrawingMethod{
    public Snake snake;

    public DrawSnake(Snake snake){
        this.snake = snake;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!snake.segmentLocations.isEmpty()) {
            switch (snake.getHeading()) {
                case RIGHT:
                    canvas.drawBitmap(snake.mBitmapHeadRight,
                            snake.segmentLocations.get(0).x
                                    * snake.mSegmentSize,
                            snake.segmentLocations.get(0).y
                                    * snake.mSegmentSize, paint);
                    break;

                case LEFT:
                    canvas.drawBitmap(snake.mBitmapHeadLeft,
                            snake.segmentLocations.get(0).x
                                    * snake.mSegmentSize,
                            snake.segmentLocations.get(0).y
                                    * snake.mSegmentSize, paint);
                    break;

                case UP:
                    canvas.drawBitmap(snake.mBitmapHeadUp,
                            snake.segmentLocations.get(0).x
                                    * snake.mSegmentSize,
                            snake.segmentLocations.get(0).y
                                    * snake.mSegmentSize, paint);
                    break;

                case DOWN:
                    canvas.drawBitmap(snake.mBitmapHeadDown,
                            snake.segmentLocations.get(0).x
                                    * snake.mSegmentSize,
                            snake.segmentLocations.get(0).y
                                    * snake.mSegmentSize, paint);
                    break;
            }

            // Draw the snake body one block at a time
            for (int i = 1; i < snake.segmentLocations.size(); i++) {
                canvas.drawBitmap(snake.mBitmapBody,
                        snake.segmentLocations.get(i).x
                                * snake.mSegmentSize,
                        snake.segmentLocations.get(i).y
                                * snake.mSegmentSize, paint);
            }
        }
    }
}
