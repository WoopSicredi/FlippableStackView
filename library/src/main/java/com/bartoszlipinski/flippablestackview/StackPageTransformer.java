/**
 * Copyright 2015 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bartoszlipinski.flippablestackview;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * Created by Bartosz Lipinski
 * 28.01.15
 */
public class StackPageTransformer implements ViewPager.PageTransformer {

    public enum Orientation {
        VERTICAL(OrientedViewPager.Orientation.VERTICAL),
        HORIZONTAL(OrientedViewPager.Orientation.HORIZONTAL);

        private final OrientedViewPager.Orientation mOrientation;

        Orientation(OrientedViewPager.Orientation orientation) {
            mOrientation = orientation;
        }

        public OrientedViewPager.Orientation getViewPagerOrientation() {
            return mOrientation;
        }
    }

    public enum Gravity {
        TOP, CENTER, BOTTOM
    }

    private int mNumberOfStacked;

    private float mAlphaFactor;
    private float mZeroPositionScale;
    private float mStackedScaleFactor;
    private float mOverlapFactor;
    private float mOverlap;
    private float mStackSpace;

    private boolean mInitialValuesCalculated = false;

    private Orientation mOrientation;
    private Gravity mGravity;

    /**
     * Used to construct the basic method for visual transformation in <code>FlippableStackView</code>.
     *
     * @param numberOfStacked  Number of pages stacked under the current page.
     * @param orientation      Orientation of the stack.
     * @param currentPageScale Scale of the current page. Must be a value from (0, 1].
     * @param topStackedScale  Scale of the top stacked page. Must be a value from
     *                         (0, <code>currentPageScale</code>].
     * @param overlapFactor    Defines the usage of available space for the overlapping by stacked
     *                         pages. Must be a value from [0, 1]. Value 1 means that the whole
     *                         available space (obtained due to the scaling with
     *                         <code>currentPageScale</code>) will be used for the purpose of displaying
     *                         stacked views. Value 0 means that no space will be used for this purpose
     *                         (in other words - no stacked views will be visible).
     * @param gravity          Specifies the alignment of the stack (vertically) withing <code>View</code>
     *                         bounds.
     */
    public StackPageTransformer(int numberOfStacked, Orientation orientation, float currentPageScale, float topStackedScale, float overlapFactor, Gravity gravity) {
        validateValues(currentPageScale, topStackedScale, overlapFactor);

        mNumberOfStacked = numberOfStacked;
        mAlphaFactor = 1.0f / (mNumberOfStacked + 1);
        mZeroPositionScale = currentPageScale;
        mStackedScaleFactor = (currentPageScale - topStackedScale) / mNumberOfStacked;
        mOverlapFactor = overlapFactor;
        mOrientation = orientation;
        mGravity = gravity;
    }

    @Override
    public void transformPage(View view, float position) {

        int dimen = 0;
        switch (mOrientation) {
            case VERTICAL:
                dimen = view.getHeight();
                break;
            case HORIZONTAL:
                dimen = view.getWidth();
                break;
        }

        if (!mInitialValuesCalculated) {
            mInitialValuesCalculated = true;
            calculateInitialValues(dimen);
        }

        if (position < -mNumberOfStacked - 1) {
            view.setAlpha(0f);
        } else if (position <= 0) {
            float scale = mZeroPositionScale + (position * mStackedScaleFactor);
            float baseTranslation = (-position * dimen);
            float shiftTranslation = calculateShiftForScale(position, scale, dimen);
            view.setScaleX(scale);
            view.setScaleY(scale);
            view.setAlpha(1.0f + (position * mAlphaFactor));
            switch (mOrientation) {
                case VERTICAL:
                    view.setTranslationY(baseTranslation + shiftTranslation);
                    break;
                case HORIZONTAL:
                    view.setTranslationX(baseTranslation + shiftTranslation);
                    break;
            }
        } else if (position <= 1) {
            if (Math.round(view.getAlpha()) == 0f)
                view.setAlpha(1.0f);

            switch (mOrientation) {
                case VERTICAL:
                    view.setScaleX(mZeroPositionScale);
                    view.setScaleY(mZeroPositionScale);
                    break;
                case HORIZONTAL:
                    view.setScaleY(mZeroPositionScale);
                    view.setScaleX(mZeroPositionScale);
                    break;
            }
        } else if (position > 1) {
            view.setAlpha(0f);
        }
    }

    private void calculateInitialValues(int dimen) {
        float scaledDimen = mZeroPositionScale * dimen;

        float overlapBase = (dimen - scaledDimen) / (mNumberOfStacked + 1);
        mOverlap = overlapBase * mOverlapFactor;

        float availableSpaceUnit = 0.5f * dimen * (1 - mOverlapFactor) * (1 - mZeroPositionScale);
        switch (mGravity) {

            case TOP:
                mStackSpace = 0;
                break;
            case CENTER:
                mStackSpace = availableSpaceUnit;
                break;
            case BOTTOM:
                mStackSpace = 2 * availableSpaceUnit;
                break;
        }
    }

    private float calculateShiftForScale(float position, float scale, int dimen) {
        //difference between centers
        return mStackSpace + ((mNumberOfStacked + position) * mOverlap) + (dimen * 0.5f * (scale - 1));
    }

    private void validateValues(float currentPageScale, float topStackedScale, float overlapFactor) {
        if (currentPageScale <= 0 || currentPageScale > 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Current page scale not correctly defined. " +
                    "Be sure to set it to value from (0, 1].");
        }

        if (topStackedScale <= 0 || topStackedScale > currentPageScale) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Top stacked page scale not correctly defined. " +
                    "Be sure to set it to value from (0, currentPageScale].");
        }

        if (overlapFactor < 0 || overlapFactor > 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Overlap factor not correctly defined. " +
                    "Be sure to set it to value from [0, 1].");
        }
    }
}
