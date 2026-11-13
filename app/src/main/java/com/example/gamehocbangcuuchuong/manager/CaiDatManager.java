package com.example.gamehocbangcuuchuong.manager;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;

/**
 * Quản lý khởi tạo và xử lý giao diện màn hình Cài đặt.
 */
public class CaiDatManager {

    public static void setup(MainActivity activity, View view, SharedPreferences sharedPreferences) {
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvMinRange = view.findViewById(R.id.tv_min_range);
        TextView tvMaxRange = view.findViewById(R.id.tv_max_range);

        ImageButton btnDecreaseMin = view.findViewById(R.id.btn_decrease_min);
        ImageButton btnIncreaseMin = view.findViewById(R.id.btn_increase_min);
        ImageButton btnDecreaseMax = view.findViewById(R.id.btn_decrease_max);
        ImageButton btnIncreaseMax = view.findViewById(R.id.btn_increase_max);

        // Nút quay lại trang chủ
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> activity.showScreen(R.layout.layout_trang_chu));
        }

        // Đọc giá trị cài đặt phạm vi bảng cửu chương hiện tại (mặc định từ 1 đến 9)
        int minRange = sharedPreferences.getInt("min_range", 1);
        int maxRange = sharedPreferences.getInt("max_range", 9);

        if (tvMinRange != null) {
            tvMinRange.setText(String.valueOf(minRange));
        }
        if (tvMaxRange != null) {
            tvMaxRange.setText(String.valueOf(maxRange));
        }

        // Nút Giảm phạm vi tối thiểu (Từ)
        if (btnDecreaseMin != null) {
            btnDecreaseMin.setOnClickListener(v -> {
                int currentMin = sharedPreferences.getInt("min_range", 1);
                if (currentMin > 1) {
                    currentMin--;
                    sharedPreferences.edit().putInt("min_range", currentMin).apply();
                    if (tvMinRange != null) {
                        tvMinRange.setText(String.valueOf(currentMin));
                    }
                } else {
                    Toast.makeText(activity, "Phạm vi nhỏ nhất là 1!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Nút Tăng phạm vi tối thiểu (Từ)
        if (btnIncreaseMin != null) {
            btnIncreaseMin.setOnClickListener(v -> {
                int currentMin = sharedPreferences.getInt("min_range", 1);
                int currentMax = sharedPreferences.getInt("max_range", 9);
                if (currentMin < currentMax) {
                    currentMin++;
                    sharedPreferences.edit().putInt("min_range", currentMin).apply();
                    if (tvMinRange != null) {
                        tvMinRange.setText(String.valueOf(currentMin));
                    }
                } else {
                    Toast.makeText(activity, "Chương đầu không được lớn hơn chương cuối!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Nút Giảm phạm vi tối đa (Đến)
        if (btnDecreaseMax != null) {
            btnDecreaseMax.setOnClickListener(v -> {
                int currentMin = sharedPreferences.getInt("min_range", 1);
                int currentMax = sharedPreferences.getInt("max_range", 9);
                if (currentMax > currentMin) {
                    currentMax--;
                    sharedPreferences.edit().putInt("max_range", currentMax).apply();
                    if (tvMaxRange != null) {
                        tvMaxRange.setText(String.valueOf(currentMax));
                    }
                } else {
                    Toast.makeText(activity, "Chương cuối không được nhỏ hơn chương đầu!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Nút Tăng phạm vi tối đa (Đến)
        if (btnIncreaseMax != null) {
            btnIncreaseMax.setOnClickListener(v -> {
                int currentMax = sharedPreferences.getInt("max_range", 9);
                if (currentMax < 9) {
                    currentMax++;
                    sharedPreferences.edit().putInt("max_range", currentMax).apply();
                    if (tvMaxRange != null) {
                        tvMaxRange.setText(String.valueOf(currentMax));
                    }
                } else {
                    Toast.makeText(activity, "Phạm vi lớn nhất là 9!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
