package br.com.reign.loftylibrary.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class Zoom {

    ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 1.0f;
    RecyclerView pages;
    Context context;

    public Zoom(RecyclerView pages, Context context) {
        this.pages = pages;
        this.context = context;
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            pages.setScaleX(scaleFactor);
            pages.setScaleY(scaleFactor);
            return true;
        }
    }
}