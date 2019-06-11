package com.oneisall.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.oneisall.R;
import com.oneisall.model.response.Task;
import com.oneisall.tasks.TaskDetailActivity;
import com.oneisall.views.custom.ItemTaskView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class MyTaskListFragment extends Fragment {
    private final static String TAG = "MyTaskListFragment";
    private List<Task> tasks;
    private List<Task> grabbed;
    private Context context;
    private OnRefreshListener refreshListener;
    private boolean forCheck=false;
    private boolean isDoing = false;
    //
    private LinearLayout tasksLinear;
    private RefreshLayout refreshLayout;

    public static MyTaskListFragment getInstance(List<Task> tasks,Context context) {
        MyTaskListFragment fragment = new MyTaskListFragment();
        fragment.tasks = tasks;
        fragment.context = context;
        fragment.grabbed=null;
        return fragment;
    }
    public static MyTaskListFragment getInstance(List<Task> tasks,List<Task> grabbed, Context context) {
        MyTaskListFragment fragment = new MyTaskListFragment();
        fragment.tasks = tasks;
        fragment.grabbed = grabbed;
        fragment.context = context;
//        Log.i(TAG,"in getInstance:"+tasks);
        return fragment;
    }
    public static MyTaskListFragment getInstance(List<Task> tasks,boolean forCheck, Context context) {
        MyTaskListFragment fragment = new MyTaskListFragment();
        fragment.tasks = tasks;
        fragment.grabbed = null;
        fragment.forCheck = forCheck;
        fragment.context = context;
//        Log.i(TAG,"in getInstance:"+tasks);
        return fragment;
    }
    public static MyTaskListFragment getInstance(List<Task> tasks,boolean forCheck,boolean isDoing, Context context) {
        MyTaskListFragment fragment = new MyTaskListFragment();
        fragment.tasks = tasks;
        fragment.grabbed = null;
        fragment.forCheck = forCheck;
        fragment.context = context;
        fragment.isDoing = isDoing;
//        Log.i(TAG,"in getInstance:"+tasks);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_task_list, null);
        tasksLinear = (LinearLayout)v.findViewById(R.id.my_task_list_linear);
        refreshLayout = (RefreshLayout)v.findViewById(R.id.refreshLayout_my_task);
        refreshList();
        //设置下拉刷新
        refreshLayout.setOnRefreshListener(refreshListener);

//        Log.i(TAG, getArguments()+"");
        return v;
    }
    public void setRefresh(OnRefreshListener listener){
        this.refreshListener = listener;
    }
    public void setTasks(List<Task> tasks){ this.tasks = tasks; }
    public void setGrabbed(List<Task> grabbed){ this.grabbed = grabbed; }
    public void refreshList(){
        //TODO；有时候tasksLinear会莫名其妙为null，还没找出原因。。这个时候不会更新。。
        //刷新没用，可能是因为
        if(tasksLinear==null) return;
        tasksLinear.removeAllViews();
        if(tasks!=null){
            for(int i=tasks.size()-1; i>=0; i--){
                ItemTaskView item =new ItemTaskView(context,tasks.get(i));
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTaskDetailActivity(item.getTask(),forCheck, isDoing);
                    }
                });
                tasksLinear.addView(item);
            }
        }
        if(grabbed!=null){
            for(int i=grabbed.size()-1; i>=0; i--){
                ItemTaskView item = new ItemTaskView(context,grabbed.get(i));
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTaskDetailActivity(item.getTask(),forCheck);
                    }
                });
                tasksLinear.addView(item);
            }
        }
    }
    void startTaskDetailActivity(Task task, boolean check){
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra("task",task);
        intent.putExtra("forCheck",check);
        startActivity(intent);
    }
    void startTaskDetailActivity(Task task, boolean check, boolean doing){
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra("task",task);
        intent.putExtra("forCheck",check);
        intent.putExtra("isDoing",doing);
        startActivity(intent);
    }

//    private static class TaskList implements Serializable{
//        private List<Task> tasks;
//        public TaskList(List<Task> tasks){
//            this.tasks = tasks;
//        }
//
//        public void setTasks(List<Task> tasks) {
//            this.tasks = tasks;
//        }
//
//        public List<Task> getTasks() {
//            return tasks;
//        }
//    }
}
