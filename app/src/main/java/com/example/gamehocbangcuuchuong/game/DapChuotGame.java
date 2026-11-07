package com.example.gamehocbangcuuchuong.game;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.utils.GameTimer;
import com.example.gamehocbangcuuchuong.utils.QuestionGenerator;
import com.example.gamehocbangcuuchuong.utils.ScoreManager;

public class DapChuotGame extends BaseGame {
    private View[] mouseHoles;
    private TextView[] tvAnswers;

    public DapChuotGame(MainActivity activity, View view) {
        super(activity, view, "Đập chuột", R.layout.layout_dap_chuot);
        initViews();
        initGameLogic();
    }

    private void initViews() {
        initCommonViews();

        mouseHoles = new View[] {
                view.findViewById(R.id.mouse_hole_1),
                view.findViewById(R.id.mouse_hole_2),
                view.findViewById(R.id.mouse_hole_3),
                view.findViewById(R.id.mouse_hole_4),
                view.findViewById(R.id.mouse_hole_5),
                view.findViewById(R.id.mouse_hole_6)
        };

        tvAnswers = new TextView[] {
                view.findViewById(R.id.tv_ans_1),
                view.findViewById(R.id.tv_ans_2),
                view.findViewById(R.id.tv_ans_3),
                view.findViewById(R.id.tv_ans_4),
                view.findViewById(R.id.tv_ans_5),
                view.findViewById(R.id.tv_ans_6)
        };
    }

    private void initGameLogic() {
        initCommonLogic();

        gameTimer = new GameTimer(new GameTimer.TimerListener() {
            @Override
            public void onTick(long remainingMs) {
                updateTimerUI(remainingMs);
            }

            @Override
            public void onFinish() {
                if (isAnswerChecked || isPaused) return;
                handleWrongAnswer("Đã hết thời gian", DapChuotGame.this::loadNextQuestion);
            }
        });

        for (int i = 0; i < 6; i++) {
            final int index = i;
            if (mouseHoles[i] != null) {
                mouseHoles[i].setOnClickListener(v -> checkAnswer(index));
            }
        }

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (!prepareTurn()) return;

        currentQuestion = QuestionGenerator.generateQuestion();
        if (tvQuestionText != null) {
            tvQuestionText.setText(currentQuestion.getQuestionText());
        }

        for (int i = 0; i < 6; i++) {
            if (tvAnswers[i] != null) {
                tvAnswers[i].setText(String.valueOf(currentQuestion.getAnswers().get(i)));
            }
        }

        gameTimer.start(10000);
    }

    private void checkAnswer(int index) {
        if (isAnswerChecked || isPaused || index < 0 || index >= tvAnswers.length || tvAnswers[index] == null || currentQuestion == null) return;

        isAnswerChecked = true;
        stopTimer();

        try {
            int selectedAns = Integer.parseInt(tvAnswers[index].getText().toString());

            if (currentQuestion.isCorrect(selectedAns)) {
                correctAnswersCount++;
                comboCount++;
                int pointsGained = ScoreManager.calculateQuestionScore(comboCount);
                gameScore += pointsGained;

                updateScoreAndComboUI();

                String toastMsg = (comboCount > 1)
                        ? "Đập chính xác! Combo x" + comboCount + " (+" + pointsGained + " KN) 🎯"
                        : "Đập chính xác! (+" + pointsGained + " KN) 🎯";
                Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();
                loadNextQuestion();
            } else {
                handleWrongAnswer("Bạn đã làm sai", this::loadNextQuestion);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
