package com.oneisall.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.oneisall.R;
import com.oneisall.model.response.Task;
import com.oneisall.model.response.TaskListResult;
import com.oneisall.tasks.TaskDetailActivity;
import com.oneisall.utils.DpUtils;
import com.oneisall.views.custom.ItemTaskView;
import com.oneisall.views.filter.ConstellationAdapter;
import com.oneisall.views.filter.GirdDropDownAdapter;
import com.oneisall.views.filter.ListDropDownAdapter;
import com.yyydjk.library.DropDownMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class TaskDisplayActivity extends SwipeBackActivity {

    //
    final private static String TAG = "TaskDisplayActivity";
    private TaskListResult result;

    //
    private SwipeBackLayout mSwipeBackLayout;
    private DropDownMenu mFilterMenu;
    @BindView(R.id.task_display_back_button)
    ImageView mBackBtn;

    //
//    private String headers[] = {"类别", "模板", "排序", "积分高于"};
//    private String headers[] = {"类别", "模板", "排序", "状态","积分高于"};
    private String headers[] = {"类别", "模板", "排序", "状态"};
    private List<View> popupViews = new ArrayList<>();

    private GirdDropDownAdapter typeAdapter;
    private ListDropDownAdapter templateAdapter;
    private ListDropDownAdapter orderAdapter;
    private ListDropDownAdapter stateAdapter;
    private ListDropDownAdapter creditAdapter;
    private ConstellationAdapter constellationAdapter;

    private String types[] = {"不限", "单选", "多选", "问答", "标注"};
    private String templates[] = {"不限", "图片", "视频", "音频"};
    private String orders[] = {"最新发布", "参与人数升序", "参与人数降序","积分升序", "积分降序"};
    private String states[] = {"开放", "关闭","不限" };
    private String credits[] = new String[30];

    private int constellationPosition = 0;

    //
    private int mType = 0;
    private int mTemplate = 0;
    private int mOrder = 0;
    private int mState = 0;
    LinearLayout contentView;
    boolean isSearchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        // 2. 绑定视图
        setContentView(R.layout.activity_display_task);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        result = (TaskListResult) intent.getSerializableExtra("result");
        mType = intent.getIntExtra("type",0);
        mSwipeBackLayout = getSwipeBackLayout();
        mFilterMenu = (DropDownMenu)findViewById(R.id.dropDownMenu);
        //
        initView();
        refreshTaskList();
//        mFilterMenu.mFilterMenu.setDropDownMenu(tabs, popupViews, contentView);
    }
    private void initView() {
        //init types menu
        final ListView typeView = new ListView(this);
        typeAdapter = new GirdDropDownAdapter(this, Arrays.asList(types));
        typeView.setDividerHeight(0);
        typeView.setAdapter(typeAdapter);

        //init templates menu
        final ListView templateView = new ListView(this);
        templateView.setDividerHeight(0);
        templateAdapter = new ListDropDownAdapter(this, Arrays.asList(templates));
        templateView.setAdapter(templateAdapter);

        //init order menu
        final ListView orderView = new ListView(this);
        orderView.setDividerHeight(0);
        orderAdapter = new ListDropDownAdapter(this, Arrays.asList(orders));
        orderView.setAdapter(orderAdapter);

        //init state menu
        final ListView stateView = new ListView(this);
        stateView.setDividerHeight(0);
        stateAdapter = new ListDropDownAdapter(this, Arrays.asList(states));
        stateView.setAdapter(stateAdapter);

        //init popupViews
        popupViews.add(typeView);
        popupViews.add(templateView);
        popupViews.add(orderView);
        popupViews.add(stateView);

        if(mType!=0){
            typeAdapter.setCheckItem(mType);
            isSearchResult = false;
        }
        else{
            isSearchResult = true;
            Log.i(TAG, "is search!");
        }
        //add item click event
        typeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typeAdapter.setCheckItem(position);
                mFilterMenu.setTabText(position == 0 ? headers[0] : types[position]);
                mFilterMenu.closeMenu();
                mType = position;
                refreshTaskList();
            }
        });

        templateView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                templateAdapter.setCheckItem(position);
                mFilterMenu.setTabText(position == 0 ? headers[1] : templates[position]);
                mFilterMenu.closeMenu();
                mTemplate = position;
                refreshTaskList();
            }
        });

        orderView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderAdapter.setCheckItem(position);
                mFilterMenu.setTabText(position == 0 ? headers[2] : orders[position]);
                mFilterMenu.closeMenu();
                mOrder = position;
                refreshTaskList();
            }
        });

        stateView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateAdapter.setCheckItem(position);
                mFilterMenu.setTabText(position == 0 ? headers[2] : states[position]);
                mFilterMenu.closeMenu();
                mState = position;
                refreshTaskList();
            }
        });

        //返回按钮
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //S
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //init context view
        contentView = new LinearLayout(this);
        contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        contentView.setOrientation(LinearLayout.VERTICAL);

        //init dropdownview
        scrollView.addView(contentView);
        mFilterMenu.setDropDownMenu(Arrays.asList(headers), popupViews, scrollView);
    }

    private void refreshTaskList(){
        contentView.removeAllViews();
        if(isSearchResult) {
            TextView textView = newText("搜索结果",Gravity.LEFT, 16);
            textView.setPadding(DpUtils.dip2px(this,10), DpUtils.dip2px(this,5),0, DpUtils.dip2px(this,5));
            contentView.addView(textView);
        }
        List<Task> list = getList();
        for(Task task: list){
            //set task view
            ItemTaskView taskView = new ItemTaskView(this,task);
            if(list.size()==1){
                taskView.setBottomDividerVisibilty(View.GONE);
            }
            taskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTaskDetailActivity(task);
                }
            });
            //add to layout
            contentView.addView(taskView);
        }
        Log.i(TAG, "num:"+contentView.getChildCount());
        contentView.addView(newText("暂无更多",Gravity.CENTER, 14));
    }

    private List<Task> getList(){
        List<Task> tList = new ArrayList<>();
        Task task;
        for(int i=result.getResultArray().size()-1; i>=0; i--){
            task = result.getResultArray().get(i);
            if(mType!=0){
                if(task.getFields().getType()!=mType){
                    continue;
                }
            }
            if(mTemplate!=0){
                if(task.getFields().getTemplate()!=mTemplate){
                    continue;
                }
            }
            if(mState!=2){
                if((mState==1 && task.getFields().isIs_closed()) ||
                        (mState ==0 && !task.getFields().isIs_closed())){

                }
                else continue;
            }
            tList.add(task);
        }
        //排序
        return getOrderedList(tList);
    }

    private List<Task> getOrderedList(List<Task> tList){
        switch (mOrder){
            case 1:{
                //参与人数升序
                return voteNumAscend(tList);
            }
            case 2:{
                //参与人数降序
                return voteNumDescend(tList);
            }
            case 3:{
                //积分升序
                return creditAscend(tList);
            }
            case 4:{
                //积分降序
                return creditDescend(tList);
            }
            default:{
                return tList;
            }
        }

    }
    private List<Task> voteNumAscend(List<Task> list){
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                //升序
                return (o2.getFields().getNum_worker()<o1.getFields().getNum_worker())?1:-1;
            }
        });
        return list;
    }

    private List<Task> voteNumDescend(List<Task> list){
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                //升序
                return (o2.getFields().getNum_worker()>o1.getFields().getNum_worker())?1:-1;
            }
        });
        return list;
    }

    private List<Task> creditAscend(List<Task> list){
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                //升序
                return (o2.getFields().getCredit()<o1.getFields().getCredit())?1:-1;
            }
        });
        return list;
    }

    private List<Task> creditDescend(List<Task> list){
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                //升序
                return (o2.getFields().getCredit()>o1.getFields().getCredit())?1:-1;
            }
        });
        return list;
    }


    private TextView newText(String text, int gravity, int size){
        //提示词
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setGravity(gravity);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        textView.setPadding(0, DpUtils.dip2px(this,20),0, DpUtils.dip2px(this,20));
        textView.setTextColor(getResources().getColor(R.color.text_black));
        return textView;
    }

    /*
     * start next activity
     */
    private void startTaskDetailActivity(Task task){
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("task",task);
        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mFilterMenu.isShowing()) {
            mFilterMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
