package no.henning.simplecamerareader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

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
