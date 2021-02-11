package com.example.cardconnectdemo;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {

    protected void showErrorDialog(@NonNull String errorMessage) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showErrorDialog(errorMessage);
        }
    }

    protected void showSnackBarMessage(@NonNull String message) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showSnackMessage(message);
        }
    }

    protected void showProgressDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showProgressDialog();
        }
    }

    protected void dismissProgressDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).dismissProgressDialog();
        }
    }
}
