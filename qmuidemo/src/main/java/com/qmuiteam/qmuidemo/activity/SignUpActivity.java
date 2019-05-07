package com.qmuiteam.qmuidemo.activity;


import android.content.Context;
import android.content.Intent;
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

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragmentActivity;
import com.qmuiteam.qmuidemo.model.request.LoginRequest;
import com.qmuiteam.qmuidemo.model.request.SignUpRequest;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.utils.DialogUtils;
import com.qmuiteam.qmuidemo.utils.UserUtils;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.UserApi.login;
import static com.qmuiteam.qmuidemo.api.UserApi.signUp;
import static com.qmuiteam.qmuidemo.utils.DialogUtils.showDialog;

/**
 * A signup screen that offers login via email/password.
 */
public class SignUpActivity extends BaseFragmentActivity {


    // UI references.
    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.confirm_password) EditText mConfirmPasswordView;
    @BindView(R.id.username) EditText mUsername;
    @BindView(R.id.sign_up_button) QMUIRoundButton mSignUpButton;
    @BindView(R.id.topbar) QMUITopBar mTopBar;


    private static final String TAG = "SignUpActivity";

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!TextUtils.isEmpty(UserUtils.getUserName(SignUpActivity.this))){
            start();
        }
        else{
            View root = LayoutInflater.from(this).inflate(R.layout.activity_signup, null);
            ButterKnife.bind(this, root);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptSignUp();
                        return true;
                    }
                    return false;
                }
            });
            mSignUpButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptSignUp();
                }
            });
            initTopBar();
            setContentView(root);
        }
    }


    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_color_blue));
        mTopBar.setTitle("登录");
    }

    private void attemptSignUp() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String username = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mConfirmPasswordView.getText().toString();
        Log.i(TAG, "attemptSignUp: " + password);
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_invalid_username));
            focusView = mUsername;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(!password.equals(passwordConfirm)){
            mConfirmPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new UserSignUpTask(SignUpActivity.this).execute(new SignUpRequest(username, email, password));
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 3;
    }


    public class UserSignUpTask extends BaseAsyncTask<SignUpRequest, Void, SingleMessageResponse> {

        private String mUsername, mPassword;
        UserSignUpTask(Context context) {
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(SignUpRequest... params) {
            mUsername = params[0].getUsername();
            mPassword = params[0].getPassword();
            return signUp(params[0]);
        }

        @Override
        protected void onPostExecute(final SingleMessageResponse response) {
            super.onPostExecute(response);
            if(null==response || null==response.getMessage()){
                DialogUtils.showDialog("网络错误",QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mEmailView);
            }else{
                if (response.getMessage().equals("注册成功！")) {
                    UserUtils.setUserInfo(context, mUsername, mPassword);
                    start();
                } else {
                    DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mEmailView);
                }
            }

        }
    }

    private void start(){
        startActivity(new Intent(SignUpActivity.this, QDMainActivity.class));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}

