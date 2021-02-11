package com.example.cardconnectdemo;

import android.os.Parcel;
import android.os.Parcelable;


import androidx.annotation.NonNull;

import com.bolt.consumersdk.CCConsumerApiBridge;
import com.bolt.consumersdk.CCConsumerApiBridgeCallbacks;
import com.bolt.consumersdk.domain.CCConsumerAccount;
import com.bolt.consumersdk.domain.response.CCConsumerApiBridgeDeleteAccountResponse;
import com.bolt.consumersdk.domain.response.CCConsumerApiBridgeGetAccountsResponse;
import com.bolt.consumersdk.domain.response.CCConsumerApiBridgeSaveAccountResponse;

/**
 * Provides an example of {@link CCConsumerApiBridge} implementation
 */
public class ApiBridgeImpl implements CCConsumerApiBridge, Parcelable {
    //Parcelable implementation required for passing this object to Consumer SDK
    public static final Creator<ApiBridgeImpl> CREATOR = new Creator<ApiBridgeImpl>() {
        @Override
        public ApiBridgeImpl createFromParcel(Parcel in) {
            return new ApiBridgeImpl(in);
        }
        @Override
        public ApiBridgeImpl[] newArray(int size) {
            return new ApiBridgeImpl[size];
        }
    };
    public ApiBridgeImpl() {
    }
    protected ApiBridgeImpl(Parcel in) {
        //unused
    }
    @Override
    public void getAccounts(@NonNull final CCConsumerApiBridgeCallbacks apiBridgeCallbacks) {
        final CCConsumerApiBridgeGetAccountsResponse response = new CCConsumerApiBridgeGetAccountsResponse();
        //TODO Implement get Accounts from Third party server here
        //TODO provide result through apiBridgeCallbacks object
    }
    @Override
    public void saveAccountToCustomer(@NonNull final CCConsumerAccount account,
                                      @NonNull final CCConsumerApiBridgeCallbacks apiBridgeCallbacks) {
        final CCConsumerApiBridgeSaveAccountResponse response = new CCConsumerApiBridgeSaveAccountResponse();
        //TODO Implement add Account to Profile from Third party server here
        //TODO provide result through apiBridgeCallbacks object
    }
    @Override
    public void deleteCustomerAccount(@NonNull CCConsumerAccount accountToDelete,
                                      @NonNull final CCConsumerApiBridgeCallbacks apiBridgeCallbacks) {
        final CCConsumerApiBridgeDeleteAccountResponse response = new CCConsumerApiBridgeDeleteAccountResponse();
        //TODO Implement remove Account to Profile from Third party server here                //TODO provide result through apiBridgeCallbacks object
    }
    @Override
    public void updateAccount(@NonNull CCConsumerAccount account,
                              @NonNull final CCConsumerApiBridgeCallbacks apiBridgeCallbacks) {
        //TODO Implement update Account to Profile from Third party server here
        //TODO provide result through apiBridgeCallbacks object
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //unused
    }
}