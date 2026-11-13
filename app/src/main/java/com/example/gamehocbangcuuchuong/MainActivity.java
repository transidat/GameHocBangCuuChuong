package com.example.gamehocbangcuuchuong;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamehocbangcuuchuong.data.DatabaseHelper;
import com.example.gamehocbangcuuchuong.manager.CaiDatManager;
import com.example.gamehocbangcuuchuong.manager.GameManager;
import com.example.gamehocbangcuuchuong.manager.NhiemVuManager;
import com.example.gamehocbangcuuchuong.manager.ThanhTichManager;
import com.example.gamehocbangcuuchuong.model.GameResult;
import com.example.gamehocbangcuuchuong.utils.ScoreManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FrameLayout mainContainer;
    private SharedPreferences sharedPreferences;
    private final GameManager gameManager = new GameManager();
    private DatabaseHelper dbHelper;
    private int lastPlayedGameLayout = R.layout.layout_dap_chuot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        mainContainer = findViewById(R.id.container);
        sharedPreferences = getSharedPreferences("GAME_DATA", MODE_PRIVATE);

        showScreen(R.layout.layout_bat_dau);
    }

    public DatabaseHelper getDbHelper() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this);
        }
        return dbHelper;
    }

    public void stopActiveGames() {
        gameManager.stopActiveGames();
    }

    public void showScreen(int layoutId) {
        stopActiveGames();
        mainContainer.removeAllViews();
        View view = LayoutInflater.from(this).inflate(layoutId, mainContainer, false);
        mainContainer.addView(view);
        setupScreenLogic(layoutId, view);
    }

    public void showResultScreen(String gameName, int gameLayoutId, int score, int correctCount, int totalQuestions, long durationSeconds, int bonus) {
        stopActiveGames();
        this.lastPlayedGameLayout = gameLayoutId;

        String playerName = sharedPreferences.getString("player_name", "Người chơi");
        DatabaseHelper db = getDbHelper();
        int finalTotalScore = ScoreManager.addTotalScore(this, db, playerName, score);

        String playDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        db.insertGameResult(new GameResult(playerName, gameName, score, correctCount, totalQuestions, playDate));

        if (correctCount > 0) {
            db.incrementMissionProgressByGame(gameLayoutId, correctCount);
        }

        ScoreManager.checkAndSaveHighScore(this, db, playerName, score);

        mainContainer.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_ket_qua, mainContainer, false);
        mainContainer.addView(view);

        TextView tvTopExp = view.findViewById(R.id.tv_kq_top_exp);
        TextView tvScore = view.findViewById(R.id.tv_kq_score);
        TextView tvCorrectCount = view.findViewById(R.id.tv_kq_correct_count);
        TextView tvDuration = view.findViewById(R.id.tv_kq_duration);
        TextView tvBonus = view.findViewById(R.id.tv_kq_bonus);

        if (tvTopExp != null) {
            tvTopExp.setText(getString(R.string.score_exp_format, finalTotalScore));
        }
        if (tvScore != null) {
            tvScore.setText(String.valueOf(score));
        }
        if (tvCorrectCount != null) {
            tvCorrectCount.setText(getString(R.string.progress_ratio_format, correctCount, totalQuestions));
        }
        if (tvDuration != null) {
            long minutes = durationSeconds / 60;
            long seconds = durationSeconds % 60;
            tvDuration.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        }
        if (tvBonus != null) {
            tvBonus.setText(getString(R.string.bonus_exp_format, bonus));
        }

        setupScreenLogic(R.layout.layout_ket_qua, view);
    }

    private void setupScreenLogic(int layoutId, View view) {
        if (layoutId == R.layout.layout_bat_dau) {
            setupBatDauScreen(view);
        } else if (layoutId == R.layout.layout_trang_chu) {
            setupTrangChuScreen(view);
        } else if (layoutId == R.layout.layout_thanh_tich) {
            ThanhTichManager.setup(this, view);
        } else if (layoutId == R.layout.layout_nhiem_vu) {
            NhiemVuManager.setup(this, view, sharedPreferences);
        } else if (layoutId == R.layout.layout_dap_chuot ||
                   layoutId == R.layout.layout_hung_bong ||
                   layoutId == R.layout.layout_dua_xe ||
                   layoutId == R.layout.layout_cheo_thuyen) {
            gameManager.startGame(layoutId, this, view);
        } else if (layoutId == R.layout.layout_cai_dat) {
            CaiDatManager.setup(this, view, sharedPreferences);
        } else if (layoutId == R.layout.layout_ket_qua) {
            setupKetQuaScreen(view);
        }
    }

    private void setupBatDauScreen(View view) {
        EditText edtPlayerName = view.findViewById(R.id.edt_player_name);
        ImageButton btnReady = view.findViewById(R.id.btn_ready);

        String savedName = sharedPreferences.getString("player_name", "");
        if (edtPlayerName != null && !savedName.isEmpty()) {
            edtPlayerName.setText(savedName);
            edtPlayerName.setSelection(savedName.length());
        }

        if (btnReady != null) {
            btnReady.setOnClickListener(v -> {
                String name = edtPlayerName != null ? edtPlayerName.getText().toString().trim() : "";
                if (name.isEmpty()) {
                    Toast.makeText(this, "Bạn ơi, hãy nhập tên của mình vào bảng gỗ nhé! 🐻", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferences.edit().putString("player_name", name).apply();
                    Toast.makeText(this, "Chào mừng " + name + " bước vào thế giới toán học! 🎉", Toast.LENGTH_SHORT).show();
                    showScreen(R.layout.layout_trang_chu);
                }
            });
        }
    }

    private void setupTrangChuScreen(View view) {
        TextView tvWelcomeName = view.findViewById(R.id.tv_welcome_name);
        TextView tvExp = view.findViewById(R.id.tv_exp);

        String savedName = sharedPreferences.getString("player_name", "");
        if (tvWelcomeName != null) {
            if (!savedName.isEmpty()) {
                tvWelcomeName.setText(getString(R.string.welcome_name_format, savedName));
            } else {
                tvWelcomeName.setText(R.string.welcome_default);
            }
        }

        int totalScore = ScoreManager.getTotalScore(this);
        if (tvExp != null) {
            tvExp.setText(getString(R.string.score_exp_format, totalScore));
        }

        View btnDapChuot = view.findViewById(R.id.btn_game_dap_chuot);
        View btnHungBong = view.findViewById(R.id.btn_game_hung_bong);
        View btnDuaXe = view.findViewById(R.id.btn_game_dua_xe);
        View btnCheoThuyen = view.findViewById(R.id.btn_game_cheo_thuyen);
        View btnNhiemVu = view.findViewById(R.id.btn_nhiem_vu);
        View btnThanhTich = view.findViewById(R.id.btn_thanh_tich);
        View btnCaiDat = view.findViewById(R.id.btn_cai_dat);

        if (btnDapChuot != null) btnDapChuot.setOnClickListener(v -> showScreen(R.layout.layout_dap_chuot));
        if (btnHungBong != null) btnHungBong.setOnClickListener(v -> showScreen(R.layout.layout_hung_bong));
        if (btnDuaXe != null) btnDuaXe.setOnClickListener(v -> showScreen(R.layout.layout_dua_xe));
        if (btnCheoThuyen != null) btnCheoThuyen.setOnClickListener(v -> showScreen(R.layout.layout_cheo_thuyen));
        if (btnNhiemVu != null) btnNhiemVu.setOnClickListener(v -> showScreen(R.layout.layout_nhiem_vu));
        if (btnThanhTich != null) btnThanhTich.setOnClickListener(v -> showScreen(R.layout.layout_thanh_tich));
        if (btnCaiDat != null) btnCaiDat.setOnClickListener(v -> showScreen(R.layout.layout_cai_dat));
    }

    private void setupKetQuaScreen(View view) {
        View btnHome = view.findViewById(R.id.btn_kq_home);
        View btnReplay = view.findViewById(R.id.btn_kq_replay);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> showScreen(R.layout.layout_trang_chu));
        }
        if (btnReplay != null) {
            btnReplay.setOnClickListener(v -> showScreen(lastPlayedGameLayout));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopActiveGames();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
