package com.dubllik.mystlviewergles20;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.jar.Attributes;

/**
 * Created by elenaozerova on 27/12/2017.
 */

public class MyGLSurfaceView extends GLSurfaceView {
    private final GLES20Renderer mRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new GLES20Renderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Create gesture detector for multitouch scale
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    private float mPreviousX;
    private float mPreviousY;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        scaleGestureDetector.onTouchEvent(e);

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                mRenderer.mAngleX += dx;
                mRenderer.mAngleY += dy;

        }

        mPreviousX = x;
        mPreviousY = y;

        requestRender();

        return true;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mRenderer.scaleFactor *= detector.getScaleFactor();

            // don't let the object get too small or too large.
            mRenderer.scaleFactor = Math.max(0.1f, Math.min(mRenderer.scaleFactor, 5.0f));

            return true;
        }
    }
}
