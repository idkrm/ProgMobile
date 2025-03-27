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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import entities.Appliance;
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
    private List<Appliance> userAppliances = new ArrayList<>();

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

        calendarAdapter.setOnDayClickListener((day, month, year) -> {
            // Créer un Calendar pour la date sélectionnée
            Calendar selectedDateCal = Calendar.getInstance();
            selectedDateCal.set(year, month, day);

            // Créer un Calendar pour le mois affiché (sans le jour)
            Calendar currentMonthCal = Calendar.getInstance();
            currentMonthCal.setTime(currentCalendar.getTime());
            currentMonthCal.set(Calendar.DAY_OF_MONTH, 1);

            // Comparer année et mois
            if (selectedDateCal.get(Calendar.YEAR) == currentMonthCal.get(Calendar.YEAR) &&
                    selectedDateCal.get(Calendar.MONTH) == currentMonthCal.get(Calendar.MONTH)) {

                // Afficher seulement si c'est le mois courant
                timeSlotsRecyclerView.setVisibility(View.VISIBLE);

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);
                timeSlotAdapter.setSelectedDate(selectedDate.getTime());
            } else {
                // Cacher si c'est un autre mois
                timeSlotsRecyclerView.setVisibility(View.GONE);
            }
        });

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
        updateMonthYear();
    }

    private void setupTimeSlots() {
        timeSlotAdapter = new TimeSlotAdapter(getContext(), userAppliances); // Passer la liste des appareils
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
        timeSlotsRecyclerView.setVisibility(View.GONE);
        calendarAdapter.setSelectedDate(null);
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        monthYearTextView.setText(sdf.format(currentCalendar.getTime()));
    }
}