package com.example.gamehocbangcuuchuong.manager;

import android.view.View;

import com.example.gamehocbangcuuchuong.MainActivity;
import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.game.DapChuotGame;
import com.example.gamehocbangcuuchuong.game.DuaXeGame;
import com.example.gamehocbangcuuchuong.game.HungBongGame;
import com.example.gamehocbangcuuchuong.game.CheoThuyenGame;

/**
 * Quản lý khởi tạo, lưu giữ và dừng các phiên chơi Mini Game.
 */
public class GameManager {
    private DapChuotGame currentDapChuotGame;
    private HungBongGame currentHungBongGame;
    private DuaXeGame currentDuaXeGame;
    private CheoThuyenGame currentCheoThuyenGame;

    public void stopActiveGames() {
        if (currentDapChuotGame != null) {
            currentDapChuotGame.stopTimer();
            currentDapChuotGame = null;
        }
        if (currentHungBongGame != null) {
            currentHungBongGame.stopTimer();
            currentHungBongGame = null;
        }
        if (currentDuaXeGame != null) {
            currentDuaXeGame.stopTimer();
            currentDuaXeGame = null;
        }
        if (currentCheoThuyenGame != null) {
            currentCheoThuyenGame.stopTimer();
            currentCheoThuyenGame = null;
        }
    }

    public void startGame(int layoutId, MainActivity activity, View view) {
        stopActiveGames();
        if (layoutId == R.layout.layout_dap_chuot) {
            currentDapChuotGame = new DapChuotGame(activity, view);
        } else if (layoutId == R.layout.layout_hung_bong) {
            currentHungBongGame = new HungBongGame(activity, view);
        } else if (layoutId == R.layout.layout_dua_xe) {
            currentDuaXeGame = new DuaXeGame(activity, view);
        } else if (layoutId == R.layout.layout_cheo_thuyen) {
            currentCheoThuyenGame = new CheoThuyenGame(activity, view);
        }
    }
}
