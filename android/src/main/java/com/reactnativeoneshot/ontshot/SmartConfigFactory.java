package com.reactnativeoneshot.ontshot;

import android.content.Context;

public class SmartConfigFactory {
	private final String version = "2.0.0";

	public ISmartConfig createSmartConfig(ConfigType configType, Context context){
		if(configType == ConfigType.UDP){
			return new UDPSmartConfig(context);
		}
		return null;
	}
	public IOneShotConfig createOneShotConfig(ConfigType configType){
		if(configType == ConfigType.UDP){
			return new UDPSmartConfig();
		}
		return null;
	}

	public String getVersion() {
		return version;
	}
}
