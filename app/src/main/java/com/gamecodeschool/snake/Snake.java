package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.gamecodeschool.snake.R;

import java.util.ArrayList;

class Snake {

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
        matrix.preRotate(180);
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

    void move() {
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

    void draw(Canvas canvas, Paint paint) {

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

    void switchHeading(MotionEvent motionEvent) {
        // Get the x position of the touch event
        int x = (int) motionEvent.getX();
        // Determine if the touch was on the right or left side of the screen
        boolean isRightSide = x > halfWayPoint;

        // Get the location of the second segment to prevent 180-degree turns
        Point secondSegment = segmentLocations.size() > 1 ? segmentLocations.get(1) : null;

        // Determine if the second segment is directly opposite the head based on the current heading
        boolean opposite = (heading == Heading.RIGHT && secondSegment != null && secondSegment.x < segmentLocations.get(0).x) ||
                (heading == Heading.LEFT && secondSegment != null && secondSegment.x > segmentLocations.get(0).x) ||
                (heading == Heading.UP && secondSegment != null && secondSegment.y > segmentLocations.get(0).y) ||
                (heading == Heading.DOWN && secondSegment != null && secondSegment.y < segmentLocations.get(0).y);

        // If there is no second segment (only the head), or the second segment is not directly opposite the head
        if (secondSegment == null || !opposite) {
            if (isRightSide) {
                // Toggle between RIGHT and LEFT
                heading = (heading == Heading.LEFT) ? Heading.RIGHT : Heading.LEFT;
            } else {
                // Toggle between UP and DOWN
                heading = (heading == Heading.UP) ? Heading.DOWN : Heading.UP;
            }
        }
    }
}
