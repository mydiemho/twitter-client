package com.mho.mytwitter.helpers;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {

    private static final String TAG = TwitterClient.class.getSimpleName() + "_DEBUG";
    private static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;

    private static final String REST_URL = "https://api.twitter.com/1.1";
    private static final String HOME_TIMELINE_PATH = "/statuses/home_timeline.json";
    private static final String MENTIONS_TIMELINE_PATH = "/statuses/mentions_timeline.json";
    private static final String USER_TIMELINE_PATH = "/statuses/user_timeline.json";
    private static final String MY_INFO_PATH = "/account/verify_credentials.json";
    private static final String UPDATE_PATH = "/statuses/update.json";

    // oauth required settings
    public static final String REST_CONSUMER_KEY = "y24E6IvyVbu068VEkFq1j17lG";
    public static final String REST_CONSUMER_SECRET
            = "me7Wkq3BxsNw2KllNX7xRXxjhRqZCmVODLxXmyyCyGzSMYzVM8";
    public static final String REST_CALLBACK_URL = "oauth://mydiemho-twitter-client";

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET,
                REST_CALLBACK_URL);
    }

    public void getHomeTimeline(
            int count,
            long sinceId,
            long maxId,
            AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl(HOME_TIMELINE_PATH);

        // NOTE: if there are no parameters, pass null instead
        RequestParams params = getRequestParams(count, sinceId, maxId);


        Log.d(TAG, "geronimo -> home");
        Log.d(TAG, params.toString());
        client.get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(
            int count,
            long sinceId,
            long maxId,
            AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl(MENTIONS_TIMELINE_PATH);

        // NOTE: if there are no parameters, pass null instead
        RequestParams params = getRequestParams(count, sinceId, maxId);

        Log.d(TAG, "geronimo -> mentions");
        Log.d(TAG, params.toString());
        client.get(apiUrl, params, handler);
    }

    public void getMyInfo(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl(MY_INFO_PATH);
        client.get(apiUrl, null, handler);
    }

    public void getUserTimeline(JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl(USER_TIMELINE_PATH);
        client.get(apiUrl, null, handler);
    }

    public void postTweet(String body, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(UPDATE_PATH);
        RequestParams params = new RequestParams();
        params.put("status", body);

        getClient().post(apiUrl, params, handler);
    }

    private RequestParams getRequestParams(int count, long sinceId, long maxId) {
        RequestParams params = new RequestParams();

        if (sinceId > 0) {
            params.put("since_id", String.valueOf(sinceId));
        }

        if (maxId > 0) {
            params.put("max_id", String.valueOf(maxId));
        }
        params.put("count", String.valueOf(count));

        return params;
    }
}