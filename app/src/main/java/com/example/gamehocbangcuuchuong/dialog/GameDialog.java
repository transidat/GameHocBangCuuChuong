package com.example.gamehocbangcuuchuong.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.utils.LivesManager;

public class GameDialog {

    // Interface giao tiếp với Game/Activity
    public interface PauseDialogListener {
        void onResumeGame();      // Khi nhấn Tiếp tục
        void onReturnHome();      // Khi nhấn Về trang chủ
    }

    public static void showPauseDialog(Context context, int score, int lives, int combo, PauseDialogListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tam_dung, null);
        dialog.setContentView(view);

        // Bo góc/nền trong suốt cho Dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Không cho bấm ra ngoài để tắt
        dialog.setCancelable(false);

        // Ánh xạ View
        TextView tvScore = view.findViewById(R.id.tvScore);
        TextView tvCombo = view.findViewById(R.id.tvCombo);
        LinearLayout layoutHearts = view.findViewById(R.id.layoutHearts);
        ImageButton btnTiepTuc = view.findViewById(R.id.btnTiepTuc);
        ImageButton btnVeTrangChu = view.findViewById(R.id.btnVeTrangChu);

        // Đổ dữ liệu
        if (tvScore != null) {
            tvScore.setText(String.valueOf(score));
        }
        if (tvCombo != null) {
            tvCombo.setText(context.getString(R.string.combo_multiplier_format, combo));
        }

        // Đổ trái tim mạng
        LivesManager.populateHeartContainer(context, layoutHearts, lives);

        // Sự kiện click nút Tiếp tục
        if (btnTiepTuc != null) {
            btnTiepTuc.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onResumeGame();
                }
            });
        }

        // Sự kiện click nút Về trang chủ
        if (btnVeTrangChu != null) {
            btnVeTrangChu.setOnClickListener(v -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onReturnHome();
                }
            });
        }

        dialog.show();
    }

    public interface WrongAnswerListener {
        void onNextTurn();
    }

    public static void showWrongAnswerDialog(Context context, View contentViewToHide, int lives, String message, WrongAnswerListener listener) {
        if (contentViewToHide != null) {
            contentViewToHide.setVisibility(View.INVISIBLE);
        }

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_chon_sai, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        TextView tvWrongMsg = view.findViewById(R.id.tv_wrong_msg);
        if (tvWrongMsg != null && message != null) {
            tvWrongMsg.setText(message);
        }

        LinearLayout containerHearts = view.findViewById(R.id.containerHearts);
        ImageButton btnNextTurn = view.findViewById(R.id.btnNextTurn);

        // Đổ trái tim mạng
        LivesManager.populateHeartContainer(context, containerHearts, lives);

        if (btnNextTurn != null) {
            btnNextTurn.setOnClickListener(v -> {
                dialog.dismiss();
                if (contentViewToHide != null) {
                    contentViewToHide.setVisibility(View.VISIBLE);
                }
                if (listener != null) {
                    listener.onNextTurn();
                }
            });
        }

        dialog.show();
    }
}
