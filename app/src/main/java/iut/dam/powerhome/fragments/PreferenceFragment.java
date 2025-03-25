package iut.dam.powerhome.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import entities.Appliance;
import entities.Booking;
import entities.TimeSlot;
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
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private List<Appliance> userAppliances = new ArrayList<>();
    private int selectedDay = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference, container, false);

        // Initialisation des vues
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        timeSlotsRecyclerView = view.findViewById(R.id.timeSlotsRecyclerView);
        monthYearTextView = view.findViewById(R.id.monthYearTextView);
        previousMonthButton = view.findViewById(R.id.previousMonthButton);
        nextMonthButton = view.findViewById(R.id.nextMonthButton);

        // Charger les données
        loadSampleData();

        // Configurer les adaptateurs
        setupCalendar();
        setupTimeSlots();
        setupMonthNavigation();

        return view;
    }

    private void loadSampleData() {
        // Exemple de données
        userAppliances.add(new Appliance(1, "Aspirateur", "MOD123", 800));
        userAppliances.add(new Appliance(2, "Machine à laver", "MOD456", 2000));

        // Création de créneaux horaires pour aujourd'hui + demain
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        // Créneaux pour aujourd'hui (utilisation à 50%)
        for (int i = 0; i < 8; i++) {
            cal.set(Calendar.DAY_OF_MONTH, currentDay);
            cal.set(Calendar.HOUR_OF_DAY, 9 + i*2);
            cal.set(Calendar.MINUTE, 0);

            TimeSlot slot = new TimeSlot(i, cal.getTime(),
                    new Date(cal.getTimeInMillis() + 2*60*60*1000), 3000);

            if (i == 2 || i == 3) {
                Booking booking = new Booking();
                booking.appliance = userAppliances.get(0); // Aspirateur 800W
                slot.bookings.add(booking);
            }

            timeSlots.add(slot);
        }

        // Créneaux pour demain (utilisation à 80%)
        for (int i = 0; i < 8; i++) {
            cal.set(Calendar.DAY_OF_MONTH, currentDay + 1);
            cal.set(Calendar.HOUR_OF_DAY, 9 + i*2);
            cal.set(Calendar.MINUTE, 0);

            TimeSlot slot = new TimeSlot(8 + i, cal.getTime(),
                    new Date(cal.getTimeInMillis() + 2*60*60*1000), 3000);

            if (i >= 1 && i <= 4) {
                Booking booking = new Booking();
                booking.appliance = userAppliances.get(1); // Machine 2000W
                slot.bookings.add(booking);
            }

            timeSlots.add(slot);
        }
    }

    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(getContext(), currentCalendar, new CalendarAdapter.OnDayClickListener() {
            @Override
            public void onDayClick(int day) {
                selectedDay = day;
                updateTimeSlotsForSelectedDay();
            }
        });

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
        updateMonthYear();
    }

    private void setupTimeSlots() {
        timeSlotAdapter = new TimeSlotAdapter(getContext(), new ArrayList<>(), new TimeSlotAdapter.OnTimeSlotClickListener() {
            @Override
            public void onTimeSlotClick(TimeSlot timeSlot) {
                showBookingDialog(timeSlot);
            }
        });

        timeSlotsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);
    }

    private void updateTimeSlotsForSelectedDay() {
        List<TimeSlot> filteredSlots = new ArrayList<>();

        if (selectedDay != -1) {
            for (TimeSlot slot : timeSlots) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(slot.begin);
                if (cal.get(Calendar.DAY_OF_MONTH) == selectedDay) {
                    filteredSlots.add(slot);
                }
            }
        }

        timeSlotAdapter.updateTimeSlots(filteredSlots);
        timeSlotsRecyclerView.setVisibility(View.VISIBLE);
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
        selectedDay = -1;
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
        monthYearTextView.setText(sdf.format(currentCalendar.getTime()));
    }

    private void showBookingDialog(TimeSlot timeSlot) {
        int availableWattage = timeSlot.maxWattage - timeSlot.getUsedWattage();
        String status;

        if (availableWattage <= 0) {
            status = "COMPLET";
        } else {
            double percentage = (double)timeSlot.getUsedWattage() / timeSlot.maxWattage * 100;
            status = String.format("%.0f%% utilisé", percentage);
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Réservation à " + String.format("%tHh%tM", timeSlot.begin, timeSlot.begin))
                .setMessage("Statut: " + status + "\nCapacité disponible: " + availableWattage + "W")
                .setPositiveButton("Réserver", (dialog, which) -> {
                    if (availableWattage > 0) {
                        showApplianceSelectionDialog(timeSlot);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showApplianceSelectionDialog(TimeSlot timeSlot) {
        CharSequence[] applianceNames = new CharSequence[userAppliances.size()];
        boolean[] checkedItems = new boolean[userAppliances.size()];
        List<Appliance> selectedAppliances = new ArrayList<>();

        for (int i = 0; i < userAppliances.size(); i++) {
            applianceNames[i] = userAppliances.get(i).getName() + " (" +
                    userAppliances.get(i).getWattage() + "W)";
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Choisissez les appareils")
                .setMultiChoiceItems(applianceNames, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedAppliances.add(userAppliances.get(which));
                    } else {
                        selectedAppliances.remove(userAppliances.get(which));
                    }
                })
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    bookTimeSlot(timeSlot, selectedAppliances);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void bookTimeSlot(TimeSlot timeSlot, List<Appliance> appliances) {
        int totalWattage = 0;
        for (Appliance appliance : appliances) {
            totalWattage += appliance.getWattage();
        }

        if (timeSlot.getUsedWattage() + totalWattage > timeSlot.maxWattage) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Capacité dépassée")
                    .setMessage("La réservation dépasse la capacité maximale du créneau.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Effectuer les réservations
        for (Appliance appliance : appliances) {
            Booking booking = new Booking();
            booking.appliance = appliance;
            booking.timeSlot = timeSlot;
            booking.bookedAt = new Date();
            timeSlot.bookings.add(booking);
        }

        // Mettre à jour l'affichage
        updateTimeSlotsForSelectedDay();
        calendarAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "Réservation effectuée", Toast.LENGTH_SHORT).show();
    }
}