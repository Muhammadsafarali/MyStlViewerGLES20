package com.dubllik.mystlviewergles20;

import android.app.Activity;
import android.app.FragmentManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GLActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl);

        FragmentStlViewer fragment_plyViewer = new FragmentStlViewer();

        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.flContent, fragment_plyViewer).commit();

        //        mGLSurfaceView = new MyGLSurfaceView(this);
//        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
