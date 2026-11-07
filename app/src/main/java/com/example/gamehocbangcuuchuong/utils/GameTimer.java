package com.example.gamehocbangcuuchuong.utils;

import android.os.CountDownTimer;

/**
 * Quản lý đếm ngược thời gian lượt chơi và tích lũy thời gian chơi thực tế cho các trò chơi.
 */
public class GameTimer {

    public interface TimerListener {
        void onTick(long remainingMs);
        void onFinish();
    }

    private CountDownTimer timer;
    private long remainingMs;
    private long totalPlayTimeMs = 0;
    private long turnStartTime = 0;
    private boolean isPaused = false;
    private final TimerListener listener;

    public GameTimer(TimerListener listener) {
        this.listener = listener;
    }

    public void start(long durationMs) {
        stopTimerOnly();
        this.remainingMs = durationMs;
        this.isPaused = false;
        this.turnStartTime = System.currentTimeMillis();

        timer = new CountDownTimer(remainingMs, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMs = millisUntilFinished;
                if (listener != null) {
                    listener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                accumulatePlayTime();
                if (listener != null) {
                    listener.onFinish();
                }
            }
        }.start();
    }

    public void pause() {
        if (isPaused) return;
        isPaused = true;
        accumulatePlayTime();
        stopTimerOnly();
    }

    public void resume() {
        if (!isPaused) return;
        isPaused = false;
        start(remainingMs);
    }

    public void stop() {
        isPaused = false;
        accumulatePlayTime();
        stopTimerOnly();
    }

    public void reset() {
        stopTimerOnly();
        isPaused = false;
        totalPlayTimeMs = 0;
        turnStartTime = 0;
        remainingMs = 0;
    }

    public void accumulatePlayTime() {
        if (turnStartTime > 0) {
            totalPlayTimeMs += (System.currentTimeMillis() - turnStartTime);
            turnStartTime = 0;
        }
    }

    private void stopTimerOnly() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public long getRemainingMs() {
        return remainingMs;
    }

    public long getTotalPlayTimeMs() {
        if (turnStartTime > 0) {
            return totalPlayTimeMs + (System.currentTimeMillis() - turnStartTime);
        }
        return totalPlayTimeMs;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
