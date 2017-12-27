package com.dubllik.mystlviewergles20;

import android.opengl.GLES20;

import com.dubllik.mystlviewergles20.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;


public class MySTLLoader {

    public ArrayList<Vector3f> mVertices;
    public ArrayList<Vector3f> mNormals;

    public FloatBuffer mVertexBuffer;
    public FloatBuffer mNormalBuffer;

    public Vector3f mSize;

    public float mRight = -4096;
    public float mLeft = 4096;
    public float mTop = -4096;
    public float mBottom = 4096;
    public float mFront = -4096;
    public float mBack = 4096;


    byte[] stlBytes = null;

    private byte[] getSTLBytes(InputStream is) {
        byte[] stlBytes = null;
        try {
            stlBytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(is);
        }
        return stlBytes;
    }

    private int getIntWithLittleEndian(byte[] bytes, int offset) {
        return (0xff & stlBytes[offset]) | ((0xff & stlBytes[offset + 1]) << 8) | ((0xff & stlBytes[offset + 2]) << 16) | ((0xff & stlBytes[offset + 3]) << 24);
    }

    public MySTLLoader(InputStream is) {
        mVertices = new ArrayList<Vector3f>();
        mNormals = new ArrayList<Vector3f>();
        this.stlBytes = getSTLBytes(is);
        try {
            if (isText(stlBytes)) {
                getStlASCII(new String(stlBytes));
            } else {
                getStlBinary(stlBytes);
            }
        } catch (Exception e) {
        }
    }

    boolean isText(byte[] bytes) {
        for (byte b : bytes) {
            if (b == 0x0a || b == 0x0d || b == 0x09) {
                // white spaces
                continue;
            }
            if (b < 0x20 || (0xff & b) >= 0x80) {
                // control codes
                return false;
            }
        }
        return true;
    }

    private void getStlASCII(String stlText) {
        String[] stlLines = stlText.split("\n");

        for (int i = 0; i < stlLines.length; i++) {
            String string = stlLines[i].trim();
            if (string.startsWith("facet normal ")) {
                string = string.replaceFirst("facet normal ", "");
                String[] normalValue = string.split("\\s+");
                float x = Float.parseFloat(normalValue[1]);
                float y = Float.parseFloat(normalValue[2]);
                float z = Float.parseFloat(normalValue[3]);
                Vector3f faceNormal = new Vector3f(x, y, z);
                for (int k = 0; k < 3; k++)
                    mNormals.add(faceNormal);
            }
            if (string.startsWith("vertex ")) {
                string = string.replaceFirst("vertex ", "");
                String[] vertexValue = string.split("\\s+");
                float x = Float.parseFloat(vertexValue[1]);
                float y = Float.parseFloat(vertexValue[2]);
                float z = Float.parseFloat(vertexValue[3]);
                mVertices.add(new Vector3f(x, y, z));
            }
        }
    }

    private void getStlBinary(byte[] stlBytes) {
        int vectorSize = getIntWithLittleEndian(stlBytes, 80);

        for (int i = 0; i < vectorSize; i++) {
            float nx = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50));
            float ny = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 4));
            float nz = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 8));
            Vector3f faceNormal = new Vector3f(nx,ny,nz);
            for (int k = 0; k < 3; k++)
                mNormals.add(faceNormal);

            float x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 12));
            float y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 16));
            float z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 20));

            mVertices.add(new Vector3f(x,y,z));

            x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 24));
            y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 28));
            z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 32));

            mVertices.add(new Vector3f(x,y,z));

            x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 36));
            y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 40));
            z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 44));

            mVertices.add(new Vector3f(x,y,z));
        }
    }

    void generateVertexArrays(float scale) {

        float[] mVA = new float[mVertices.size()*3];
        float[] mVNA = new float[mVertices.size()*3];

        for (int i = 0; i < mVertices.size(); i++) {

            mVA[i*3]   = mVertices.get(i).x * scale;
            mVA[i*3+1] = mVertices.get(i).y * scale;
            mVA[i*3+2] = mVertices.get(i).z * scale;

            //if (mVA[i*3+2] < 0.0f) { mVA[i*3+2] = 0.0f; };


            // Extremes
            if (mVA[i*3] < mLeft)
                mLeft = mVA[i*3];
            if (mVA[i*3] > mRight)
                mRight = mVA[i*3];

            if (mVA[i*3+1] < mBottom)
                mBottom = mVA[i*3+1];
            if (mVA[i*3+1] > mTop)
                mTop = mVA[i*3+1];

            if (mVA[i*3+2] < mBack)
                mBack = mVA[i*3+2];
            if (mVA[i*3+2] > mFront)
                mFront = mVA[i*3+2];

            mVNA[i*3]   = mNormals.get(i).x;
            mVNA[i*3+1] = mNormals.get(i).y;
            mVNA[i*3+2] = mNormals.get(i).z;

        }


        // Center object

        mSize = new Vector3f(mRight - mLeft, mTop - mBottom, mFront - mBack);

        for (int i = 0; i < mVertices.size(); i++) {
            mVA[i*3]   -= (mRight + mLeft) / 2.0f;
            mVA[i*3+1] -= (mTop + mBottom) / 2.0f;
            mVA[i*3+2] -= (mFront + mBack) / 2.0f;
        }

        mVertexBuffer = ByteBuffer.allocateDirect(mVA.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVA).position(0);

        mNormalBuffer = ByteBuffer.allocateDirect(mVNA.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormalBuffer.put(mVNA).position(0);
    }

    void render(int positionHandle, int normalHandle) {
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, mNormalBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexBuffer.capacity() / 3);
    }
}
