package com.tyx.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import com.sam.ui.base.activity.SamBaseActivity;
import com.sam.utils.network.NetworkUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseNetWorkActivity extends AppCompatActivity {
    private boolean enableNetworkCheck = true;
    private NetWorkChangReceiver networkChangReceiver;

    public void setEnableNetworkCheck(boolean enable) {
        this.enableNetworkCheck = enable;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.isNetAvailable()) {
            onNetWorkChange(NetworkStateDef.NO_NET_WORK);
        }
        //注册网络状态监听广播
        if (enableNetworkCheck) {
            if (networkChangReceiver == null) networkChangReceiver = new NetWorkChangReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (enableNetworkCheck && networkChangReceiver != null) {
            unregisterReceiver(networkChangReceiver);
        }
    }

    public class NetWorkChangReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    int type2 = networkInfo.getType();
                    String typeName = networkInfo.getTypeName();
                    switch (type2) {
                        case ConnectivityManager.TYPE_MOBILE://移动 网络
                            onNetWorkChange(NetworkStateDef.MOBILE);
                            break;
                        case ConnectivityManager.TYPE_WIFI: //wifi网络
                            onNetWorkChange(NetworkStateDef.WIFI);
                            break;
                        case ConnectivityManager.TYPE_ETHERNET:  //网线连接
                            onNetWorkChange(NetworkStateDef.ETHERNET);
                            break;
                    }
                } else {
                    // 无网络
                    onNetWorkChange(NetworkStateDef.NO_NET_WORK);
                }
            }
        }
    }

    public void onNetWorkChange(@NetworkStateDef int state) {

    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {NetworkStateDef.MOBILE, NetworkStateDef.WIFI, NetworkStateDef.ETHERNET, NetworkStateDef.NO_NET_WORK})
    public @interface NetworkStateDef {
        int MOBILE = 1;
        int WIFI = 2;
        int ETHERNET = 3;
        int NO_NET_WORK = 4;
    }
}
