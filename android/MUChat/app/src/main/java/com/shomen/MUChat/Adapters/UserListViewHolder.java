package com.shomen.MUChat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shomen.MUChat.Models.User;
import com.shomen.MUChat.Interfaces.OnItemClickListener;
import com.shomen.MUChat.R;

/**
 * Created by server on 1/25/2016.
 */
public class UserListViewHolder extends RecyclerView.ViewHolder {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();

    public View itemView;
    public TextView user_name;
    public TextView notification_count;

    public UserListViewHolder(final View itemView, final OnItemClickListener listener ) {
        super(itemView);
        this.itemView = itemView;
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        notification_count = (TextView) itemView.findViewById(R.id.notification_counter);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(itemView, getLayoutPosition());
            }
        });
    }

    public void bind(User user) {
        user_name.setText(user.getName());
        Log.d(LOG_TAG, "-----noti :" + user.getNoti());
        if(user.getNoti()>0){
            Log.d(LOG_TAG,"noti :"+user.getNoti());
            notification_count.setText(String.valueOf(user.getNoti()));
            notification_count.setVisibility(View.VISIBLE);
        }else{
            notification_count.setVisibility(View.GONE);
        }
    }
}
