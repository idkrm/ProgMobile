package iut.dam.powerhome.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import iut.dam.powerhome.R;

public class PreferenceFragment extends Fragment {

    private RecyclerView calendarRecyclerView, timeSlotsRecyclerView;
    private TextView monthYearTextView;
    private Button previousMonthButton, nextMonthButton;
    private CalendarAdapter calendarAdapter;
    private TimeSlotAdapter timeSlotAdapter;
    private Calendar currentCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference, container, false);

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        timeSlotsRecyclerView = view.findViewById(R.id.timeSlotsRecyclerView);
        monthYearTextView = view.findViewById(R.id.monthYearTextView);
        previousMonthButton = view.findViewById(R.id.previousMonthButton);
        nextMonthButton = view.findViewById(R.id.nextMonthButton);

        setupCalendar();
        setupTimeSlots();
        setupMonthNavigation();

        return view;
    }

    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(getContext(), currentCalendar);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);

        updateMonthYear();
    }

    private void setupTimeSlots() {
        timeSlotAdapter = new TimeSlotAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3); // 3 colonnes
        timeSlotsRecyclerView.setLayoutManager(layoutManager);
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);

        // Centrer les éléments dans le RecyclerView
        timeSlotsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int totalItems = parent.getAdapter().getItemCount();

                // Centrer les éléments
                if (position % 3 == 0) { // Premier élément de la ligne
                    outRect.left = (parent.getWidth() - (3 * view.getWidth())) / 2;
                }
                if (position % 3 == 2) { // Dernier élément de la ligne
                    outRect.right = (parent.getWidth() - (3 * view.getWidth())) / 2;
                }
            }
        });
    }

    private void setupMonthNavigation() {
        previousMonthButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void updateCalendar() {
        calendarAdapter.setCalendar(currentCalendar);
        updateMonthYear();
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH); // Mois en français
        monthYearTextView.setText(sdf.format(currentCalendar.getTime()));
    }

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        private Context context;
        private Calendar calendar;
        private int selectedPosition = -1;
        private int startDayOfWeek; // Jour de la semaine du premier jour du mois (1 = lundi, 2 = mardi, etc.)
        private int daysInMonth; // Nombre de jours dans le mois

        public CalendarAdapter(Context context, Calendar calendar) {
            this.context = context;
            this.calendar = calendar;
            this.calendar.set(Calendar.DAY_OF_MONTH, 1); // Réinitialiser au premier jour du mois
            this.startDayOfWeek = getFrenchDayOfWeek(calendar); // Jour de la semaine du premier jour (lundi = 1)
            this.daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Nombre de jours dans le mois
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
            this.calendar.set(Calendar.DAY_OF_MONTH, 1); // Réinitialiser au premier jour du mois
            this.startDayOfWeek = getFrenchDayOfWeek(calendar); // Jour de la semaine du premier jour (lundi = 1)
            this.daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            notifyDataSetChanged();
        }

        // Convertir le jour de la semaine en format français (lundi = 1, dimanche = 7)
        private int getFrenchDayOfWeek(Calendar calendar) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = dimanche, 2 = lundi, etc.
            return (dayOfWeek + 5) % 7 + 1; // Convertir en lundi = 1, dimanche = 7
        }

        @NonNull
        @Override
        public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
            return new CalendarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if (position < startDayOfWeek - 1 || position >= startDayOfWeek - 1 + daysInMonth) {
                // Jour vide (avant le premier jour du mois ou après le dernier jour du mois)
                holder.dayTextView.setText("");
                holder.itemView.setActivated(false);
                holder.itemView.setSelected(false);
                holder.itemView.setClickable(false);
                holder.itemView.setBackgroundResource(R.drawable.day_background_empty); // Fond transparent
            } else {
                // Jour réel
                int day = position - (startDayOfWeek - 2); // Calcul du jour correct
                holder.dayTextView.setText(String.valueOf(day));
                holder.itemView.setActivated(position == selectedPosition);
                holder.itemView.setClickable(true);
                holder.itemView.setBackgroundResource(R.drawable.day_background); // Fond coloré par défaut
                holder.itemView.setOnClickListener(v -> {
                    selectedPosition = position;
                    notifyDataSetChanged();
                    showTimeSlots();
                });
            }
        }

        @Override
        public int getItemCount() {
            return 42; // 6 semaines maximum (7 jours * 6 semaines)
        }

         class CalendarViewHolder extends RecyclerView.ViewHolder {
            TextView dayTextView;

            public CalendarViewHolder(@NonNull View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.dayTextView);
            }
        }
    }

    private class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
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
            holder.timeSlotButton.setOnClickListener(v -> showBookingDialog(timeSlots.get(position)));
        }

        @Override
        public int getItemCount() {
            return timeSlots.size();
        }

        class TimeSlotViewHolder extends RecyclerView.ViewHolder {
            Button timeSlotButton;

            public TimeSlotViewHolder(@NonNull View itemView) {
                super(itemView);
                timeSlotButton = itemView.findViewById(R.id.timeSlotButton);
            }
        }
    }

    private void showTimeSlots() {
        timeSlotsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showBookingDialog(String timeSlot) {
        new AlertDialog.Builder(getContext())
                .setTitle("Réservation")
                .setMessage("Voulez vous réserver ce créneau " + timeSlot + "?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    // Handle booking
                })
                .setNegativeButton("Non", null)
                .show();
    }
}