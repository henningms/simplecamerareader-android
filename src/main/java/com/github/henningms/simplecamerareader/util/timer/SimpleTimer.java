package com.github.henningms.simplecamerareader.util.timer;

import android.os.Handler;

public class SimpleTimer
{
	private Handler handler;
	private SimpleTimerTick onTickEvent;
	private TimeSpan interval;
	private Runnable checkTimer;
	private long startTime = 0L;
	private boolean cancel = false;

	private long timeToCheck = 10L;

	public SimpleTimer()
	{
		this(null,null);
	}

	public SimpleTimer(TimeSpan interval, SimpleTimerTick tick)
	{
		handler = new Handler();
		setInterval(interval);
		setOnTickListener(tick);

		// Sets up run-routine when Handler calls checkTimer-callback
		checkTimer = new Runnable(){
			public void run()
			{
				// Calls our check-routine
				runCheck();

			}
		};
	}

	/**
	 * start()
	 *
	 * Starts the timer
	 */
	public void start()
	{
		if (handler == null) return;
		if (onTickEvent == null) return;
		if (checkTimer == null) return;
		if (interval == null) return;
		if (interval.getSeconds() <= 0 && interval.getMinutes() <= 0
				&& interval.getHours() <= 0 && interval.getMilliseconds() <= 0) return;
		cancel = false;

		reset();

		startTime = System.currentTimeMillis();

		handler.removeCallbacks(checkTimer);
		handler.postDelayed(checkTimer, timeToCheck);


	}

	/**
	 * stop()
	 *
	 * Stops the timer
	 *
	 * TODO: Timer will keep on going until it's stopped, even if app "closes"
	 */
	public void stop()
	{
		if (handler == null) return;
		if (checkTimer == null) return;

		cancel = true;
		handler.removeCallbacks(checkTimer);
	}

	/**
	 * reset()
	 *
	 * Reset timer
	 */
	private void reset()
	{
		startTime = 0L;
	}

	/**
	 * runCheck()
	 *
	 * Our internal tick routine.
	 * Checks to see if our interval has been hit.
	 */
	private void runCheck()
	{
		// Checks to see if timer has been stopped
		if (cancel) return;

		long start = startTime;
		long areWeThereYet = System.currentTimeMillis() - start;
		long theAnswer = interval.getMillis();

		// Are we there yet?
		if (areWeThereYet >= theAnswer)
		{
			// We're here! Call users handler onTick event
			startTime = System.currentTimeMillis();

			onTickEvent.onTick();
		}

		// Remove all callbacks and start timer again :)
		handler.removeCallbacks(checkTimer);
		handler.postDelayed(checkTimer, timeToCheck);

	}

	public TimeSpan getInterval()
	{
		return interval;
	}

	public void setInterval(TimeSpan interval)
	{
		this.interval = interval;
	}

	public void setOnTickListener(SimpleTimerTick event)
	{
		if (event != null)
			onTickEvent = event;
	}

	public void removeOnTickListener()
	{
		onTickEvent = null;
	}
}
