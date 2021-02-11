package com.example.cardconnectdemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bolt.consumersdk.CCConsumer;

public class UrlFragmentDialog extends DialogFragment {
    public final static String TAG = com.example.cardconnectdemo.UrlFragmentDialog.class.getName();

    public static com.example.cardconnectdemo.UrlFragmentDialog newInstance() {
        return new com.example.cardconnectdemo.UrlFragmentDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShowsDialog(true);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View dialogView = View.inflate(getActivity(), R.layout.dialog_url, null);
        final EditText urlEditText = (EditText)dialogView.findViewById(R.id.edit_text_url);
        urlEditText.setText(CCConsumer.getInstance().getApi().getEndPoint());

        return new AlertDialog.Builder(getActivity()).setView(dialogView).setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CCConsumer.getInstance().getApi().setEndPoint(urlEditText.getText().toString());
                        dismiss();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).show();
    }
}
