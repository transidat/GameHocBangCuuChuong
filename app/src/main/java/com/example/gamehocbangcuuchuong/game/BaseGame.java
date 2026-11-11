package com.example.gamehocbangcuuchuong.game;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.dialog.GameDialog;
import com.example.gamehocbangcuuchuong.model.Question;
import com.example.gamehocbangcuuchuong.utils.GameTimer;
import com.example.gamehocbangcuuchuong.utils.LivesManager;
import com.example.gamehocbangcuuchuong.utils.ScoreManager;

/**
 * Lớp cơ sở chứa logic và thành phần giao diện dùng chung cho các trò chơi mini game.
 */
public abstract class BaseGame {
    protected final MainActivity activity;
    protected final View view;
    protected final String gameName;
    protected final int gameLayoutId;

    // Trạng thái chung
    protected int gameScore = 0;
    protected int gameLives = 3;
    protected int correctAnswersCount = 0;
    protected int comboCount = 0;
    protected boolean isPaused = false;
    protected boolean isAnswerChecked = false;
    protected Question currentQuestion;
    protected GameTimer gameTimer;
    protected LivesManager livesManager;

    // View dùng chung
    protected View layoutGameContent;
    protected TextView tvScore, tvTimer, tvQuestionText, tvCombo;
    protected View btnBack, btnPause;

    // Toast/Banner thông báo cộng điểm phía trên
    private TextView tvScoreToast;
    private Runnable hideToastRunnable;

    public BaseGame(MainActivity activity, View view, String gameName, int gameLayoutId) {
        this.activity = activity;
        this.view = view;
        this.gameName = gameName;
        this.gameLayoutId = gameLayoutId;
    }

    protected void initCommonViews() {
        layoutGameContent = view.findViewById(R.id.layout_game_content);
        tvScore = view.findViewById(R.id.tv_score);
        tvTimer = view.findViewById(R.id.tv_timer);
        tvQuestionText = view.findViewById(R.id.tv_question_text);
        tvCombo = view.findViewById(R.id.tv_combo);

        btnBack = view.findViewById(R.id.btn_game_back);
        btnPause = view.findViewById(R.id.btn_game_pause);

        livesManager = new LivesManager(view);
    }

    protected void initCommonLogic() {
        gameScore = 0;
        gameLives = 3;
        correctAnswersCount = 0;
        comboCount = 0;
        isPaused = false;
        isAnswerChecked = false;

        updateScoreAndComboUI();

        // Nút Quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                stopTimer();
                activity.showScreen(R.layout.layout_trang_chu);
            });
        }

        // Nút Tạm dừng
        if (btnPause != null) {
            btnPause.setOnClickListener(v -> {
                if (isAnswerChecked || isPaused) return;
                pauseGame();
                GameDialog.showPauseDialog(activity, gameScore, gameLives, comboCount, new GameDialog.PauseDialogListener() {
                    @Override
                    public void onResumeGame() {
                        resumeGame();
                    }

                    @Override
                    public void onReturnHome() {
                        stopTimer();
                        activity.showScreen(R.layout.layout_trang_chu);
                    }
                });
            });
        }
    }

    protected void updateHeartsUI() {
        if (livesManager != null) {
            livesManager.updateHearts(gameLives);
        }
    }

    protected boolean prepareTurn() {
        stopTimer();
        isPaused = false;
        isAnswerChecked = false;

        if (layoutGameContent != null) {
            layoutGameContent.setVisibility(View.VISIBLE);
        }

        updateHeartsUI();

        if (gameLives <= 0) {
            showResultScreen();
            return false;
        }
        return true;
    }

    protected void updateScoreAndComboUI() {
        if (tvScore != null) tvScore.setText(activity.getString(R.string.score_exp_format, gameScore));
        if (tvCombo != null) tvCombo.setText(activity.getString(R.string.combo_format, comboCount));
    }

    protected void updateTimerUI(long remainingMs) {
        long secondsLeft = (remainingMs + 999) / 1000;
        if (tvTimer != null) tvTimer.setText(activity.getString(R.string.timer_seconds_format, secondsLeft));
    }

    protected void pauseGame() {
        if (isPaused) return;
        isPaused = true;
        if (gameTimer != null) {
            gameTimer.pause();
        }
        if (tvScoreToast != null) {
            if (hideToastRunnable != null) tvScoreToast.removeCallbacks(hideToastRunnable);
            tvScoreToast.setVisibility(View.GONE);
        }
        if (layoutGameContent != null) {
            layoutGameContent.setVisibility(View.INVISIBLE);
        }
    }

    protected void resumeGame() {
        if (!isPaused) return;
        isPaused = false;
        if (layoutGameContent != null) {
            layoutGameContent.setVisibility(View.VISIBLE);
        }
        if (gameTimer != null) {
            gameTimer.resume();
        }
    }

    public void stopTimer() {
        isPaused = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    protected void showResultScreen() {
        stopTimer();
        long durationSeconds = Math.max(1, (gameTimer != null ? gameTimer.getTotalPlayTimeMs() : 0) / 1000);
        int bonus = ScoreManager.calculateBonusScore(gameScore);
        activity.showResultScreen(gameName, gameLayoutId, gameScore, correctAnswersCount, durationSeconds, bonus);
    }

    protected void handleWrongAnswer(String message, Runnable onNextQuestion) {
        gameLives--;
        comboCount = 0;
        updateScoreAndComboUI();

        if (gameLives <= 0) {
            showResultScreen();
        } else {
            GameDialog.showWrongAnswerDialog(activity, layoutGameContent, gameLives, message, () -> {
                if (onNextQuestion != null) {
                    onNextQuestion.run();
                }
            });
        }
    }

    /**
     * Xử lý khi trả lời đúng: tăng số câu đúng, tăng combo, tính và cộng điểm,
     * cập nhật giao diện và hiển thị thông báo.
     *
     * @param actionMessage Thông báo hành động (ví dụ: "Đập chính xác!", "Lái xe về đích xuất sắc!")
     * @param emoji Emoji đính kèm cuối thông báo
     * @return Số điểm nhận được trong lượt này
     */
    protected int handleCorrectAnswer(String actionMessage, String emoji) {
        correctAnswersCount++;
        comboCount++;
        int pointsGained = ScoreManager.calculateQuestionScore(comboCount);
        gameScore += pointsGained;

        updateScoreAndComboUI();

        String notificationMsg = (comboCount > 1)
                ? actionMessage + " Combo x" + comboCount + " (+" + pointsGained + " KN) " + emoji
                : actionMessage + " (+" + pointsGained + " KN) " + emoji;

        showInAppScoreNotification(notificationMsg);

        return pointsGained;
    }

    /**
     * Hiển thị banner thông báo cộng điểm ở phía trên màn hình (ngay dưới thanh Combo),
     * tuyệt đối không đè lên các nút đáp án ở phía dưới.
     */
    protected void showInAppScoreNotification(String text) {
        if (view == null || !(view instanceof ViewGroup)) return;

        if (tvScoreToast == null) {
            tvScoreToast = new TextView(activity);
            tvScoreToast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvScoreToast.setTextColor(Color.WHITE);
            tvScoreToast.setTypeface(null, Typeface.BOLD);
            tvScoreToast.setGravity(Gravity.CENTER);

            float density = activity.getResources().getDisplayMetrics().density;
            int padH = (int) (16 * density);
            int padV = (int) (8 * density);
            tvScoreToast.setPadding(padH, padV, padH, padV);

            // Nền viên thuốc bo tròn màu xám đen trong suốt nhẹ
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(24 * density);
            shape.setColor(Color.parseColor("#E6222222"));
            tvScoreToast.setBackground(shape);

            tvScoreToast.setElevation(8 * density);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            params.topToBottom = R.id.layout_combo;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topMargin = (int) (6 * density);

            tvScoreToast.setLayoutParams(params);

            View gameContent = view.findViewById(R.id.layout_game_content);
            if (gameContent instanceof ViewGroup) {
                ((ViewGroup) gameContent).addView(tvScoreToast);
            } else {
                ((ViewGroup) view).addView(tvScoreToast);
            }
        }

        if (hideToastRunnable != null) {
            tvScoreToast.removeCallbacks(hideToastRunnable);
        }

        tvScoreToast.setText(text);
        tvScoreToast.setVisibility(View.VISIBLE);
        tvScoreToast.setAlpha(1.0f);
        tvScoreToast.bringToFront();

        hideToastRunnable = () -> {
            if (tvScoreToast != null) {
                tvScoreToast.animate()
                        .alpha(0.0f)
                        .setDuration(250)
                        .withEndAction(() -> tvScoreToast.setVisibility(View.GONE))
                        .start();
            }
        };
        tvScoreToast.postDelayed(hideToastRunnable, 1200);
    }
}
