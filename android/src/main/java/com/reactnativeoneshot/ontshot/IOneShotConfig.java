package com.reactnativeoneshot.ontshot;

import android.content.Context;

public interface IOneShotConfig {
	void start(String ssid, String key, int timeout, Context context);
	void stop();
}
