package iut.dam.powerhome.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import entities.Appliance;
import entities.TimeSlot;
import iut.dam.powerhome.R;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    private Context context;
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private Date selectedDate;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);

    public TimeSlotAdapter(Context context) {
        this.context = context;
        initTimeSlots();
    }

    private void initTimeSlots() {
        try {
            timeSlots.clear();
            timeSlots.add(new TimeSlot(1, timeFormat.parse("00:00"), timeFormat.parse("01:59"), 250));
            timeSlots.add(new TimeSlot(2, timeFormat.parse("02:00"), timeFormat.parse("03:59"), 250));
            timeSlots.add(new TimeSlot(3, timeFormat.parse("04:00"), timeFormat.parse("05:59"), 250));
            timeSlots.add(new TimeSlot(4, timeFormat.parse("06:00"), timeFormat.parse("07:59"), 250));
            timeSlots.add(new TimeSlot(5, timeFormat.parse("08:00"), timeFormat.parse("09:59"), 250));
            timeSlots.add(new TimeSlot(6, timeFormat.parse("10:00"), timeFormat.parse("11:59"), 250));
            timeSlots.add(new TimeSlot(7, timeFormat.parse("12:00"), timeFormat.parse("13:59"), 250));
            timeSlots.add(new TimeSlot(8, timeFormat.parse("14:00"), timeFormat.parse("15:59"), 250));
            timeSlots.add(new TimeSlot(9, timeFormat.parse("16:00"), timeFormat.parse("17:59"), 250));
            timeSlots.add(new TimeSlot(10, timeFormat.parse("18:00"), timeFormat.parse("19:59"), 250));
            timeSlots.add(new TimeSlot(11, timeFormat.parse("20:00"), timeFormat.parse("21:59"), 250));
            timeSlots.add(new TimeSlot(12, timeFormat.parse("22:00"), timeFormat.parse("23:59"), 250));

        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        String timeText = timeFormat.format(timeSlot.begin) + " - " + timeFormat.format(timeSlot.end);
        holder.timeSlotButton.setText(timeText);

        holder.timeSlotButton.setOnClickListener(v -> {
            showReservationDialog(timeSlot, timeText);
        });
    }

    private void showReservationDialog(TimeSlot timeSlot, String timeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation de réservation");

        // msg avec date et créneau
        String message = "Voulez-vous réserver le créneau " + timeText;
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            message += " le " + dateFormat.format(selectedDate);
        }
        message += "?";

        builder.setMessage(message);

        builder.setPositiveButton("Oui", (dialog, which) -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", null);
            String password = sharedPreferences.getString("password", null);

            if (email != null && password != null) {
                fetchUserAppliances(timeSlot, email, password);
            }
        });

        builder.setNegativeButton("Non", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchUserAppliances(TimeSlot timeSlot, String email, String password) {
        // recup les appliances du user
        String url = "http://10.0.2.2/ecopower/getUserAppliances.php";

        Ion.with(context)
                .load(url)
                .setBodyParameter("email", email.trim())
                .setBodyParameter("password", password.trim())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            if (e != null || result == null) {
                                throw new Exception(e != null ? e.getMessage() : "Réponse vide");
                            }

                            JSONObject jsonResponse = new JSONObject(result);
                            Log.d("API_RESPONSE", jsonResponse.toString(2));

                            if (!jsonResponse.has("appliances")) {
                                throw new Exception("Aucun appareil trouvé dans la réponse");
                            }

                            JSONArray appliancesArray = jsonResponse.getJSONArray("appliances");
                            List<Appliance> applianceList = new ArrayList<>();

                            if (appliancesArray.length() == 0) {
                                Toast.makeText(context, "Aucun appareil disponible", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int i = 0; i < appliancesArray.length(); i++) {
                                JSONObject appliance = appliancesArray.getJSONObject(i);
                                applianceList.add(new Appliance(
                                        appliance.getInt("id"),
                                        appliance.getString("name"),
                                        appliance.getString("reference"),
                                        appliance.getInt("wattage")
                                ));
                            }

                            // si tout est ok, autre boite de dialogue pour choix d'appliances
                            applianceSelectionDialog(timeSlot, applianceList);

                        } catch (JSONException ex) {
                            Log.e("JSON_ERROR", "Erreur parsing JSON: " + ex.getMessage());
                            Toast.makeText(context, "Erreur de format de données: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Log.e("API_ERROR", ex.getMessage());
                            Toast.makeText(context, "Erreur: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void applianceSelectionDialog(TimeSlot timeSlot, List<Appliance> appliances) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sélection des appareils");

        String[] applianceNames = new String[appliances.size()];
        boolean[] checkedItems = new boolean[appliances.size()];
        List<Appliance> selectedAppliances = new ArrayList<>();

        for (int i = 0; i < appliances.size(); i++) {
            Appliance appliance = appliances.get(i);
            applianceNames[i] = appliance.getName() + " (" + appliance.getWattage() + "W)";
        }

        builder.setMultiChoiceItems(applianceNames, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedAppliances.add(appliances.get(which));
            } else {
                selectedAppliances.remove(appliances.get(which));
            }
        });

        builder.setPositiveButton("Confirmer", (dialog, which) -> {
            if (!selectedAppliances.isEmpty()) {
                // fais la reservation
                makeReservation(timeSlot, selectedAppliances);
            } else {
                Toast.makeText(context, "Veuillez sélectionner au moins un appareil", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void makeReservation(TimeSlot timeSlot, List<Appliance> appliances) {
        int totalWattage = 0;
        for (Appliance appliance : appliances) {
            totalWattage += appliance.getWattage();
        }

        if (totalWattage > timeSlot.getAvailableWattage()) {
            Toast.makeText(context, "Capacité dépassée pour ce créneau", Toast.LENGTH_SHORT).show();
            return;
        }

        sendReservationToServer(timeSlot, appliances);
    }

    private void sendReservationToServer(TimeSlot timeSlot, List<Appliance> appliances) {
        String url = "http://10.0.2.2/ecopower/addReservation.php";

        // Convertir les appliances en JSONArray
        JSONArray appliancesArray = new JSONArray();
        for (Appliance appliance : appliances) {
            JSONObject appJson = new JSONObject();
            try {
                appJson.put("id", appliance.getId());
                appJson.put("wattage", appliance.getWattage());
                appliancesArray.put(appJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Récupérer l'email de l'utilisateur
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        Ion.with(context)
                .load(url)
                .setBodyParameter("email", email)
                .setBodyParameter("time_slot_id", String.valueOf(timeSlot.id))
                .setBodyParameter("appliances", appliancesArray.toString())
                .setBodyParameter("start_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeSlot.begin))
                .setBodyParameter("end_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeSlot.end))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(context, "Erreur réseau: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            // Vérification basique avant parsing JSON
                            if (result == null || result.isEmpty()) {
                                throw new JSONException("Réponse vide");
                            }

                            // Debug: afficher la réponse brute
                            Log.d("API_RESPONSE", "Réponse: " + result);

                            JSONObject response = new JSONObject(result);

                            if (!response.has("status")) {
                                throw new JSONException("Champ 'status' manquant");
                            }

                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                Toast.makeText(context, "Réservation enregistrée avec succès!", Toast.LENGTH_SHORT).show();

                                // Rafraîchir l'interface si nécessaire
                                notifyDataSetChanged();
                            } else {
                                String message = response.has("message") ?
                                        response.getString("message") : "Erreur inconnue";
                                Toast.makeText(context, "Erreur: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(context,
                                    "Format de réponse invalide: " + ex.getMessage() + "\nRéponse: " + result,
                                    Toast.LENGTH_LONG).show();
                            Log.e("JSON_ERROR", "Erreur parsing: " + result, ex);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        notifyDataSetChanged();
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        Button timeSlotButton;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotButton = itemView.findViewById(R.id.timeSlotButton);
        }
    }
}