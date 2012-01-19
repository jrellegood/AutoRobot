package com.mst.autorobot;

public class Datapack 
{
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int NEITHER = 2;
	
	public static int iteration;
	public static int status;
	public static boolean first_time;
	public static int opflow_direction;
	
	public Datapack()
	{
		status = 0;
		iteration = 0;
		first_time = true;
	}

}
