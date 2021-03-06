package com.example.spotify.ExternalLibraries;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

// custom class that waits for double tap from user
public class OnDoubleTapListenerOne implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    public OnDoubleTapListenerOne(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            OnDoubleTapListenerOne.this.onDoubleTap(e);
            return super.onDoubleTap(e);
        }
    }

    public void onDoubleTap(MotionEvent e) {
        // to be overridden when implementing listener
    }
}
