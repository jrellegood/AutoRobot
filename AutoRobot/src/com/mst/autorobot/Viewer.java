package com.mst.autorobot;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;

class Viewer extends ViewerBase 
{
	private final int NUM_PERMITS = 1;
	
	private static Semaphore mylock;
    private Mat mGray;
    private Mat mRgba;
    private static Mat mNewImg;
    private static Mat mSentImg;
    
    public Viewer(Context context) 
    {
        super(context);
        mylock = new Semaphore(NUM_PERMITS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) 
    {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) 
        {
            mGray = new Mat();  
            mNewImg = new Mat();
            mSentImg = new Mat();
            mRgba = new Mat();
        }
    }

    @Override
    protected Bitmap processFrame(VideoCapture capture) 
    {
    	capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);
    	Imgproc.cvtColor(mGray, mRgba, Imgproc.COLOR_GRAY2RGBA);

        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
        
        if (Utils.matToBitmap(mRgba, bmp))
        {
        	try {
				mylock.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		mGray.copyTo(mNewImg);
    		mylock.release();
            return bmp;
        }

        bmp.recycle();
        return null;
    }

	@Override
    public void run() {
        super.run();

        synchronized (this) 
        {
            if (mGray != null)
                mGray.release();
            if (mRgba != null)
            	mRgba.release();
            
            mGray = null;
            mRgba = null;
        }
    }
	
	public static Mat get_current_img()
	{
		if( mNewImg != null )
		{
			try {
				mylock.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mNewImg.copyTo(mSentImg);
			mylock.release();
		}
		return mSentImg;
		
	}
}
