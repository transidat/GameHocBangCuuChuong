package com.example.gamehocbangcuuchuong.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.utils.GameTimer;

import java.util.Random;

public class DuaXeGame extends BaseGame {
    private final Random random = new Random();

    // Làn đường mở của vật cản hiện tại (0: Trái có vật cản -> làn Phải trống, 1: Phải có vật cản -> làn Trái trống)
    private int openLane = 0; // 0: Trái (an toàn), 1: Phải (an toàn)

    private Animator activeAnimator;

    // View riêng
    private ImageView layoutObstacleLeft, layoutObstacleRight, imgCar;
    private View[] btnAnsLayouts;
    private TextView[] tvAnswers;

    public DuaXeGame(MainActivity activity, View view) {
        super(activity, view, "Đua xe", R.layout.layout_dua_xe);
        initViews();
        initGameLogic();
    }

    private void initViews() {
        initCommonViews();

        layoutObstacleLeft = view.findViewById(R.id.layout_obstacle_left);
        layoutObstacleRight = view.findViewById(R.id.layout_obstacle_right);
        imgCar = view.findViewById(R.id.img_car);

        btnAnsLayouts = new View[]{
                view.findViewById(R.id.btn_ans_layout_1),
                view.findViewById(R.id.btn_ans_layout_2),
                view.findViewById(R.id.btn_ans_layout_3),
                view.findViewById(R.id.btn_ans_layout_4)
        };

        tvAnswers = new TextView[]{
                view.findViewById(R.id.tv_ans_1),
                view.findViewById(R.id.tv_ans_2),
                view.findViewById(R.id.tv_ans_3),
                view.findViewById(R.id.tv_ans_4)
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
                animateCarToCrash(() -> handleWrongAnswer("Đã hết thời gian!", DuaXeGame.this::loadNextQuestion));
            }
        });

        for (int i = 0; i < 4; i++) {
            final int index = i;
            if (btnAnsLayouts[i] != null) {
                btnAnsLayouts[i].setOnClickListener(v -> checkAnswer(index));
            }
        }

        loadNextQuestion();
    }

    @Override
    protected void pauseGame() {
        super.pauseGame();
        if (activeAnimator != null && activeAnimator.isRunning()) {
            activeAnimator.pause();
        }
    }

    @Override
    protected void resumeGame() {
        super.resumeGame();
        if (activeAnimator != null && activeAnimator.isPaused()) {
            activeAnimator.resume();
        }
    }

    @Override
    public void stopTimer() {
        if (activeAnimator != null) {
            activeAnimator.cancel();
            activeAnimator = null;
        }
        super.stopTimer();
    }

    private void loadNextQuestion() {
        if (!prepareTurn()) return;

        for (int i = 0; i < 4; i++) {
            if (btnAnsLayouts[i] != null) {
                btnAnsLayouts[i].setBackgroundResource(R.drawable.bg_pill_white);
            }
            if (tvAnswers[i] != null) {
                tvAnswers[i].setTextColor(Color.parseColor("#B5214D"));
            }
        }

        currentQuestion = getNextQuestion(4);
        if (tvQuestionText != null) {
            tvQuestionText.setText(currentQuestion.getQuestionText());
        }

        for (int i = 0; i < 4; i++) {
            if (tvAnswers[i] != null && i < currentQuestion.getAnswers().size()) {
                tvAnswers[i].setText(String.valueOf(currentQuestion.getAnswers().get(i)));
            }
        }

        int obstacleSide = random.nextInt(2);
        openLane = (obstacleSide == 0) ? 1 : 0;

        if (layoutObstacleLeft != null) {
            layoutObstacleLeft.setVisibility(obstacleSide == 0 ? View.VISIBLE : View.INVISIBLE);
        }
        if (layoutObstacleRight != null) {
            layoutObstacleRight.setVisibility(obstacleSide == 1 ? View.VISIBLE : View.INVISIBLE);
        }

        if (imgCar != null) {
            imgCar.setTranslationX(0f);
            imgCar.setTranslationY(0f);
            imgCar.setRotation(0f);

            if (imgCar.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imgCar.getLayoutParams();
                params.horizontalBias = 0.50f;
                imgCar.setLayoutParams(params);
            }
        }

        gameTimer.start(10000);
    }

    private void checkAnswer(int index) {
        if (isAnswerChecked || isPaused || index < 0 || index >= tvAnswers.length || tvAnswers[index] == null || currentQuestion == null) {
            return;
        }

        isAnswerChecked = true;
        stopTimer();

        if (btnAnsLayouts[index] != null) {
            btnAnsLayouts[index].setBackgroundResource(R.drawable.bg_btn_blue);
        }
        if (tvAnswers[index] != null) {
            tvAnswers[index].setTextColor(Color.WHITE);
        }

        try {
            int selectedAns = Integer.parseInt(tvAnswers[index].getText().toString());

            if (currentQuestion.isCorrect(selectedAns)) {
                handleCorrectAnswer("Lái xe về đích xuất sắc!", "🏎️🏁");
                animateCarToFinish(this::loadNextQuestion);
            } else {
                animateCarToCrash(() -> handleWrongAnswer("Bạn đã đâm \nvào vật cản!", this::loadNextQuestion));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void animateCarToFinish(Runnable onComplete) {
        if (imgCar == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        View roadContainer = view.findViewById(R.id.layout_road_container);
        float roadWidth = (roadContainer != null && roadContainer.getWidth() > 0)
                ? roadContainer.getWidth()
                : imgCar.getResources().getDisplayMetrics().density * 260f;

        float targetX = (openLane == 0) ? -0.25f * roadWidth : 0.25f * roadWidth;

        float carStartY = imgCar.getTop();
        float targetY = (carStartY > 0)
                ? -carStartY + (imgCar.getHeight() * 0.1f)
                : -imgCar.getResources().getDisplayMetrics().density * 220f;

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, targetX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, targetY);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(imgCar, pvhX, pvhY);
        moveAnim.setDuration(750);
        moveAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        moveAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        activeAnimator = moveAnim;
        moveAnim.start();
    }

    private void animateCarToCrash(Runnable onComplete) {
        if (imgCar == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        View roadContainer = view.findViewById(R.id.layout_road_container);
        float roadWidth = (roadContainer != null && roadContainer.getWidth() > 0)
                ? roadContainer.getWidth()
                : imgCar.getResources().getDisplayMetrics().density * 260f;

        float crashX = (openLane == 0) ? 0.25f * roadWidth : -0.25f * roadWidth;

        ImageView obstacle = (openLane == 0) ? layoutObstacleRight : layoutObstacleLeft;
        float carStartY = imgCar.getTop();
        float obstacleY = (obstacle != null && obstacle.getTop() > 0)
                ? obstacle.getTop()
                : imgCar.getResources().getDisplayMetrics().density * 50f;

        float targetY = obstacleY - carStartY + (imgCar.getHeight() * 0.25f);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, crashX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, targetY);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(imgCar, pvhX, pvhY);
        moveAnim.setDuration(500);
        moveAnim.setInterpolator(new AccelerateInterpolator());

        moveAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator shakeX = ObjectAnimator.ofFloat(imgCar, View.TRANSLATION_X,
                        crashX, crashX + 18f, crashX - 18f, crashX + 10f, crashX - 10f, crashX);
                ObjectAnimator tilt = ObjectAnimator.ofFloat(imgCar, View.ROTATION, 0f, 20f, -12f, 0f);

                AnimatorSet crashEffects = new AnimatorSet();
                crashEffects.playTogether(shakeX, tilt);
                crashEffects.setDuration(350);

                crashEffects.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });

                activeAnimator = crashEffects;
                crashEffects.start();
            }
        });

        activeAnimator = moveAnim;
        moveAnim.start();
    }
}
