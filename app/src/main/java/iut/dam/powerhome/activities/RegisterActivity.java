package iut.dam.powerhome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import iut.dam.powerhome.R;

public class RegisterActivity extends AppCompatActivity {
    private ImageView back;
    private Spinner spinnerPrefix;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // Expression régulière pour un e-mail

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser le Spinner pour les préfixes téléphoniques
        spinnerPrefix = findViewById(R.id.prefixeTel);
        String[] items = new String[]{"+33", "+44", "+34"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerPrefix.setAdapter(adapter);

        // Bouton de retour
        back = findViewById(R.id.retour);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextSurname = findViewById(R.id.editTextSurname);
        EditText editTextMail = findViewById(R.id.editTextMail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Spinner spinnerPrefix = findViewById(R.id.prefixeTel);
        EditText editTextPhone = findViewById(R.id.editTextPhone);

        Button btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs
                String firstName = editTextName.getText().toString().trim();
                String lastName = editTextSurname.getText().toString().trim();
                String email = editTextMail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String phonePrefix = spinnerPrefix.getSelectedItem().toString();
                String phoneNumber = editTextPhone.getText().toString().trim();
                String fullPhone = phonePrefix + phoneNumber; // Concaténer le préfixe et le numéro

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "E-mail invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendDataToServer(firstName, lastName, email, password, fullPhone);
            }
        });
    }

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    private void sendDataToServer(String firstName, String lastName, String email, String password, String phone) {
        String url = "http://192.168.1.67/ecopower/register.php";

        Ion.with(this)
                .load("POST", url)
                .setBodyParameter("firstname", firstName)
                .setBodyParameter("lastname", lastName)
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
                .setBodyParameter("phone_number", phone)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            // Parser la réponse JSON
                            JSONObject jsonResponse = new JSONObject(result);
                            String status = jsonResponse.getString("status");

                            // Traiter la réponse du serveur
                            if (status.equals("success")) {
                                Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Erreur de traitement de la réponse du serveur", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}