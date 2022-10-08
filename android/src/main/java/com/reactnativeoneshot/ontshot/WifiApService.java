package com.reactnativeoneshot.ontshot;

import java.lang.reflect.Method;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

class WifiApService {

	static boolean isWifiApEnabled(WifiManager wifiManager) {
		return getWifiApState(wifiManager) == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
	}

	private static WIFI_AP_STATE getWifiApState(WifiManager wifiManager){
		int tmp;
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			tmp = ((Integer) method.invoke(wifiManager));
			// Fix for Android 4
			if (tmp > 10) {
				tmp = tmp - 10;
			}
			return WIFI_AP_STATE.class.getEnumConstants()[tmp];
		} catch (Exception e) {
			e.printStackTrace();
			return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
		}
	}

	static WifiConfiguration getWifiApConfiguration(WifiManager wifiManager){
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
			WifiConfiguration tmp = ((WifiConfiguration) method.invoke(wifiManager));

			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//	static boolean isMulticastEnabled(WifiManager wifiManager){
//		try {
//			Method method = wifiManager.getClass().getMethod(
//					"isMulticastEnabled");
//			boolean tmp = ((Boolean) method.invoke(wifiManager));
//			boolean ret = tmp;
//			return ret;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
}
