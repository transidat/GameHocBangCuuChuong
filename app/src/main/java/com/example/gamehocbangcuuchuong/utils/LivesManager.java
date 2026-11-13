package com.example.gamehocbangcuuchuong.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.gamehocbangcuuchuong.R;

/**
 * Quản lý hiển thị trái tim mạng sống cho game và các dialog.
 */
public class LivesManager {

    private final ImageView heart1;
    private final ImageView heart2;
    private final ImageView heart3;

    public LivesManager(View rootView) {
        this.heart1 = rootView.findViewById(R.id.img_heart1);
        this.heart2 = rootView.findViewById(R.id.img_heart2);
        this.heart3 = rootView.findViewById(R.id.img_heart3);
    }

    public void updateHearts(int lives) {
        if (heart1 != null) heart1.setVisibility(lives >= 1 ? View.VISIBLE : View.INVISIBLE);
        if (heart2 != null) heart2.setVisibility(lives >= 2 ? View.VISIBLE : View.INVISIBLE);
        if (heart3 != null) heart3.setVisibility(lives >= 3 ? View.VISIBLE : View.INVISIBLE);
    }

    public static void populateHeartContainer(Context context, LinearLayout container, int lives) {
        if (container == null || context == null) return;

        container.removeAllViews();
        float density = context.getResources().getDisplayMetrics().density;
        int size = (int) (20 * density);
        int margin = (int) (2 * density);

        for (int i = 0; i < lives; i++) {
            ImageView heart = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            heart.setLayoutParams(params);
            heart.setImageResource(R.drawable.tim);
            heart.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.addView(heart);
        }
    }
}
