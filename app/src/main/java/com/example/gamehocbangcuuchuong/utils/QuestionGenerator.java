package com.example.gamehocbangcuuchuong.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.gamehocbangcuuchuong.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionGenerator {
    /**
     * Sinh danh sách N câu hỏi KHÔNG TRÙNG LẶP cho một lượt chơi 10 câu.
     */
    public static List<Question> generateQuestionList(Context context, int count, int totalAnswers) {
        int minTable = 1;
        int maxTable = 9;
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
            return generateQuestionList(sp.getInt("min_range", 1), sp.getInt("max_range", 9), count, totalAnswers);
        }
        return generateQuestionList(minTable, maxTable, count, totalAnswers);
    }

    public static List<Question> generateQuestionList(int minTable, int maxTable, int count, int totalAnswers) {
        int startTable = Math.max(1, minTable);
        int endTable = Math.max(startTable, maxTable);

        // 1. Tạo danh sách tất cả các cặp phép tính (a x b) trong phạm vi chọn
        List<int[]> pairs = new ArrayList<>();
        for (int a = startTable; a <= endTable; a++) {
            for (int b = 1; b <= 9; b++) {
                pairs.add(new int[]{a, b});
            }
        }

        List<Question> questions = new ArrayList<>();
        int[] lastPair = null;

        while (questions.size() < count) {
            // Xáo trộn ngẫu nhiên danh sách phép tính cho mỗi lượt
            List<int[]> currentBatch = new ArrayList<>(pairs);
            Collections.shuffle(currentBatch);

            // Đảm bảo phần tử đầu tiên của lượt mới không trùng với câu hỏi ngay trước đó
            if (lastPair != null && currentBatch.size() > 1 && isSamePair(currentBatch.get(0), lastPair)) {
                for (int i = 1; i < currentBatch.size(); i++) {
                    if (!isSamePair(currentBatch.get(i), lastPair)) {
                        Collections.swap(currentBatch, 0, i);
                        break;
                    }
                }
            }

            // Thêm các phép tính vào danh sách câu hỏi
            for (int[] pair : currentBatch) {
                if (questions.size() >= count) break;

                // Tránh thêm câu hỏi lặp liên tiếp nếu xảy ra trùng lặp
                if (lastPair != null && isSamePair(pair, lastPair) && currentBatch.size() > 1) {
                    continue;
                }

                questions.add(buildQuestion(pair[0], pair[1], totalAnswers));
                lastPair = pair;
            }
        }

        return questions;
    }

    private static boolean isSamePair(int[] p1, int[] p2) {
        return p1[0] == p2[0] && p1[1] == p2[1];
    }

    private static Question buildQuestion(int a, int b, int totalAnswers) {
        Random random = new Random();
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
