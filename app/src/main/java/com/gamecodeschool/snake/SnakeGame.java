package com.gamecodeschool.snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable{
    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;
    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    // How many points does the player have
    private int mScore;
    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;

    // Pause Button properties
    private Rect mPauseButton;
    private boolean mIsGamePaused = false; // Track pause state
    private int mButtonSize; // Size of the pause button

    private long lastTurnTime = System.currentTimeMillis();
    private static final long TURN_DELAY = 100; // 100 milliseconds


    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Initialize the Pause Button
        int buttonPadding = 20; //can adjust
        mButtonSize = size.x / 14; // size
        mPauseButton = new Rect(buttonPadding, size.y - buttonPadding - mButtonSize, buttonPadding + mButtonSize, size.y - buttonPadding);


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

    }
    // Called to start a new game
    public void newGame() {
        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Get the apple ready for dinner
        mApple.spawn();
        // Reset the mScore
        mScore = 0;
        // Ensure the game starts in an active, unpaused state
        mPaused = false;
        mIsGamePaused = false;
        mPlaying = true;
        mNextFrameTime = System.currentTimeMillis();
    }
    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }
    // Check to see if it is time for an update
    public boolean updateRequired() {
        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;
        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed
            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;
            // Return true so that the update and draw
            // methods are executed
            return true;
        }
        return false;
    }
    // Update all the game objects
    public void update() {
        // Move the snake
        mSnake.move();
        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            mApple.spawn();
            // Add to  mScore
            mScore = mScore + 1;
            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            mPaused =true;
            mIsGamePaused = false; // Reset the pause state
            mPlaying = false; // Signal that the game loop should stop

        }

    }
    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            // Fill the screen with a color, this color has been made pink to be unique
            mCanvas.drawColor(Color.argb(255, 200, 100, 209));
            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);
            mPaint.setTypeface(Typeface.SERIF); // Serif font

            // Draw the score
            mCanvas.drawText("" + mScore, 20, 120, mPaint);
            // Draw Galileo & Eric's names
            mCanvas.drawText("Galileo & Eric", 1425, 120, mPaint);


            // Draw the apple and the snake
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Draw the pause button
            mPaint.setColor(Color.argb(255, 255, 255, 255)); // White color
            mCanvas.drawRect(mPauseButton, mPaint);


            // Draw some text while paused
            // Draw "Tap to Play" or "Paused" message
            if(mPaused && !mPlaying){
                mPaint.setTextSize(100);
                mCanvas.drawText("Tap to Play!", 50, mNumBlocksHigh / 2 * mButtonSize, mPaint);
            } else if (mIsGamePaused) {
                mPaint.setTextSize(100);
                mCanvas.drawText("Paused", 50, mNumBlocksHigh / 2 * mButtonSize, mPaint);
            }
            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTurnTime < TURN_DELAY) {
            return true; // Ignore this touch event, it's too soon after the last turn
        }

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPauseButton.contains(x, y) && mPlaying) {
                    mIsGamePaused = !mIsGamePaused; // Toggle pause state
                    mPaused = mIsGamePaused; // Update the game's pause state
                    return true; // Event handled
                } else if (mPaused) {
                    // "Tap to Play" logic
                    mIsGamePaused = false;
                    mPaused = false;
                    mPlaying = true;
                    newGame();
                    resume(); // Start the game loop
                    return true; // Event handled
                }
                break;
        }
        if (!mPaused && mPlaying) {
            mSnake.switchHeading(motionEvent);
        }
        lastTurnTime = currentTime; // Update the last turn time
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
        mThread = new Thread(this);
        mThread.start();
    }
}