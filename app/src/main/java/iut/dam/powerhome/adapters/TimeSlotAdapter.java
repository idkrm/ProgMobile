package iut.dam.powerhome.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import iut.dam.powerhome.R;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    private Context context;
    private List<String> timeSlots = Arrays.asList("9h00", "11h00", "13h00", "15h00", "17h00", "19h00", "21h00", "23h00");

    public TimeSlotAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        holder.timeSlotButton.setText(timeSlots.get(position));
        holder.timeSlotButton.setOnClickListener(v -> {
            // Gérer le clic sur le créneau horaire
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