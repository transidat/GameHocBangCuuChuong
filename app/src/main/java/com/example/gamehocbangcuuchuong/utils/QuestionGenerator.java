package com.example.gamehocbangcuuchuong.utils;

import com.example.gamehocbangcuuchuong.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuestionGenerator {

    public static Question generateQuestion() {
        return generateQuestion(6);
    }

    public static Question generateQuestion(int totalAnswers) {
        Random random = new Random();

        int a = random.nextInt(8) + 2; // Bảng từ 2 đến 9
        int b = random.nextInt(9) + 1; // Nhân từ 1 đến 9
        int correctAnswer = a * b;

        String questionText = a + " × " + b + " = ?";

        ArrayList<Integer> answers = new ArrayList<>();
        answers.add(correctAnswer);

        // Vòng lặp sinh thêm các đáp án sai
        while (answers.size() < totalAnswers) {
            int wrongAnswer = correctAnswer + (random.nextInt(15) - 7);
            if (wrongAnswer > 0 && !answers.contains(wrongAnswer)) {
                answers.add(wrongAnswer);
            }
        }

        Collections.shuffle(answers);
        return new Question(questionText, correctAnswer, answers);
    }
}
