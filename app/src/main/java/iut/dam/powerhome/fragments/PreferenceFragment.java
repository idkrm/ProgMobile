package iut.dam.powerhome.fragments;

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
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import iut.dam.powerhome.R;
import iut.dam.powerhome.adapters.CalendarAdapter;
import iut.dam.powerhome.adapters.TimeSlotAdapter;

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

        // Ajoutez ce listener
        calendarAdapter.setOnDayClickListener((day, month, year) -> {
            // Afficher les créneaux horaires
            timeSlotsRecyclerView.setVisibility(View.VISIBLE);

            // Mettre à jour la date sélectionnée
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);
            timeSlotAdapter.setSelectedDate(selectedDate.getTime());
        });

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
        updateMonthYear();
    }

    private void setupTimeSlots() {
        timeSlotAdapter = new TimeSlotAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        timeSlotsRecyclerView.setLayoutManager(layoutManager);
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotsRecyclerView.setVisibility(View.GONE);
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
        timeSlotsRecyclerView.setVisibility(View.GONE); // Cache les time slots
        calendarAdapter.setSelectedDate(null); // Réinitialise la sélection
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        monthYearTextView.setText(sdf.format(currentCalendar.getTime()));
    }
}