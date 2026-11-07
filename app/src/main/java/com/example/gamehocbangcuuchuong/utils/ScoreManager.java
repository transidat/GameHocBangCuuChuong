package com.example.gamehocbangcuuchuong.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.gamehocbangcuuchuong.data.DatabaseHelper;
import com.example.gamehocbangcuuchuong.model.Player;

/**
 * Class quản lý các thao tác liên quan đến điểm số trong trò chơi:
 * - Tính điểm cho mỗi câu trả lời đúng (kèm combo/chuỗi đúng)
 * - Tính điểm thưởng bonus khi kết thúc màn chơi
 * - Lấy/lưu/so sánh điểm cao nhất và tổng điểm kinh nghiệm (KN)
 */
public class ScoreManager {

    private static final String PREF_NAME = "GAME_DATA";
    private static final String KEY_TOTAL_SCORE = "total_score";
    private static final String KEY_HIGH_SCORE_PREFIX = "high_score_";
    public static final int BASE_SCORE_PER_QUESTION = 10;

    // Tính điểm cho 1 câu trả lời đúng, có tính thêm combo chuỗi đúng
    public static int calculateQuestionScore(int comboCount) {
        if (comboCount <= 1) {
            return BASE_SCORE_PER_QUESTION;
        }
        // Thưởng thêm điểm tương ứng với chuỗi combo (Combo x1: 10 KN, Combo x2: 11 KN, Combo x3: 12 KN,...)
        return BASE_SCORE_PER_QUESTION + (comboCount - 1);
    }

    // Tính điểm thưởng bonus khi kết thúc trận đấu dựa vào điểm số trận đấu
    public static int calculateBonusScore(int gameScore) {
        return gameScore;
    }

    // Kiểm tra xem điểm số hiện tại có vượt qua điểm cao nhất không
    public static boolean isHighScore(int currentScore, int highScore) {
        return currentScore > highScore;
    }

    // Lấy điểm cao nhất của người chơi từ SharedPreferences
    public static int getHighScore(Context context, String playerName) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_HIGH_SCORE_PREFIX + playerName, 0);
    }

    // Lưu điểm cao nhất mới cho người chơi (nếu cao hơn điểm cũ)
    public static boolean checkAndSaveHighScore(Context context, String playerName, int newScore) {
        DatabaseHelper dbHelper = (context instanceof com.example.gamehocbangcuuchuong.MainActivity) 
                ? ((com.example.gamehocbangcuuchuong.MainActivity) context).getDbHelper() : null;
        return checkAndSaveHighScore(context, dbHelper, playerName, newScore);
    }

    public static boolean checkAndSaveHighScore(Context context, DatabaseHelper dbHelper, String playerName, int newScore) {
        int currentHighScore = getHighScore(context, playerName);
        if (newScore > currentHighScore) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(KEY_HIGH_SCORE_PREFIX + playerName, newScore).apply();

            // Cập nhật đồng thời vào DatabaseHelper nếu có người chơi trong DB
            if (dbHelper != null) {
                Player player = dbHelper.getPlayer(playerName);
                if (player != null) {
                    player.updateHighScore(newScore);
                    dbHelper.savePlayer(player);
                }
            } else {
                DatabaseHelper tempDb = new DatabaseHelper(context);
                Player player = tempDb.getPlayer(playerName);
                if (player != null) {
                    player.updateHighScore(newScore);
                    tempDb.savePlayer(player);
                }
                tempDb.close();
            }
            return true; // Trả về true nếu lập kỷ lục mới
        }
        return false;
    }

    // Lấy tổng điểm kinh nghiệm (KN) toàn cục
    public static int getTotalScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TOTAL_SCORE, 0);
    }

    // Cộng thêm điểm kinh nghiệm (KN) và lưu vào SharedPreferences + Database
    public static int addTotalScore(Context context, String playerName, int scoreToAdd) {
        DatabaseHelper dbHelper = (context instanceof com.example.gamehocbangcuuchuong.MainActivity) 
                ? ((com.example.gamehocbangcuuchuong.MainActivity) context).getDbHelper() : null;
        return addTotalScore(context, dbHelper, playerName, scoreToAdd);
    }

    public static int addTotalScore(Context context, DatabaseHelper dbHelper, String playerName, int scoreToAdd) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int currentTotal = prefs.getInt(KEY_TOTAL_SCORE, 0);
        int newTotal = currentTotal + scoreToAdd;
        prefs.edit().putInt(KEY_TOTAL_SCORE, newTotal).apply();

        // Cập nhật vào Database
        if (playerName != null && !playerName.isEmpty()) {
            if (dbHelper != null) {
                Player player = dbHelper.getPlayer(playerName);
                if (player != null) {
                    player.addScore(scoreToAdd);
                    dbHelper.savePlayer(player);
                } else {
                    dbHelper.savePlayer(new Player(playerName, newTotal, scoreToAdd));
                }
            } else {
                DatabaseHelper tempDb = new DatabaseHelper(context);
                Player player = tempDb.getPlayer(playerName);
                if (player != null) {
                    player.addScore(scoreToAdd);
                    tempDb.savePlayer(player);
                } else {
                    tempDb.savePlayer(new Player(playerName, newTotal, scoreToAdd));
                }
                tempDb.close();
            }
        }
        return newTotal;
    }

    /**
     * Class chứa thông tin chi tiết về cấp độ người chơi
     */
    public static class LevelInfo {
        public final int level;
        public final int currentExp;
        public final int maxExp;

        public LevelInfo(int level, int currentExp, int maxExp) {
            this.level = level;
            this.currentExp = currentExp;
            this.maxExp = maxExp;
        }
    }

    /**
     * Tính toán cấp độ và tiến độ kinh nghiệm hiện tại:
     * - Cấp 1 cần 200 KN
     * - Mỗi cấp tiếp theo cần tăng thêm 100 KN (Cấp 2 cần 300 KN, Cấp 3 cần 400 KN, Cấp 4 cần 500 KN,...)
     */
    public static LevelInfo calculateLevelInfo(int totalScore) {
        int level = 1;
        int expRemaining = Math.max(0, totalScore);
        int expNeeded = 200; // Cấp 1 cần 200 KN

        while (expRemaining >= expNeeded) {
            expRemaining -= expNeeded;
            level++;
            expNeeded += 100; // Mỗi cấp sau tăng thêm 100 KN
        }

        return new LevelInfo(level, expRemaining, expNeeded);
    }
}
