package com.example.gamehocbangcuuchuong.model;

/**
 * Class đại diện cho thông tin người chơi.
 * Chứa: Tên người chơi, tổng điểm, điểm cao nhất.
 */
public class Player {
    private String name;        // Tên người chơi
    private int totalScore;     // Tổng điểm
    private int highScore;      // Điểm cao nhất

    public Player(String name, int totalScore, int highScore) {
        this.name = name;
        this.totalScore = totalScore;
        this.highScore = highScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    // Cộng thêm điểm vào tổng điểm và cập nhật điểm cao nhất nếu cần
    public void addScore(int score) {
        this.totalScore += score;
        if (score > this.highScore) {
            this.highScore = score;
        }
    }

    // Cập nhật điểm cao nhất
    public void updateHighScore(int score) {
        if (score > this.highScore) {
            this.highScore = score;
        }
    }
}
