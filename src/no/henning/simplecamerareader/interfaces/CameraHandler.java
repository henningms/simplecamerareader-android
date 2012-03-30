package no.henning.simplecamerareader.interfaces;

import com.google.zxing.Result;

public interface CameraHandler
{
	public void onDecodeCompleted(Result result);
	public void onError(Exception ex);	
}