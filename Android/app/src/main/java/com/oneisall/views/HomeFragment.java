package com.oneisall.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.oneisall.R;
import com.oneisall.base.BaseAsyncTask;
import com.oneisall.model.request.AllTasksRequest;
import com.oneisall.model.request.LogoutRequest;
import com.oneisall.model.request.MyTaskRequest;
import com.oneisall.model.request.UserInfoRequest;
import com.oneisall.model.response.MyTaskRequestResult;
import com.oneisall.model.response.SingleMessageResponse;
import com.oneisall.model.response.Task;
import com.oneisall.model.response.TaskListResult;
import com.oneisall.model.response.UserInfoRequestResponse;
import com.oneisall.tasks.TaskDetailActivity;
import com.oneisall.utils.DialogUtils;
import com.oneisall.utils.UserUtils;
import com.oneisall.views.custom.ItemTaskView;
import com.oneisall.views.custom.PersonAttributeView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vondear.rxui.view.dialog.RxDialogSure;
import com.wyt.searchbox.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import static com.oneisall.api.TaskApi.getAllTasks;
import static com.oneisall.api.TaskApi.getMyTask;
import static com.oneisall.api.TaskApi.getRecommendTasks;
import static com.oneisall.api.UserApi.getUserInfo;
import static com.oneisall.api.UserApi.logout;
import static com.oneisall.constants.TaskTypes.TASK_DONE;
import static com.oneisall.constants.TaskTypes.TASK_FAVORITE;
import static com.oneisall.constants.TaskTypes.TASK_INVITED_TO_CHECK;
import static com.oneisall.constants.TaskTypes.TASK_REJECTED;
import static com.oneisall.constants.TaskTypes.TASK_RELEASED;
import static com.oneisall.constants.TaskTypes.TASK_TO_BE_CHECKED;
import static com.oneisall.constants.TaskTypes.TASK_TO_BE_DONE;
import static com.oneisall.utils.DialogUtils.showDialog;
//import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

/**
 *
 */
public class HomeFragment extends Fragment {

//    final private HomeActivity homeActivity = (HomeActivity) getActivity();
    final private static String TAG = "HomeFragment";
    private Context context;
    private LinearLayout mSearchIcon;
    private LinearLayout fragmentContainer;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchFragment searchFragment;
    private ConvenientBanner convenientBanner;
    private LinearLayout mAllTasksLinear;
    private LinearLayout mRecLinear;
    private LinearLayout typeSingle;
    private LinearLayout typeMulti;
    private LinearLayout typeQA;
    private LinearLayout typeLabel;
    //
    private SegmentTabLayout mTabCategory;  //一级目录
    private CommonTabLayout mTabSecondaryCategory;  //二级
    private ViewPager mViewPager;
    private MyPagerAdapter mAdapter;
    private ArrayList<CustomTabEntity> mTabEntities1,mTabEntities2;
    private ArrayList<MyTaskListFragment> mFragments1,mFragments2;
    private boolean firstFlag=true;//记录是否为第一次获取mytask界面。否则在getmytask后采用refresh而非init
    //
    private ImageView mPortrait;
    private TextView mUserName;
    private TextView mCredits;
    private LinearLayout mPersonAttrList;
    /**
     * Create a new instance of the fragment
     */
    public static HomeFragment newInstance(int index) {
        HomeFragment fragment = new HomeFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        context = getActivity();
        if (getArguments().getInt("index", 0) == 0) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            initFragmentHome(view);
        } else if(getArguments().getInt("index",0)==1){
            firstFlag = true;
            view = inflater.inflate(R.layout.fragment_my_task, container, false);
            mTabCategory = (SegmentTabLayout)view.findViewById(R.id.tab_category);
            mTabSecondaryCategory = (CommonTabLayout) view.findViewById(R.id.tab_detailed_category);
            mViewPager = (ViewPager)view.findViewById(R.id.vp2) ;
            mAdapter = new MyPagerAdapter(getFragmentManager());
            mViewPager.setAdapter(mAdapter);
            new GetMyTask(context).execute(new MyTaskRequest(UserUtils.getUserName(context)));
//            initDemoList(view);
//            return view;
        } else{
            view = inflater.inflate(R.layout.fragment_person_info,container,false);
            initPersonFragment(view);
        }
        return view;
    }
    /*
     * fragment for home page
     */
    public void initFragmentHome(View view){
//        fragmentContainer = (LinearLayout)view.findViewById(R.id.fragment_container);
        mAllTasksLinear = (LinearLayout)view.findViewById(R.id.all_tasks_list);
        mSearchIcon = (LinearLayout)view.findViewById(R.id.search_icon_bar);
        typeSingle = (LinearLayout)view.findViewById(R.id.home_category_single);
        typeMulti = (LinearLayout)view.findViewById(R.id.home_category_multi);
        typeQA = (LinearLayout)view.findViewById(R.id.home_category_qa);
        typeLabel = (LinearLayout)view.findViewById(R.id.home_category_label);
        mRecLinear = (LinearLayout)view.findViewById(R.id.today_recommend_list);
        initBanner(view);
        new GetAllTasks(context)
                .execute(new AllTasksRequest(UserUtils.getUserName(context)));
        new GetRecommendTasks(context).execute(new AllTasksRequest(UserUtils.getUserName(context)));

        //下拉刷新
        RefreshLayout refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new GetAllTasks(context)
                        .execute(new AllTasksRequest(UserUtils.getUserName(context)));
                new GetRecommendTasks(context).execute(new AllTasksRequest(UserUtils.getUserName(context)));
                refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        });
        //搜索
        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });
        //信箱
        ((ImageView)view.findViewById(R.id.home_message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"sdfsf",Toast.LENGTH_SHORT).show();
                //提示弹窗
                final RxDialogSure rxDialogSure = new RxDialogSure(context);
                rxDialogSure.getTitleView().setVisibility(View.GONE);
                TextView content = rxDialogSure.getContentView();
                content.setTextSize(16);
                content.setTextColor(getResources().getColor(R.color.qmui_config_color_75_pure_black));
                rxDialogSure.setContent("会话功能暂未上线！");
                rxDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rxDialogSure.cancel();
                    }
                });
                rxDialogSure.show();
            }
        });
    }
    //init banner, for home page, used in initFragmentHome()
    void initBanner(View view){
        ArrayList<Integer> localImages = new ArrayList<Integer>();
        localImages.add(R.mipmap.adver_1);
        localImages.add(R.mipmap.adver_2);
        convenientBanner=(ConvenientBanner)view.findViewById(R.id.convenientBanner);
        convenientBanner.setPages(
                new CBViewHolderCreator() {
                    @Override
                    public LocalImageHolderView createHolder(View itemView) {
                        return new LocalImageHolderView(itemView);
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.item_localimage;
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
//                .setOnItemClickListener(this);
                //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                .setOnPageChangeListener(this)//监听翻页事件
        ;

    }
    //init tasklist, for homepage, used in initFragmentHome()
    public void initAllTasks(TaskListResult taskListResult){
        mAllTasksLinear.removeAllViews();
        List<Task> tasks = taskListResult.getResultArray();
        //最新的任务放在最前
        for(int i=tasks.size()-1; i>=0; i--){
            Task task = tasks.get(i);
            //set task view
            ItemTaskView taskView = new ItemTaskView(context,task);
            if(tasks.size()==1){
                taskView.setBottomDividerVisibilty(View.GONE);
            }
            taskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTaskDetailActivity(task);
                }
            });
            //add to layout
            mAllTasksLinear.addView(taskView);
        }
    }
    public void initRecTasks(TaskListResult taskListResult){
        mRecLinear.removeAllViews();
        List<Task> tasks = taskListResult.getResultArray();
        //最新的任务放在最前
        for(int i=tasks.size()-1; i>=0; i--){
            Task task = tasks.get(i);
            //set task view
            ItemTaskView taskView = new ItemTaskView(context,task);
            if(tasks.size()==1){
                taskView.setBottomDividerVisibilty(View.GONE);
            }
            taskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTaskDetailActivity(task);
                }
            });
            //add to layout
            mRecLinear.addView(taskView);
        }
    }

    private void initCategoryButton(TaskListResult tasks){
        typeSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaskDisplayActivity(tasks, 1);
            }
        });
        typeMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaskDisplayActivity(tasks, 2);
            }
        });
        typeQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaskDisplayActivity(tasks, 3);
            }
        });
        typeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaskDisplayActivity(tasks, 4);
            }
        });
    }

    /**
     * my task
     */
    private void initFragmentMyTask(MyTaskRequestResult result){
        //init
        firstFlag = false;
        mTabEntities1 = new ArrayList<>();
        mTabEntities2 = new ArrayList<>();
        mFragments2 = new ArrayList<>();
        mFragments1 = new ArrayList<>();
        String[] mFirstTitles = getResources().getStringArray(R.array.tab_first_category_array);
        String[] mSecondTitles_1 = getResources().getStringArray(R.array.tab_secondary_category_list1);
        String[] mSecondTitles_2 = getResources().getStringArray(R.array.tab_secondary_category_list2);
        //设置各级标题,初始化七个view pager
//        Log.i(TAG, result.toString());
        mTabCategory.setTabData(mFirstTitles);
        for (int i = 0; i < mSecondTitles_1.length; i++) {
            mTabEntities1.add(new TabEntity(mSecondTitles_1[i], 0, 0));
        }
        for (int i = 0; i < mSecondTitles_2.length; i++) {
            mTabEntities2.add(new TabEntity(mSecondTitles_2[i], 0, 0));
        }
        //设置view pager中的fragments
        initFragments(result);
        //初次跳到该界面
        mTabCategory.setCurrentTab(0);
        mAdapter.setFragments(mFragments1,0);
        initSecondaryCategory(mTabEntities1);
        //监听一级标题改动事件
        mTabCategory.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
//                Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();
                if(position==0){
                    mAdapter.setFragments(mFragments1,0);
                    initSecondaryCategory(mTabEntities1);
                }

                else {
                    mAdapter.setFragments(mFragments2,4);
                    initSecondaryCategory(mTabEntities2);
                }
            }

            @Override
            public void onTabReselect(int position) {
                Toast.makeText(context,"reselect",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initFragments(MyTaskRequestResult result){
        OnRefreshListener listener = new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                Toast.makeText(context,"refresh!",Toast.LENGTH_SHORT).show();
                new GetMyTask(context).execute(new MyTaskRequest(UserUtils.getUserName(context)));
                refreshlayout.finishRefresh(800/*,false*/);//传入false表示刷新失败
            }
        };
        for (int i = 0; i < mTabEntities1.size(); i++) {
            if(i==TASK_TO_BE_DONE){
                mFragments1.add(MyTaskListFragment.getInstance(result.getDoing(),result.getGrabbed(),context));
            }
            else if(i==TASK_TO_BE_CHECKED) {
                mFragments1.add(MyTaskListFragment.getInstance(result.getUnreviewed(),context));
            }
            else if(i==TASK_REJECTED) mFragments1.add(MyTaskListFragment.getInstance(result.getRejected(),context));
            else mFragments1.add(MyTaskListFragment.getInstance(result.getDone(),context));
            mFragments1.get(i).setRefresh(listener);
        }
        for (int i = 0; i < mTabEntities2.size(); i++) {
            if(i==TASK_RELEASED) mFragments2.add(MyTaskListFragment.getInstance(result.getReleased(),true,context));
            else if(i==TASK_INVITED_TO_CHECK){
                mFragments2.add(MyTaskListFragment.getInstance(result.getInvited(),true,context));
            }
            else mFragments2.add(MyTaskListFragment.getInstance(result.getFavorite(),context));
            mFragments2.get(i).setRefresh(listener);
        }
    }
    private void initSecondaryCategory(ArrayList<CustomTabEntity> mTabEntities){
        mTabSecondaryCategory.setTabData(mTabEntities);
        mTabSecondaryCategory.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabSecondaryCategory.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabSecondaryCategory.setCurrentTab(0);
        mViewPager.setCurrentItem(0);

    }
    private void refreshFragments(MyTaskRequestResult result){
        for(int i=0; i<mTabEntities1.size(); i++){
            mFragments1.get(i).setTasks(getTaskList(0,i,result));
            if(i==0){
                mFragments1.get(0).setGrabbed(result.getGrabbed());
            }
            mFragments1.get(i).refreshList();
        }
        for(int i=0; i<mTabEntities2.size(); i++){
            mFragments2.get(i).setTasks(getTaskList(1,i,result));
            mFragments2.get(i).refreshList();
        }
//        Toast.makeText(context,"refresh!",Toast.LENGTH_SHORT).show();

    }
    private List<Task> getTaskList(int category1, int category2, MyTaskRequestResult result){
        if(category1 ==0){
            switch (category2){
                case TASK_TO_BE_DONE:{
                    return result.getDoing();
                }
                case TASK_TO_BE_CHECKED:{
                    return result.getUnreviewed();
                }
                case TASK_REJECTED:{
                    return result.getRejected();
                }
                case TASK_DONE:{
                    return result.getDone();
                }
            }
        }
        else{
            switch(category2){
                case TASK_RELEASED:{
                    return result.getReleased();
                }
                case TASK_INVITED_TO_CHECK:{
                    return result.getInvited();
                }
                case TASK_FAVORITE:{
                    return result.getFavorite();
                }
            }
        }
        return null;
    }

    /*
     * person information
     */
    private void initPersonFragment(View view){
        mPortrait = (ImageView)view.findViewById(R.id.person_portrait);
        //TODO:添加网络请求失败时的头像
        Glide.with(this)
                .load("http://101.132.71.247:8092/static/img/touxiang.jpg")
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(mPortrait);
        //设置下拉刷新
        RefreshLayout refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshLayout_person_info);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new GetUserInfo(context)
                        .execute(new UserInfoRequest(UserUtils.getUserName(context)));
                refreshLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        });
        //设置退出登录
        ((TextView)view.findViewById(R.id.setting_logout_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提示弹窗
                final RxDialogSure rxDialogSure = new RxDialogSure(context);
                rxDialogSure.getLogoView().setImageResource(R.mipmap.oneisall_icon);
                rxDialogSure.getTitleView().setVisibility(View.GONE);
                TextView content = rxDialogSure.getContentView();
                content.setTextSize(16);
                content.setTextColor(getResources().getColor(R.color.qmui_config_color_75_pure_black));
                rxDialogSure.setContent("确定要退出登录吗?");
                rxDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new UserLogoutTask(context).execute(new LogoutRequest(UserUtils.getUserName(context),UserUtils.getPassword(context)));
                        rxDialogSure.cancel();
                    }
                });
                rxDialogSure.show();
            }
        });
        //添加个人信息中的属性
        mUserName = (TextView)view.findViewById(R.id.person_username);
        mCredits = (TextView)view.findViewById(R.id.person_total_credits);
        mPersonAttrList = (LinearLayout)view.findViewById(R.id.person_attr_list);
        new GetUserInfo(context).execute(new UserInfoRequest(UserUtils.getUserName(context)));
    }
    private void initPersonAttrList(UserInfoRequestResponse response){
        mPersonAttrList.removeAllViews();
        mUserName.setText(response.getUsername());
        mCredits.setText("总积分："+response.getTotal_credits());
        //attribute list
        //邮箱
        PersonAttributeView emailAttr = new PersonAttributeView(context);
        emailAttr.setAttrName(R.string.person_info_email_note);
        emailAttr.setAttrText(response.getEmail());
        //通过任务数
        PersonAttributeView acAttr = new PersonAttributeView(context);
        acAttr.setAttrName(R.string.person_info_num_accepted_note);
        acAttr.setAttrText(response.getNum_label_accepted()+"");
        //上次登录时间
        PersonAttributeView loginAttr = new PersonAttributeView(context);
        loginAttr.setAttrName(R.string.person_info_last_login_time_note);
        loginAttr.setAttrText(response.getLast_login_time());

        mPersonAttrList.addView(emailAttr);
        mPersonAttrList.addView(acAttr);
        mPersonAttrList.addView(loginAttr);

    }
    private int getUserLevel(int num_label_ac){
        int[] level_list = new int[]{0, 0, 200, 500, 1000, 2000};
        for(int i=level_list.length-1; i>0; i--){
            if(num_label_ac>=level_list[i]) return i;
        }
        return 1;
    }
    /*
     * start next activity
     */
    private void startTaskDetailActivity(Task task){
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra("task",task);
        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void startTaskDisplayActivity(TaskListResult tasks,int type){
        Intent intent = new Intent(context, TaskDisplayActivity.class);
        intent.putExtra("result",tasks);
        intent.putExtra("type",type);
        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
    /**
     * Refresh
     */
    public void refresh() {

    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        // Do what you want here, for example animate the content
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
        if (getArguments().getInt("index", 0) == 1) {
            mTabCategory.setCurrentTab(0);
            mAdapter.setFragments(mFragments1,0);
            initSecondaryCategory(mTabEntities1);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }
    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        if(getArguments().getInt("index", 0) == 0){
            //开始自动翻页
            convenientBanner.startTurning();
        }
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        if(getArguments().getInt("index", 0) == 0){
            //停止翻页
            convenientBanner.stopTurning();
        }
    }
    //for banner
    class LocalImageHolderView extends Holder<Integer> {
        private ImageView imageView;

        public LocalImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            imageView =itemView.findViewById(R.id.ivPost);
        }

        @Override
        public void updateUI(Integer data) {
            imageView.setImageResource(data);
        }
    }

    class TabEntity implements CustomTabEntity {
        public String title;
        public int selectedIcon;
        public int unSelectedIcon;

        public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
            this.title = title;
            this.selectedIcon = selectedIcon;
            this.unSelectedIcon = unSelectedIcon;
            Log.i("tab",title);
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public int getTabSelectedIcon() {
            return selectedIcon;
        }

        @Override
        public int getTabUnselectedIcon() {
            return unSelectedIcon;
        }
    }
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        //确保两个二级目录的子fragment不会重叠
        private int start=0;
        private ArrayList<MyTaskListFragment> fragments=new ArrayList<>();
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public void setFragments(ArrayList<MyTaskListFragment> fs, int s){
            fragments = fs;
            start = s;
            this.notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ""+position;
//            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        //下面这个方法不能从根本上解决问题。。。return POSITION_NONE就好了
//        @Override
//        public long getItemId(int position) {
//            return super.getItemId(position) + start;
//        }
        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return PagerAdapter.POSITION_NONE;
        }
    }
    /*
     *  network utils
     */
    //现在的dialog展示有问题。。。正常了，原因是没有在style中设置继承属性"QMUI.Compat.NoActionBar"
    //home
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
                initCategoryButton(taskListResult);
                initAllTasks(taskListResult);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mAllTasksLinear);
            }
        }
    }
    private class GetRecommendTasks extends BaseAsyncTask<AllTasksRequest, Void, TaskListResult> {

        final private static String TAG = "GetRecommendTasks";

        private GetRecommendTasks(Context context){
            super(context);
        }

        @Override
        protected TaskListResult doInBackground(AllTasksRequest... allTasksRequests) {
            return getRecommendTasks(allTasksRequests[0]);
        }

        @Override
        protected void onPostExecute(TaskListResult taskListResult) {
            super.onPostExecute(taskListResult);
            if(taskListResult != null){
                Log.i(TAG, "onPostExecute: "+taskListResult.toString());
                initRecTasks(taskListResult);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mAllTasksLinear);
            }
        }
    }

    //my task
    private class GetMyTask extends BaseAsyncTask<MyTaskRequest, Void, MyTaskRequestResult> {

        private GetMyTask(Context context){
            super(context);
        }

        @Override
        protected MyTaskRequestResult doInBackground(MyTaskRequest... requests) {
            return getMyTask(requests[0]);
        }

        @Override
        protected void onPostExecute(MyTaskRequestResult result) {
            super.onPostExecute(result);
            if(result != null){
                Log.i(TAG, "onPostExecute: "+result.toString());
                if(firstFlag)
                    initFragmentMyTask(result);
                else refreshFragments(result);
            }else{
                showDialog("网络错误", QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mViewPager);
            }
        }
    }
    //person info
    private class GetUserInfo extends BaseAsyncTask<UserInfoRequest, Void, UserInfoRequestResponse> {

        private GetUserInfo(Context context){
            super(context);
        }

        @Override
        protected UserInfoRequestResponse doInBackground(UserInfoRequest... requests) {
            return getUserInfo(requests[0]);
        }
        @Override
        protected void onPostExecute(UserInfoRequestResponse response) {
            super.onPostExecute(response);
            if(response != null){
                UserUtils.setUserLevel(context, getUserLevel(response.getNum_label_accepted()));
                initPersonAttrList(response);
                Log.i(TAG, "onPostExecute: "+response.toString());

            }else{
                showDialog("网络错误",QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mPersonAttrList);
            }
        }
    }
    public class UserLogoutTask extends BaseAsyncTask<LogoutRequest, Void, SingleMessageResponse> {

        UserLogoutTask(Context context) {
            super(context);
        }

        @Override
        protected SingleMessageResponse doInBackground(LogoutRequest... params) {
            return logout(params[0]);
        }

        @Override
        protected void onPostExecute(final SingleMessageResponse response) {
            super.onPostExecute(response);
            if (response.getMessage().equals("注销成功")) {
                UserUtils.clearUserInfo(context);
                context.startActivity(new Intent(context, LoginActivity.class));
                ((HomeActivity)context).finish();
            } else {
                DialogUtils.showDialog(response.getMessage(), QMUITipDialog.Builder.ICON_TYPE_FAIL, context, mPersonAttrList);
            }
        }
    }

}