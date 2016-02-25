package com.shomen.MUChat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shomen.MUChat.Singletons.UsersListController;
import com.shomen.MUChat.Models.Conversation;
import com.shomen.MUChat.R;

import java.util.List;

/**
 * Created by server on 1/28/2016.
 */
public class RecyclerViewChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();
    private UsersListController usersListController = UsersListController.getIntance();
    private String id;
    private int VIEW_TYPE;
    private List<Conversation> conversationList;

    public RecyclerViewChatListAdapter(String id) {
        this.id = id;
    }

    public RecyclerViewChatListAdapter(List<Conversation> conv) {
        this.conversationList = conv;
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder itemViewHolder, int i) {
        Log.d(LOG_TAG,"inside onBindViewHolder method ");
        final Conversation model = usersListController.getUserListMap().get(id).getConversationList().get(i);
        itemViewHolder.bind(model);
    }

    @Override
    public int getItemViewType(int i) {
        if(usersListController.getUserListMap().get(id).getConversationList().get(i).isFromMe())
            return VIEW_TYPE = 0;
        else
            return VIEW_TYPE = 1;
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG,"inside onCreateViewHolder method "+i);
        View view;

        if( VIEW_TYPE == 0 ){
            Log.d(LOG_TAG,"from me");
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chat_list_view, viewGroup, false);
        }else{
            Log.d(LOG_TAG,"not from me");
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chat_list_view_me, viewGroup, false);
        }
        return new ChatListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        Log.d(LOG_TAG,"inside getItemCount method ");
        return usersListController.getUserListMap().get(id).getConversationList().size();
    }

}

