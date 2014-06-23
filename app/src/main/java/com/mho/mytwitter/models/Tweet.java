package com.mho.mytwitter.models;

import com.google.common.base.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    private long tweetId;
    private String body;
    private String createdAt;
    private User user;

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        JSONObject tweetJsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                tweetJsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = fromJsonObject(tweetJsonObject);
            if(tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    private static Tweet fromJsonObject(JSONObject jsonObject)
    {
        Tweet tweet = new Tweet();

        // populate object with extracted values;
        try {
            tweet.body = jsonObject.getString("text");
            tweet.tweetId = jsonObject.getLong("id_str");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJsonObject(jsonObject.getJSONObject("user"));
            Log.d("DEBUG", tweet.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }

        return tweet;
    }

    public long getTweetId() {
        return tweetId;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("user", user.getName())
                .add("tweetId", tweetId)
                .add("body", body)
                .toString();
    }
}
