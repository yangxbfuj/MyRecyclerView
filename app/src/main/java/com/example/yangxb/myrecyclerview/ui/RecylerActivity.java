package com.example.yangxb.myrecyclerview.ui;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.example.yangxb.myrecyclerview.R;
import com.example.yangxb.myrecyclerview.RecyclerGoupLayout;

public class RecylerActivity extends AppCompatActivity {

        //Views
        private View mBlank;
        private Toolbar mToolbar;
        private RecyclerView mList;
        private View mContentView;
        private RecyclerGoupLayout mViewGroup;

        // Views's dimens
        private int mContentHeight;
        private int mItemHeight;
        private int mStatusBarHeight;
        private int mToolbarHeight;
        private int mBlankHeight;

        //是否已经设置了blank的高度
        private boolean isBlankHeightSetted = false;

        //List是否把第一个Item完全显示了
        private boolean isFullFirstItem = true;

        //Screen status
        private boolean mIsHalfSreen = true;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            intView();
        }

    private void intView(){
        setContentView(R.layout.activity_recyler);
        mBlank = findViewById(R.id.my_recycler_blank);
        mToolbar = (Toolbar) findViewById(R.id.my_recycler_toolbar);
        mList = (RecyclerView) findViewById(R.id.my_recycler_list);
        mContentView = findViewById(android.R.id.content);
        mViewGroup = (RecyclerGoupLayout) findViewById(R.id.my_recycler_group);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(new MyAdapter());
        mViewGroup.setmList(mList);
        initViewDimen();
    }

    private void initViewDimen(){
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(isBlankHeightSetted)
                    return;
                Log.i("XXX","onGlobalLayout");
                //获取内容高度
                if(mContentView != null)
                    mContentHeight = mContentView.getHeight();
                    Log.i("XXX","content height = "+mContentHeight);
                //获取List中Item的高度,设置
                mItemHeight = getItemHeight();

                //获得Toolbar的高度
                mToolbarHeight = mToolbar.getHeight();

                //获得状态栏高度
                mStatusBarHeight = getStatusBarHeight();

                //获得空白区域的高度
                setViewHeight();

                mViewGroup.setScrollY(0);
            }
        });

        DisplayMetrics displayMetrics =  new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = findViewById(android.R.id.content).getHeight();

        mViewGroup.setScrollListener(mHalfScreenSrollListener);
    }

    private Boolean isHalfScreen(){
        return mIsHalfSreen;
    }

    /**
     * 获取List中的Item的高度
     * @return
     */
    private int getItemHeight(){
        int height = 0;
        if(mList.getChildCount()>0) {
            height = mList.getChildAt(0).getHeight();
        }
        return height;
    }

    private int getStatusBarHeight(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 根据实际情况设置blank和list的高度
     */
    private void setViewHeight(){

        //如果已经计算了
        if(isBlankHeightSetted){
            return;
        }

        //根据List中的Item数量计算List应该多高
        int listChildCount =  mList.getChildCount();

        //如果List没有Item，那么空白View应该占满全屏
        if (listChildCount <= 0){
            //高度就是内容高度 ＝ 显示屏幕高度 － 状态栏高度
            mBlankHeight = mContentHeight;
        }else if (listChildCount == 1){
            //空白高度 ＝ 内容高度－toolbar高度－1个Item的高度
            mBlankHeight = mContentHeight - mToolbarHeight - mItemHeight;
        }else {  //当Item数量大于2时，空白高度 ＝ 内容高度－toolbar高度－2个Item的高度
            mBlankHeight = mContentHeight - mToolbarHeight - mItemHeight*2;
        }

        Log.i("XXX","status bar height = "+mBlankHeight);

        //设置空白区域的高度
        mBlank.getLayoutParams().height = mBlankHeight;

        //设置list的高度
        mList.getLayoutParams().height = mContentHeight - mToolbarHeight;

        mBlank.requestLayout();

        isBlankHeightSetted = true;
    }

    private RecyclerGoupLayout.ScrollListener mHalfScreenSrollListener = new RecyclerGoupLayout.ScrollListener() {
        @Override
        public void scrollUpToTop() {
            mViewGroup.setScrollY(mBlankHeight);
            //切换成全屏逻辑
            mViewGroup.setScrollListener(mFullScreenSrollListener);

            mViewGroup.setFillScreen(true);

        }

        @Override
        public void scrollDownToBottom() {
            //半屏模式下，向下拉动，退出Acitity
            RecylerActivity.this.onBackPressed();
        }

        @Override
        public void scrollBack() {
            mViewGroup.setScrollY(0);
        }
    };


    private RecyclerGoupLayout.ScrollListener mFullScreenSrollListener = new RecyclerGoupLayout
            .ScrollListener() {
        @Override
        public void scrollUpToTop() {

        }

        @Override
        public void scrollDownToBottom() {
            mViewGroup.setScrollY(0);

            mViewGroup.setScrollListener(mHalfScreenSrollListener);

            mViewGroup.setFillScreen(false);
        }

        @Override
        public void scrollBack() {
            mViewGroup.setScrollY(mBlankHeight);
        }
    };

}
