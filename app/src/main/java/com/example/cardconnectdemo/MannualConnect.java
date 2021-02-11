package com.example.cardconnectdemo;
import android.util.Log;

import com.bolt.consumersdk.CCConsumerTokenCallback;
import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.CCConsumerCardInfo;
import com.bolt.consumersdk.domain.CCConsumerError;
import com.bolt.consumersdk.swiper.SwiperControllerListener;

public class MannualConnect {
    private CCConsumerCardInfo mCCConsumerCardInfo;
    private SwiperControllerListener mSwiperControllerListener = null;
    private String accountToken;
    private String errorMessage;

    public void generateToken(String tokeniseUrl, String cardNumber, String amount){

        SwiperControllerManager.getInstance().setSwiperControllerListener(mSwiperControllerListener);
        mCCConsumerCardInfo = new CCConsumerCardInfo();
        mCCConsumerCardInfo.setCardNumber(cardNumber);
        mCCConsumerCardInfo.setCvv("123");
        mCCConsumerCardInfo.setExpirationDate("09/23");
        //showProgressDialog();

        MainApp.getConsumerApi().setEndPoint(tokeniseUrl);
        MainApp.getConsumerApi().generateAccountForCard(mCCConsumerCardInfo, new CCConsumerTokenCallback() {
            @Override
            public void onCCConsumerTokenResponseError(CCConsumerError error) {
                errorMessage = error.getResponseMessage();
                Log.d("accountToken ", errorMessage);
            }

            @Override
            public void onCCConsumerTokenResponse(CCConsumerAccount consumerAccount) {
                //dismissProgressDialog();
                accountToken = consumerAccount.getToken();
                Log.d("accountToken ", accountToken);
            }
        });
    }




}
