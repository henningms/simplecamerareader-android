package no.henning.simplecamerareader;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import no.henning.simplecamerareader.interfaces.CameraHandler;
import no.henning.simplecamerareader.util.PlanarYUVLuminanceSource;
import no.henning.simplecamerareader.util.QRFormatCodes;
import no.henning.simplecamerareader.util.timer.SimpleTimer;
import no.henning.simplecamerareader.util.timer.SimpleTimerTick;
import no.henning.simplecamerareader.util.timer.TimeSpan;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.EAN13Reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

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
