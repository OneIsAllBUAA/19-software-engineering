package com.oneisall.Views;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.oneisall.Api.TaskApi;
import com.oneisall.Model.AllTasksRequestResult;
import com.oneisall.R;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "HomeActivity";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_all_tasks);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_my_tasks);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_personal_info);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        new GetAllTasks().execute();
    }

    private static class GetAllTasks extends AsyncTask<Void, Void, AllTasksRequestResult>{
        @Override
        protected AllTasksRequestResult doInBackground(Void... voids) {
            return TaskApi.getAllTasks(null);
        }

        @Override
        protected void onPostExecute(AllTasksRequestResult allTasksRequestResult) {
            Log.i(TAG, "onPostExecute: " + allTasksRequestResult.getResultArray().get(0).getFields().getC_time());
        }
    }


}
