package com.mst.autorobot;

import java.util.ArrayList;
import java.util.List;

import com.mst.autorobot.Viewer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import android.util.Log;

public class Imageproc
{
	private static final String TAG             = "Imageproc";

	private static final int MAX_POINTS = 40;
	
	private Mat mNew;
	private Mat mOld;
	private Mat mInt;
	
    private List<Point> old_points = new ArrayList<Point>();
    private List<Point> new_points = new ArrayList<Point>();

	
	public Imageproc()
	{
		mNew = new Mat();
		mOld = new Mat();
	}
	
	public void optical_flow()
	{
		if( Datapack.first_time )
		{	
			mInt = Viewer.get_current_img();
			if(mInt != null)
			{
				mInt.copyTo(mOld);
				Datapack.first_time = false;
				Log.i(TAG, "Copied data");
				
				Imgproc.goodFeaturesToTrack( mOld, old_points, MAX_POINTS, .1, 5 );
				Log.i(TAG, "Tracked Features");
			}
			else
			{
			}
			
		}
		else
		{
			List<Byte> status = new ArrayList<Byte>();
			List<Float> err = new ArrayList<Float>();
			
			mInt = Viewer.get_current_img();
			
			if( mInt != null)
			{
				mInt.copyTo(mNew);
				
				Video.calcOpticalFlowPyrLK(mOld, mNew, old_points, new_points, status, err );
				determine_direction( status, err);
				
				mNew.copyTo(mOld);
				old_points = new_points;
				new_points = new ArrayList<Point>();
				Datapack.iteration++;
				
				if( Datapack.iteration == 40 )
				{
					Datapack.first_time = true;
					Datapack.iteration = 0;
				}
				Log.i(TAG, "Calculated OpFlow");
				
			}
			
			
		}
	}
	
	private void determine_direction( List<Byte> status, List<Float> err)
	{
		int halfway = mNew.rows() / 2;
		int num_left = 0;
		int num_right = 0;
		float total_left = 0;
		float total_right = 0;
		float dist;
		
		for( int i = 0; i < status.size(); i++ )
		{
			if( status.get(i) != null && status.get(i) == 1 )
			{
				
				dist = (float) Math.sqrt(( old_points.get(i).x - new_points.get(i).x) 
						+ (old_points.get(i).y - new_points.get(i).y) ); //Thanks Pythagoras!
				
				if( old_points.get(i).x < halfway )
				{
					//Point originates on left side of image
					total_right = total_right + dist;
					num_right++;
				}
				else
				{
					//Point originates on right side of image
					total_left = total_left + dist;
					num_left++;
				}
			}
		}
		
		if(num_left != 0 && num_right != 0)
		{
			if( (total_right / num_right) < (total_left / num_left) )
			{
				Datapack.opflow_direction = Datapack.LEFT;
				Log.i(TAG, "Go left!");
			}
			else
			{
				Datapack.opflow_direction = Datapack.RIGHT;
				Log.i(TAG, "Go right!");
			}
		}
		else
		{
			//Were no points in one or both sides, data is invalid and should be reset
			Datapack.first_time = true;
			Datapack.iteration = 0;
		}
	}
}
