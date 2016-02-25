package com.shomen.MUChat.Adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shomen.MUChat.Interfaces.OnItemClickListener;
import com.shomen.MUChat.Interfaces.OnLoadMoreListener;
import com.shomen.MUChat.Models.SongListDataModels;
import com.shomen.MUChat.R;
import com.shomen.MUChat.Utils.CONSTANT;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by alchemy software on 1/16/2016.
 */
public class RecyclerViewSongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private List<SongListDataModels> songListDataModels;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private static OnItemClickListener clickListener;

    public RecyclerViewSongListAdapter(List<SongListDataModels> data , RecyclerView rv) {

        this.songListDataModels = data;

        if (rv.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv.getLayoutManager();
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    Log.d(TAG,"onScrolled method get called dx dy :"+dx+" "+dy);
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
//        Log.d(TAG," getItemViewType get called position = "+position);
        return songListDataModels.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG," onCreateViewHolder get called...");

        RecyclerView.ViewHolder rvViewHolder;

        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_song_list_view, parent, false);

            rvViewHolder = new SongListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_bar_item, parent, false);

            rvViewHolder = new ProgressBarViewHolder(view);
        }

        return rvViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG," onBindViewHolder get called...");
        if (holder instanceof SongListViewHolder) {
            ((SongListViewHolder) holder).songTitle.setText(songListDataModels.get(position).getTitle());
            Picasso.with(((SongListViewHolder) holder).songThumb.getContext())
                    .load(CONSTANT.PRODUCTION_URL.SONG_LIST_URL)
                    .placeholder(R.drawable.sound)
                    .into(((SongListViewHolder) holder).songThumb);

        } else {
            ((ProgressBarViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return songListDataModels.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

    public static class SongListViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        ImageView songThumb;

        public SongListViewHolder(final View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songThumb = (ImageView) itemView.findViewById(R.id.song_th_iv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null)
                        clickListener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    public static class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressBarViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

}