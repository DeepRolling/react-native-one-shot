package com.reactnativeoneshot.ontshot;


public interface ISmartConfig {
	boolean startConfig(String password) throws OneShotException;
	void stopConfig();
	boolean sendData(String data) throws OneShotException;
}
