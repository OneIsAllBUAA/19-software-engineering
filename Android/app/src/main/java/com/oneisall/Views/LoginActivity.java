package com.oneisall.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oneisall.R;
import com.oneisall.base.BaseAsyncTask;
import com.oneisall.model.request.LoginRequest;
import com.oneisall.model.request.RecoverPasswordRequest;
import com.oneisall.model.request.SignUpRequest;
import com.oneisall.model.response.SingleMessageResponse;
import com.oneisall.utils.DialogUtils;
import com.oneisall.utils.UserUtils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.vondear.rxui.view.dialog.RxDialogEditSureCancel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oneisall.api.UserApi.login;
import static com.oneisall.api.UserApi.recoverPassword;
import static com.oneisall.api.UserApi.signUp;
import static com.vondear.rxtool.RxConstTool.REGEX_EMAIL;

//import android.support.v7.app.AppCompatActivity;

//import android.support.design.widget.Snackbar;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    final private static String TAG = "LoginActivity";
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
//    private AutoCompleteTextView mEmailView;
//    private EditText mPasswordView;
//    private View mProgressView;
//    private View mLoginFormView;
    //
    @BindView(R.id.login_switch) Button mSelectLogin;
    @BindView(R.id.sign_up_switch) Button mSelectSignUp;
    @BindView(R.id.login_progress) ProgressBar mProgressView;
    //
    @BindView(R.id.email) EditText mLoginEmail;
    @BindView(R.id.password) EditText mLoginPwd;
    @BindView(R.id.login_btn) TextView mLogin;
    @BindView(R.id.forget_password) TextView mForget;
    @BindView(R.id.login_form) LinearLayout mLoginForm;
    //
    @BindView(R.id.username) EditText mSignupName;
    @BindView(R.id.pwd_1) EditText mPwd1;
    @BindView(R.id.pwd_2) EditText mPwd2;
    @BindView(R.id.user_email) EditText mSignupEmail;
    @BindView(R.id.sign_up) TextView mSignUp;
    @BindView(R.id.signup_form) LinearLayout mSignUpForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!TextUtils.isEmpty(UserUtils.getUserName(LoginActivity.this))){
            start();
        }
        else {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            // Set up the login form.
            mLoginPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
            mLogin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            mSignUp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptSignUp();
                }
            });
            //初始化为登录界面
            mSelectLogin.setBackgroundColor(getResources().getColor(R.color.blue_2196F3));
            mSelectLogin.setTextColor(Color.parseColor("#ffffff"));
            mSelectSignUp.setBackgroundColor(Color.parseColor("#ffffff"));
            mSelectSignUp.setTextColor(getResources().getColor(R.color.colorPrimary));
            mLoginForm.setVisibility(View.VISIBLE);
            mSignUpForm.setVisibility(View.GONE);
            //密码不可见
            mLoginPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );//设置密码不可见
            mPwd1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );//设置密码不可见
            mPwd2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );//设置密码不可见

            //设置忘记密码
            mForget.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(LoginActivity.this);
                    rxDialogEditSureCancel.getTitleView().setBackgroundResource(R.mipmap.oneisall_icon_1);
                    EditText editText = rxDialogEditSureCancel.getEditText();
                    editText.setHint("请在此输入您的注册邮箱...");
                    editText.setTextSize(14);
                    rxDialogEditSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String myEmail = editText.getText().toString();
                            new ResetPasswordTask(LoginActivity.this).execute(new RecoverPasswordRequest(myEmail));
                            rxDialogEditSureCancel.cancel();
                        }
                    });
                    rxDialogEditSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rxDialogEditSureCancel.cancel();
                        }
                    });
                    rxDialogEditSureCancel.show();
                }
            });
            //设置登录注册切换监听器
            mSelectLogin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectLogin.setBackgroundColor(getResources().getColor(R.color.blue_2196F3));
                    mSelectLogin.setTextColor(Color.parseColor("#ffffff"));
                    mSelectSignUp.setBackgroundColor(Color.parseColor("#ffffff"));
                    mSelectSignUp.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mLoginForm.setVisibility(View.VISIBLE);
                    mSignUpForm.setVisibility(View.GONE);
                }
            });
            mSelectSignUp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectSignUp.setBackgroundColor(getResources().getColor(R.color.blue_2196F3));
                    mSelectSignUp.setTextColor(Color.parseColor("#ffffff"));
                    mSelectLogin.setBackgroundColor(Color.parseColor("#ffffff"));
                    mSelectLogin.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mLoginForm.setVisibility(View.GONE);
                    mSignUpForm.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginEmail.setError(null);
        mLoginPwd.setError(null);

        // Store values at the time of the login attempt.
        String email = mLoginEmail.getText().toString();
        String password = mLoginPwd.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mLoginPwd.setError(getString(R.string.error_invalid_password));
            focusView = mLoginPwd;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mLoginEmail.setError(getString(R.string.error_field_required));
            focusView = mLoginEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mLoginEmail.setError(getString(R.string.error_invalid_email));
            focusView = mLoginEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new UserLoginTask(LoginActivity.this).execute(new LoginRequest(email, password));
        }
    }

    private void attemptSignUp(){
        // Reset errors.
        mSignupName.setError(null);
        mPwd1.setError(null);

        // Store values at the time of the login attempt.
        String email = mSignupEmail.getText().toString();
        String username = mSignupName.getText().toString();
        String password = mPwd1.getText().toString();
        String passwordConfirm = mPwd2.getText().toString();
        Log.i(TAG, "attemptSignUp: " + password);
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mSignupName.setError(getString(R.string.error_invalid_username));
            focusView = mSignupName;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPwd1.setError(getString(R.string.error_invalid_password));
            focusView = mPwd1;
            cancel = true;
        }

        if(!password.equals(passwordConfirm)){
            mPwd2.setError(getString(R.string.error_incorrect_password));
            focusView = mPwd2;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mSignupEmail.setError(getString(R.string.error_field_required));
            focusView = mSignupEmail;
            cancel = true;
        }
        else if(!isEmailValid(email)){
            mSignupEmail.setError(getString(R.string.error_invalid_email));
            focusView = mSignupEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new UserSignUpTask(LoginActivity.this).execute(new SignUpRequest(username, email, password));
        }
    }
    private boolean isEmailValid(String email) {
        if(email.contains("@")){
            return email.matches(REGEX_EMAIL);
        }
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends BaseAsyncTask<LoginRequest, Void, SingleMessageResponse> {

        private String mUsername, mPassword;
        UserLoginTask(Context context) {
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(LoginRequest... params) {
            mUsername = params[0].getUsername();
            mPassword = params[0].getPassword();
            return login(params[0]);
        }

        @Override
        protected void onPostExecute(final SingleMessageResponse response) {
            super.onPostExecute(response);
            if(null==response || null==response.getMessage()){
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, LoginActivity.this, mLoginForm);
            }else{
                if (response.getMessage().contains("登陆成功")) {
                    UserUtils.setUserInfo(LoginActivity.this, mUsername, mPassword);
                    start();
                } else {
                    DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mLoginForm);
                }
            }
        }
    }
    private void start(){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
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
                DialogUtils.showDialog("网络错误",QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mSignUpForm);
            }else{
                if (response.getMessage().equals("注册成功！")) {
                    UserUtils.setUserInfo(context, mUsername, mPassword);
                    start();
                } else {
                    DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mSignUpForm);
                }
            }

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
            if(null!=response && response.getMessage()!=null) {
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_INFO, LoginActivity.this, mLoginForm);
            }
        }
    }
}

