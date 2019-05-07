package com.qmuiteam.qmuidemo.activity;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragmentActivity;
import com.qmuiteam.qmuidemo.model.request.RecoverPasswordRequest;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.utils.DialogUtils;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.UserApi.recoverPassword;

/**
 * A login screen that offers login via email/password.
 */
public class RecoverPasswordActivity extends BaseFragmentActivity {


    // UI references.
    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.recover_password_button) QMUIRoundButton mRecoverPasswordButton;
    @BindView(R.id.topbar) QMUITopBar mTopBar;


    private static final String TAG = "RecoverPasswordActivity";

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_password_recover, null);
        ButterKnife.bind(this, root);
        mRecoverPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    attemptRecoverPassowrd();
                }
        });
        initTopBar();
        setContentView(root);

    }


    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_color_blue));
        mTopBar.setTitle("找回密码");
    }

    private void attemptRecoverPassowrd() {
        // Reset errors.
        mEmailView.setError(null);
        
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            new ResetPasswordTask(RecoverPasswordActivity.this).execute(new RecoverPasswordRequest(email));
        }
    }

    public class ResetPasswordTask extends BaseAsyncTask<RecoverPasswordRequest, Void, SingleMessageResponse> {

        private String mEmail;
        ResetPasswordTask(Context context) {
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(RecoverPasswordRequest... params) {
            mEmail = params[0].getEmail();
            return recoverPassword(params[0]);
        }

        @Override
        protected void onPostExecute(final SingleMessageResponse response) {
            super.onPostExecute(response);
            if(null!=response && response.getMessage()!=null)
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, context, mEmailView);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

}

