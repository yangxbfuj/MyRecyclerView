package com.example.yangxb.myrecyclerview.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yangxb.myrecyclerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxb on 16/8/7.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Integer> list = new ArrayList<>();

    private OnDataChangedListener mOnDataChangedListener;
    public MyAdapter(){
        list.add(1);
//        list.add(2);
//        list.add(3);
    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_recycler, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyHolder myHolder = (MyHolder) holder;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click"+position,Toast.LENGTH_SHORT).show();
                list.add(new Integer(position+1));
                notifyDataSetChanged();
            }
        });
        myHolder.info = list.get(position);

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mOnDataChangedListener!=null)
            mOnDataChangedListener.onDataChanged();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (mOnDataChangedListener!=null)
            mOnDataChangedListener.onDataChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnDataChangedListener(OnDataChangedListener listener){
        this.mOnDataChangedListener = listener;
    }

}


class MyHolder extends RecyclerView.ViewHolder{
    public Integer info;
    public View view;
    public MyHolder(View itemView) {
        super(itemView);
        view =itemView;
    }
}

interface OnDataChangedListener{
    void onDataChanged();
}
