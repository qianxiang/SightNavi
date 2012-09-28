package org.jint.util;

import java.io.IOException;

import android.util.Log;

/**
 * @author jintian
 *
 */
public class LogUtil {
	private static String tag;
	private static String logFilePath;
	private static boolean isWriteToFile = false;
	
	public static void debug(String msg) {
		String appendedMsg = appendMsgAndInfo(msg, getCurrentInfo());
		Log.d(tag, appendedMsg);
		if(isWriteToFile){
			writeToFile("DEBUG", appendedMsg);
		}
	}
	
	public static void error(String msg, Throwable tr) {
		String appendedMsg = appendMsgAndInfo(msg, getCurrentInfo());
		Log.e(tag, appendedMsg, tr);
		if(isWriteToFile){
			writeToFile("error", appendedMsg);
		}
	}

	public static void info(String msg) {
		String appendedMsg = appendMsgAndInfo(msg, getCurrentInfo());
		Log.i(tag, appendedMsg);
		if(isWriteToFile){
			writeToFile("INFO", appendedMsg);
		}
	}

	private static String getCurrentInfo() {

		StackTraceElement[] eles = Thread.currentThread().getStackTrace();
		StackTraceElement targetEle = eles[5];
		String info = "(" + targetEle.getClassName() + "."
				+ targetEle.getMethodName() + ":" + targetEle.getLineNumber()
				+ ")";
		if(info.length()>25) {
			info = info.substring(25);
		}
		return info;
	}

	private static void writeToFile(String type, String msg){
		msg ="[" + type + "]" + DateUtil.getCurrentDate("hh:mm:ss") + " " + msg + "\n";
		try {
			FileUtil.appendDataToFile(msg.getBytes(), logFilePath);
		} catch (IOException e) {
			Log.e(tag, "Error when log to file", e);
		}
	}
	
	private static String appendMsgAndInfo(String msg, String info) {
		return msg + " " + getCurrentInfo();
	}

	public static String getTag() {
		return tag;
	}

	public static void setTag(String tag) {
		LogUtil.tag = tag;
	}

	public static String getLogFilePath() {
		return logFilePath;
	}

	public static void setLogFilePath(String logFilePath) {
		LogUtil.logFilePath = logFilePath;
	}

	public static boolean isWriteToFile() {
		return isWriteToFile;
	}

	public static void setWriteToFile(boolean isWriteToFile) {
		LogUtil.isWriteToFile = isWriteToFile;
	}
}
