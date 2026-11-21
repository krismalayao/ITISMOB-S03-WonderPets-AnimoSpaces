package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.model.NotificationItem;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;

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

        // Expanded card state
        if (item.isExpanded()) {
            holder.tvMessage.setMaxLines(100);
            holder.tvMessage.setEllipsize(null);
            if (holder.cardNotifExpanded != null) {
                holder.cardNotifExpanded.setVisibility(View.VISIBLE);
            }
            if (holder.tvExpCardType != null) {
                holder.tvExpCardType.setText(item.getTitle().toUpperCase());
            }
            if (holder.tvExpCardStatus != null) {
                holder.tvExpCardStatus.setText(item.getType().name());
            }
            if (holder.tvExpCardMessage != null) {
                holder.tvExpCardMessage.setText(item.getMessage());
            }

            // Look up linked reservation if any
            if (holder.layoutExpReservationSpecs != null) {
                Reservation res = ReservationRepository.getInstance().getReservationById(item.getReservationId());
                if (res != null) {
                    holder.layoutExpReservationSpecs.setVisibility(View.VISIBLE);
                    if (holder.tvExpVenue != null) {
                        holder.tvExpVenue.setText("📍 Venue: " + res.getRoomCode() + " (" + res.getBuildingName() + ")");
                    }
                    if (holder.tvExpSchedule != null) {
                        holder.tvExpSchedule.setText("📅 " + res.getDateString() + " | ⏰ " + res.getTimeSlot());
                    }
                } else {
                    holder.layoutExpReservationSpecs.setVisibility(View.GONE);
                }
            }

            if (holder.ivExpand != null) {
                holder.ivExpand.setRotation(180f);
            }
        } else {
            holder.tvMessage.setMaxLines(2);
            holder.tvMessage.setEllipsize(TextUtils.TruncateAt.END);
            if (holder.cardNotifExpanded != null) {
                holder.cardNotifExpanded.setVisibility(View.GONE);
            }
            if (holder.ivExpand != null) {
                holder.ivExpand.setRotation(0f);
            }
        }

        // Read state
        if (item.isRead()) {
            holder.viewUnreadDot.setVisibility(View.GONE);
            holder.cardView.setAlpha(0.7f);
        } else {
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
            holder.cardView.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                item.setRead(true);
                item.toggleExpanded();
                notifyItemChanged(pos);
                if (clickListener != null) {
                    clickListener.onNotificationClick(item, pos);
                }
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
        ImageView ivExpand;
        TextView tvTitle;
        TextView tvMessage;
        TextView tvTime;
        View viewUnreadDot;

        // Expanded Card Views
        MaterialCardView cardNotifExpanded;
        TextView tvExpCardType;
        TextView tvExpCardStatus;
        TextView tvExpCardMessage;
        View layoutExpReservationSpecs;
        TextView tvExpVenue;
        TextView tvExpSchedule;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_notification);
            ivIcon = itemView.findViewById(R.id.iv_notif_icon);
            ivExpand = itemView.findViewById(R.id.iv_notif_expand);
            tvTitle = itemView.findViewById(R.id.tv_notif_title);
            tvMessage = itemView.findViewById(R.id.tv_notif_message);
            tvTime = itemView.findViewById(R.id.tv_notif_time);
            viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);

            cardNotifExpanded = itemView.findViewById(R.id.card_notif_expanded);
            tvExpCardType = itemView.findViewById(R.id.tv_exp_card_type);
            tvExpCardStatus = itemView.findViewById(R.id.tv_exp_card_status);
            tvExpCardMessage = itemView.findViewById(R.id.tv_exp_card_message);
            layoutExpReservationSpecs = itemView.findViewById(R.id.layout_exp_reservation_specs);
            tvExpVenue = itemView.findViewById(R.id.tv_exp_venue);
            tvExpSchedule = itemView.findViewById(R.id.tv_exp_schedule);
        }
    }
}
