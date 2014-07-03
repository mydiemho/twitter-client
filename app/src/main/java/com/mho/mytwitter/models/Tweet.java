package com.mho.mytwitter.models;

import com.google.common.base.Objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static com.activeandroid.annotation.Column.ForeignKeyAction;

@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {

    private static final String TAG = Tweet.class.getSimpleName() + "_DEBUG";

    // Define table fields
    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long tweetId;

    // Define table fields
    @Column(name = "body")
    private String body;

    // Define table fields
    @Column(name = "created_at")
    private String createdAt;

    // Define table fields
    @Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "media_url")
    private String mediaUrl = "";

    // Define table fields
    @Column(name = "retweet_count")
    private int retweetCount;

    @Column(name = "favorite_count")
    private int favoriteCount;

    // necessary to use ActiveAndroid
    public Tweet() {
        super();
    }

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
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    public static Tweet fromJsonObject(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        // populate object with extracted values;
        try {
            tweet.body = jsonObject.getString("text");
            tweet.tweetId = jsonObject.getLong("id_str");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJsonObject(jsonObject.getJSONObject("user"));
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");

            JSONObject entities = jsonObject.getJSONObject("entities");

            try {
                JSONArray media = entities.getJSONArray("media");
                JSONObject mediaObject = media.getJSONObject(0);
                tweet.mediaUrl = mediaObject.getString("media_url");

//                Log.d(TAG, "mediaUrl not null");
//                Log.d(TAG, tweet.toString());
            } catch (JSONException exception) {
//                Log.d(TAG, exception.toString());
//                Log.d(TAG, tweet.getMediaUrl());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("tweetId", tweetId)
                .add("body", body)
                .add("createdAt", createdAt)
//                .add("user", user)
//                .add("mediaUrl", mediaUrl)
                .toString() + "\n";
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
        dest.writeString(this.mediaUrl);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favoriteCount);
    }

    private Tweet(Parcel in) {
        this.tweetId = in.readLong();
        this.body = in.readString();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.mediaUrl = in.readString();
        this.retweetCount = in.readInt();
        this.favoriteCount = in.readInt();
    }

    public static Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public static List<Tweet> getAll() {
        return new Select()
                .from(Tweet.class)
//                .where("Category = ?", category.getId())
                .orderBy("tweet_id DESC")
                .execute();
    }
}
