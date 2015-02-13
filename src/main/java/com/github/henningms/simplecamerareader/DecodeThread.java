package com.github.henningms.simplecamerareader;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import android.os.Handler;
import android.os.Looper;

final class DecodeThread extends Thread {

	  public static final String BARCODE_BITMAP = "barcode_bitmap";

	  private final SimpleCameraReader activity;
	  private final Map<DecodeHintType,Object> hints;
	  private Handler handler;
	  private final CountDownLatch handlerInitLatch;

	  DecodeThread(SimpleCameraReader activity,
	               Collection<BarcodeFormat> decodeFormats,
	               String characterSet, ViewfinderResultPointCallback resultPointCallback) {

	    this.activity = activity;
	    handlerInitLatch = new CountDownLatch(1);

	    hints = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);

	    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

	    if (characterSet != null) {
	      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
	    }

	    if (resultPointCallback != null)
	    	hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
	  }

	  Handler getHandler() {
	    try {
	      handlerInitLatch.await();
	    } catch (InterruptedException ie) {
	      // continue?
	    }
	    return handler;
	  }

	  @Override
	  public void run() {
	    Looper.prepare();
	    handler = new DecodeHandler(activity, hints);
	    handlerInitLatch.countDown();
	    Looper.loop();
	  }

	}
