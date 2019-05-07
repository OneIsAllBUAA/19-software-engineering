package com.qmuiteam.qmuidemo.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragmentActivity;
import com.qmuiteam.qmuidemo.model.request.ResetPasswordRequest;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.utils.DialogUtils;

import java.util.List;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.UserApi.resetPassword;

/**
 * A signup screen that offers login via email/password.
 */
public class ResetPasswordActivity extends BaseFragmentActivity{


    // UI references.
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.confirm_password) EditText mConfirmPasswordView;
    @BindView(R.id.reset_password_button) QMUIRoundButton mSignUpButton;
    @BindView(R.id.topbar) QMUITopBar mTopBar;


    private static final String TAG = "ResetPasswordActivity";

    private String email = "youShouldNotSeeThis@message.com";
    private static final String ACTION  = "android.intent.action.VIEW";

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if(null!=data){
            email = data.getQueryParameter("email");
            Log.i(TAG, "onCreate: "+data.getQueryParameter("email"));
        }else{
            Log.i(TAG, "onCreate: data is null");
        }


        View root = LayoutInflater.from(this).inflate(R.layout.activity_reset_password, null);
        ButterKnife.bind(this, root);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptResetPassword();
                    return true;
                }
                return false;
            }
        });
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    attemptResetPassword();
                }
        });
        initTopBar();
        setContentView(root);

    }


    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_color_blue));
        mTopBar.setTitle("密码重置");
    }

    private void attemptResetPassword() {
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString();
        Log.i(TAG, "attemptResetPassword: " + password);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
           new ResetPasswordTask(ResetPasswordActivity.this).execute(new ResetPasswordRequest(email, password));
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 3;
    }
    public class ResetPasswordTask extends BaseAsyncTask<ResetPasswordRequest, Void, SingleMessageResponse> {

        ResetPasswordTask(Context context) {
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(ResetPasswordRequest... params) {
            return resetPassword(params[0]);
        }

        @Override
        protected void onPostExecute(final SingleMessageResponse response) {
            super.onPostExecute(response);
            if(null!=response && response.getMessage()!=null){
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, context, mPasswordView);
            }
        }
    }
    private void start(){
        startActivity(new Intent(ResetPasswordActivity.this, QDMainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}

