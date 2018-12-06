/**
 * Copyright 2015 Bartosz Lipinski
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bartoszlipinski.flippablestackview.sample.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.bartoszlipinski.flippablestackview.FlippableStackView;
import com.bartoszlipinski.flippablestackview.StackPageTransformer;
import com.bartoszlipinski.flippablestackview.sample.R;
import com.bartoszlipinski.flippablestackview.sample.fragment.ColorFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Lipinski
 * 12.12.14
 */
public class MainActivity extends AppCompatActivity {

    private static final int NUMBER_OF_FRAGMENTS = 9;

    private List<Fragment> mViewPagerFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createViewPagerFragments();

        FragmentPagerAdapter mPageAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mViewPagerFragments.size();
            }

            @Override
            public Fragment getItem(int i) {
                return mViewPagerFragments.get(i);
            }
        };

        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        FlippableStackView mFlippableStack = findViewById(R.id.flippable_stack_view);
        mFlippableStack.initStack(2, portrait ?
                StackPageTransformer.Orientation.VERTICAL :
                StackPageTransformer.Orientation.HORIZONTAL);
        mFlippableStack.setAdapter(mPageAdapter);
    }

    private void createViewPagerFragments() {
        mViewPagerFragments = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_FRAGMENTS; ++i) {
            mViewPagerFragments.add(ColorFragment.newInstance());
        }
    }
}