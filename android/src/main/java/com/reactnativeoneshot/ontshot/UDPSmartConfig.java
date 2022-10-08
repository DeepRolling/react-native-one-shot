package com.reactnativeoneshot.ontshot;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class UDPSmartConfig implements ISmartConfig, IOneShotConfig {
	private static final int SLEEP_TIME = 8;
	private static final int SLEEP_GUIDE = 3000;
	private static final int SLEEP_DATA = 4000;

	private DatagramSocket clientSocket = null;
	private ConfigProperty property = null;
	private Context context = null;
	private static int packageCount = 0;
	UDPSmartConfig()
	{
		property = new ConfigProperty();
	}

	UDPSmartConfig(Context context){
		this.context = context;
		property = new ConfigProperty();
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stopConfig();
	}
	@Override
	public boolean startConfig(String password) throws OneShotException{
		return SendGuidAndData(null, password, null, null);
	}

	@Override
	public void stopConfig() {
		property.errorId = OneShotException.ERROR_USER_STOP;
		if(clientSocket != null){
			clientSocket.close();
			clientSocket = null;
		}
	}

	private boolean SendGuidAndData(String ssid, String pwd, String bssid, String userData) throws OneShotException{
		String ssid1;
		try {
			property.errorId = 0;
			if(context == null){
				return false;
			}
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if(wifiManager == null){
				return false;
			}
			if(wifiManager.isWifiEnabled() == false && !WifiApService.isWifiApEnabled(wifiManager)){
				property.errorId = OneShotException.ERROR_WIFI_DISABLED;
				throw new OneShotException(property.errorId);
			}

			if (wifiManager.isWifiEnabled()) {
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if (wifiInfo == null) {
					property.errorId = OneShotException.ERROR_NETWORK_DISCONNECTED;
					throw new OneShotException(property.errorId);
				}
				ssid1 = wifiInfo.getSSID();
				int version = getAndroidSDKVersion();
				if (version > 16 && ssid1.startsWith("\"")
						&& ssid1.endsWith("\"")) {
					ssid1 = ssid1.substring(1, ssid1.length() - 1);
				}
				if(ssid == null){
					ssid = ssid1;
				}
				if(bssid == null || ssid == ssid1){
					bssid = wifiInfo.getBSSID();
					if (bssid == null || "".equals(bssid)) {
						property.errorId = OneShotException.ERROR_NETWORK_DISCONNECTED;
						throw new OneShotException(property.errorId);
					}
				}
			}
			else if(WifiApService.isWifiApEnabled(wifiManager))
			{
				WifiConfiguration conf = WifiApService.getWifiApConfiguration(wifiManager);
				if(conf != null){
					ssid = conf.SSID;
					if(bssid == null){
						bssid = conf.BSSID;
					}
				}
			}

			if(clientSocket == null){
				clientSocket = new DatagramSocket();
				clientSocket.setBroadcast(true);
			}
			PackManager packM = new PackManager();
			List<DataPack> packs = packM.preparePack(ssid, pwd, bssid, userData);
			GuidePack guideP = new GuidePack();
			long startTime = System.currentTimeMillis();
			while(true){
				sendPack(guideP);
				if(System.currentTimeMillis() - startTime > SLEEP_GUIDE){
					break;
				}
			}
			startTime = System.currentTimeMillis();
			while(true){
				for(DataPack pack : packs){
					sendPack(pack);
				}
				if(System.currentTimeMillis() - startTime > SLEEP_DATA){
					break;
				}
			}
		} catch (OneShotException one) {
			one.printStackTrace();
			throw one;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static int getNextCount() {
        return 1 + (packageCount++) % 128;
    }
	private String getDestHostName() {
        int count = getNextCount();
        return "225.1.1." + count;
    }
	private void sendPack(IDataPack pack) throws Exception{
		String host = getDestHostName();
		for(short send : pack.getShorts()){
			if(sendByte(send, host) == false){
				throw new Exception("user stopped!");
			}
		}
	}
	private boolean sendByte(short bSend, String hostName) throws Exception{
		int blen = 0;
		InetAddress IPAddress = InetAddress.getByName(hostName);
		blen = bSend;
		byte[] sendData = new byte[blen];
		for(int j=0; j< blen; j++) {
			sendData[j] = 65;
		}
		if(sendData(sendData, IPAddress) == false){
			return false;
		}
		return true;
	}
	private boolean sendData(byte[] sendData, InetAddress IPAddress) throws Exception {
		if(property.errorId > 0){
			throw new OneShotException(property.errorId);
		}
		try {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(sendPacket);
		} catch (java.net.SocketException e) {
			//e.printStackTrace();
		}
		Thread.sleep(SLEEP_TIME);
		return true;
	}
	private int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (NumberFormatException e) {
			Log.e(e.toString(), e.getMessage());
		}
		return version;
	}

	@Override
	public boolean sendData(String data) throws OneShotException {
		return SendGuidAndData("", null, null, data);
	}
	@Override
	public void start(String ssid, String key, int timeout, Context context) {
		this.context = context;
		long time_start = System.currentTimeMillis();
		long time_now = time_start;
		try {
			while(true){
				if(SendGuidAndData(ssid, key, null, null) == false)
				{
					break;
				}
				time_now = System.currentTimeMillis();
				if((time_now - time_start) > timeout * 1000 ){
					throw new OneShotException(OneShotException.ERROR_TIMEOUT);
				}
			}
		}
		catch (OneShotException oe) {
			throw new OneShotException(oe.getErrorID());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			stop();
		}
	}
	@Override
	public void stop() {
		stopConfig();
	}
}
