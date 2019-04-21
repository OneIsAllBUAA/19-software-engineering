package com.qmuiteam.qmuidemo.view;


import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import com.qmuiteam.qmuidemo.base.BaseFragment;
import com.qmuiteam.qmuidemo.base.BaseFragmentActivity;
import com.qmuiteam.qmuidemo.fragment.home.HomeFragment;
import com.qmuiteam.qmuidemo.fragment.lab.QDArchSurfaceTestFragment;
import com.qmuiteam.qmuidemo.fragment.lab.QDArchTestFragment;
import com.qmuiteam.qmuidemo.fragment.lab.QDArchWebViewTestFragment;
import com.qmuiteam.qmuidemo.fragment.util.QDNotchHelperFragment;
import com.qmuiteam.qmuidemo.model.request.LoginRequest;
import com.qmuiteam.qmuidemo.model.response.SingleMessageResponse;
import com.qmuiteam.qmuidemo.utils.DialogUtils;
import com.qmuiteam.qmuidemo.utils.UserUtils;
import static com.qmuiteam.qmuidemo.api.UserApi.login;
import static com.qmuiteam.qmuidemo.fragment.QDWebExplorerFragment.EXTRA_TITLE;
import static com.qmuiteam.qmuidemo.fragment.QDWebExplorerFragment.EXTRA_URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseFragmentActivity {


    // UI references.
    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.sign_in_button) QMUIRoundButton mEmailSignInButton;
    @BindView(R.id.topbar) QMUITopBar mTopBar;


    private static final String TAG = "LoginActivity";

    private static final String KEY_FRAGMENT = "key_fragment";
    private static final String KEY_URL = "key_url";
    private static final String KEY_TITLE = "key_title";
    private static final int VALUE_FRAGMENT_HOME = 0;
    private static final int VALUE_FRAGMENT_NOTCH_HELPER = 1;
    private static final int VALUE_FRAGMENT_ARCH_TEST = 2;
    private static final int VALUE_FRAGMENT_WEB_EXPLORER_TEST = 3;
    private static final int VALUE_FRAGMENT_SURFACE_TEST = 4;

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!TextUtils.isEmpty(UserUtils.getUserName(LoginActivity.this))){

            start();
        }
        else{
            View root = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
            ButterKnife.bind(this, root);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
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

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        Log.i(TAG, "attemptLogin: " + password);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new UserLoginTask(LoginActivity.this).execute(new LoginRequest(email, password));
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 3;
    }


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
            if (response.getMessage().equals("登陆成功")) {
                UserUtils.setUserInfo(context, mUsername, mPassword);
                start();
            } else {
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mEmailView);
            }
        }
    }

    private void start(){
        BaseFragment fragment = getFirstFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(getContextViewId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public static Intent createNotchHelperIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_NOTCH_HELPER);
        return intent;
    }

    public static Intent createArchTestIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_ARCH_TEST);
        return intent;
    }

    public static Intent createWebExplorerIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_WEB_EXPLORER_TEST);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        return intent;
    }

    public static Intent createSurfaceTestIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_SURFACE_TEST);
        return intent;
    }

    private BaseFragment getFirstFragment() {
        Intent intent = getIntent();
        int ret = intent.getIntExtra(KEY_FRAGMENT, 0);
        BaseFragment fragment;
        if (ret == VALUE_FRAGMENT_NOTCH_HELPER) {
            fragment = new QDNotchHelperFragment();
        } else if (ret == VALUE_FRAGMENT_ARCH_TEST) {
            fragment = new QDArchTestFragment();
        } else if (ret == VALUE_FRAGMENT_WEB_EXPLORER_TEST) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_URL, intent.getStringExtra(KEY_URL));
            bundle.putString(EXTRA_TITLE, intent.getStringExtra(KEY_TITLE));
            fragment = new QDArchWebViewTestFragment();
            fragment.setArguments(bundle);
        } else if (ret == VALUE_FRAGMENT_SURFACE_TEST) {
            fragment = new QDArchSurfaceTestFragment();
        } else {
            fragment = new HomeFragment();
        }

        return fragment;
    }

}

