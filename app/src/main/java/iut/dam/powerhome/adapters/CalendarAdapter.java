package iut.dam.powerhome.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import iut.dam.powerhome.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private Context context;
    private Calendar calendar;
    private int selectedPosition = -1;
    private int startDayOfWeek;
    private int daysInMonth;
    private OnDayClickListener dayClickListener;

    public CalendarAdapter(Context context, Calendar calendar) {
        this.context = context;
        setCalendar(calendar);
    }
    public interface OnDayClickListener {
        void onDayClick(int day, int month, int year);
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.dayClickListener = listener;
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
            holder.itemView.setActivated(false);
            holder.itemView.setSelected(false);
            holder.itemView.setClickable(false);
            holder.itemView.setBackgroundResource(R.drawable.day_background_empty);
        } else {
            int day = position - (startDayOfWeek - 2);
            holder.dayTextView.setText(String.valueOf(day));
            holder.itemView.setActivated(position == selectedPosition);
            holder.itemView.setClickable(true);
            holder.itemView.setBackgroundResource(R.drawable.day_background);
            holder.itemView.setOnClickListener(v -> {
                selectedPosition = position;
                notifyDataSetChanged();
            });
        }
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();

            // Ajoutez ceci pour notifier le fragment
            if (dayClickListener != null) {
                int day = position - (startDayOfWeek - 2);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                dayClickListener.onDayClick(day, month, year);
            }
        });
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
}