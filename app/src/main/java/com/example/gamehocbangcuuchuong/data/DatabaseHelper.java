package com.example.gamehocbangcuuchuong.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.model.GameResult;
import com.example.gamehocbangcuuchuong.model.Mission;
import com.example.gamehocbangcuuchuong.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class quản lý tạo và thao tác với cơ sở dữ liệu SQLite trong ứng dụng.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameHocBangCuuChuong.db";
    private static final int DATABASE_VERSION = 2;

    // --- TÊN BẢNG VÀ CÁC CỘT CHO PLAYER ---
    public static final String TABLE_PLAYER = "players";
    public static final String COLUMN_PLAYER_ID = "id";
    public static final String COLUMN_PLAYER_NAME = "name";
    public static final String COLUMN_PLAYER_TOTAL_SCORE = "total_score";
    public static final String COLUMN_PLAYER_HIGH_SCORE = "high_score";

    // --- TÊN BẢNG VÀ CÁC CỘT CHO GAME_RESULT ---
    public static final String TABLE_GAME_RESULT = "game_results";
    public static final String COLUMN_RESULT_ID = "id";
    public static final String COLUMN_RESULT_PLAYER_NAME = "player_name";
    public static final String COLUMN_RESULT_GAME_NAME = "game_name";
    public static final String COLUMN_RESULT_SCORE = "score";
    public static final String COLUMN_RESULT_CORRECT = "correct_answers";
    public static final String COLUMN_RESULT_TOTAL = "total_questions";
    public static final String COLUMN_RESULT_DATE = "play_date";

    // --- TÊN BẢNG VÀ CÁC CỘT CHO MISSION ---
    public static final String TABLE_MISSION = "missions";
    public static final String COLUMN_MISSION_ID = "id";
    public static final String COLUMN_MISSION_TITLE = "title";
    public static final String COLUMN_MISSION_DESCRIPTION = "description";
    public static final String COLUMN_MISSION_CURRENT_PROGRESS = "current_progress";
    public static final String COLUMN_MISSION_MAX_PROGRESS = "max_progress";
    public static final String COLUMN_MISSION_REWARD = "reward_points";
    public static final String COLUMN_MISSION_IS_COMPLETED = "is_completed";
    public static final String COLUMN_MISSION_IS_REWARD_CLAIMED = "is_reward_claimed";
    public static final String COLUMN_MISSION_ICON_RES_ID = "icon_res_id";
    public static final String COLUMN_MISSION_ICON_BG_COLOR = "icon_bg_color";
    public static final String COLUMN_MISSION_GAME_LAYOUT_ID = "game_layout_id";
    public static final String COLUMN_MISSION_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPlayerTable(db);
        createGameResultTable(db);
        createMissionTable(db);

        // Nạp danh sách nhiệm vụ mặc định ban đầu
        seedDefaultMissions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Chỉ làm mới bảng nhiệm vụ (dữ liệu mặc định), giữ nguyên dữ liệu người chơi và kết quả
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MISSION);
        createMissionTable(db);
        seedDefaultMissions(db);
    }

    private void createPlayerTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_PLAYER + " (" +
                COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYER_NAME + " TEXT UNIQUE, " +
                COLUMN_PLAYER_TOTAL_SCORE + " INTEGER DEFAULT 0, " +
                COLUMN_PLAYER_HIGH_SCORE + " INTEGER DEFAULT 0" +
                ")";
        db.execSQL(sql);
    }

    private void createGameResultTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_GAME_RESULT + " (" +
                COLUMN_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESULT_PLAYER_NAME + " TEXT, " +
                COLUMN_RESULT_GAME_NAME + " TEXT, " +
                COLUMN_RESULT_SCORE + " INTEGER, " +
                COLUMN_RESULT_CORRECT + " INTEGER, " +
                COLUMN_RESULT_TOTAL + " INTEGER, " +
                COLUMN_RESULT_DATE + " TEXT" +
                ")";
        db.execSQL(sql);
    }

    private void createMissionTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_MISSION + " (" +
                COLUMN_MISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MISSION_TITLE + " TEXT, " +
                COLUMN_MISSION_DESCRIPTION + " TEXT, " +
                COLUMN_MISSION_CURRENT_PROGRESS + " INTEGER DEFAULT 0, " +
                COLUMN_MISSION_MAX_PROGRESS + " INTEGER, " +
                COLUMN_MISSION_REWARD + " INTEGER, " +
                COLUMN_MISSION_IS_COMPLETED + " INTEGER DEFAULT 0, " +
                COLUMN_MISSION_IS_REWARD_CLAIMED + " INTEGER DEFAULT 0, " +
                COLUMN_MISSION_ICON_RES_ID + " INTEGER, " +
                COLUMN_MISSION_ICON_BG_COLOR + " TEXT, " +
                COLUMN_MISSION_GAME_LAYOUT_ID + " INTEGER, " +
                COLUMN_MISSION_CATEGORY + " TEXT" +
                ")";
        db.execSQL(sql);
    }

    // Kho dữ liệu nhiệm vụ mặc định ban đầu
    private void seedDefaultMissions(SQLiteDatabase db) {
        // Nhiệm vụ HÔM NAY (Today) - Đủ cả 4 game
        insertMissionWithDb(db, "Dũng sĩ đập chuột", "Đập trúng 10 chú chuột", 0, 10, 10, R.drawable.iconchuot, "#FFF176", R.layout.layout_dap_chuot, "today");
        insertMissionWithDb(db, "Khéo tay hứng bóng", "Hứng 10 quả bóng chính xác", 0, 10, 10, R.drawable.iconbong, "#A5D6A7", R.layout.layout_hung_bong, "today");
        insertMissionWithDb(db, "Tay lái cừ khôi", "Vượt qua 10 chặng đua xe", 0, 10, 10, R.drawable.iconxe, "#FFCC80", R.layout.layout_dua_xe, "today");
        insertMissionWithDb(db, "Tay chèo nhanh nhạy", "Chèo thuyền qua 10 chướng ngại vật", 0, 10, 10, R.drawable.iconthuyen, "#90CAF9", R.layout.layout_cheo_thuyen, "today");

        // Nhiệm vụ TUẦN NÀY (Week) - Đủ cả 4 game
        insertMissionWithDb(db, "Thợ chèo thuyền", "Chèo thuyền qua 20 chướng ngại vật", 0, 20, 20, R.drawable.iconthuyen, "#90CAF9", R.layout.layout_cheo_thuyen, "week");
        insertMissionWithDb(db, "Vua đập chuột", "Đập trúng 30 chú chuột", 0, 30, 30, R.drawable.iconchuot, "#FFF176", R.layout.layout_dap_chuot, "week");
        insertMissionWithDb(db, "Siêu sao hứng bóng", "Hứng 30 quả bóng chính xác", 0, 30, 30, R.drawable.iconbong, "#A5D6A7", R.layout.layout_hung_bong, "week");
        insertMissionWithDb(db, "Tay đua huyền thoại", "Vượt qua 30 chặng đua xe", 0, 30, 30, R.drawable.iconxe, "#FFCC80", R.layout.layout_dua_xe, "week");
    }

    private void insertMissionWithDb(SQLiteDatabase db, String title, String description, int current, int max, int reward,
                                      int iconRes, String iconBg, int gameLayoutId, String category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MISSION_TITLE, title);
        values.put(COLUMN_MISSION_DESCRIPTION, description);
        values.put(COLUMN_MISSION_CURRENT_PROGRESS, current);
        values.put(COLUMN_MISSION_MAX_PROGRESS, max);
        values.put(COLUMN_MISSION_REWARD, reward);
        values.put(COLUMN_MISSION_IS_COMPLETED, current >= max ? 1 : 0);
        values.put(COLUMN_MISSION_IS_REWARD_CLAIMED, 0);
        values.put(COLUMN_MISSION_ICON_RES_ID, iconRes);
        values.put(COLUMN_MISSION_ICON_BG_COLOR, iconBg);
        values.put(COLUMN_MISSION_GAME_LAYOUT_ID, gameLayoutId);
        values.put(COLUMN_MISSION_CATEGORY, category);
        db.insert(TABLE_MISSION, null, values);
    }

    // ==========================================
    // CÁC THAO TÁC TRÊN BẢNG PLAYER
    // ==========================================

    public void savePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, player.getName());
        values.put(COLUMN_PLAYER_TOTAL_SCORE, player.getTotalScore());
        values.put(COLUMN_PLAYER_HIGH_SCORE, player.getHighScore());

        int rows = db.update(TABLE_PLAYER, values, COLUMN_PLAYER_NAME + " = ?", new String[]{player.getName()});
        if (rows == 0) {
            db.insert(TABLE_PLAYER, null, values);
        }
    }

    public Player getPlayer(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PLAYER, null, COLUMN_PLAYER_NAME + " = ?",
                new String[]{name}, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int totalScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_TOTAL_SCORE));
                int highScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_HIGH_SCORE));
                return new Player(name, totalScore, highScore);
            }
        }
        return null;
    }

    // ==========================================
    // CÁC THAO TÁC TRÊN BẢNG GAME_RESULT
    // ==========================================

    public long insertGameResult(GameResult result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESULT_PLAYER_NAME, result.getPlayerName());
        values.put(COLUMN_RESULT_GAME_NAME, result.getGameName());
        values.put(COLUMN_RESULT_SCORE, result.getScore());
        values.put(COLUMN_RESULT_CORRECT, result.getCorrectAnswers());
        values.put(COLUMN_RESULT_TOTAL, result.getTotalQuestions());
        values.put(COLUMN_RESULT_DATE, result.getPlayDate());

        return db.insert(TABLE_GAME_RESULT, null, values);
    }

    // ==========================================
    // CÁC THAO TÁC TRÊN BẢNG MISSION
    // ==========================================

    private Mission cursorToMission(Cursor cursor) {
        return new Mission(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MISSION_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MISSION_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_CURRENT_PROGRESS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_MAX_PROGRESS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_REWARD)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_IS_COMPLETED)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_IS_REWARD_CLAIMED)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_ICON_RES_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MISSION_ICON_BG_COLOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MISSION_GAME_LAYOUT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MISSION_CATEGORY))
        );
    }

    // Lấy tất cả nhiệm vụ theo danh mục ("today" hoặc "week")
    public List<Mission> getMissionsByCategory(String category) {
        List<Mission> missions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_MISSION, null, COLUMN_MISSION_CATEGORY + " = ?",
                new String[]{category}, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    missions.add(cursorToMission(cursor));
                } while (cursor.moveToNext());
            }
        }
        return missions;
    }

    // Lấy tất cả nhiệm vụ theo ID giao diện game
    public List<Mission> getMissionsByLayout(SQLiteDatabase db, int gameLayoutId) {
        List<Mission> missions = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_MISSION, null, COLUMN_MISSION_GAME_LAYOUT_ID + " = ?",
                new String[]{String.valueOf(gameLayoutId)}, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    missions.add(cursorToMission(cursor));
                } while (cursor.moveToNext());
            }
        }
        return missions;
    }

    // Cập nhật tiến độ nhiệm vụ
    public void updateMissionProgress(SQLiteDatabase db, int missionId, int currentProgress, boolean isCompleted) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MISSION_CURRENT_PROGRESS, currentProgress);
        values.put(COLUMN_MISSION_IS_COMPLETED, isCompleted ? 1 : 0);

        db.update(TABLE_MISSION, values, COLUMN_MISSION_ID + " = ?", new String[]{String.valueOf(missionId)});
    }

    public void updateMissionProgress(int missionId, int currentProgress, boolean isCompleted) {
        updateMissionProgress(this.getWritableDatabase(), missionId, currentProgress, isCompleted);
    }

    // Cập nhật trạng thái nhận thưởng nhiệm vụ
    public void setMissionRewardClaimed(int missionId, boolean isClaimed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MISSION_IS_REWARD_CLAIMED, isClaimed ? 1 : 0);

        db.update(TABLE_MISSION, values, COLUMN_MISSION_ID + " = ?", new String[]{String.valueOf(missionId)});
    }

    // Cập nhật tiến độ nhiệm vụ theo loại game (ví dụ khi chơi xong Đập chuột -> cộng thêm tiến độ)
    public void incrementMissionProgressByGame(int gameLayoutId, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Mission> missions = getMissionsByLayout(db, gameLayoutId);

        db.beginTransaction();
        try {
            for (Mission mission : missions) {
                mission.updateProgress(amount);
                updateMissionProgress(db, mission.getId(), mission.getCurrentProgress(), mission.isCompleted());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Lấy tổng số câu đúng tích lũy theo tên game
    public int getTotalCorrectByGame(String gameName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;
        try (Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_RESULT_CORRECT + ") FROM " + TABLE_GAME_RESULT + " WHERE " + COLUMN_RESULT_GAME_NAME + " = ?",
                new String[]{gameName}
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }
        }
        return total;
    }

    // Lấy điểm cao nhất theo tên game
    public int getHighestScoreByGame(String gameName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int maxScore = 0;
        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COLUMN_RESULT_SCORE + ") FROM " + TABLE_GAME_RESULT + " WHERE " + COLUMN_RESULT_GAME_NAME + " = ?",
                new String[]{gameName}
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                maxScore = cursor.getInt(0);
            }
        }
        return maxScore;
    }

    // Kiểm tra có lượt chơi đạt tối đa (không sai câu nào, từ 5 câu trở lên) hay không
    public boolean hasPerfectGame(String gameName) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_GAME_RESULT + " WHERE " + COLUMN_RESULT_GAME_NAME + " = ? AND " + COLUMN_RESULT_CORRECT + " >= 5 AND " + COLUMN_RESULT_CORRECT + " = " + COLUMN_RESULT_TOTAL,
                new String[]{gameName}
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        }
        return false;
    }
}
