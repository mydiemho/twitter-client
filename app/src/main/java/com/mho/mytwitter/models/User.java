package com.mho.mytwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private long userId;
    private String name;
    private String screenName;
    private String profileImageUrl;

    public static User fromJsonObject(JSONObject jsonObject) {
        User user = new User();

        try {
            user.userId = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return user;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
