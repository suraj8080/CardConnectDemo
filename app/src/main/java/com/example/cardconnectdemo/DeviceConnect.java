package com.example.cardconnectdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.CCConsumerCardInfo;
import com.bolt.consumersdk.domain.CCConsumerError;
import com.bolt.consumersdk.swiper.CCSwiperController;
import com.bolt.consumersdk.swiper.SwiperControllerListener;
import com.bolt.consumersdk.swiper.enums.BatteryState;
import com.bolt.consumersdk.swiper.enums.SwiperCaptureMode;
import com.bolt.consumersdk.swiper.enums.SwiperError;

public class DeviceConnect extends AppCompatActivity {
    public static final String TAG = com.example.cardconnectdemo.DeviceConnect.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private SwiperControllerListener mSwiperControllerListener = null;
    private boolean bConnected = false;
    private String mConnectionState;
    private String accountToken;
    private String errorMessage;
    private Context mContext;

    public void generateToken(String tokeniseUrl, String amount){

    }

    public DeviceConnect(String mode, Context context){
        mContext = context;
        if(mode.equals("swipe"))
            SwiperControllerManager.getInstance().setSwiperCaptureMode(SwiperCaptureMode.SWIPE_TAP);
        else SwiperControllerManager.getInstance().setSwiperCaptureMode(SwiperCaptureMode.SWIPE_INSERT);
        requestRecordAudioPermission();
        setupListeners();
    }


    private void requestRecordAudioPermission(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            //initSwiperForTokenGeneration(MainApp.getInstance().getSwiperType());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //initSwiperForTokenGeneration(MainApp.getInstance().getSwiperType());
            }
        }
    }


    private void setupListeners(){
        mSwiperControllerListener = new SwiperControllerListener() {
            @Override
            public void onTokenGenerated(CCConsumerAccount account, CCConsumerError error) {
                Log.d(TAG, "onTokenGenerated");
                if (error == null) {
                    Log.d(TAG, "Token Generated "+account.getToken());
                    accountToken = account.getToken();
                } else {
                    errorMessage = error.getResponseMessage();
                }
            }

            @Override
            public void onError(SwiperError swipeError) {
                Log.d(TAG, swipeError.toString());
                errorMessage = swipeError.toString();
            }

            @Override
            public void onSwiperReadyForCard() {
                Log.d(TAG, "Swiper ready for card");
                mConnectionState = "Swiper ready for card";
            }

            @Override
            public void onSwiperConnected() {
                Log.d(TAG, "Swiper connected");
                mConnectionState = "Swiper Connected";
                bConnected = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetSwiper();
                    }
                }, 2000);

               // mSwitchSwipeOrTap.setEnabled(true);
            }

            @Override
            public void onSwiperDisconnected() {
                Log.d(TAG, "Swiper disconnected");
                mConnectionState = "Swiper Disconnected";
            }

            @Override
            public void onBatteryState(BatteryState batteryState) {
                Log.d(TAG, batteryState.toString());
                switch (batteryState){
                    case LOW:
                        mConnectionState = "Battery is low!";
                        break;
                    case CRITICALLY_LOW:
                        mConnectionState = "Battery is critically low!";
                        break;
                }
            }

            @Override
            public void onStartTokenGeneration() {
                Log.d(TAG, "Token Generation started.");
                mConnectionState = "Token Generation started.";
            }

            @Override
            public void onLogUpdate(String strLogUpdate) {
                mConnectionState = strLogUpdate;
                //mConnectionStateTextView.setText(mConnectionStateTextView.getText() + "\r\n" + strLogUpdate);
            }

            @Override
            public void onDeviceConfigurationUpdate(String s) {
            }

            @Override
            public void onConfigurationProgressUpdate(double v) {

            }

            @Override
            public void onConfigurationComplete(boolean b) {

            }

            @Override
            public void onTimeout() {

                //resetSwiper();
            }

            @Override
            public void onLCDDisplayUpdate(String str) {
                mConnectionState = str;
            }

            @Override
            public void onRemoveCardRequested() {
                mConnectionState = "Show Remove Card Dialog";
            }

            @Override
            public void onCardRemoved() {
                mConnectionState = "Hide Remove Card Dialog";
                resetSwiper();
            }
        };
        SwiperControllerManager.getInstance().setSwiperControllerListener(mSwiperControllerListener);
    }

    private void resetSwiper() {
        ((CCSwiperController) SwiperControllerManager.getInstance().getSwiperController()).startReaders(SwiperControllerManager.getInstance().getSwiperCaptureMode());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SwiperControllerManager.getInstance().isSwiperConnected()) {
            mSwiperControllerListener.onSwiperConnected();
        } else {
            SwiperControllerManager.getInstance().connectToDevice();
        }
    }

}
