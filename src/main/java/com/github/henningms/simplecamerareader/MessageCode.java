package com.github.henningms.simplecamerareader;

public enum MessageCode
{
	DECODE, QUIT, DECODE_SUCCESS, DECODE_FAILED, START_PREVIEW, STOP_PREVIEW, RESTART_PREVIEW, AUTO_FOCUS;

	public static MessageCode toM(int i)
	{
		return MessageCode.values()[i];
	}
}
