package no.henning.simplecamerareader.util.timer;

import java.util.Calendar;
import java.util.Date;

public class TimeSpan
{
	private int hours;
	private int minutes;
	private int seconds;
	private int milliseconds;

	public TimeSpan()
	{
		this(0,0,0,0);
	}

	public TimeSpan(int hours, int minutes, int seconds, int milliseconds)
	{
		setHours(hours);
		setMinutes(minutes);
		setSeconds(seconds);
		setMilliseconds(milliseconds);
	}

	public int getHours()
	{
		return hours;
	}
	public void setHours(int hours)
	{
		this.hours = hours;
	}
	public int getMinutes()
	{
		return minutes;
	}
	public void setMinutes(int minutes)
	{
		this.minutes = minutes;
	}
	public int getSeconds()
	{
		return seconds;
	}
	public void setSeconds(int seconds)
	{
		this.seconds = seconds;
	}

	public int getMilliseconds()
	{
		return milliseconds;
	}

	public void setMilliseconds(int milliseconds)
	{
		this.milliseconds = milliseconds;
	}

	public long getMillis()
	{
		long hours = getHours() * 3600000;
		long minutes = getMinutes() * 60000;
		long seconds = getSeconds() * 1000;
		long milliseconds = getMilliseconds();

		long millis = milliseconds + seconds + minutes + hours;

		return millis;
	}

	/**
	 * http://www.velocityreviews.com/forums/t139746-how-to-subtract-dates.html
	 * @param d1
	 * @param d2
	 */
	public static int DateDiff(Date date1, Date date2)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);

		int d1 = cal.get(Calendar.DATE);

		cal.setTime(date2);

		int d2 = cal.get(Calendar.DATE);

		return d2 - d1;
	}
}