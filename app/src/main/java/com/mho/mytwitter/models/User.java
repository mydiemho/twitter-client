package com.mho.mytwitter.models;

import com.google.common.base.Objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

@Table(name = "Users")
public class User extends Model implements Parcelable {

    // Define table fields, avoid duplicates based on a unique ID
    @Column(name = "user_id")
    private long userId;

    // Define table fields
    @Column(name = "name")
    private String name;

    // Define table fields
    @Column(name = "screen_name")
    private String screenName;

    // Define table fields
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "followers_count")
    private int followersCount;

    @Column(name = "friends_count")
    private int friendsCount;

    public User() {
        super();
    }

    public static User fromJsonObject(JSONObject jsonObject) {
        User user = new User();

        try {
            user.userId = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.followersCount = jsonObject.getInt("followers_count");
            user.friendsCount = jsonObject.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DEBUG", "error converting to user");
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

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.friendsCount);
    }

    private User(Parcel in) {
        this.userId = in.readLong();
        this.name = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.followersCount = in.readInt();
        this.friendsCount = in.readInt();
    }

    public static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("name", name)
                .add("screenName", screenName)
                .add("profileImageUrl", profileImageUrl)
                .add("followersCount", followersCount)
                .add("friendsCount", friendsCount)
                .toString();
    }
}
