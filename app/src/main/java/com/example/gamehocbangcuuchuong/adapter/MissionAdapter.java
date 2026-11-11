package com.example.gamehocbangcuuchuong.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamehocbangcuuchuong.R;
import com.example.gamehocbangcuuchuong.model.Mission;

import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionViewHolder> {

    public interface OnMissionClickListener {
        void onDoMission(Mission mission);
        void onClaimReward(Mission mission);
    }

    private final List<Mission> missionList;
    private final OnMissionClickListener listener;

    public MissionAdapter(List<Mission> missionList, OnMissionClickListener listener) {
        this.missionList = missionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Mission mission = missionList.get(position);

        Context context = holder.itemView.getContext();
        holder.tvTitle.setText(mission.getTitle());
        holder.tvDesc.setText(mission.getDescription());
        holder.tvProgressText.setText(context.getString(R.string.progress_ratio_format, mission.getCurrentProgress(), mission.getMaxProgress()));
        holder.tvReward.setText(context.getString(R.string.reward_exp_format, mission.getRewardPoints()));

        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(mission.getProgressPercentage());

        // Hiển thị Icon và màu nền khung chứa Icon
        if (mission.getIconResId() != 0) {
            holder.imgIcon.setImageResource(mission.getIconResId());
        }

        try {
            float density = context.getResources().getDisplayMetrics().density;
            int bgColor = Color.parseColor(mission.getIconBgColor() != null ? mission.getIconBgColor() : "#FFF176");
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(16f * density); // Bo góc 16dp
            shape.setColor(bgColor);
            holder.layoutIconContainer.setBackground(shape);
        } catch (Exception e) {
            holder.layoutIconContainer.setBackgroundResource(R.drawable.bg_pill_white);
        }

        // Cấu hình trạng thái Nút bấm
        if (mission.isRewardClaimed()) {
            holder.btnAction.setText(R.string.mission_btn_claimed);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_action_completed);
            holder.btnAction.setOnClickListener(null);
        } else if (mission.isCompleted()) {
            holder.btnAction.setText(R.string.mission_btn_claim);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_action_blue);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClaimReward(mission);
                }
            });
        } else {
            holder.btnAction.setText(R.string.mission_btn_do_now);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_action_blue);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDoMission(mission);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return missionList != null ? missionList.size() : 0;
    }

    public static class MissionViewHolder extends RecyclerView.ViewHolder {
        FrameLayout layoutIconContainer;
        ImageView imgIcon;
        TextView tvTitle, tvDesc, tvProgressText, tvReward, btnAction;
        ProgressBar progressBar;

        public MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutIconContainer = itemView.findViewById(R.id.layout_icon_container);
            imgIcon = itemView.findViewById(R.id.img_mission_icon);
            tvTitle = itemView.findViewById(R.id.tv_mission_title);
            tvDesc = itemView.findViewById(R.id.tv_mission_desc);
            tvProgressText = itemView.findViewById(R.id.tv_mission_progress_text);
            tvReward = itemView.findViewById(R.id.tv_mission_reward);
            btnAction = itemView.findViewById(R.id.btn_mission_action);
            progressBar = itemView.findViewById(R.id.progress_mission);
        }
    }
}
