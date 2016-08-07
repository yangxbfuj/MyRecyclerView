package com.example.yangxb.myrecyclerview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.yangxb.myrecyclerview.R;
import com.example.yangxb.myrecyclerview.RecyclerGoupLayout;

public class RecyclerActivity extends AppCompatActivity {


    //Views
    private View mBlank;
    private Toolbar mToolbar;
    private RecyclerView mList;
    private View mContentView;
    private RecyclerGoupLayout mViewGroup;
    // Views's dimens
    private int mContentHeight;
    private int mItemHeight;
    private int mToolbarHeight;
    private int mBlankHeight;
    //是否已经设置了blank的高度
    private boolean hasMeasured = false;
    //ListView相关对象
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView.OnScrollListener mOnScrollListener;

    private static final String SCREEN_STATUS = "screen_orientation";
    private static final String LIST_POSITION = "list_position";

    //如果这个activity是屏幕方向变化重新创建的activity
    private boolean isFullScreen = false;
    private int mListPosition = 0;
    private MyAdapter mMyAdapter;

    private RecyclerGoupLayout.ScrollListener mFullScreenScrollListener = new RecyclerGoupLayout
            .ScrollListener() {
        @Override
        public void scrollUpToTop() {

        }

        @Override
        public void scrollDownToBottom() {
            mViewGroup.setScrollListener(mHalfScreenScrollListener);
            mViewGroup.setFillScreen(false,mBlankHeight);
        }

        @Override
        public void scrollBack() {
            mViewGroup.setScrollY(mBlankHeight);
        }
    };
    private RecyclerGoupLayout.ScrollListener mHalfScreenScrollListener = new RecyclerGoupLayout
            .ScrollListener() {
        @Override
        public void scrollUpToTop() {
            //切换成全屏逻辑
            mViewGroup.setScrollListener(mFullScreenScrollListener);
            mViewGroup.setFillScreen(true,mBlankHeight);
        }

        @Override
        public void scrollDownToBottom() {
            //半屏模式下，向下拉动，退出Activity
            RecyclerActivity.this.onBackPressed();
        }

        @Override
        public void scrollBack() {
            mViewGroup.setScrollY(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            isFullScreen  =  savedInstanceState.getBoolean(SCREEN_STATUS,false);
            mListPosition =  savedInstanceState.getInt(LIST_POSITION,0);
        }
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_recyler);
        mBlank = findViewById(R.id.my_recycler_blank);
        mToolbar = (Toolbar) findViewById(R.id.my_recycler_toolbar);
        mList = (RecyclerView) findViewById(R.id.my_recycler_list);
        mContentView = findViewById(android.R.id.content);
        mViewGroup = (RecyclerGoupLayout) findViewById(R.id.my_recycler_group);

        //initList
        mLinearLayoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(mLinearLayoutManager);
        mMyAdapter = new MyAdapter();
        mMyAdapter.setOnDataChangedListener(new OnDataChangedListener() {
            @Override
            public void onDataChanged() {
                relayoutBlankView();
            }
        });
        mList.setAdapter(mMyAdapter);
        mOnScrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // 如果控件滑动到顶
                    if (mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        mViewGroup.setListIsTop(true);
                    } else {
                        mViewGroup.setListIsTop(false);
                    }
                }
            };

        mList.addOnScrollListener(mOnScrollListener);

        listenLayout();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SCREEN_STATUS,mViewGroup.isFullScreen());
        outState.putInt(LIST_POSITION,mLinearLayoutManager.findFirstVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    private void listenLayout() {
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 如果已经测量完高度，就跳出
                if (hasMeasured)
                    return;
                initViewDimen();

                initScreenStatus();
                mViewGroup.requestLayout();
            }
        });



    }

    public void initScreenStatus(){
        mViewGroup.setScrollListener(mHalfScreenScrollListener);
        mViewGroup.setFillScreen(isFullScreen,mBlankHeight);
        mLinearLayoutManager.scrollToPosition(mListPosition);
    }

    /**
     * 获取List中的Item的高度
     *
     * @return
     */
    private int getItemHeight() {
        int height = 0;
        if (mList.getChildCount() > 0) {
            height = mList.getChildAt(0).getHeight();
        }
        return height;
    }

    /**
     * 根据实际情况设置blank和list的高度
     */
    private void initViewDimen() {

        //获取内容高度
        if (mContentView != null)
            mContentHeight = mContentView.getHeight();
        //获取List中Item的高度,设置
        mItemHeight = getItemHeight();

        //获得Toolbar的高度
        mToolbarHeight = mToolbar.getHeight();

        //如果已经计算了
        if (hasMeasured) {
            return;
        }

        //根据List中的Item数量计算List应该多高
        int listChildCount = mList.getChildCount();

        //如果List没有Item，那么空白View应该占满全屏
        if (listChildCount <= 0) {
            //高度就是内容高度 ＝ 显示屏幕高度 － 状态栏高度
            mBlankHeight = mContentHeight;
        } else if (listChildCount == 1) {
            //空白高度 ＝ 内容高度－toolbar高度－1个Item的高度
            mBlankHeight = mContentHeight - mToolbarHeight - mItemHeight;
        } else {  //当Item数量大于2时，空白高度 ＝ 内容高度－toolbar高度－2个Item的高度
            mBlankHeight = mContentHeight - mToolbarHeight - mItemHeight * 2;
        }

        //设置空白区域的高度
        mBlank.getLayoutParams().height = mBlankHeight;

        //设置list的高度
        mList.getLayoutParams().height = mContentHeight - mToolbarHeight;

        hasMeasured = true;
    }

    /**
     * 应该在ListItem的增加和修改时调用
     */
    private void relayoutBlankView(){

        //创建新值，用于匹配
        int newValue = mBlankHeight;
        //根据List中的Item数量计算List应该多高
        int listChildCount = mList.getChildCount();

        //如果List没有Item，那么空白View应该占满全屏
        if (listChildCount <= 0) {
            //高度就是内容高度 ＝ 显示屏幕高度 － 状态栏高度
            newValue = mContentHeight;
        } else if (listChildCount == 1) {
            //空白高度 ＝ 内容高度－toolbar高度－1个Item的高度
            newValue = mContentHeight - mToolbarHeight - mItemHeight;
        } else {  //当Item数量大于2时，空白高度 ＝ 内容高度－toolbar高度－2个Item的高度
            newValue = mContentHeight - mToolbarHeight - mItemHeight * 2;
        }

        //没有变化
        if (newValue == mBlankHeight)
            return;

        if(newValue > mBlankHeight){ //新值大于旧值
            //TODO 设置增高动画

        }else if(newValue < mBlankHeight){
            //TODO 设置减小动画
        }
        mBlankHeight = newValue;
        //设置空白区域的高度
        mBlank.getLayoutParams().height = mBlankHeight;

    }

}
