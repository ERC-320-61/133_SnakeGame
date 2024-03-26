package com.gamecodeschool.snake.mechanics;

import android.util.Log;
import android.view.MotionEvent;
import com.gamecodeschool.snake.buttons.dpad;
import com.gamecodeschool.snake.interfaces.DirectionalControl;

public class dpadTouchHandler extends TouchHandler {
    private com.gamecodeschool.snake.buttons.dpad dpad;
    private DirectionalControl directionalControl;

    public dpadTouchHandler(dpad dpad, DirectionalControl directionalControl) {
        this.dpad = dpad;
        this.directionalControl = directionalControl;
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        boolean handled = dpad.handleTouch(event, directionalControl);
        if (handled) {
            // Log a message indicating that the DPad has handled the touch event
            Log.d("DPadTouch", "DPad handled touch event at: (" + event.getX() + ", " + event.getY() + ")");
        }
        return handled;
    }
}
