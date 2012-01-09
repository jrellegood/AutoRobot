package com.mst.autorobot;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.video.Video; //<-- THIS

import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;

class Viewer extends ViewerBase {
	
    private Mat mRgba;
    private Mat mGray;
    private Mat mResult;
    private Mat mOld;
    private Mat mNew;
    private final int MAX_POINTS = 40;
    private int iterator;
    
    private FeatureDetector Detector;
    private boolean runonce = false;
    
    private List<Point> old_points;
    private List<Point> new_points = new ArrayList<Point>();
    //private List<KeyPoint> old_keypoints;
    //private List<KeyPoint> new_keypoints = new ArrayList<KeyPoint>();

    public Viewer(Context context) {
        super(context);
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
            // initialize Mats before usage
            mGray = new Mat();
            mRgba = new Mat();
            mNew = new Mat();
            mOld = new Mat();
            mResult = new Mat();
            Detector = FeatureDetector.create(FeatureDetector.FAST);
            runonce = false;
            iterator = 0;
            
        }
    }

    @Override
    protected Bitmap processFrame(VideoCapture capture) {
        switch (AutoRobotActivity.viewMode) {
        case AutoRobotActivity.VIEW_MODE_RGBA:
            capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
            break;
        case AutoRobotActivity.VIEW_MODE_OPFLOW:
        	
        	capture.retrieve(mNew, Highgui.CV_CAP_ANDROID_GREY_FRAME);
        	
        	if( runonce == false)
        	{
        		List<KeyPoint> new_keypoints = new ArrayList<KeyPoint>();
        		
        		Imgproc.goodFeaturesToTrack(mNew, new_points, MAX_POINTS, .1, 5);
        		cvtpt2kpt(new_points, new_keypoints);

        		Features2d.drawKeypoints(mNew, new_keypoints, mRgba);
        		Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGBA);
        		
        		mNew.copyTo(mOld);
        		old_points = new_points;
        		new_points = new ArrayList<Point>();
        		runonce = true;
        		iterator = 0;
        	}
        	else
        	{
        		List<Byte> status = new ArrayList<Byte>();
        		List<Float> err = new ArrayList<Float>(); //<= What we want to use for balance strategy, along with points
        		List<KeyPoint> new_keypoints = new ArrayList<KeyPoint>();
        		
        		Video.calcOpticalFlowPyrLK( mOld, mNew, old_points, new_points, status, err);
        		cvtpt2kpt(new_points, new_keypoints);
        		
        		Features2d.drawKeypoints(mNew, new_keypoints, mRgba);
        		Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGBA);	
        		mNew.copyTo(mOld);
        		old_points = new_points;
        		new_points = new ArrayList<Point>();
        		iterator++;
        		
        		if( iterator == 100 )
        		{
        			//Call this to refresh the points we are using.
        			runonce = false;
        		}
        	}
        	
        	//This code simply finds features and displays them
        	/*capture.retrieve(mNew, Highgui.CV_CAP_ANDROID_GREY_FRAME );
        	Detector.detect( mNew, new_keypoints );
        	Features2d.drawKeypoints(mNew, new_keypoints, mRgba);
        	Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGBA); */
        	
        	//THIS IS MATCHING IMAGES
        	/*
        	capture.retrieve(mNew, Highgui.CV_CAP_ANDROID_GREY_FRAME);
    		//Imgproc.cvtColor(mNew, mNew, Imgproc.COLOR_GRAY2BGRA, 4);
    		//Imgproc.Canny(mGray, mIntermediateMat, 80, 100);
			Detector.detect( mNew, new_keypoints);
			
			if( runonce == false )
			{
				
				Features2d.drawKeypoints(mNew, new_keypoints, mRgba);
	        	Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGBA);
	        	mNew.copyTo(mOld);
				old_keypoints = new_keypoints;
				new_keypoints = new ArrayList<KeyPoint>();
	        	runonce = true;
			}
			else
			{
				Mat new_descriptors = new Mat();
				Mat old_descriptors = new Mat();

				DescriptorExtractor surfDE = DescriptorExtractor.create(DescriptorExtractor.SURF);
				surfDE.compute(mNew, new_keypoints, new_descriptors);
				surfDE.compute(mOld, old_keypoints, old_descriptors);
				DescriptorMatcher dm = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
				List<DMatch> matches = new ArrayList<DMatch>();
				dm.match(new_descriptors, old_descriptors, matches);

				Features2d.drawMatches(mOld, old_keypoints, mNew, new_keypoints, matches, mResult, Scalar.all(-1), Scalar.all(-1), null, Features2d.NOT_DRAW_SINGLE_POINTS);
				//Features2d.drawMatches(mOld, old_keypoints, mNew, new_keypoints, matches, mResult);
				Imgproc.cvtColor( mResult, mRgba, Imgproc.COLOR_BGR2RGBA);
				mNew.copyTo(mOld);
				old_keypoints = new_keypoints;
				new_keypoints = new ArrayList<KeyPoint>();
			}
			*/
			
        	break;
        }

        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
        

        if (Utils.matToBitmap(mRgba, bmp))
        {
        	//mRgba.copyTo(old_mRgba);
            return bmp;
        }

        bmp.recycle();
        return null;
    }

    private void cvtpt2kpt(List<Point> new_points2, List<KeyPoint> new_keypoints) 
    {
    	for( int i = 0; i < new_points2.size(); i++)
    	{
    		KeyPoint temp = new KeyPoint();
    		temp.pt = new_points2.get(i);
    		new_keypoints.add(temp);
    	}
	}

	@Override
    public void run() {
        super.run();

        synchronized (this) {
            // Explicitly deallocate Mats
            if (mRgba != null)
                mRgba.release();
            if (mGray != null)
                mGray.release();

            mRgba = null;
            mGray = null;
        }
    }
}
