package com.example.chatsapp.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserStory {

    private String name, profileName;
    private long lastUpdated;
    private ArrayList<Story> userStoryList;

    public UserStory(){

    }

    public UserStory(String name, String profileName, long lastUpdated, ArrayList<Story> userStoryList) {
        this.name = name;
        this.profileName = profileName;
        this.lastUpdated = lastUpdated;
        this.userStoryList = userStoryList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Story> getUserStoryList() {
        return userStoryList;
    }

    public void setUserStoryList(ArrayList<Story> userStoryList) {
        this.userStoryList = userStoryList;
    }
}
