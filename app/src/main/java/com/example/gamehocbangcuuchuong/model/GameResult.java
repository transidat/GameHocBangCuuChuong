package com.example.gamehocbangcuuchuong.model;

/**
 * Class đại diện cho kết quả của một lần chơi.
 * Chứa: Tên người chơi, tên trò chơi, số điểm, số câu đúng, tổng số câu hỏi, thời gian chơi.
 */
public class GameResult {
    private int id;                 // ID lượt chơi
    private String playerName;      // Tên người chơi
    private String gameName;        // Tên trò chơi (Đập chuột, Đua xe, Hứng bóng, Chèo thuyền)
    private int score;              // Điểm số đạt được
    private int correctAnswers;     // Số câu trả lời đúng
    private int totalQuestions;     // Tổng số câu hỏi
    private String playDate;        // Thời gian chơi

    public GameResult(String playerName, String gameName, int score, int correctAnswers, int totalQuestions, String playDate) {
        this.playerName = playerName;
        this.gameName = gameName;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.playDate = playDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }
}
