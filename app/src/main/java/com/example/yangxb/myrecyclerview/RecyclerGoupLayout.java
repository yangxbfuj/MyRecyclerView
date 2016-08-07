package com.example.yangxb.myrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by yangxb on 16/8/7.
 */
public class RecyclerGoupLayout extends LinearLayout {

    //是否拦截了滚动事件
    private boolean isIntercepted = false;

    //是否是全屏显示
    private boolean isFullScreen = false;

    //上点的Y左边
    private float lastY;

    //纪录触摸时的坐标值
    private float beginY;

    //处理滚动的阀值
    public static float GATE_VALUE = 100;

    //当前监听的list
    private RecyclerView mList;

    private boolean isListTop = true;

    private ScrollListener mScrollListener;

    public RecyclerGoupLayout(Context context) {
        super(context);
    }

    public RecyclerGoupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerGoupLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RecyclerGoupLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            lastY = ev.getY();
            beginY = ev.getY();
        }

        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            //如果diff<0 代表上移动
            int diff = (int) (ev.getY() - beginY);

            //判断是不是要拦截该事件
            return isIntercepted(diff,isListTop);
        }

        if(ev.getAction() == MotionEvent.ACTION_CANCEL){

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            this.scrollBy(0, (int) (lastY - ev.getY()));
            lastY = ev.getY();
        }

        if (ev.getAction() == MotionEvent.ACTION_UP ||
                ev.getAction() == MotionEvent.ACTION_CANCEL){
            float thisY = ev.getY();
            //计算位移
            float diffY = thisY - beginY;

            //往上滑动，位移小于0
            if(diffY < -GATE_VALUE){
                if(mScrollListener != null){
                    mScrollListener.scrollUpToTop();
                }
            }else if (diffY > GATE_VALUE){ //往下面滑动，位移大于0
                if(mScrollListener != null){
                    mScrollListener.scrollDownToBottom();
                }
            }else { //如果没有超过阀门值
                if(mScrollListener != null){
                    mScrollListener.scrollBack();
                }
            }
            return true;
        }

        return super.onTouchEvent(ev);
    }

    public void setScrollListener(ScrollListener mScrollListener){
        this.mScrollListener = mScrollListener;
    }

    public void setFillScreen(boolean isFull){
        isFullScreen = isFull;
    }

    public void setIntercepted(boolean intercepted) {
        isIntercepted = intercepted;
    }

    /**
     *
     * @param diff 小于0 为向上移动
     * @return
     */
    public boolean isIntercepted(int diff,boolean isListTop){
        boolean isIntercepted = false;

        if(!isFullScreen){ //如果是半屏 那么拦截
            isIntercepted = true;
        }

        //如果是全屏，且在下拉，并且内嵌List到顶 那么当前控件拦截事件
        if (isFullScreen && diff>0 && isListTop){
            isIntercepted =  true;
        }

        return isIntercepted;
    }

    public void setmList( RecyclerView mList) {
        this.mList = mList;
        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 如果控件滑动到顶
                if(manager.findFirstCompletelyVisibleItemPosition() == 0){
                    isListTop = true;
                }else {
                    isListTop = false;
                }
            }
        });
    }


    public interface ScrollListener{
        public void scrollUpToTop();
        public void scrollDownToBottom();
        public void scrollBack();
    }
}
