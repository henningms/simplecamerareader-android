package com.github.henningms.simplecamerareader;

import java.util.Collection;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class SimpleCameraHandler extends Handler {

	  private static final String TAG = SimpleCameraReader.class.getSimpleName();

	  private final SimpleCameraReader scr;
	  private final DecodeThread decodeThread;
	  private State state;
	  private final CameraManager cameraManager;

	  private enum State {
	    PREVIEW,
	    SUCCESS,
	    DONE
	  }

	  SimpleCameraHandler(SimpleCameraReader scr,
	                         Collection<BarcodeFormat> decodeFormats,
	                         String characterSet,
	                         CameraManager cameraManager) {
	    this.scr = scr;
	    //decodeThread = new DecodeThread(scr, decodeFormats, characterSet, new ViewfinderResultPointCallback(scr.getViewfinderView()));
	    decodeThread = new DecodeThread(scr, decodeFormats, characterSet, null);
	    decodeThread.start();
	    state = State.SUCCESS;

	    // Start ourselves capturing previews and decoding.
	    this.cameraManager = cameraManager;
	    cameraManager.startPreview();
	    restartPreviewAndDecode();
	  }

	  @Override
	  public void handleMessage(Message message)
	  {
		  MessageCode mc = MessageCode.toM(message.what);


	    switch (mc) {
	    	case AUTO_FOCUS:
	    		if (state == State.PREVIEW)
	    		{
	    			cameraManager.requestAutoFocus(this, 7);
	    		}
	    		break;
	      case RESTART_PREVIEW:
	        Log.d(TAG, "Got restart preview message");
	        restartPreviewAndDecode();
	        break;
	      case DECODE_SUCCESS:
	        Log.d(TAG, "Got decode succeeded message");
	        state = State.SUCCESS;
	        Bundle bundle = message.getData();
	        Bitmap barcode = bundle == null ? null :
	            (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
	        scr.handleDecode((Result) message.obj, barcode);
	        break;
	      case DECODE_FAILED:
	        // We're decoding as fast as possible, so when one decode fails, start another.
	        state = State.PREVIEW;
	        cameraManager.requestPreviewFrame(decodeThread.getHandler(), 0);
	        break;
	    }
	  }

	  public void quitSynchronously() {
	    state = State.DONE;
	    cameraManager.stopPreview();
	    Message quit = Message.obtain(decodeThread.getHandler(), 1);
	    quit.sendToTarget();
	    try {
	      // Wait at most half a second; should be enough time, and onPause() will timeout quickly
	      decodeThread.join(500L);
	    } catch (InterruptedException e) {
	      // continue
	    }

	    // Be absolutely sure we don't send any queued up messages
	    removeMessages(2);
	    removeMessages(3);
	  }

	  private void restartPreviewAndDecode() {
	    if (state == State.SUCCESS) {
	      state = State.PREVIEW;
	      cameraManager.requestPreviewFrame(decodeThread.getHandler(), 0);
	      cameraManager.requestAutoFocus(this, 7);
	      //activity.drawViewfinder();
	    }
	  }
}
