package com.vrd.tech.yfd.util;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by Administrator on 2016/6/24.
 */
public class WifiUtil {

    public static WifiManager getWifiManger(Context context){
        return  (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }

    public static boolean isConnectedTargetWifi(Context context){
        WifiManager wifiManager = getWifiManger(context);
        if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
                && wifiManager.getConnectionInfo()!= null
                && wifiManager.getConnectionInfo().getNetworkId() != -1
                && wifiManager.getConnectionInfo().getSSID().contains("BTDVR")){
            return true;
        }else{
            return false;
        }
    }
}
