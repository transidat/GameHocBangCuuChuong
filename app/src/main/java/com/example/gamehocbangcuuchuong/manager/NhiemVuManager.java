package com.example.gamehocbangcuuchuong.manager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.adapter.MissionAdapter;
import com.example.gamehocbangcuuchuong.data.DatabaseHelper;
import com.example.gamehocbangcuuchuong.model.Mission;
import com.example.gamehocbangcuuchuong.utils.ScoreManager;

import java.util.List;

/**
 * Quản lý khởi tạo và xử lý giao diện màn hình Nhiệm vụ.
 */
public class NhiemVuManager {

    public static void setup(MainActivity activity, View view, SharedPreferences sharedPreferences) {
        DatabaseHelper dbHelper = activity.getDbHelper();
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvTotalScore = view.findViewById(R.id.tv_total_score);
        View tabToday = view.findViewById(R.id.tab_today);
        View tabWeek = view.findViewById(R.id.tab_week);
        TextView tvTabToday = view.findViewById(R.id.tv_tab_today);
        TextView tvTabWeek = view.findViewById(R.id.tv_tab_week);
        RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);
        ImageView imgChest = view.findViewById(R.id.img_chest);
        TextView tvChestDesc = view.findViewById(R.id.tv_chest_desc);
        ProgressBar progressChest = view.findViewById(R.id.progress_chest);
        TextView tvChestRatio = view.findViewById(R.id.tv_chest_ratio);
        View layoutBottomChest = view.findViewById(R.id.layout_bottom_chest);

        // Quay lại trang chủ
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> activity.showScreen(R.layout.layout_trang_chu));
        }

        // Điểm KN hiện tại
        int totalScore = ScoreManager.getTotalScore(activity);
        if (tvTotalScore != null) {
            tvTotalScore.setText(activity.getString(R.string.score_exp_format, totalScore));
        }

        if (rvTasks != null) {
            rvTasks.setLayoutManager(new LinearLayoutManager(activity));
        }

        final String[] currentTab = {"today"};

        Runnable updateMissionList = new Runnable() {
            @Override
            public void run() {
                List<Mission> missions = dbHelper.getMissionsByCategory(currentTab[0]);

                // Cập nhật giao diện tab active
                if ("today".equals(currentTab[0])) {
                    if (tabToday != null) tabToday.setBackgroundResource(R.drawable.bg_tab_selected);
                    if (tabWeek != null) tabWeek.setBackgroundResource(R.drawable.bg_tab_unselected);
                    if (tvTabToday != null) tvTabToday.setTextColor(Color.WHITE);
                    if (tvTabWeek != null) tvTabWeek.setTextColor(Color.parseColor("#0B1B72"));
                } else {
                    if (tabToday != null) tabToday.setBackgroundResource(R.drawable.bg_tab_unselected);
                    if (tabWeek != null) tabWeek.setBackgroundResource(R.drawable.bg_tab_selected);
                    if (tvTabToday != null) tvTabToday.setTextColor(Color.parseColor("#0B1B72"));
                    if (tvTabWeek != null) tvTabWeek.setTextColor(Color.WHITE);
                }

                // Tính tiến độ rương phần thưởng
                int completedCount = 0;
                for (Mission m : missions) {
                    if (m.isCompleted()) {
                        completedCount++;
                    }
                }

                int displayChestProgress = Math.min(3, completedCount);
                if (progressChest != null) {
                    progressChest.setMax(3);
                    progressChest.setProgress(displayChestProgress);
                }
                if (tvChestRatio != null) {
                    tvChestRatio.setText(activity.getString(R.string.progress_ratio_format, displayChestProgress, 3));
                }

                // Kiểm tra trạng thái đã nhận thưởng rương chưa
                boolean isChestClaimed = sharedPreferences.getBoolean("chest_claimed_" + currentTab[0], false);

                if (displayChestProgress >= 3 && !isChestClaimed) {
                    if (tvChestDesc != null) {
                        tvChestDesc.setText(R.string.chest_desc_claimable);
                    }

                    // Hiệu ứng rương rung rinh nhẹ khi chưa nhận
                    if (imgChest != null) {
                        RotateAnimation rotate = new RotateAnimation(
                                -6f, 6f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotate.setDuration(180);
                        rotate.setRepeatCount(Animation.INFINITE);
                        rotate.setRepeatMode(Animation.REVERSE);
                        imgChest.startAnimation(rotate);
                    }

                    if (layoutBottomChest != null) {
                        layoutBottomChest.setOnClickListener(v -> {
                            // Đánh dấu đã nhận rương phần thưởng
                            sharedPreferences.edit().putBoolean("chest_claimed_" + currentTab[0], true).apply();

                            String playerName = sharedPreferences.getString("player_name", "Người chơi");
                            int newTotal = ScoreManager.addTotalScore(activity, dbHelper, playerName, 50);
                            if (tvTotalScore != null) {
                                tvTotalScore.setText(activity.getString(R.string.score_exp_format, newTotal));
                            }
                            Toast.makeText(activity, "Chúc mừng! Bạn mở rương nhận thêm 50 KN! 🎉", Toast.LENGTH_LONG).show();

                            // Nạp lại giao diện để dừng hiệu ứng rung rinh
                            run();
                        });
                    }
                } else {
                    // Đã nhận rồi hoặc chưa đủ 3 nhiệm vụ -> Đứng yên
                    if (imgChest != null) {
                        imgChest.clearAnimation();
                        imgChest.setRotation(0f);
                    }

                    if (isChestClaimed) {
                        if (tvChestDesc != null) {
                            tvChestDesc.setText(R.string.chest_desc_claimed);
                        }
                        if (layoutBottomChest != null) {
                            layoutBottomChest.setOnClickListener(v ->
                                Toast.makeText(activity, "Bạn đã nhận phần thưởng rương rồi! 🎁", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } else {
                        if (tvChestDesc != null) {
                            tvChestDesc.setText(R.string.chest_desc_uncompleted);
                        }
                        if (layoutBottomChest != null) {
                            layoutBottomChest.setOnClickListener(null);
                        }
                    }
                }

                // Gán Adapter cho danh sách
                if (rvTasks != null) {
                    MissionAdapter adapter = new MissionAdapter(missions, new MissionAdapter.OnMissionClickListener() {
                        @Override
                        public void onDoMission(Mission mission) {
                            activity.showScreen(mission.getGameLayoutId());
                        }

                        @Override
                        public void onClaimReward(Mission mission) {
                            dbHelper.setMissionRewardClaimed(mission.getId(), true);
                            String playerName = sharedPreferences.getString("player_name", "Người chơi");
                            int newTotal = ScoreManager.addTotalScore(activity, dbHelper, playerName, mission.getRewardPoints());
                            if (tvTotalScore != null) {
                                tvTotalScore.setText(activity.getString(R.string.score_exp_format, newTotal));
                            }
                            Toast.makeText(activity, "Nhận thành công +" + mission.getRewardPoints() + " KN!", Toast.LENGTH_SHORT).show();
                            run(); // Nạp lại giao diện
                        }
                    });
                    rvTasks.setAdapter(adapter);
                }
            }
        };

        if (tabToday != null) {
            tabToday.setOnClickListener(v -> {
                currentTab[0] = "today";
                updateMissionList.run();
            });
        }

        if (tabWeek != null) {
            tabWeek.setOnClickListener(v -> {
                currentTab[0] = "week";
                updateMissionList.run();
            });
        }

        // Tải danh sách ban đầu
        updateMissionList.run();
    }
}
