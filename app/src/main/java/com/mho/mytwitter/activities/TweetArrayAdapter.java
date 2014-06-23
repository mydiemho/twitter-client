package com.mho.mytwitter.activities;

import com.google.common.base.Splitter;

import com.mho.mytwitter.R;
import com.mho.mytwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get data for the current position
        Tweet tweet = getItem(position);

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

        ImageLoader imageLoader = ImageLoader.getInstance();

        // populate views with tweet data
        imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
        viewHolder.tvTimestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvBody.setText(tweet.getBody());

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

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            relativeDate = DateUtils.getRelativeTimeSpanString(
                    dateMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // hack to get twitter-like relative time format
        Iterable<String> tokens = Splitter.on(' ').split(relativeDate);
//        Log.d("DEBUG", tokens.toString());
        Iterator<String> iterator = tokens.iterator();
        String value = iterator.next();
        char unit = iterator.next().charAt(0);

        return value + unit;
    }
}
