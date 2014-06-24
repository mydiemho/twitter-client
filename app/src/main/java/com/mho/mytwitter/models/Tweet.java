package com.mho.mytwitter.models;

import com.google.common.base.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Tweet implements Parcelable {
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

    public static Tweet fromJsonObject(JSONObject jsonObject)
    {
        Tweet tweet = new Tweet();

        // populate object with extracted values;
        try {
            tweet.body = jsonObject.getString("text");
            tweet.tweetId = jsonObject.getLong("id_str");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJsonObject(jsonObject.getJSONObject("user"));
//            Log.d("DEBUG", tweet.toString());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.tweetId);
        dest.writeString(this.body);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.user, 0);
    }

    public Tweet() {
    }

    private Tweet(Parcel in) {
        this.tweetId = in.readLong();
        this.body = in.readString();
        this.createdAt = in.readString();
        this.user = in.readParcelable(((Object) user).getClass().getClassLoader());
    }

    public static Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
