package com.github.henningms.simplecamerareader;

import java.io.IOException;

import com.github.henningms.simplecamerareader.util.PlanarYUVLuminanceSource;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public final class CameraManager
{

	private static final String TAG = CameraManager.class.getSimpleName();

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 600;
	private static final int MAX_FRAME_HEIGHT = 400;

	private final Context context;
	private final CameraConfigurationManager configManager;
	private Camera camera;
	private Rect framingRect;
	private Rect framingRectInPreview;
	private boolean initialized;
	private boolean previewing;
	private boolean reverseImage;
	private int requestedFramingRectWidth;
	private int requestedFramingRectHeight;

	private boolean changeRotate = false;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;
	/**
	 * Autofocus callbacks arrive here, and are dispatched to the Handler which
	 * requested them.
	 */

	private final AutoFocusCallback autoFocusCallback;

	public CameraManager(Context context)
	{
		this.context = context;
		this.configManager = new CameraConfigurationManager(context);
		previewCallback = new PreviewCallback(configManager);
		autoFocusCallback = new AutoFocusCallback();
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 *
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws Exception
	 */
	public void openDriver(SurfaceHolder holder, int width, int height)
			throws Exception
	{
		Camera theCamera = camera;
		if (theCamera == null)
		{
			try
			{
				theCamera = Camera.open();
				if (theCamera == null)
				{
					throw new IOException();
				}
				camera = theCamera;
			}
			catch (Exception ex)
			{
				throw new Exception("Unable to access camera, make sure it's released properly or not in use", ex);
			}
		}
		theCamera.setPreviewDisplay(holder);

		if (!initialized)
		{
			initialized = true;
			configManager.initFromCameraParameters(theCamera, width, height);
			if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0)
			{
				setManualFramingRect(requestedFramingRectWidth,
						requestedFramingRectHeight);
				requestedFramingRectWidth = 0;
				requestedFramingRectHeight = 0;
			}
		}
		configManager.setDesiredCameraParameters(theCamera);

		reverseImage = false;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public void closeDriver()
	{
		if (camera != null)
		{
			camera.release();
			camera = null;
			// Make sure to clear these each time we close the camera, so that
			// any scanning rect
			// requested by intent is forgotten.
			framingRect = null;
			framingRectInPreview = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public void startPreview()
	{
		Camera theCamera = camera;
		if (theCamera != null && !previewing)
		{
			theCamera.startPreview();
			previewing = true;
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public void stopPreview()
	{
		if (camera != null && previewing)
		{
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			autoFocusCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 *
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public void requestPreviewFrame(Handler handler, int message)
	{
		Camera theCamera = camera;
		if (theCamera != null && previewing)
		{
			previewCallback.setHandler(handler, message);
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	/**
	 * Asks the camera hardware to perform an autofocus.
	 *
	 * @param handler
	 *            The Handler to notify when the autofocus completes.
	 * @param message
	 *            The message to deliver.
	 */
	public void requestAutoFocus(Handler handler, int message)
	{
		if (camera != null && previewing)
		{
			autoFocusCallback.setHandler(handler, message);
			try
			{
				camera.autoFocus(autoFocusCallback);
			}
			catch (RuntimeException re)
			{
				// Have heard RuntimeException reported in Android 4.0.x+;
				// continue?
				Log.w(TAG, "Unexpected exception while focusing", re);
			}
		}
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 *
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public Rect getFramingRect()
	{
		if (framingRect == null)
		{
			if (camera == null)
			{
				return null;
			}

			Point screenResolution = configManager.getScreenResolution();
			int width = screenResolution.x * 3 / 4;
			if (width < MIN_FRAME_WIDTH)
			{
				width = MIN_FRAME_WIDTH;
			}
			else if (width > MAX_FRAME_WIDTH)
			{
				width = MAX_FRAME_WIDTH;
			}
			int height = screenResolution.y * 3 / 4;
			if (height < MIN_FRAME_HEIGHT)
			{
				height = MIN_FRAME_HEIGHT;
			}
			else if (height > MAX_FRAME_HEIGHT)
			{
				height = MAX_FRAME_HEIGHT;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(TAG, "Calculated framing rect: " + framingRect);
		}
		return framingRect;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 */
	public Rect getFramingRectInPreview()
	{
		if (framingRectInPreview == null)
		{
			Rect framingRect = getFramingRect();
			if (framingRect == null)
			{
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			rect.left = rect.left * cameraResolution.x / screenResolution.x;
			rect.right = rect.right * cameraResolution.x / screenResolution.x;
			rect.top = rect.top * cameraResolution.y / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
			framingRectInPreview = rect;
		}
		return framingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions,
	 * rather than determine them automatically based on screen resolution.
	 *
	 * @param width
	 *            The width in pixels to scan.
	 * @param height
	 *            The height in pixels to scan.
	 */
	public void setManualFramingRect(int width, int height)
	{
		if (initialized)
		{
			Point screenResolution = configManager.getScreenResolution();
			if (width > screenResolution.x)
			{
				width = screenResolution.x;
			}
			if (height > screenResolution.y)
			{
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(TAG, "Calculated manual framing rect: " + framingRect);
			framingRectInPreview = null;
		}
		else
		{
			requestedFramingRectWidth = width;
			requestedFramingRectHeight = height;
		}
	}

	/**
	 * A factory method to build the appropriate LuminanceSource object based on
	 * the format of the preview buffers, as described by Camera.Parameters.
	 *
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
			int width, int height)
	{
		Rect rect = getFramingRectInPreview();
		if (rect == null)
		{
			return null;
		}

		changeRotate = !changeRotate;

		if (changeRotate) data = Rotate(data, width, height);
		// Go ahead and assume it's YUV rather than die.
		return new PlanarYUVLuminanceSource(data, width, height, 0, 0, width,
				height, reverseImage);
	}

	private byte[] Rotate(byte[] data, int width, int height)
	{
		int nw = height;
		byte[] newData = new byte[width * height];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				newData[nw * (x + 1) - y - 1] = data[y * width + x];
			}
		}

		return newData;
	}
}
