package com.example.chatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.R;
import com.example.chatsapp.activity.MainActivity;
import com.example.chatsapp.databinding.ItemStoryBinding;
import com.example.chatsapp.model.Story;
import com.example.chatsapp.model.UserStory;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends
        RecyclerView.Adapter<StoryAdapter.StoryViewHolder>{

    Context context;
    ArrayList<UserStory> userStoryList;


    public StoryAdapter(Context context, ArrayList<UserStory> storiesList) {
        this.context = context;
        this.userStoryList = storiesList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        UserStory userStory = userStoryList.get(position);

        Story lastStory = userStory.getUserStoryList().get(userStory.getUserStoryList().size() - 1 );

        Glide.with(context).load(lastStory.getImageUrl()).into(holder.binding.avatarStory);
        holder.binding.circularStatusView.setPortionsCount(userStory.getUserStoryList().size());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Story story: userStory.getUserStoryList()){
                    myStories.add(new MyStory(story.getImageUrl()));
                }

                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStory.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStory.getProfileName()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userStoryList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {

        ItemStoryBinding binding;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStoryBinding.bind(itemView);

        }
    }
}
