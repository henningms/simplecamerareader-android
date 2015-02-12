package com.github.henningms.simplecamerareader;

import java.io.IOException;
import java.util.Collection;

import com.github.henningms.simplecamerareader.interfaces.CameraHandler;
import com.github.henningms.simplecamerareader.util.QRFormatCodes;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.SurfaceHolder;

public class SimpleCameraReader
{
	private Context context;

	private CameraManager cameraManager;
	private SimpleCameraHandler handler;
	private Result savedResult;
	private CameraHandler returnHandler;
	private CameraView cv;
	private ViewfinderView vv;
	private Collection<BarcodeFormat> formats;

	public SimpleCameraReader(Context context, Collection<BarcodeFormat> formats)
	{
		this.context = context;
		this.formats = formats;

		cv = new CameraView(context, this);
	}

	public Handler getHandler()
	{
		return handler;
	}
	public CameraView getCameraView()
	{
		return cv;
	}

	public CameraManager getCameraManager()
	{
		return cameraManager;
	}

	public void setResultHandler(CameraHandler callback)
	{
		returnHandler = callback;

	}

	public void focus()
	{

	}

	public void start() throws IOException
	{

	}

	public void init(SurfaceHolder holder, int width, int height)
	{
		cameraManager = new CameraManager(context);

		try
		{
			cameraManager.openDriver(cv.getHolder(), width, height);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (formats == null || formats.size() <= 0)
			formats = QRFormatCodes.ALL_FORMATS;

		handler = new SimpleCameraHandler(this, formats, null, cameraManager);



		//vv.setCameraManager(cameraManager);
	}

	public void stop()
	{
		cameraManager.closeDriver();
	}

	public ViewfinderView getViewfinderView()
	{
		return vv;

	}

	public void setViewFinder(ViewfinderView vv)
	{
		this.vv = vv;
	}

	public void restartDecodingProcess()
	{
		if (handler == null) return;

		handler.sendEmptyMessage(MessageCode.RESTART_PREVIEW.ordinal());
	}

	/**
	   * A valid barcode has been found, so give an indication of success and show the results.
	   *
	   * @param rawResult The contents of the barcode.
	   * @param barcode   A greyscale bitmap of the camera data which was decoded.
	   */
	  public void handleDecode(Result rawResult, Bitmap barcode) {
	    returnHandler.onDecodeCompleted(rawResult);
	  }
}
