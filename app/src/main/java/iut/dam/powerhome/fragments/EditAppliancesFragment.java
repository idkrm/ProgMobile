package iut.dam.powerhome.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import entities.Appliance;
import iut.dam.powerhome.R;

public class EditAppliancesFragment extends Fragment {
    private LinearLayout appliancesContainer;
    private Button btnAddAppliance, btnSave, btnBack;
    private List<Appliance> userAppliances = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_appliances, container, false);

        appliancesContainer = view.findViewById(R.id.editAppliancesContainer);
        btnAddAppliance = view.findViewById(R.id.btnAddAppliance);
        btnSave = view.findViewById(R.id.btnSave);

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonHabitatFragment fragment = new MonHabitatFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentFL, fragment);
                transaction.commit();
            }
        });

        // Charger les appareils existants
        loadUserAppliances();

        btnAddAppliance.setOnClickListener(v -> addNewApplianceItem());
        btnSave.setOnClickListener(v -> saveAppliances());

        return view;
    }

    private void loadUserAppliances() {
        // Récupérer les données depuis SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        if (!email.isEmpty() && !password.isEmpty()) {
            fetchUserAppliances(email, password);
        }
    }

    private void fetchUserAppliances(String email, String password) {
        String url = "http://192.168.1.67/ecopower/getUserData.php";

        Ion.with(this)
                .load(url)
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONObject userData = jsonResponse.getJSONObject("data");

                                if (userData.has("appliances")) {
                                    JSONArray appliances = userData.getJSONArray("appliances");
                                    for (int i = 0; i < appliances.length(); i++) {
                                        JSONObject appliance = appliances.getJSONObject(i);
                                        Appliance app = new Appliance(
                                                appliance.getString("name"),
                                                appliance.getString("reference"),
                                                appliance.getInt("wattage")
                                        );
                                        userAppliances.add(app);
                                        addApplianceItem(app);
                                    }
                                }
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void addApplianceItem(Appliance appliance) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View applianceView = inflater.inflate(R.layout.item_appliance, appliancesContainer, false);

        // Remplir les champs avec les données existantes
        Spinner applianceSpinner = applianceView.findViewById(R.id.applianceSpinner);
        EditText editTextRef = applianceView.findViewById(R.id.editTextRef);
        EditText editTextWattage = applianceView.findViewById(R.id.editTextWattage);
        ImageView imageViewAppliance = applianceView.findViewById(R.id.imageViewAppliance);

        // Configurer le Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.appliance_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        applianceSpinner.setAdapter(adapter);

        // Sélectionner le bon type dans le Spinner
        int position = adapter.getPosition(appliance.getName());
        applianceSpinner.setSelection(position);

        // Remplir les autres champs
        editTextRef.setText(appliance.getReference());
        editTextWattage.setText(String.valueOf(appliance.getWattage()));

        // Changer l'image selon le type
        setApplianceImage(imageViewAppliance, appliance.getName());

        // Gérer le changement de type
        applianceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                setApplianceImage(imageViewAppliance, selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Bouton de suppression
        ImageView btnRemove = applianceView.findViewById(R.id.imageViewRemove);
        btnRemove.setOnClickListener(v -> {
            appliancesContainer.removeView(applianceView);
            userAppliances.remove(appliance);
        });

        appliancesContainer.addView(applianceView);
    }

    private void addNewApplianceItem() {
        Appliance newAppliance = new Appliance("Aspirateur", "", 0);
        userAppliances.add(newAppliance);
        addApplianceItem(newAppliance);
    }

    private void setApplianceImage(ImageView imageView, String applianceType) {
        switch (applianceType) {
            case "Aspirateur":
                imageView.setImageResource(R.drawable.aspi);
                break;
            case "Climatiseur":
                imageView.setImageResource(R.drawable.climatiseur);
                break;
            case "Machine à laver":
                imageView.setImageResource(R.drawable.machine);
                break;
            case "Fer à repasser":
                imageView.setImageResource(R.drawable.repasser);
                break;
            default:
                imageView.setImageResource(R.drawable.aspi);
        }
    }

    private void saveAppliances() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Session invalide, veuillez vous reconnecter", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray appliancesArray = new JSONArray();
        boolean hasErrors = false;

        // Construction du JSON
        for (int i = 0; i < appliancesContainer.getChildCount(); i++) {
            View applianceView = appliancesContainer.getChildAt(i);

            Spinner spinner = applianceView.findViewById(R.id.applianceSpinner);
            EditText ref = applianceView.findViewById(R.id.editTextRef);
            EditText wattage = applianceView.findViewById(R.id.editTextWattage);

            if (ref.getText().toString().isEmpty() || wattage.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                hasErrors = true;
                break;
            }

            try {
                JSONObject appliance = new JSONObject();
                appliance.put("type", spinner.getSelectedItem().toString());
                appliance.put("reference", ref.getText().toString());
                appliance.put("wattage", Integer.parseInt(wattage.getText().toString()));
                appliancesArray.put(appliance);
            } catch (JSONException | NumberFormatException e) {
                hasErrors = true;
            }
        }

        if (hasErrors || appliancesArray.length() == 0) {
            Toast.makeText(getContext(), "Erreur dans les données des appareils", Toast.LENGTH_SHORT).show();
            return;
        }

        // Envoi au serveur avec débogage amélioré
        String url = "http://192.168.1.67/ecopower/updateAppliances.php";

        Ion.with(this)
                .load(url)
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
                .setBodyParameter("appliances", appliancesArray.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur de connexion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            // Vérification basique avant parsing JSON
                            if (result == null || result.isEmpty()) {
                                throw new JSONException("Réponse vide");
                            }

                            JSONObject response = new JSONObject(result);

                            if (!response.has("status")) {
                                throw new JSONException("Champ 'status' manquant");
                            }

                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                MonHabitatFragment fragment = new MonHabitatFragment();
                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.contentFL, fragment);
                                transaction.commit();

                                Toast.makeText(getContext(), "Mise à jour réussie!", Toast.LENGTH_SHORT).show();
                                loadUserAppliances();
                            } else {
                                String message = response.has("message") ?
                                        response.getString("message") : "Erreur inconnue";
                                Toast.makeText(getContext(), "Erreur: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(getContext(),
                                    "Format de réponse invalide: " + ex.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}