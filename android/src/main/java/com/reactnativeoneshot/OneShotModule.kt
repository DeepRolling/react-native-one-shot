package com.reactnativeoneshot

import android.content.Context
import android.net.wifi.WifiManager
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.reactnativeoneshot.ontshot.ConfigType
import com.reactnativeoneshot.ontshot.OneShotException
import com.reactnativeoneshot.ontshot.SmartConfigFactory


//通过修改参数ConfigType，确定使用何种方式进行一键配置，需要和固件侧保持一致。
val oneshotConfig = SmartConfigFactory().createOneShotConfig(ConfigType.UDP)

class OneShotModule(val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "OneShot"
  }

  /**判断热点开启状态 */
  fun isWifiApEnabled(): Boolean {
    return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED
  }

  private fun getWifiApState(): WIFI_AP_STATE {
    var tmp: Int
    val wifiManager = reactContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    return try {
      val method = wifiManager.javaClass.getMethod("getWifiApState")
      tmp = method.invoke(wifiManager) as Int
      // Fix for Android 4
      if (tmp > 10) {
        tmp = tmp - 10
      }
      WIFI_AP_STATE::class.java.enumConstants!![tmp]
    } catch (e: Exception) {
      e.printStackTrace()
      WIFI_AP_STATE.WIFI_AP_STATE_FAILED
    }
  }

  enum class WIFI_AP_STATE {
    WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
  }


  @ReactMethod
  fun startConfig(ssid: String, psw: String, timeoutSecond: Int, promise: Promise) {
    Thread(Runnable {
      try {
        val wifiManager: WifiManager? = reactContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiManager?.let {
          if (wifiManager.isWifiEnabled || isWifiApEnabled()) {
            oneshotConfig.start(ssid, psw, timeoutSecond, reactContext.currentActivity)
          }
        }
      } catch (oe: OneShotException) {
        val ERROR_WIFI_DISABLED = 101
        val ERROR_NETWORK_DISCONNECTED = 102
        val ERROR_NETWORK_NOT_SUPPORT = 103
        val ERROR_TIMEOUT = 104
        val ERROR_USER_STOP = 199
        var reason:String = ""
        when (oe.errorID) {
          ERROR_WIFI_DISABLED -> {
            reason = "配对失败：wifi不可用"
          }
          ERROR_NETWORK_DISCONNECTED -> {
            reason = "配对失败：网络连接失败"
          }
          ERROR_NETWORK_NOT_SUPPORT -> {
            reason = "配对失败：暂不支持5g网络"
          }
          ERROR_TIMEOUT -> {
            reason = "配对失败：连接超时"
          }
          ERROR_USER_STOP -> {
            reason = "配对失败：您已取消配对"
          }
        }
        promise.reject(oe.errorID.toString(), reason)
      } catch (e: Exception) {
        e.printStackTrace()
      } finally {
        oneshotConfig.stop()
      }
    }).start()
  }


  @ReactMethod
  fun stopConfig() {
    oneshotConfig.stop()
  }


}
