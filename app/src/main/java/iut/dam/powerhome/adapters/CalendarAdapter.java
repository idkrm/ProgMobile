package iut.dam.powerhome.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iut.dam.powerhome.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private Context context;
    private Calendar calendar;
    private int selectedPosition = -1;
    private int startDayOfWeek;
    private int daysInMonth;
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(int day);
    }

    public CalendarAdapter(Context context, Calendar calendar, OnDayClickListener listener) {
        this.context = context;
        this.listener = listener;
        setCalendar(calendar);
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.calendar.set(Calendar.DAY_OF_MONTH, 1);
        this.startDayOfWeek = getFrenchDayOfWeek(calendar);
        this.daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        notifyDataSetChanged();
    }

    private int getFrenchDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek + 5) % 7 + 1;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        if (position < startDayOfWeek - 1 || position >= startDayOfWeek - 1 + daysInMonth) {
            holder.dayTextView.setText("");
            holder.itemView.setBackgroundResource(R.drawable.day_background_empty);
            holder.itemView.setClickable(false);
        } else {
            int day = position - (startDayOfWeek - 2);
            holder.dayTextView.setText(String.valueOf(day));
            holder.itemView.setClickable(true);

            // Appliquer la couleur en fonction du wattage
            if (dayWattageMap.containsKey(day)) {
                int wattage = dayWattageMap.get(day);
                double percentage = (double)wattage / 5000 * 100; // 5000W = max théorique

                if (percentage > 70) {
                    holder.itemView.setBackgroundResource(R.drawable.day_background_red);
                } else if (percentage > 30) {
                    holder.itemView.setBackgroundResource(R.drawable.day_background_orange);
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.day_background);
                }
            } else {
                holder.itemView.setBackgroundResource(R.drawable.day_background);
            }

            holder.itemView.setOnClickListener(v -> {
                selectedPosition = position;
                if (listener != null) {
                    listener.onDayClick(day);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 42;
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
    private Map<Integer, Integer> dayWattageMap = new HashMap<>();

    public void setDayWattageMap(Map<Integer, Integer> dayWattageMap) {
        this.dayWattageMap = dayWattageMap;
        notifyDataSetChanged();
    }


}