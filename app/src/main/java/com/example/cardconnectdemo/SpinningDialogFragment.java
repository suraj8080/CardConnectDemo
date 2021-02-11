package com.example.cardconnectdemo;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class SpinningDialogFragment extends DialogFragment {

    public static final String TAG = com.example.cardconnectdemo.SpinningDialogFragment.class.getSimpleName();

    public static com.example.cardconnectdemo.SpinningDialogFragment newInstance() {
        return new com.example.cardconnectdemo.SpinningDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_fragment_spinner, container, false);

        // In order to disable default style and apply custom defined by the layout
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((ProgressBar)v.findViewById(R.id.progress)).getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShowsDialog(true);
        setCancelable(false);
    }
}
