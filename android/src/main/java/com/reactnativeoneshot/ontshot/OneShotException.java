package com.reactnativeoneshot.ontshot;

public class OneShotException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3637035711283521408L;
	public static final int ERROR_WIFI_DISABLED = 101;
	public static final int ERROR_NETWORK_DISCONNECTED = 102;
	public static final int ERROR_NETWORK_NOT_SUPPORT = 103;
	public static final int ERROR_TIMEOUT = 104;
	public static final int ERROR_USER_STOP = 199;

	private int errorID;

	public OneShotException(int errorId){
		errorID = errorId;
	}

	public int getErrorID() {
		return errorID;
	}

}
