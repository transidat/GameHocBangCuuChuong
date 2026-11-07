package com.example.gamehocbangcuuchuong.manager;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.data.DatabaseHelper;
import com.example.gamehocbangcuuchuong.utils.ScoreManager;

/**
 * Quản lý khởi tạo và xử lý giao diện màn hình Thành tích.
 */
public class ThanhTichManager {

    public static void setup(MainActivity activity, View view) {
        DatabaseHelper dbHelper = activity.getDbHelper();
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvTopScore = view.findViewById(R.id.tv_top_score);
        TextView tvLevelTitle = view.findViewById(R.id.tv_level_title);
        ProgressBar progressLevel = view.findViewById(R.id.progress_level);
        TextView tvLevelRatio = view.findViewById(R.id.tv_level_ratio);

        TextView tvStatMouse = view.findViewById(R.id.tv_stat_mouse);
        TextView tvStatBall = view.findViewById(R.id.tv_stat_ball);
        TextView tvStatCar = view.findViewById(R.id.tv_stat_car);
        TextView tvStatBoat = view.findViewById(R.id.tv_stat_boat);

        // Nút quay lại trang chủ
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> activity.showScreen(R.layout.layout_trang_chu));
        }

        // Điểm kinh nghiệm tích lũy
        int totalScore = ScoreManager.getTotalScore(activity);
        if (tvTopScore != null) {
            tvTopScore.setText(activity.getString(R.string.score_exp_format, totalScore));
        }

        // Tính toán Cấp độ và Tiến độ
        ScoreManager.LevelInfo levelInfo = ScoreManager.calculateLevelInfo(totalScore);

        if (tvLevelTitle != null) {
            tvLevelTitle.setText(activity.getString(R.string.level_format, levelInfo.level));
        }
        if (progressLevel != null) {
            progressLevel.setMax(levelInfo.maxExp);
            progressLevel.setProgress(levelInfo.currentExp);
        }
        if (tvLevelRatio != null) {
            tvLevelRatio.setText(activity.getString(R.string.level_ratio_format, levelInfo.currentExp, levelInfo.maxExp));
        }

        // Lấy dữ liệu thống kê từ CSDL
        int mouseCount = dbHelper.getTotalCorrectByGame("Đập chuột");
        int ballCount = dbHelper.getTotalCorrectByGame("Hứng bóng");
        int carCount = dbHelper.getTotalCorrectByGame("Đua xe");
        int boatCount = dbHelper.getTotalCorrectByGame("Chèo thuyền");

        if (tvStatMouse != null) {
            tvStatMouse.setText(String.valueOf(mouseCount));
        }
        if (tvStatBall != null) {
            tvStatBall.setText(String.valueOf(ballCount));
        }
        if (tvStatCar != null) {
            tvStatCar.setText(String.valueOf(carCount));
        }
        if (tvStatBoat != null) {
            tvStatBoat.setText(String.valueOf(boatCount));
        }

        // Kiểm tra điều kiện đạt danh hiệu theo ảnh:
        // 1. Dũng sĩ diệt chuột: Đập trúng 100 chú chuột
        boolean badge1Achieved = mouseCount >= 100;

        // 2. Bàn tay khéo léo: Hứng được 100 quả bóng
        boolean badge2Achieved = ballCount >= 100;

        // 3. Tay lái cừ khôi: Vượt qua 120 chặng đua
        boolean badge3Achieved = carCount >= 120;

        // 4. Vua đập chuột: Đạt chuỗi 500 điểm (Điểm cao nhất Đập chuột >= 500)
        int maxMouseScore = dbHelper.getHighestScoreByGame("Đập chuột");
        boolean badge4Achieved = maxMouseScore >= 500;

        // 5. Kỷ lục hứng bóng: Không bỏ sót quả nào trong một lượt chơi Hứng bóng
        boolean badge5Achieved = dbHelper.hasPerfectGame("Hứng bóng");

        TextView tvNoBadges = view.findViewById(R.id.tv_no_badges);
        View layoutBadgeRow1 = view.findViewById(R.id.layout_badge_row_1);
        View layoutBadgeRow2 = view.findViewById(R.id.layout_badge_row_2);

        View cardBadge1 = view.findViewById(R.id.card_badge_1);
        View cardBadge2 = view.findViewById(R.id.card_badge_2);
        View cardBadge3 = view.findViewById(R.id.card_badge_3);
        View cardBadge4 = view.findViewById(R.id.card_badge_4);
        View cardBadge5 = view.findViewById(R.id.card_badge_5);

        boolean hasAnyBadge = badge1Achieved || badge2Achieved || badge3Achieved || badge4Achieved || badge5Achieved;

        if (tvNoBadges != null) {
            tvNoBadges.setVisibility(hasAnyBadge ? View.GONE : View.VISIBLE);
        }

        if (cardBadge1 != null) cardBadge1.setVisibility(badge1Achieved ? View.VISIBLE : View.GONE);
        if (cardBadge2 != null) cardBadge2.setVisibility(badge2Achieved ? View.VISIBLE : View.GONE);
        if (cardBadge3 != null) cardBadge3.setVisibility(badge3Achieved ? View.VISIBLE : View.GONE);
        if (cardBadge4 != null) cardBadge4.setVisibility(badge4Achieved ? View.VISIBLE : View.GONE);
        if (cardBadge5 != null) cardBadge5.setVisibility(badge5Achieved ? View.VISIBLE : View.GONE);

        if (layoutBadgeRow1 != null) {
            boolean row1HasItem = badge1Achieved || badge2Achieved || badge3Achieved;
            layoutBadgeRow1.setVisibility(row1HasItem ? View.VISIBLE : View.GONE);
        }

        if (layoutBadgeRow2 != null) {
            boolean row2HasItem = badge4Achieved || badge5Achieved;
            layoutBadgeRow2.setVisibility(row2HasItem ? View.VISIBLE : View.GONE);
        }
    }
}
