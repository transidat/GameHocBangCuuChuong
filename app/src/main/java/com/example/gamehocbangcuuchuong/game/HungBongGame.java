package com.example.gamehocbangcuuchuong.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.utils.GameTimer;
import com.example.gamehocbangcuuchuong.utils.QuestionGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HungBongGame extends BaseGame {

    private long questionStartTime = 0;
    private final ObjectAnimator[] ballAnimators = new ObjectAnimator[3];
    private final boolean[] ballHasStarted = new boolean[3];
    private final long[] ballScheduledDelay = new long[3];
    private final long[] ballRemainingDelay = new long[3];

    // Vị trí giỏ hứng (0: Trái, 1: Giữa, 2: Phải)
    private int basketLane = 1;
    private final float[] laneBiases = {0.18f, 0.50f, 0.82f};

    // Tốc độ rơi bóng
    private static final long BALL_FALL_DURATION = 10200;
    private final Handler ballHandler = new Handler(Looper.getMainLooper());

    // View riêng
    private View[] ballLayouts;
    private TextView[] tvAnswers;
    private ImageView imgBasket;
    private View btnMoveLeft, btnMoveRight;

    public HungBongGame(MainActivity activity, View view) {
        super(activity, view, "Hứng bóng", R.layout.layout_hung_bong);
        initViews();
        initGameLogic();
    }

    private void initViews() {
        initCommonViews();

        ballLayouts = new View[]{
                view.findViewById(R.id.layout_ball1),
                view.findViewById(R.id.layout_ball2),
                view.findViewById(R.id.layout_ball3)
        };

        tvAnswers = new TextView[]{
                view.findViewById(R.id.tv_ans_1),
                view.findViewById(R.id.tv_ans_2),
                view.findViewById(R.id.tv_ans_3)
        };

        imgBasket = view.findViewById(R.id.img_basket);
        btnMoveLeft = view.findViewById(R.id.btn_move_left);
        btnMoveRight = view.findViewById(R.id.btn_move_right);
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
                handleWrongOrMissed("Đã hết thời gian");
            }
        });

        // Di chuyển giỏ TRÁI
        if (btnMoveLeft != null) {
            btnMoveLeft.setOnClickListener(v -> {
                if (basketLane > 0) {
                    basketLane--;
                    updateBasketPosition();
                    checkCollisionOnMove();
                }
            });
        }

        // Di chuyển giỏ PHẢI
        if (btnMoveRight != null) {
            btnMoveRight.setOnClickListener(v -> {
                if (basketLane < 2) {
                    basketLane++;
                    updateBasketPosition();
                    checkCollisionOnMove();
                }
            });
        }

        basketLane = 1; // Vị trí giữa
        updateBasketPosition();

        if (imgBasket != null) {
            imgBasket.post(this::loadNextQuestion);
        } else {
            loadNextQuestion();
        }
    }

    private void updateBasketPosition() {
        if (imgBasket != null && imgBasket.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imgBasket.getLayoutParams();
            params.horizontalBias = laneBiases[basketLane];
            imgBasket.setLayoutParams(params);
        }
    }

    @Override
    protected void pauseGame() {
        super.pauseGame();

        ballHandler.removeCallbacksAndMessages(null);
        long elapsed = System.currentTimeMillis() - questionStartTime;

        for (int i = 0; i < 3; i++) {
            if (ballHasStarted[i]) {
                if (ballAnimators[i] != null) {
                    if (ballAnimators[i].isStarted() || ballAnimators[i].isRunning()) {
                        ballAnimators[i].pause();
                    }
                }
            } else {
                ballRemainingDelay[i] = Math.max(0, ballScheduledDelay[i] - elapsed);
            }
        }
    }

    @Override
    protected void resumeGame() {
        super.resumeGame();

        for (int i = 0; i < 3; i++) {
            final int laneIndex = i;
            final ObjectAnimator animator = ballAnimators[laneIndex];
            final View ballView = ballLayouts[laneIndex];
            if (animator == null || ballView == null) continue;

            if (ballHasStarted[laneIndex]) {
                if (animator.isPaused()) {
                    animator.resume();
                } else if (!animator.isStarted() && !animator.isRunning()) {
                    ballView.setVisibility(View.VISIBLE);
                    animator.start();
                }
            } else {
                long delay = ballRemainingDelay[laneIndex];
                ballHandler.postDelayed(() -> {
                    if (isAnswerChecked || isPaused) return;
                    ballHasStarted[laneIndex] = true;
                    ballView.setVisibility(View.VISIBLE);
                    ballView.setTranslationY(0f);
                    animator.start();
                }, delay);
            }
        }
    }

    private void loadNextQuestion() {
        stopBallAnimation();
        if (!prepareTurn()) {
            stopBallAnimation();
            return;
        }

        currentQuestion = QuestionGenerator.generateQuestion(3);
        if (tvQuestionText != null) {
            tvQuestionText.setText(currentQuestion.getQuestionText());
        }

        for (int i = 0; i < 3; i++) {
            if (tvAnswers[i] != null && i < currentQuestion.getAnswers().size()) {
                tvAnswers[i].setText(String.valueOf(currentQuestion.getAnswers().get(i)));
            }
            if (ballLayouts[i] != null) {
                ballLayouts[i].setVisibility(View.INVISIBLE);
                ballLayouts[i].setTranslationY(0f);
            }
        }

        gameTimer.start(10000);
        startFallingAllBalls();
    }

    private void startFallingAllBalls() {
        stopBallAnimation();
        if (isAnswerChecked) return;

        questionStartTime = System.currentTimeMillis();

        List<Integer> spawnOrder = Arrays.asList(0, 1, 2);
        Collections.shuffle(spawnOrder);

        for (int step = 0; step < 3; step++) {
            final int laneIndex = spawnOrder.get(step);
            View ballView = ballLayouts[laneIndex];
            if (ballView == null) continue;

            ballHasStarted[laneIndex] = false;
            ballScheduledDelay[laneIndex] = step * 1500L;
            ballRemainingDelay[laneIndex] = step * 1500L;

            ballView.setVisibility(View.INVISIBLE);
            ballView.setTranslationY(0f);

            float density = activity.getResources().getDisplayMetrics().density;
            float fallDistance = 350f * density;

            if (imgBasket != null && imgBasket.getTop() > 0 && ballView.getTop() > 0) {
                fallDistance = imgBasket.getTop() - ballView.getTop() + (imgBasket.getHeight() / 2f);
            }

            final float targetFallDistance = fallDistance;
            ObjectAnimator animator = ObjectAnimator.ofFloat(ballView, "translationY", 0f, targetFallDistance);
            animator.setDuration(BALL_FALL_DURATION);
            animator.setInterpolator(new LinearInterpolator());

            animator.addUpdateListener(animation -> {
                if (isAnswerChecked || isPaused) return;
                if (basketLane == laneIndex && isBallCollidingWithBasket(ballView, imgBasket)) {
                    checkAnswer(laneIndex);
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isAnswerChecked || isPaused) return;

                    if (basketLane == laneIndex) {
                        checkAnswer(laneIndex);
                    } else {
                        if (tvAnswers[laneIndex] != null && currentQuestion != null) {
                            try {
                                int ballAnswer = Integer.parseInt(tvAnswers[laneIndex].getText().toString());
                                if (currentQuestion.isCorrect(ballAnswer)) {
                                    handleWrongOrMissed("Bỏ lỡ đáp án đúng rồi!");
                                } else {
                                    ballView.setVisibility(View.INVISIBLE);
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            });

            ballAnimators[laneIndex] = animator;

            long delay = step * 1500L;
            ballHandler.postDelayed(() -> {
                if (isAnswerChecked || isPaused) return;
                ballHasStarted[laneIndex] = true;
                ballView.setVisibility(View.VISIBLE);
                ballView.setTranslationY(0f);
                animator.start();
            }, delay);
        }
    }

    private boolean isBallCollidingWithBasket(View ballView, View basketView) {
        if (isAnswerChecked || isPaused || ballView == null || basketView == null || ballView.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] ballLocation = new int[2];
        int[] basketLocation = new int[2];
        ballView.getLocationOnScreen(ballLocation);
        basketView.getLocationOnScreen(basketLocation);

        float ballTop = ballLocation[1];
        float ballBottom = ballTop + ballView.getHeight();
        float basketTop = basketLocation[1];
        float basketBottom = basketTop + basketView.getHeight();

        return ballBottom >= basketTop && ballTop <= basketBottom;
    }

    private void checkCollisionOnMove() {
        if (isAnswerChecked || isPaused || basketLane < 0 || basketLane >= ballLayouts.length) return;

        View ballView = ballLayouts[basketLane];
        if (ballView != null && isBallCollidingWithBasket(ballView, imgBasket)) {
            checkAnswer(basketLane);
        }
    }

    private void checkAnswer(int index) {
        if (isAnswerChecked || isPaused || index < 0 || index >= tvAnswers.length || tvAnswers[index] == null || currentQuestion == null) return;

        isAnswerChecked = true;
        stopBallAnimation();
        stopTimer();

        try {
            int selectedAns = Integer.parseInt(tvAnswers[index].getText().toString());

            if (currentQuestion.isCorrect(selectedAns)) {
                handleCorrectAnswer("Hứng trúng đáp án đúng!", "🎈");

                if (layoutGameContent != null) {
                    layoutGameContent.postDelayed(this::loadNextQuestion, 400);
                } else {
                    loadNextQuestion();
                }
            } else {
                processWrongOrMissed("Hứng nhầm đáp án sai!");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleWrongOrMissed(String message) {
        if (isAnswerChecked || isPaused) return;
        isAnswerChecked = true;
        stopBallAnimation();
        stopTimer();
        processWrongOrMissed(message);
    }

    private void processWrongOrMissed(String message) {
        handleWrongAnswer(message, this::loadNextQuestion);
    }

    private void stopBallAnimation() {
        isPaused = false;
        ballHandler.removeCallbacksAndMessages(null);
        for (int i = 0; i < ballAnimators.length; i++) {
            if (ballAnimators[i] != null) {
                ballAnimators[i].removeAllListeners();
                ballAnimators[i].removeAllUpdateListeners();
                ballAnimators[i].cancel();
                ballAnimators[i] = null;
            }
            ballHasStarted[i] = false;
        }
    }

    @Override
    public void stopTimer() {
        stopBallAnimation();
        super.stopTimer();
    }
}
