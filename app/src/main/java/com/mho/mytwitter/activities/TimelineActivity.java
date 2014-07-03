package com.mho.mytwitter.activities;

import com.astuetz.PagerSlidingTabStrip;
import com.mho.mytwitter.R;
import com.mho.mytwitter.adapters.MyPagerAdapter;
import com.mho.mytwitter.apps.TwitterApplication;
import com.mho.mytwitter.fragments.HomeTimelineFragment;
import com.mho.mytwitter.fragments.MentionsTimelineFragment;
import com.mho.mytwitter.fragments.TweetsListFragment;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.Tweet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.app.ActionBar.Tab;

public class TimelineActivity extends ActionBarActivity {

    private Tab mTabHome;
    private Tab mTabMentions;

    private FragmentPagerAdapter mAdapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MUST request the feature before setting content view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_timeline);

        setUpActionBar();
        setupTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_compose:
                displayComposePanel();
                return true;
            case R.id.action_display_profile:
                displayProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupTabs() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Initialize the ViewPager and set an adapter
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        mAdapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
        vpPager.setAdapter(mAdapterViewPager);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(vpPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAdapterViewPager.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void displayComposePanel() {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, Utils.COMPOSE_REQUEST_CODE);
    }

    private void displayProfile() {
        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
        i.putExtra("user", TwitterApplication.getUser());
        startActivity(i);
    }

    private void setUpActionBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", "Back to timeline: " + String.valueOf(requestCode));
        if ((requestCode == Utils.COMPOSE_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            Tweet latestTweet = data.getParcelableExtra("tweet");

            // persist data
            latestTweet.getUser().save();
            latestTweet.save();

            Log.d("DEBUG", "latestTweet: " + latestTweet.toString());

            ((TweetsListFragment) getSupportFragmentManager().findFragmentByTag("home"))
                    .postNewTweetToTop(
                            latestTweet);
            getSupportActionBar().selectTab(mTabHome);
        }
    }

    private List<Fragment> getFragments()
    {
        List<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(HomeTimelineFragment.newInstance("Home", 0));
        fragments.add(MentionsTimelineFragment.newInstance("Mentions", 1));

        return fragments;
    }
}
