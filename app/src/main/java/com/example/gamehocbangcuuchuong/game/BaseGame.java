package com.example.gamehocbangcuuchuong.game;

import android.view.View;
import android.widget.TextView;

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
}
