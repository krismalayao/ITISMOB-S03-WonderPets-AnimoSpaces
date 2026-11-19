package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item, int position);
    }

    private final List<NotificationItem> notifications = new ArrayList<>();
    private final OnNotificationClickListener clickListener;

    public NotificationAdapter(OnNotificationClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setNotifications(List<NotificationItem> list) {
        this.notifications.clear();
        if (list != null) {
            this.notifications.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());
        holder.tvTime.setText(item.getTimestampFormatted());

        if (item.getType() == NotificationItem.Type.APPROVAL) {
            holder.ivIcon.setImageResource(android.R.drawable.checkbox_on_background);
        } else if (item.getType() == NotificationItem.Type.REJECTION) {
            holder.ivIcon.setImageResource(android.R.drawable.ic_delete);
        } else {
            holder.ivIcon.setImageResource(android.R.drawable.ic_dialog_info);
        }

        if (item.isRead()) {
            holder.viewUnreadDot.setVisibility(View.GONE);
            holder.cardView.setAlpha(0.7f);
        } else {
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
            holder.cardView.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onNotificationClick(item, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvMessage;
        TextView tvTime;
        View viewUnreadDot;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_notification);
            ivIcon = itemView.findViewById(R.id.iv_notif_icon);
            tvTitle = itemView.findViewById(R.id.tv_notif_title);
            tvMessage = itemView.findViewById(R.id.tv_notif_message);
            tvTime = itemView.findViewById(R.id.tv_notif_time);
            viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);
        }
    }
}