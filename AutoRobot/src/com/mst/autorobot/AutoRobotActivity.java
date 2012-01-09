package com.mst.autorobot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class AutoRobotActivity extends Activity 
{
    /** Called when the activity is first created. */
	private static final String TAG             = "Sample::Activity";

    public static final int     VIEW_MODE_RGBA  = 0;
    public static final int		VIEW_MODE_OPFLOW = 1;
	
    private MenuItem            mItemBaseActions;
    private MenuItem            mItemRemote;
    private MenuItem            mItemOpticalFlow;
    private MenuItem			mItemAutonomous;
    
    public static int           viewMode        = VIEW_MODE_RGBA;
	
    public AutoRobotActivity() 
    {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Viewer(this));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        Log.i(TAG, "onCreateOptionsMenu");
        mItemBaseActions = menu.add("Basic Actions");
        mItemRemote = menu.add("Remote Control");
        mItemOpticalFlow = menu.add("Optical Flow");
        mItemAutonomous = menu.add("Autonomous");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
        if (item == mItemBaseActions)
            viewMode = VIEW_MODE_RGBA;
        else if (item == mItemRemote)
            viewMode = VIEW_MODE_RGBA;
        else if (item == mItemOpticalFlow)
            viewMode = VIEW_MODE_OPFLOW;
        else if (item == mItemAutonomous)
        	viewMode = VIEW_MODE_RGBA;
        return true;
    }
}