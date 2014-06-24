package com.mho.mytwitter.fragments;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.helpers.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;
import com.mho.mytwitter.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class ComposeDiaglog extends DialogFragment implements DialogInterface.OnClickListener{

    public interface ComposeDialogListener {
        void onFinishTweet(Tweet tweet);
    }

    private static final int MAX_CHAR_COUNT = 140;

    private ImageView ivUserProfileImage;
    private TextView tvUserName;
    private TextView tvUserScreenName;
    private EditText etTweet;
    private TextView tvCharsLeft;
    private Button btnTweet;

    private TwitterClient twitterClient;
    private User user;

    public ComposeDiaglog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ComposeDiaglog newInstance(String title) {
        ComposeDiaglog composeDiaglog = new ComposeDiaglog();
        Bundle args = new Bundle();
        args.putString("title", title);
        composeDiaglog.setArguments(args);
        return composeDiaglog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        twitterClient = TwitterApplication.getTwitterClient();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compose, container);

        user = TwitterApplication.getUser();

        setUpViews(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void setUpViews(View view) {

        ivUserProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) view.findViewById(R.id.tvUsername);
        tvUserScreenName = (TextView) view.findViewById(R.id.tvUserScreenName);

        Picasso
                .with(getDialog().getContext())
                .load(user.getProfileImageUrl())
                .into(ivUserProfileImage);

        tvUserName.setText(user.getName());
        tvUserName.setText(user.getScreenName());

        etTweet = (EditText) view.findViewById(R.id.etTweet);
        tvCharsLeft = (TextView) view.findViewById(R.id.tvCharsLeft);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);

        setUpListeners();
    }

    private void setUpListeners() {
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // update chars count label
                int charsLeft = MAX_CHAR_COUNT - etTweet.getText().length();
//                Log.d("DEBUG", String.valueOf(charsLeft));

                if (charsLeft < 0) {
                    tvCharsLeft.setTextColor(
                            getResources().getColor(R.color.twitter_over_char_limit));
                } else {
                    tvCharsLeft.setTextColor(getResources().getColor(R.color.twitter_normal_text));
                }

                btnTweet.setEnabled(charsLeft >= 0);
                btnTweet.setAlpha(charsLeft >= 0 ? 1 : (float) 0.4);
                tvCharsLeft.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        updateStatus();
    }

    private void updateStatus() {
        Log.d("DEBUG", "calling send tweet");
        // jump back to TimelineActivity when done

        twitterClient.postTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                Log.d("DEBUG", "successfully send tweet");
                Log.d("DEBUG", "response: " + response);

                Tweet tweet = Tweet.fromJsonObject(response);

                Log.d("DEBUG", "new tweet: " + tweet);

                ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                listener.onFinishTweet(tweet);
                dismiss();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    notifyUser(getString(R.string.msg_send_fail));
                } else {
                    notifyUser(getString(R.string.msg_network_unavailble));
                }
                Log.d("DEBUG", "error: " + error.toString());
                Log.d("DEBUG", "content: " + content);
            }
        });
    }

    private void notifyUser(String msg) {
        Crouton.showText(getActivity(), msg, Utils.STYLE);
    }
}
