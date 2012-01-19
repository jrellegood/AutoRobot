package com.mst.autorobot;

public class Looper implements Runnable
{
	public Imageproc image;
	
	public Looper()
	{
		super();
		image = new Imageproc();
	}

	@Override
	public void run() 
	{
    	/*This function will be the main loop for our application
    	 * It will follow these steps:
    	 * 1) Capture Image
    	 * 2) Compute Optical Flow
    	 * 3) Obtain sensor data
    	 * 4) Make decisions
    	 * 5) Execute decisions (move) (we will wait until this action is entirely carried out before trying again).
    	 */
    	
    	while(Datapack.status != -1)
    	{
    		switch( Datapack.status )
    		{
    			case 0:
    				image.optical_flow();
    				break;
    			case 1:
    				break;
    			case 2: 
    				break;
    			case 3: 
    				break;
    		}
    		Datapack.status = (Datapack.status + 1) % 4;
    	}
		
	}

}
