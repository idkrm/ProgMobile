package iut.dam.powerhome.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iut.dam.powerhome.R;

public class AppliancesActivity extends AppCompatActivity {
    private String firstName, lastName, email, password, phone;
    private LinearLayout mainLayout;
    private ImageView btnRetour;
    private Button btnAddAppliance, btnSignup;
    private Spinner floor;
    private List<View> applianceViews = new ArrayList<>();
    private boolean isLastApplianceFilled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_appliances);

        btnRetour = findViewById(R.id.retour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(AppliancesActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        floor = findViewById(R.id.floor);
        String[] items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        floor.setPrompt(getString(R.string.etage));
        floor.setAdapter(adapter);

        mainLayout = findViewById(R.id.main);
        btnAddAppliance = findViewById(R.id.btnAddAppliance);

        // Récupérer les données de RegisterActivity
        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstname");
        lastName = intent.getStringExtra("lastname");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        phone = intent.getStringExtra("phone");

        btnAddAppliance.setOnClickListener(v -> {
            if (isLastApplianceFilled) {
                addApplianceItem();
                isLastApplianceFilled = false;
                btnAddAppliance.setEnabled(false);
            }
        });

        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(v -> {
            // Vérifier que tous les appareils sont remplis
            if (!areAllAppliancesFilled()) {
                Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
                return;
            }
            sendDataToServer();
        });
    }

    private void addApplianceItem() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View applianceView = inflater.inflate(R.layout.item_appliance, mainLayout, false);

        EditText editTextRef = applianceView.findViewById(R.id.editTextRef);
        EditText editTextWattage = applianceView.findViewById(R.id.editTextWattage);
        ImageView imageViewRemove = applianceView.findViewById(R.id.imageViewRemove);
        ImageView imageViewAppliance = applianceView.findViewById(R.id.imageViewAppliance);

        Spinner applianceImg = applianceView.findViewById(R.id.applianceSpinner);
        String[] items = new String[]{"Aspirateur", "Climatiseur", "Machine à laver", "Fer à repasser"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        applianceImg.setAdapter(adapter);

        applianceImg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (applianceImg.getSelectedItem().toString()){
                    case "Aspirateur":
                        imageViewAppliance.setImageResource(R.drawable.aspi);
                        break;
                    case "Climatiseur":
                        imageViewAppliance.setImageResource(R.drawable.climatiseur);
                        break;
                    case "Machine à laver":
                        imageViewAppliance.setImageResource(R.drawable.machine);
                        break;
                    case "Fer à repasser":
                        imageViewAppliance.setImageResource(R.drawable.repasser);
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Ajouter un TextWatcher pour vérifier si les champs sont remplis
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean isFilled = !editTextRef.getText().toString().isEmpty()
                        && !editTextWattage.getText().toString().isEmpty();

                if (isFilled) {
                    isLastApplianceFilled = true;
                    btnAddAppliance.setEnabled(true);
                }
            }
        };

        editTextRef.addTextChangedListener(textWatcher);
        editTextWattage.addTextChangedListener(textWatcher);

        imageViewRemove.setOnClickListener(v -> {
            LinearLayout appliancesContainer = findViewById(R.id.appliancesContainer);
            appliancesContainer.removeView(applianceView);
            applianceViews.remove(applianceView);

            if (applianceViews.isEmpty()) {
                isLastApplianceFilled = true;
                btnAddAppliance.setEnabled(true);
            }
        });

        // Modifiez cette partie pour ajouter dans le conteneur :
        LinearLayout appliancesContainer = findViewById(R.id.appliancesContainer);
        appliancesContainer.addView(applianceView);

        applianceViews.add(applianceView);

        // Désactiver le bouton après ajout
        isLastApplianceFilled = false;
        btnAddAppliance.setEnabled(false);

    }

    private boolean areAllAppliancesFilled() {
        EditText editTextArea = findViewById(R.id.editTextArea);
        if (editTextArea.getText().toString().isEmpty())
            return false;

        for (View applianceView : applianceViews) {
            EditText editTextRef = applianceView.findViewById(R.id.editTextRef);
            EditText editTextWattage = applianceView.findViewById(R.id.editTextWattage);

            if (editTextRef.getText().toString().isEmpty() ||
                    editTextWattage.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void sendDataToServer() {
        // Récupérer les données du logement (depuis les champs de l'interface)
        Spinner floorSpinner = findViewById(R.id.floor);
        EditText editTextArea = findViewById(R.id.editTextArea);

        String floor = floorSpinner.getSelectedItem().toString();
        String area = editTextArea.getText().toString();

        // Préparer la liste des appareils
        JSONArray appliancesArray = new JSONArray();

        for (View applianceView : applianceViews) {
            Spinner applianceSpinner = applianceView.findViewById(R.id.applianceSpinner);
            EditText editTextRef = applianceView.findViewById(R.id.editTextRef);
            EditText editTextWattage = applianceView.findViewById(R.id.editTextWattage);

            JSONObject applianceObj = new JSONObject();
            try {
                applianceObj.put("type", applianceSpinner.getSelectedItem().toString());
                applianceObj.put("reference", editTextRef.getText().toString());
                applianceObj.put("wattage", editTextWattage.getText().toString());
                appliancesArray.put(applianceObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String url = "http://192.168.1.67/ecopower/register.php";

        Ion.with(this)
                .load("POST", url)
                .setBodyParameter("firstname", firstName)
                .setBodyParameter("lastname", lastName)
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
                .setBodyParameter("phone_number", phone)
                .setBodyParameter("floor", floor)
                .setBodyParameter("area", area)
                .setBodyParameter("appliances", appliancesArray.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(AppliancesActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String status = jsonResponse.getString("status");
                            Log.d("API_STATUS", "Status: " + status);

                            if (status.equals("success")) {
                                // Stocker les informations d'inscription
                                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();

                                Toast.makeText(AppliancesActivity.this, R.string.successful_reg, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AppliancesActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = "Erreur inconnue";
                                try {
                                    errorMessage = jsonResponse.getString("message");
                                    Log.e("API_ERROR", "Message d'erreur: " + errorMessage);
                                } catch (JSONException je) {
                                    Log.e("API_JSON_ERROR", "Champ 'message' manquant dans la réponse", je);
                                }
                                Toast.makeText(AppliancesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            Log.e("API_JSON_PARSE", "Erreur d'analyse JSON. Réponse: " + result, ex);
                            Toast.makeText(AppliancesActivity.this,
                                    "Erreur de traitement de la réponse. Voir les logs.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}