package com.oneisall.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.oneisall.R;
import com.oneisall.base.BaseAsyncTask;
import com.oneisall.model.request.AllTasksRequest;
import com.oneisall.model.response.TaskListResult;
import com.oneisall.utils.DialogUtils;
import com.oneisall.utils.UserUtils;
import com.oneisall.views.MySearchView.ICallBack;
import com.oneisall.views.MySearchView.SearchView;
import com.oneisall.views.MySearchView.bCallBack;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.oneisall.api.TaskApi.getAllTasks;

public class SearchActivity extends SwipeBackActivity {
    // 1. 初始化搜索框变量
    @BindView(R.id.search_view) SearchView mSearchView ;
    private SwipeBackLayout mSwipeBackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        // 2. 绑定视图
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mSwipeBackLayout = getSwipeBackLayout();

        // 4. 设置点击键盘上的搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        mSearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                if(string.trim().equals("")){
                    DialogUtils.showDialog2s("查询字段不可为空！", QMUITipDialog.Builder.ICON_TYPE_FAIL,SearchActivity.this,mSearchView);
                    return;
                }
                else if(string.contains("'")){
                    DialogUtils.showDialog("请不要包含特殊字符", QMUITipDialog.Builder.ICON_TYPE_FAIL,SearchActivity.this,mSearchView);
                    return;
                }
                new GetAllTasks(SearchActivity.this).execute(new AllTasksRequest(UserUtils.getUserName(SearchActivity.this),string));
//                finish();
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        mSearchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });
    }

    private void startTaskDisplay(TaskListResult result){
//        Toast.makeText(this, "start taskDisplay!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TaskDisplayActivity.class);
        intent.putExtra("result",result);
        startActivity(intent);
        finish();
    }

    private class GetAllTasks extends BaseAsyncTask<AllTasksRequest, Void, TaskListResult> {

        final private static String TAG = "GetAllTasks";

        private GetAllTasks(Context context){
            super(context);
        }

        @Override
        protected TaskListResult doInBackground(AllTasksRequest... allTasksRequests) {
            return getAllTasks(allTasksRequests[0]);
        }

        @Override
        protected void onPostExecute(TaskListResult taskListResult) {
            super.onPostExecute(taskListResult);
            if(taskListResult != null){
                Log.i(TAG, "onPostExecute: "+taskListResult.toString());
                //TODO:填充进入任务结果
                startTaskDisplay(taskListResult);
            }else{
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mSearchView);
            }
        }
    }
}
