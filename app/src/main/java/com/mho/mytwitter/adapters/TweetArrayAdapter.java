package com.mho.mytwitter.adapters;

import com.mho.mytwitter.R;
import com.mho.mytwitter.activities.ProfileActivity;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;
import com.mho.mytwitter.models.User;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    private static final String TAG = TweetArrayAdapter.class.getSimpleName() + "_DEBUG";

    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get data for the current position
        final Tweet tweet = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tweet_item, parent, false);

            // find the views within tweet_item template
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // clean out previous image
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        viewHolder.ivProfileImage.setTag(tweet.getUser().getUserId());

        // populate views with tweet data
        Picasso
                .with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.ivProfileImage);

        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
        viewHolder.tvTimestamp.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvBody.setText(tweet.getBody());

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfileActivity(tweet.getUser());
            }
        });
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimestamp;
        TextView tvBody;
    }




    private void launchProfileActivity(User user) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("user", user);
        getContext().startActivity(intent);
    }
}
