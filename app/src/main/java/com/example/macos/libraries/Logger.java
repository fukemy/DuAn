/*
 * Name: Logger.java $
 * Version: 1.0  $
 * Date: Jul 16, 2013 2:57:50 PM $
 * $
 * Copyright (C) 2013 FPT Software. All rights reserved.
 */
package com.example.macos.libraries;

import android.util.Log;

/**
 * This class is common logger for project.
 * You can not a object of this class.
 */
public final class Logger
{
	private static final String TAG = "ROAD PROJECT LOG";
	private static final String DEFAULT_STRING_NULL = "null";
	/**this variable allow show log*/
	/*private static final boolean ENABLE_SHOW_LOG = false;
	
	private static final boolean ENABLE_SHOW_LOG_MMS = false;*/
	private Logger()
	{
	}

	/**
	 * Send an ERROR log message.
	 * @param msg The message you would like logged.
	 */
	public static void error(String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
//		if(msg.length() > 4000) {
//			Log.e(TAG, msg.substring(0, 4000));
//			error(msg.substring(4000));
//		} else
			Log.e(TAG, msg);
	}
	
	/**
	 * Send an ERROR log message.
	 * @param tag The tag you would like logged.
	 * @param msg The message you would like logged.
	 */
	public static void error(String tag, String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		if (null == tag)
		{
			tag = TAG;
		}
		Log.e(tag, msg);
	}

	/**
	 * Send an DEBUG log message.
	 * @param msg The message you would like logged.
	 */
	public static void debug(String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		Log.d(TAG, msg);
	}
	
	/**
	 * Send an DEBUG log message.
	 * @param tag The tag you would like logged.
	 * @param msg The message you would like logged.
	 */
	public static void debug(String tag, String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		if (null == tag)
		{
			tag = TAG;
		}
		Log.d(tag, msg);
	}

	/**
	 * Send an INFOR log message.
	 * @param msg The message you would like logged.
	 */
	public static void infor(String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		Log.i(TAG, msg);
	}
	
	/**
	 * Send an INFOR log message.
	 * @param tag The tag you would like logged.
	 * @param msg The message you would like logged.
	 */
	public static void infor(String tag, String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		if (null == tag)
		{
			tag = TAG;
		}
		Log.i(tag, msg);
	}

	/**
	 * Send an WARN log message.
	 * @param msg The message you would like logged.
	 */
	public static void warn(String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		Log.w(TAG, msg);
	}
	
	/**
	 * Send an WARN log message.
	 * @param tag The tag you would like logged.
	 * @param msg The message you would like logged.
	 */
	public static void warn(String tag, String msg)
	{
		if (null == msg)
		{
			msg = DEFAULT_STRING_NULL;
		}
		if (null == tag)
		{
			tag = TAG;
		}
		Log.i(tag, msg);
	}

	/**
	 * Send an THROWABLE ERROR log message.
	 * @param e The exception
	 */
	public static void exceptionError(Throwable e)
	{
		if (null != e)
		{
			Log.e(TAG, e.toString(), e);
		}
		else
		{
			Log.e(TAG, DEFAULT_STRING_NULL);
		}
	}
	
	/**
	 * Send an THROWABLE ERROR log message.
	 * @param tag The tag you would like logged.
	 * @param e The exception
	 */
	public static void exceptionError(String tag, Throwable e)
	{
		if (null == tag)
		{
			tag = TAG;
		}
		if (null != e)
		{
			Log.e(tag, e.toString(), e.getCause());
			StackTraceElement[] arrSte = e.getStackTrace();
			for (StackTraceElement ste : arrSte)
			{
				Log.e(tag, "at " + ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")");
			}
		}
		else
		{
			Log.e(tag, DEFAULT_STRING_NULL);
		}
	}

	/**
	 * print out Throwable
	 */
	public static void error(Throwable ex) {
		// Log.e(TAG, GlobalParams.BLANK_STRING, ex);
		exceptionError(ex);
	}

}
