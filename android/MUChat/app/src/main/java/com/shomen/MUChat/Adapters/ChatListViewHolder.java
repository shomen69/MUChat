package com.shomen.MUChat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shomen.MUChat.Models.Conversation;
import com.shomen.MUChat.R;

/**
 * Created by server on 1/28/2016.
 */
public class ChatListViewHolder extends RecyclerView.ViewHolder {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();

    public View itemView;
    public TextView msg ;

    public ChatListViewHolder(View itemView) {
        super(itemView);
        Log.d(LOG_TAG, "inside ChatListViewHolder constructor..");
        this.itemView = itemView;
        msg = (TextView) itemView.findViewById(R.id.msg);
    }

    public void bind(Conversation conversation) {
        Log.d(LOG_TAG, "inside bind method..");
        msg.setText(conversation.getMessage());
    }
}
