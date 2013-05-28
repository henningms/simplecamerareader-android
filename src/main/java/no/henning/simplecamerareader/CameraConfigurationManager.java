package no.henning.simplecamerareader;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

final class CameraConfigurationManager
{

	private static final String TAG = "CameraConfiguration";
	private static final int MIN_PREVIEW_PIXELS = 320 * 240; // small screen
	private static final int MAX_PREVIEW_PIXELS = 1280 * 720; // large/HD screen

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;

	CameraConfigurationManager(Context context)
	{
		this.context = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	void initFromCameraParameters(Camera camera, int width, int height)
	{
		Camera.Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width2 = display.getWidth();
		int height2 = display.getHeight();
		// We're landscape-only, and have apparently seen issues with display
		// thinking it's portrait
		// when waking from sleep. If it's not landscape, assume it's mistaken
		// and reverse them:
		/*
		 * if (width < height) { Log.i(TAG,
		 * "Display reports portrait orientation; assuming this is incorrect");
		 * int temp = width; width = height; height = temp; }
		 */

		screenResolution = new Point(width2, height2);
		Log.i(TAG, "Screen resolution: " + screenResolution);

		//Size size = getOptimalPreviewSize(
			//	parameters.getSupportedPreviewSizes(), width, height);

		// cameraResolution = new Point(size.width, size.height);
		cameraResolution = findBestPreviewSizeValue(parameters,
				screenResolution);
		Log.i(TAG, "Camera resolution: " + cameraResolution);
	}

	void setDesiredCameraParameters(Camera camera)
	{
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null)
		{
			Log.w(TAG,
					"Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		/*
		 * SharedPreferences prefs =
		 * PreferenceManager.getDefaultSharedPreferences(context);
		 * 
		 * initializeTorch(parameters, prefs); String focusMode =
		 * findSettableValue(parameters.getSupportedFocusModes(),
		 * Camera.Parameters.FOCUS_MODE_AUTO,
		 * Camera.Parameters.FOCUS_MODE_MACRO); if (focusMode != null) {
		 * parameters.setFocusMode(focusMode); }
		 */

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		parameters.set("orientation", "portrait");
		parameters.setRotation(90);
		
		setDisplayOrientation(camera, 90);
		camera.setParameters(parameters);
	}

	Point getCameraResolution()
	{
		return cameraResolution;
	}

	Point getScreenResolution()
	{
		return screenResolution;
	}

	/*
	 * void setTorch(Camera camera, boolean newSetting) { Camera.Parameters
	 * parameters = camera.getParameters(); doSetTorch(parameters, newSetting);
	 * camera.setParameters(parameters); SharedPreferences prefs =
	 * PreferenceManager.getDefaultSharedPreferences(context); boolean
	 * currentSetting = prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT,
	 * false); if (currentSetting != newSetting) { SharedPreferences.Editor
	 * editor = prefs.edit();
	 * editor.putBoolean(PreferencesActivity.KEY_FRONT_LIGHT, newSetting);
	 * editor.commit(); } }
	 * 
	 * private static void initializeTorch(Camera.Parameters parameters,
	 * SharedPreferences prefs) { boolean currentSetting =
	 * prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false);
	 * doSetTorch(parameters, currentSetting); }
	 */

	/*
	 * private static void doSetTorch(Camera.Parameters parameters, boolean
	 * newSetting) { String flashMode; if (newSetting) { flashMode =
	 * findSettableValue(parameters.getSupportedFlashModes(),
	 * Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_ON); }
	 * else { flashMode = findSettableValue(parameters.getSupportedFlashModes(),
	 * Camera.Parameters.FLASH_MODE_OFF); } if (flashMode != null) {
	 * parameters.setFlashMode(flashMode); } }
	 */

	private Point findBestPreviewSizeValue(Camera.Parameters parameters,
			Point screenResolution)
	{

		List<Camera.Size> supportedPreviewSizes = parameters
				.getSupportedPreviewSizes();

		//Camera.Size sp = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
		
		//return new Point(sp.width, sp.height);
		
		if (Log.isLoggable(TAG, Log.INFO))
		{
			StringBuilder previewSizesString = new StringBuilder();
			for (Camera.Size supportedPreviewSize : supportedPreviewSizes)
			{
				previewSizesString.append(supportedPreviewSize.width)
						.append('x').append(supportedPreviewSize.height)
						.append(' ');
			}
			Log.i(TAG, "Supported preview sizes: " + previewSizesString);
		}

		for (Camera.Size supportedPreviewSize : supportedPreviewSizes)
		
		{
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS)
			{
				continue;
			}
			/*boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;*/
			
			if (realWidth == screenResolution.x
					&& realHeight == screenResolution.y)
			{
				return new Point(realWidth, realHeight);
			}
		}

		Point bestSize = null;

		int diff = Integer.MAX_VALUE;
		for (Camera.Size supportedPreviewSize : supportedPreviewSizes)
		{
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS)
			{
				continue;
			}
			/*boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;*/
			
			int newDiff = Math.abs(screenResolution.x * realWidth
					- screenResolution.y * realHeight);
			if (newDiff == 0)
			{
				return new Point(realWidth, realHeight);
			}
			if (newDiff < diff)
			{
				bestSize = new Point(realWidth, realHeight);
				diff = newDiff;
			}
		}

		if (bestSize == null)
		{
			Camera.Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);
			Log.i(TAG, "No suitable preview sizes, using default: " + bestSize);
		}

		return bestSize;
	}

	private static String findSettableValue(Collection<String> supportedValues,
			String... desiredValues)
	{
		Log.i(TAG, "Supported values: " + supportedValues);
		String result = null;
		if (supportedValues != null)
		{
			for (String desiredValue : desiredValues)
			{
				if (supportedValues.contains(desiredValue))
				{
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(TAG, "Settable value: " + result);
		return result;
	}

	protected void setDisplayOrientation(Camera camera, int angle)
	{
		Method downPolymorphic;
		try
		{
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null) downPolymorphic.invoke(camera,
					new Object[] { angle });
		}
		catch (Exception e1)
		{
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h)
	{
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes)
		{
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff)
			{
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null)
		{
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes)
			{
				if (Math.abs(size.height - targetHeight) < minDiff)
				{
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

}
