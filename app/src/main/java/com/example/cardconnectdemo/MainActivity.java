package com.example.cardconnectdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bolt.consumersdk.CCConsumer;
import com.bolt.consumersdk.CCConsumerTokenCallback;
import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.CCConsumerCardInfo;
import com.bolt.consumersdk.domain.CCConsumerError;
import com.bolt.consumersdk.enums.CCConsumerMaskFormat;
import com.bolt.consumersdk.swiper.SwiperControllerListener;
import com.bolt.consumersdk.swiper.enums.SwiperCaptureMode;
import com.bolt.consumersdk.views.CCConsumerCreditCardNumberEditText;
import com.bolt.consumersdk.views.CCConsumerCvvEditText;
import com.bolt.consumersdk.views.CCConsumerExpirationDateEditText;
import com.bolt.consumersdk.views.CCConsumerUiTextChangeListener;
import com.bolt.consumersdk.views.payment.accounts.PaymentAccountsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private CCConsumerCreditCardNumberEditText mCardNumberEditText;
    private CCConsumerExpirationDateEditText mExpirationDateEditText;
    private CCConsumerCvvEditText mCvvEditText;
    private EditText mPostalCodeEditText;
    private CCConsumerCardInfo mCCConsumerCardInfo;
    private TextView mConnectionStatus;
    private SwiperControllerListener mSwiperControllerListener = null;
    private Switch switchUsingDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupListeners();
        /* */
        switchUsingDevice = (Switch) findViewById(R.id.switchUsingDevice);
        switchUsingDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Using Device ", ""+isChecked);
                if (isChecked) {
                    switchUsingDevice.setEnabled(false);
                    startActivity(new Intent(MainActivity.this, SwiperTestActivity.class));
                } else {

                }
            }
        });

        mCardNumberEditText = (CCConsumerCreditCardNumberEditText)findViewById(R.id.text_edit_credit_card_number);
        mExpirationDateEditText =
                (CCConsumerExpirationDateEditText)findViewById(R.id.text_edit_credit_card_expiration_date);
        mCvvEditText = (CCConsumerCvvEditText)findViewById(R.id.text_edit_credit_card_cvv);
        mPostalCodeEditText = (EditText)findViewById(R.id.text_edit_credit_card_postal_code);
        mConnectionStatus = (TextView)findViewById(R.id.text_view_connection_status);
        mCCConsumerCardInfo = new CCConsumerCardInfo();
        mCardNumberEditText.setCreditCardTextChangeListener(
                new CCConsumerUiTextChangeListener() {
                    @Override
                    public void onTextChanged() {
                        // This callback will be used for displaying custom UI showing validation completion
                        if (!mCardNumberEditText.isCardNumberValid() && mCardNumberEditText.getText().length() != 0) {
                            mCardNumberEditText.setError(getString(R.string.card_not_valid));
                        } else {
                            mCardNumberEditText.setError(null);
                        }
                    }
                });

        mExpirationDateEditText.setExpirationDateTextChangeListener(new CCConsumerUiTextChangeListener() {
            @Override
            public void onTextChanged() {
                // This callback will be used for displaying custom UI showing validation completion
                if (!mExpirationDateEditText.isExpirationDateValid() &&
                        mExpirationDateEditText.getText().length() != 0) {
                    mExpirationDateEditText.setError(getString(R.string.date_not_valid));
                } else {
                    mExpirationDateEditText.setError(null);
                }
            }
        });

        mCvvEditText.setCvvTextChangeListener(new CCConsumerUiTextChangeListener() {
            @Override
            public void onTextChanged() {
                // This callback will be used for displaying custom UI showing validation completion
                if (!mCvvEditText.isCvvCodeValid() && mCvvEditText.getText().length() != 0) {
                    mCvvEditText.setError(getString(R.string.cvv_not_valid));
                } else {
                    mCvvEditText.setError(null);
                }
            }
        });

        setupTabMaskOptions();
        // Request android permissions for Swiper
        requestRecordAudioPermission();
    }

    private void setupListeners(){
        SwiperControllerManager.getInstance().setSwiperControllerListener(mSwiperControllerListener);
    }

    private void setupTabMaskOptions() {
        TabLayout maskOptionsTabLayout = (TabLayout)findViewById(R.id.tab_layout_mask_options);
        maskOptionsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Selected LAST_FOUR", Toast.LENGTH_LONG).show();
                        mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.LAST_FOUR);
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Selected FIRST_LAST_FOUR", Toast.LENGTH_LONG).show();
                        mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.FIRST_LAST_FOUR);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "Selected CARD_MASK_LAST_FOUR", Toast.LENGTH_LONG).show();
                        mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.CARD_MASK_LAST_FOUR);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Unused
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Update default preselection
                if (tab.getPosition() == 0) {
                    mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.LAST_FOUR);
                }
            }
        });
        TabLayout.Tab selectedTab = maskOptionsTabLayout.getTabAt(0);
        if (selectedTab != null) {
            selectedTab.select();
        }
    }

    public void generateToken(View view) {


        //new MannualConnect().generateToken("https://fts-uat.cardconnect.com/cardsecure/api/v1/ccn/tokenize", "2226350117310279", "50");

        // If using Custom UI Card object needs to be populated from within the component.
        mCardNumberEditText.setCardNumberOnCardInfo(mCCConsumerCardInfo);
        mExpirationDateEditText.setExpirationDateOnCardInfo(mCCConsumerCardInfo);
        mCvvEditText.setCvvCodeOnCardInfo(mCCConsumerCardInfo);
        Log.d("Selected Tab ", mCardNumberEditText.getCCConsumerMaskFormat().name());
        if (!TextUtils.isEmpty(mPostalCodeEditText.getText())) {
            mCCConsumerCardInfo.setPostalCode(mPostalCodeEditText.getText().toString());
        }

        if (!mCCConsumerCardInfo.isCardValid()) {
            showErrorDialog(getString(R.string.card_invalid));
            return;
        }

        showProgressDialog();

        //MainApp.getConsumerApi().setEndPoint("https://fts.cardconnect.com:6443");
        MainApp.getConsumerApi().setEndPoint(getString(R.string.cardconnect_uat_post_url));
        MainApp.getConsumerApi().generateAccountForCard(mCCConsumerCardInfo, new CCConsumerTokenCallback() {
            @Override
            public void onCCConsumerTokenResponseError(CCConsumerError error) {
                dismissProgressDialog();
                showErrorDialog(error.getResponseMessage());
            }

            @Override
            public void onCCConsumerTokenResponse(CCConsumerAccount consumerAccount) {
                dismissProgressDialog();
                showSnackBarMessage(consumerAccount.getToken());
                mCardNumberEditText.getText().clear();
                mCvvEditText.getText().clear();
                mExpirationDateEditText.getText().clear();
                mPostalCodeEditText.getText().clear();
            }
        });
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.token_generated_format,
                message), Snackbar.LENGTH_SHORT).show();
    }

    private void requestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchUsingDevice.setEnabled(true);
        switchUsingDevice.setChecked(false);
    }
}
