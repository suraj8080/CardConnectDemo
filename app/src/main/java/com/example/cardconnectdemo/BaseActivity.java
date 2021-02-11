package com.example.cardconnectdemo;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseActivity extends AppCompatActivity {
    AlertDialog dialogRemoveCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof MainActivity && getResources().getBoolean(R.bool.is_phone)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected void showProgressDialog() {
        if (getSupportFragmentManager().findFragmentByTag(SpinningDialogFragment.TAG) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(SpinningDialogFragment.newInstance(), SpinningDialogFragment.TAG);
            transaction.commitAllowingStateLoss();
        }
    }

    /*protected void setupToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (!(this instanceof MainActivity) && getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            setChangeUrlAction();
        }
    }*/

    protected void showSnackMessage(@NonNull String message) {
        Snackbar.make(this.findViewById(android.R.id.content), message,
                Snackbar.LENGTH_SHORT).show();
    }

    protected void dismissProgressDialog() {
        SpinningDialogFragment fragment =
                (SpinningDialogFragment)getSupportFragmentManager().findFragmentByTag(SpinningDialogFragment.TAG);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    protected void showErrorDialog(@NonNull String errorMessage) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.error)).setMessage(errorMessage)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    /*private void setChangeUrlAction() {
        TextView changeUrlTextView = (TextView)findViewById(R.id.text_view_change_url);
        if (changeUrlTextView != null) {
            changeUrlTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangeUrlDialog();
                }
            });
        }
    }*/

    private void showChangeUrlDialog() {
        UrlFragmentDialog urlFragmentDialog =
                (UrlFragmentDialog)getSupportFragmentManager().findFragmentByTag(UrlFragmentDialog.TAG);

        if (urlFragmentDialog != null) {
            getSupportFragmentManager().beginTransaction().remove(urlFragmentDialog).commit();
        }

        UrlFragmentDialog.newInstance().show(getSupportFragmentManager(), UrlFragmentDialog.TAG);
    }

    public void showRemoveCardDialog() {
        if (dialogRemoveCard == null) {
            dialogRemoveCard = new AlertDialog.Builder(this).setTitle("Please Remove Card").setMessage("Please Remove Card")
                    .setCancelable(false)
                    .create();
            dialogRemoveCard.show();
        }
    }

    public void hideRemoveCardDialog() {
        if (dialogRemoveCard != null) {
            dialogRemoveCard.dismiss();
            dialogRemoveCard = null;
        }
    }
}
