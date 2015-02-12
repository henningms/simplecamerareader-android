package com.github.henningms.simplecamerareader;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback
{

	private SurfaceHolder holder;
	private boolean hasSurface = false;
	private SimpleCameraReader scr;

	public CameraView(Context context, SimpleCameraReader scr)
	{
		super(context);

		this.scr = scr;

		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// TODO Auto-generated constructor stub
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		// TODO Auto-generated method stub
		scr.init(holder, width, height);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{

	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub

	}


}
