package com.mho.mytwitter.helpers;
//        ~ Copyright 2013 Chris Banes
//        ~
//        ~ Licensed under the Apache License, Version 2.0 (the "License");
//        ~ you may not use this file except in compliance with the License.
//        ~ You may obtain a copy of the License at
//        ~
//        ~     http://www.apache.org/licenses/LICENSE-2.0
//        ~
//        ~ Unless required by applicable law or agreed to in writing, software
//        ~ distributed under the License is distributed on an "AS IS" BASIS,
//        ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//        ~ See the License for the specific language governing permissions and
//        ~ limitations under the License.
//
import com.mho.mytwitter.R;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;

public class CustomisedHeaderTransformer
        extends HeaderTransformer {

    private View mHeaderView;
    private TextView mMainTextView;
    private TextView mProgressTextView;

    @Override
    public void onViewCreated(Activity activity, View headerView) {
        mHeaderView = headerView;
        mMainTextView = (TextView) headerView.findViewById(R.id.ptr_text);
        mProgressTextView = (TextView) headerView.findViewById(R.id.ptr_text_secondary);
    }

    @Override
    public void onReset() {
        mMainTextView.setVisibility(View.VISIBLE);
        mMainTextView.setText(R.string.pull_to_refresh_pull_label);

        mProgressTextView.setVisibility(View.GONE);
        mProgressTextView.setText("");
    }

    @Override
    public void onPulled(float percentagePulled) {
        mProgressTextView.setVisibility(View.VISIBLE);
        mProgressTextView.setText(Math.round(100f * percentagePulled) + "%");
    }

    @Override
    public void onRefreshStarted() {
        mMainTextView.setText(R.string.pull_to_refresh_refreshing_label);
        mProgressTextView.setVisibility(View.GONE);
    }

    @Override
    public void onReleaseToRefresh() {
        mMainTextView.setText(R.string.pull_to_refresh_release_label);
    }

    @Override
    public void onRefreshMinimized() {
        // In this header transformer, we will ignore this call
    }

    @Override
    public boolean showHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() != View.VISIBLE;
        if (changeVis) {
            mHeaderView.setVisibility(View.VISIBLE);
        }
        return changeVis;
    }

    @Override
    public boolean hideHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() == View.VISIBLE;
        if (changeVis) {
            mHeaderView.setVisibility(View.GONE);
        }
        return changeVis;
    }
}
