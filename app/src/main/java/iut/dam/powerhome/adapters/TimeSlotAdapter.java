package iut.dam.powerhome.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import entities.TimeSlot;
import iut.dam.powerhome.R;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    private Context context;
    private List<TimeSlot> timeSlots;
    private OnTimeSlotClickListener listener;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot);
    }

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlots, OnTimeSlotClickListener listener) {
        this.context = context;
        this.timeSlots = timeSlots != null ? timeSlots : new ArrayList<>();
        this.listener = listener;
    }

    public void updateTimeSlots(List<TimeSlot> newTimeSlots) {
        this.timeSlots = newTimeSlots;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        String timeText = String.format("%tHh%tM", timeSlot.begin, timeSlot.begin);
        holder.timeSlotButton.setText(timeText);

        // Désactiver si complet
        if (timeSlot.getUsedWattage() >= timeSlot.maxWattage) {
            holder.timeSlotButton.setEnabled(false);
            holder.timeSlotButton.setBackgroundResource(R.drawable.day_background_empty);
        } else {
            holder.timeSlotButton.setEnabled(true);

            // Couleur en fonction du pourcentage d'utilisation
            double percentage = (double)timeSlot.getUsedWattage() / timeSlot.maxWattage * 100;
            if (percentage > 70) {
                holder.timeSlotButton.setBackgroundResource(R.drawable.day_background_red);
            } else if (percentage > 30) {
                holder.timeSlotButton.setBackgroundResource(R.drawable.day_background_orange);
            } else {
                holder.timeSlotButton.setBackgroundResource(R.drawable.day_background);
            }
        }

        holder.timeSlotButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimeSlotClick(timeSlot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        Button timeSlotButton;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotButton = itemView.findViewById(R.id.timeSlotButton);
        }
    }
}