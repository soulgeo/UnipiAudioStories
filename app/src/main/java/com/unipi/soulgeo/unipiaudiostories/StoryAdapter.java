package com.unipi.soulgeo.unipiaudiostories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> storyList;
    private OnEventClickListener listener;

    // Interface to handle clicks
    public interface OnEventClickListener {
        void onEventClick(Story event);
    }

    public StoryAdapter(List<Story> storyList, OnEventClickListener listener) {
        this.storyList = storyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.tvTitle.setText(story.getTitle());
        holder.tvAuthor.setText(story.getAuthor());

        // Load Image with Blur
        Glide.with(holder.itemView.getContext())
                .load(story.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 2))) // radius, sampling
                .placeholder(R.drawable.ic_launcher_background) // Placeholder while loading
                .into(holder.imgBackground);

        holder.itemView.setOnClickListener(v -> listener.onEventClick(story));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor;
        android.widget.ImageView imgBackground; // Add this

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            imgBackground = itemView.findViewById(R.id.imgBackground); // Add this
        }
    }
}

