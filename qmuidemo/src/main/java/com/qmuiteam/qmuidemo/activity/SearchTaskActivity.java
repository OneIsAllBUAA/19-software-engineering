package com.qmuiteam.qmuidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmuidemo.R;
import com.qmuiteam.qmuidemo.base.BaseAsyncTask;
import com.qmuiteam.qmuidemo.base.BaseFragmentActivity;
import com.qmuiteam.qmuidemo.fragment.task.TaskDetailFragment;
import com.qmuiteam.qmuidemo.model.request.SearchTaskRequest;
import com.qmuiteam.qmuidemo.model.response.Fields;
import com.qmuiteam.qmuidemo.model.response.Task;
import com.qmuiteam.qmuidemo.model.response.TaskListResult;
import com.qmuiteam.qmuidemo.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qmuiteam.qmuidemo.api.TaskApi.searchTask;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTemplateName;
import static com.qmuiteam.qmuidemo.constants.TaskTypes.getTypeName;

public class SearchTaskActivity extends BaseFragmentActivity {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.searchBar)
    MaterialSearchBar mSearchBar;
    @BindView(R.id.task_search_groupListView)
    QMUIGroupListView mGroupListView;

    private int size;

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_search, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initSearchBar();
        initGroupListView();
        setContentView(root);
    }

    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_color_blue));
        mTopBar.setTitle("搜索任务");
    }

    private void initGroupListView(){
        size = QMUIDisplayHelper.dp2px(SearchTaskActivity.this, 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(SearchTaskActivity.this);
        section.setTitle("搜索结果").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        section.addTo(mGroupListView);
    }

    private void initSearchBar(){
        mSearchBar.setHint("输入任务名称");
        mSearchBar.setSpeechMode(false);
        mSearchBar.setBackgroundColor(getResources().getColor(R.color.searchBarPlaceholderColorDark));
        mSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if(!TextUtils.isEmpty(mSearchBar.getText()) ){
                    search(mSearchBar.getText());
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });
    }

    private void search(String text){
        new SearchTask(SearchTaskActivity.this).execute(new SearchTaskRequest(text));
    }

    public class SearchTask extends BaseAsyncTask<SearchTaskRequest, Void, TaskListResult> {

        SearchTask(Context context) {
            super(context);
        }

        @Override
        protected TaskListResult doInBackground(SearchTaskRequest... params) {
            return searchTask(params[0]);
        }

        @Override
        protected void onPostExecute(final TaskListResult response) {
            super.onPostExecute(response);
            if(null==response || null==response.getResultArray()){
                DialogUtils.showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mGroupListView);
            }else{
                fillInSearchResult(response);
            }
        }
    }

    private void fillInSearchResult(TaskListResult response){
        mGroupListView.removeAllViews();
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(SearchTaskActivity.this);
        section.setTitle("搜索结果").setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(Task task : response.getResultArray()){
            Fields fields = task.getFields();
            String name = fields.getName();
            String templateName = getTemplateName(fields.getTemplate());
            String typeName = getTypeName(fields.getType());
            QMUICommonListItemView item = mGroupListView.createItemView(name);
            item.setDetailText(templateName + typeName);
            item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section.addItemView(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
                    taskDetailFragment.setTask(task);
                    startFragment(taskDetailFragment);
                    */
                }
            });
        }
        section.addTo(mGroupListView);
    }
}
