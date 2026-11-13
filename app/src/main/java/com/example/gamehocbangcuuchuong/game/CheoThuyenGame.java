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

public class CheoThuyenGame extends BaseGame {
    private final Random random = new Random();

    // Làn sông mở của vật cản hiện tại (0: Trên có vật cản -> làn Dưới trống, 1: Dưới có vật cản -> làn Trên trống)
    private int openLane = 0; // 0: Trên (an toàn), 1: Dưới (an toàn)

    private Animator activeAnimator;

    // View riêng
    private ImageView layoutObstacleTop, layoutObstacleBottom, imgBoat;
    private View[] btnAnsLayouts;
    private TextView[] tvAnswers;

    public CheoThuyenGame(MainActivity activity, View view) {
        super(activity, view, "Chèo thuyền", R.layout.layout_cheo_thuyen);
        initViews();
        initGameLogic();
    }

    private void initViews() {
        initCommonViews();

        layoutObstacleTop = view.findViewById(R.id.img_obstacle_vat3);
        layoutObstacleBottom = view.findViewById(R.id.img_obstacle_vat4);
        imgBoat = view.findViewById(R.id.img_player_boat);

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
                animateBoatToCrash(() -> handleWrongAnswer("Đã hết thời gian!", CheoThuyenGame.this::loadNextQuestion));
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

        if (layoutObstacleTop != null) {
            layoutObstacleTop.setVisibility(obstacleSide == 0 ? View.VISIBLE : View.INVISIBLE);
        }
        if (layoutObstacleBottom != null) {
            layoutObstacleBottom.setVisibility(obstacleSide == 1 ? View.VISIBLE : View.INVISIBLE);
        }

        if (imgBoat != null) {
            imgBoat.setTranslationX(0f);
            imgBoat.setTranslationY(0f);
            imgBoat.setRotation(0f);

            if (imgBoat.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imgBoat.getLayoutParams();
                params.verticalBias = 0.50f;
                imgBoat.setLayoutParams(params);
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
                handleCorrectAnswer("Chèo thuyền về đích xuất sắc!", "🚣‍♂️💨");
                animateBoatToFinish(this::loadNextQuestion);
            } else {
                animateBoatToCrash(() -> handleWrongAnswer("Bạn đã đâm \nvào vật cản!", this::loadNextQuestion));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void animateBoatToFinish(Runnable onComplete) {
        if (imgBoat == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        View riverContainer = view.findViewById(R.id.layout_river_container);
        float riverHeight = (riverContainer != null && riverContainer.getHeight() > 0)
                ? riverContainer.getHeight()
                : imgBoat.getResources().getDisplayMetrics().density * 200f;

        float riverWidth = (riverContainer != null && riverContainer.getWidth() > 0)
                ? riverContainer.getWidth()
                : imgBoat.getResources().getDisplayMetrics().density * 300f;

        float targetY = (openLane == 0) ? -0.25f * riverHeight : 0.25f * riverHeight;

        float boatStartX = imgBoat.getLeft();
        float targetX = (boatStartX > 0)
                ? (riverWidth - boatStartX - imgBoat.getWidth() * 0.9f)
                : imgBoat.getResources().getDisplayMetrics().density * 220f;

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, targetX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, targetY);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(imgBoat, pvhX, pvhY);
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

    private void animateBoatToCrash(Runnable onComplete) {
        if (imgBoat == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        View riverContainer = view.findViewById(R.id.layout_river_container);
        float riverHeight = (riverContainer != null && riverContainer.getHeight() > 0)
                ? riverContainer.getHeight()
                : imgBoat.getResources().getDisplayMetrics().density * 200f;

        float crashY = (openLane == 0) ? 0.25f * riverHeight : -0.25f * riverHeight;

        ImageView obstacle = (openLane == 0) ? layoutObstacleBottom : layoutObstacleTop;
        float boatStartX = imgBoat.getLeft();
        float obstacleX = (obstacle != null && obstacle.getLeft() > 0)
                ? obstacle.getLeft()
                : imgBoat.getResources().getDisplayMetrics().density * 200f;

        float targetX = obstacleX - boatStartX - (imgBoat.getWidth() * 0.25f);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, targetX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, crashY);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(imgBoat, pvhX, pvhY);
        moveAnim.setDuration(500);
        moveAnim.setInterpolator(new AccelerateInterpolator());

        moveAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator shakeY = ObjectAnimator.ofFloat(imgBoat, View.TRANSLATION_Y,
                        crashY, crashY + 18f, crashY - 18f, crashY + 10f, crashY - 10f, crashY);
                ObjectAnimator tilt = ObjectAnimator.ofFloat(imgBoat, View.ROTATION, 0f, 20f, -12f, 0f);

                AnimatorSet crashEffects = new AnimatorSet();
                crashEffects.playTogether(shakeY, tilt);
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
