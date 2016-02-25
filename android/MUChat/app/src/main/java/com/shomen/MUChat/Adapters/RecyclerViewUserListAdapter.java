package com.shomen.MUChat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shomen.MUChat.Models.User;
import com.shomen.MUChat.Interfaces.OnItemClickListener;
import com.shomen.MUChat.R;
import com.shomen.MUChat.Singletons.UsersListController;

/**
 * Created by server on 1/25/2016.
 */
public class RecyclerViewUserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {

    private UsersListController usersListController = UsersListController.getIntance();
    private static OnItemClickListener listener;


    public RecyclerViewUserListAdapter() {
    }

    @Override
    public void onBindViewHolder(UserListViewHolder itemViewHolder, int i) {
        final User model = usersListController.getUserList().get(i);
        itemViewHolder.bind(model);
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_user_list_view, viewGroup, false);
        return new UserListViewHolder(view,listener);
    }

    @Override
    public int getItemCount() {
        return usersListController.getUserList().size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
