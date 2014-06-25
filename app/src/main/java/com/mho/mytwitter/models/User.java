package com.mho.mytwitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

@Table(name = "Users")
public class User extends Model implements Parcelable {

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Define table fields, avoid duplicates based on a unique ID
    @Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
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
    }

    private User(Parcel in) {
        this.userId = in.readLong();
        this.name = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
