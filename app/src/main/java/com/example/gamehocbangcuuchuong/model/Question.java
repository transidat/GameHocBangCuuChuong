package com.example.gamehocbangcuuchuong.model;

import java.util.ArrayList;

/**
 * Class đại diện cho câu hỏi trong trò chơi.
 * Chứa: Nội dung câu hỏi, đáp án đúng, danh sách đáp án.
 */
public class Question {
    private String questionText;        // Nội dung phép tính hiển thị (Ví dụ: "4 × 8 = ?")
    private int correctAnswer;          // Giá trị số của đáp án đúng (Ví dụ: 32)
    private ArrayList<Integer> answers; // Danh sách các đáp án lựa chọn

    public Question(String questionText, int correctAnswer, ArrayList<Integer> answers) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.answers = answers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public ArrayList<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Integer> answers) {
        this.answers = answers;
    }

    // Kiểm tra câu trả lời người chơi chọn có chính xác không
    public boolean isCorrect(int selectedAnswer) {
        return selectedAnswer == correctAnswer;
    }
}
