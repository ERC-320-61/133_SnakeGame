package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.gamecodeschool.snake.interfaces.GameObject;

import java.util.ArrayList;

class Snake implements GameObject {

    private ArrayList<Point> segmentLocations;
    private int mSegmentSize;
    private Point mMoveRange;
    private int halfWayPoint;

    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    private Heading heading = Heading.RIGHT;

    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;
    private Bitmap mBitmapBody;


    public Snake(Context context, Point mr, int ss) {
        segmentLocations = new ArrayList<>();
        mSegmentSize = ss;
        mMoveRange = mr;

        // Original bitmap for the head facing right
        mBitmapHeadRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        mBitmapHeadRight = Bitmap.createScaledBitmap(mBitmapHeadRight, mSegmentSize, mSegmentSize, false);

        // Initialize Matrix for transformations
        Matrix matrix = new Matrix();

        // Flip horizontally to get the left-facing head
        matrix.preScale(-1, 1);
        mBitmapHeadLeft = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, mSegmentSize, mSegmentSize, matrix, true);

        // Reset matrix and rotate for up-facing head
        matrix.reset();
        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, mSegmentSize, mSegmentSize, matrix, true);

        // Reset and rotate for down-facing head
        matrix.reset();
        matrix.preRotate(90);
        mBitmapHeadDown = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, mSegmentSize, mSegmentSize, matrix, true);

        // Body bitmap initialization (unchanged)
        mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.body);
        mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody, mSegmentSize, mSegmentSize, false);

        halfWayPoint = mr.x * mSegmentSize / 2;
    }


    void reset(int w, int h) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(w / 2, h / 2));
    }



    public void move() {
        if (segmentLocations.isEmpty()) {
            return; // Early return if there are no segments to move
        }

        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }

        Point p = segmentLocations.get(0);

        switch (heading) {
            case UP:
                p.y--;
                break;
            case RIGHT:
                p.x++;
                break;
            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
                break;
        }

        segmentLocations.set(0, p);
    }

    boolean detectDeath() {
        boolean dead = false;
        if (segmentLocations.get(0).x == -1 || segmentLocations.get(0).x > mMoveRange.x || segmentLocations.get(0).y == -1 || segmentLocations.get(0).y > mMoveRange.y) {
            dead = true;
        }

        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (segmentLocations.get(0).x == segmentLocations.get(i).x && segmentLocations.get(0).y == segmentLocations.get(i).y) {
                dead = true;
            }
        }

        return dead;
    }

    boolean checkDinner(Point l) {
        if (segmentLocations.get(0).x == l.x && segmentLocations.get(0).y == l.y) {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }
    @Override

    public void draw(Canvas canvas, Paint paint) {

        if (!segmentLocations.isEmpty()) {
            Bitmap headBitmap;
            switch (heading) {
                case RIGHT:
                    headBitmap = mBitmapHeadRight;
                    break;
                case LEFT:
                    headBitmap = mBitmapHeadLeft;
                    break;
                case UP:
                    headBitmap = mBitmapHeadUp;
                    break;
                case DOWN:
                    headBitmap = mBitmapHeadDown;
                    break;
                default:
                    headBitmap = mBitmapHeadRight; // Default case, should not happen
            }
            canvas.drawBitmap(headBitmap, segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);

            // Draw the snake body one block at a time
            for (int i = 1; i < segmentLocations.size(); i++) {
                canvas.drawBitmap(mBitmapBody,
                        segmentLocations.get(i).x * mSegmentSize,
                        segmentLocations.get(i).y * mSegmentSize, paint);
            }
        }
    }

    @Override
    public void update() {
        move(); // Encapsulate the movement logic within the update method
    }

    void switchHeading(MotionEvent motionEvent) {
        // Log entry for method entry with motion event details
        Log Log = null;
        Log.d("SnakeGame", "switchHeading called. X: " + motionEvent.getX() + ", Y: " + motionEvent.getY());

        // Get the x position of the touch event
        int x = (int) motionEvent.getX();
        // Get the y position of the touch event
        int y = (int) motionEvent.getY();
        // Determine if the touch was on the right or left side of the screen
        boolean isRightSide = x > halfWayPoint;

        // Log the side of the screen touched
        Log.d("SnakeGame", "Touch detected on " + (isRightSide ? "right" : "left") + " side of the screen.");

        // Get the location of the second segment to prevent 180-degree turns
        Point secondSegment = segmentLocations.size() > 1 ? segmentLocations.get(1) : null;

        // Determine if the second segment is directly opposite the head based on the current heading
        boolean opposite = (heading == Heading.RIGHT && secondSegment != null && secondSegment.x < segmentLocations.get(0).x) ||
                (heading == Heading.LEFT && secondSegment != null && secondSegment.x > segmentLocations.get(0).x) ||
                (heading == Heading.UP && secondSegment != null && secondSegment.y > segmentLocations.get(0).y) ||
                (heading == Heading.DOWN && secondSegment != null && secondSegment.y < segmentLocations.get(0).y);

        // Log the current heading and position of the second segment
        Log.d("SnakeGame", "Current heading: " + heading + ", Second segment: " + (secondSegment == null ? "null" : "x=" + secondSegment.x + ", y=" + secondSegment.y));

        // Assuming isRightSide is determined by whether the touch was on the right half of the screen.
        if (isRightSide) {
            // Handling horizontal direction changes
            if (heading == Heading.UP || heading == Heading.DOWN) {
                // If the snake is currently moving vertically, it can safely switch to a horizontal direction
                Heading newHeading = (heading == Heading.UP) ? Heading.RIGHT : Heading.LEFT;
                Log.d("SnakeGame", "Switching heading from " + heading + " to " + newHeading);
                heading = newHeading;
            }
        } else {
            // Handling vertical direction changes
            if (heading == Heading.LEFT || heading == Heading.RIGHT) {
                // If the snake is currently moving horizontally, it can safely switch to a vertical direction
                Heading newHeading = (heading == Heading.LEFT) ? Heading.UP : Heading.DOWN;
                Log.d("SnakeGame", "Switching heading from " + heading + " to " + newHeading);
                heading = newHeading;
            }
        }


    }
}
