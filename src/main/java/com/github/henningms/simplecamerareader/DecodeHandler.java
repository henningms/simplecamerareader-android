package com.github.henningms.simplecamerareader;

import java.util.Map;

import com.github.henningms.simplecamerareader.util.PlanarYUVLuminanceSource;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

final class DecodeHandler extends Handler
{
	  private static final String TAG = DecodeHandler.class.getSimpleName();

	  private final SimpleCameraReader scr;
	  private final MultiFormatReader multiFormatReader;
	  private boolean running = true;

	  DecodeHandler(SimpleCameraReader scr, Map<DecodeHintType,Object> hints) {
	    multiFormatReader = new MultiFormatReader();
	    multiFormatReader.setHints(hints);
	    this.scr = scr;
	  }

	  @Override
	  public void handleMessage(Message message)
	  {
	    if (!running)
	    {
	      return;
	    }

	    MessageCode mc = MessageCode.toM(message.what);

	    switch (mc)
	    {
	      case DECODE:
	        decode((byte[]) message.obj, message.arg1, message.arg2);
	        break;
	      case QUIT:
	        running = false;
	        Looper.myLooper().quit();
	        break;
	    }
	  }

	  /**
	   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
	   * reuse the same reader objects from one decode to the next.
	   *
	   * @param data   The YUV preview frame.
	   * @param width  The width of the preview frame.
	   * @param height The height of the preview frame.
	   */
	  private void decode(byte[] data, int width, int height)
	  {
	    long start = System.currentTimeMillis();
	    Result rawResult = null;
	    PlanarYUVLuminanceSource source = scr.getCameraManager().buildLuminanceSource(data, width, height);
	    if (source != null) {
	      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	      try {
	        rawResult = multiFormatReader.decodeWithState(bitmap);
	      } catch (ReaderException re) {
	        // continue
	      } finally {
	        multiFormatReader.reset();
	      }
	    }

	    Handler handler = scr.getHandler();
	    if (rawResult != null)
	    {
	      // Don't log the barcode contents for security.
	      long end = System.currentTimeMillis();
	      Log.d(TAG, "Found barcode in " + (end - start) + " ms");
	      if (handler != null) {
	        Message message = Message.obtain(handler, 2, rawResult);
	        Bundle bundle = new Bundle();
	        bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
	        message.setData(bundle);
	        message.sendToTarget();
	      }
	    }
	    else
	    {
	      if (handler != null)
	      {
	        Message message = Message.obtain(handler, 3);
	        message.sendToTarget();
	      }
	    }
	  }

	}
