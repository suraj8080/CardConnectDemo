package com.example.cardconnectdemo;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.bolt.consumersdk.CCConsumer;
import com.bolt.consumersdk.listeners.BluetoothSearchResponseListener;
import com.bolt.consumersdk.network.CCConsumerApi;
import com.bolt.consumersdk.swiper.enums.SwiperType;

public class MainApp extends Application {

    private static String TAG = com.example.cardconnectdemo.MainApp.class.getSimpleName();
    private static com.example.cardconnectdemo.MainApp sAppContext;

    public static CCConsumerApi getConsumerApi() {
        return CCConsumer.getInstance().getApi();
    }

    public static com.example.cardconnectdemo.MainApp getInstance() {
        return sAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = (com.example.cardconnectdemo.MainApp) getApplicationContext();

        SwiperControllerManager.getInstance().setContext(sAppContext);
        SwiperControllerManager.getInstance().setSwiperType(SwiperType.IDTech);

    }

    @Override
    public void onTerminate() {
        SwiperControllerManager.getInstance().disconnectFromDevice();
        super.onTerminate();
    }


}
