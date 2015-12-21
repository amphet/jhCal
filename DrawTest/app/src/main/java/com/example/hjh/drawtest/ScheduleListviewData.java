package com.example.hjh.drawtest;


import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

public class ScheduleListviewData {
    /**
     * ??? ??? ?? ?? ?? ??
     */
    // ???
    public Drawable mIcon;

    // ??
    public String mTitle;

    // ??
    public String mDate;

    public int mColor;
    public int nId;
    /**
     * ??? ???? ??
     */
    public static final Comparator<ScheduleListviewData> ALPHA_COMPARATOR = new Comparator<ScheduleListviewData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ScheduleListviewData mListDate_1, ScheduleListviewData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
