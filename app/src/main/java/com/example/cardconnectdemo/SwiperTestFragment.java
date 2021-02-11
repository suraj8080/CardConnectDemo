package com.example.cardconnectdemo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bolt.consumersdk.CCConsumer;
import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.CCConsumerError;
import com.bolt.consumersdk.listeners.BluetoothSearchResponseListener;
import com.bolt.consumersdk.swiper.CCSwiperController;
import com.bolt.consumersdk.swiper.SwiperControllerListener;
import com.bolt.consumersdk.swiper.enums.BatteryState;
import com.bolt.consumersdk.swiper.enums.SwiperCaptureMode;
import com.bolt.consumersdk.swiper.enums.SwiperError;

public class SwiperTestFragment extends BaseFragment {
    public static final String TAG = com.example.cardconnectdemo.SwiperTestFragment.class.getSimpleName();
    private int REQUEST_PERMISSIONS = 1000;
    private TextView mConnectionStateTextView;
    private Switch mSwitchSwipeOrTap;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = null;
    private boolean bConnected = false;
    private SwiperControllerListener mSwiperControllerListener = null;
    private BluetoothSearchResponseListener bluetoothSearchResponseListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_swiper_test, container, false);

        setupListeners();

        mConnectionStateTextView = (TextView) v.findViewById(R.id.text_view_connection);
        mConnectionStateTextView.setText("Attempting to Connect .");

        mSwitchSwipeOrTap = (Switch) v.findViewById(R.id.fragment_swiper_test_switchSwipeORTap);
        mSwitchSwipeOrTap.setOnCheckedChangeListener(mOnCheckedChangeListener);


        if (!checkPermission()){
            requestPermission();
           // return;
        }else {
            CCConsumer.getInstance().getApi().startBluetoothDeviceSearch(bluetoothSearchResponseListener, getActivity(), false);
        }
        updateConnectionProgress();


        return v;
    }

    private void setupListeners() {
        bluetoothSearchResponseListener = new BluetoothSearchResponseListener() {
            @Override
            public void onDeviceFound(BluetoothDevice bluetoothDevice) {
                Toast.makeText(getContext(), " Bluetooth MacAddress "+bluetoothDevice.getAddress(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, " Bluetooth MacAddress "+bluetoothDevice.getAddress() +" "+bluetoothDevice.getName() +" "+bluetoothDevice.getType());

            }
        };

        mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwitchSwipeOrTap.setEnabled(false);
                mConnectionStateTextView.setText(mConnectionStateTextView.getText() + "\r\nSWITCHING MODES...");
                if (isChecked) {
                    SwiperControllerManager.getInstance().setSwiperCaptureMode(SwiperCaptureMode.SWIPE_TAP);
                } else {
                    SwiperControllerManager.getInstance().setSwiperCaptureMode(SwiperCaptureMode.SWIPE_INSERT);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSwitchText();
                    }
                }, 20000);
            }
        };

        mSwiperControllerListener = new SwiperControllerListener() {
            @Override
            public void onTokenGenerated(CCConsumerAccount account, CCConsumerError error) {
                Log.d(TAG, "onTokenGenerated");
                dismissProgressDialog();
                if (error == null) {
                    Log.d(TAG, "Token Generated");
                    showSnackBarMessage("Token Generated: " + account.getToken());
                    mConnectionStateTextView.setText(mConnectionStateTextView.getText() + "\r\n" + "Token Generated: " + account.getToken());
                } else {
                    showErrorDialog(error.getResponseMessage());
                }
            }

            @Override
            public void onError(SwiperError swipeError) {
                Log.d(TAG, swipeError.toString());
            }

            @Override
            public void onSwiperReadyForCard() {
                Log.d(TAG, "Swiper ready for card");
                showSnackBarMessage(getString(R.string.ready_for_swipe));
            }

            @Override
            public void onSwiperConnected() {
                Log.d(TAG, "Swiper connected");
                mConnectionStateTextView.setText(R.string.connected);
                bConnected = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSwitchText();
                        resetSwiper();
                    }
                }, 2000);

                mSwitchSwipeOrTap.setEnabled(true);
            }

            @Override
            public void onSwiperDisconnected() {
                Log.d(TAG, "Swiper disconnected");
                mConnectionStateTextView.setText(R.string.disconnected);
            }

            @Override
            public void onBatteryState(BatteryState batteryState) {
                Log.d(TAG, batteryState.toString());
                switch (batteryState){
                    case LOW:
                        showSnackBarMessage("Battery is low!");
                        break;
                    case CRITICALLY_LOW:
                        showSnackBarMessage("Battery is critically low!");
                        break;
                }
            }

            @Override
            public void onStartTokenGeneration() {
                showProgressDialog();
                Log.d(TAG, "Token Generation started.");
            }

            @Override
            public void onLogUpdate(String strLogUpdate) {
                mConnectionStateTextView.setText(mConnectionStateTextView.getText() + "\r\n" + strLogUpdate);
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
                mConnectionStateTextView.setText(mConnectionStateTextView.getText() + "\r\n" + str);
            }

            @Override
            public void onRemoveCardRequested() {
                showRemoveCardDialog();
            }

            @Override
            public void onCardRemoved() {
                hideRemoveCardDialog();
                resetSwiper();
            }
        };
        SwiperControllerManager.getInstance().setSwiperControllerListener(mSwiperControllerListener);
    }

    private void hideRemoveCardDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).hideRemoveCardDialog();
        }
    }

    private void showRemoveCardDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showRemoveCardDialog();
        }
    }

    private void resetSwiper() {
        ((CCSwiperController) SwiperControllerManager.getInstance().getSwiperController()).startReaders(SwiperControllerManager.getInstance().getSwiperCaptureMode());
    }

    private void updateSwitchText() {
        switch (SwiperControllerManager.getInstance().getSwiperCaptureMode()) {
            case SWIPE_INSERT:
                mSwitchSwipeOrTap.setText("Swipe/Dip Enabled");
                if (mSwitchSwipeOrTap.isChecked()) {
                    mSwitchSwipeOrTap.setChecked(false);
                }
                break;
            case SWIPE_TAP:
                mSwitchSwipeOrTap.setText("Tap Enabled");
                if (!mSwitchSwipeOrTap.isChecked()) {
                    mSwitchSwipeOrTap.setChecked(true);
                }
                break;
        }
        mSwitchSwipeOrTap.setEnabled(true);
    }

    private Boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(getActivity(), getPermissionDeniedString(permissions[i]), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String getPermissionDeniedString(String str){
        String strResult = "";

        if (str.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
            strResult = "Bluetooth permission denied, Unable to connect to bluetooth device.";
        } else if (str.equals(Manifest.permission.RECORD_AUDIO)){
            strResult = "Record Audio permission denied, Unable to connect to audio jack device.";
        }

        return strResult;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SwiperControllerManager.getInstance().isSwiperConnected()) {
            mSwiperControllerListener.onSwiperConnected();
        } else {
            SwiperControllerManager.getInstance().connectToDevice();
            updateConnectionProgress();
        }

        switch (SwiperControllerManager.getInstance().getSwiperType()) {
            case BBPosDevice:
                Log.d("Type ", "BBPosDevice");
                mSwitchSwipeOrTap.setVisibility(View.GONE);
                break;
            case IDTech:
                mSwitchSwipeOrTap.setVisibility(View.VISIBLE);
                Log.d("Type ", "IDTech");
                break;
        }
    }

    @Override
    public void onPause() {
        CCConsumer.getInstance().getApi().removeBluetoothListener();

        super.onPause();
    }

    private void updateConnectionProgress() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!bConnected) {
                    mConnectionStateTextView.setText(mConnectionStateTextView.getText() + ".");
                    updateConnectionProgress();
                }
            }
        }, 2000);
    }
}
