package com.example.gamehocbangcuuchuong.model;

/**
 * Class đại diện cho thông tin và tiến độ nhiệm vụ trong trò chơi.
 */
public class Mission {
    private int id;                 // ID nhiệm vụ
    private String title;           // Tên nhiệm vụ
    private String description;     // Mô tả nhiệm vụ
    private int currentProgress;    // Tiến độ hiện tại
    private int maxProgress;        // Tiến độ cần đạt được
    private int rewardPoints;       // Điểm thưởng khi hoàn thành
    private boolean isCompleted;    // Trạng thái hoàn thành
    private boolean isRewardClaimed;// Đã nhận phần thưởng chưa
    private int iconResId;          // Id hình ảnh icon
    private String iconBgColor;     // Màu nền đệm icon (Mã hex: #FFF176...)
    private int gameLayoutId;       // Màn hình game tương ứng khi bấm "Làm ngay"
    private String category;        // "today" (Hôm nay) hoặc "week" (Tuần này)

    public Mission(int id, String title, String description, int currentProgress, int maxProgress,
                   int rewardPoints, boolean isCompleted, boolean isRewardClaimed,
                   int iconResId, String iconBgColor, int gameLayoutId, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
        this.rewardPoints = rewardPoints;
        this.isCompleted = isCompleted;
        this.isRewardClaimed = isRewardClaimed;
        this.iconResId = iconResId;
        this.iconBgColor = iconBgColor;
        this.gameLayoutId = gameLayoutId;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        updateCompletionState();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        updateCompletionState();
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isRewardClaimed() {
        return isRewardClaimed;
    }

    public void setRewardClaimed(boolean rewardClaimed) {
        isRewardClaimed = rewardClaimed;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getIconBgColor() {
        return iconBgColor;
    }

    public void setIconBgColor(String iconBgColor) {
        this.iconBgColor = iconBgColor;
    }

    public int getGameLayoutId() {
        return gameLayoutId;
    }

    public void setGameLayoutId(int gameLayoutId) {
        this.gameLayoutId = gameLayoutId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Cập nhật tiến độ nhiệm vụ
    public void updateProgress(int amount) {
        if (amount <= 0 || this.isCompleted) return;
        this.currentProgress = Math.min(this.maxProgress, this.currentProgress + amount);
        updateCompletionState();
    }

    private void updateCompletionState() {
        if (this.maxProgress > 0) {
            this.isCompleted = (this.currentProgress >= this.maxProgress);
        }
    }

    public int getProgressPercentage() {
        if (maxProgress <= 0) return 0;
        return Math.min(100, (currentProgress * 100) / maxProgress);
    }
}
