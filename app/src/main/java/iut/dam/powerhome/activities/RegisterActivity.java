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

import iut.dam.powerhome.database.DataBaseHelper;
import iut.dam.powerhome.utils.PasswordUtils;
import iut.dam.powerhome.R;
import iut.dam.powerhome.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {
    private ImageView back;
    private EditText editTextName, editTextSurname, editTextEmail, editTextPassword, editTextPhone;
    private Spinner spinnerPrefix;
    private Button buttonSignUp;
    private DataBaseHelper dbHelper;

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


        spinnerPrefix = findViewById(R.id.prefixeTel);
        String[] items = new String[]{"+33", "+44", "+34"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerPrefix.setAdapter(adapter);

        back=findViewById(R.id.retour);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        // Initialiser les vues
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextEmail = findViewById(R.id.editTextMail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSignUp = findViewById(R.id.btnSignup);

        // Initialiser DatabaseHelper
        dbHelper = new DataBaseHelper(this);

        // Gérer le clic sur le bouton "Sign Up"
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs
                String name = editTextName.getText().toString().trim();
                String surname = editTextSurname.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String phonePrefix = spinnerPrefix.getSelectedItem().toString(); // Préfixe téléphonique
                String phone = editTextPhone.getText().toString().trim();
                String fullPhone = phonePrefix + phone; // Concaténer le préfixe et le numéro

                // Valider les champs (optionnel)
                if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || fullPhone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ValidationUtils.isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "E-mail invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.isEmailExists(email)) {
                    Toast.makeText(RegisterActivity.this, "Cet e-mail est déjà utilisé", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!ValidationUtils.isValidPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "Le mot de passe doit contenir au moins 8 caractères, une majuscule et un chiffre", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!ValidationUtils.isValidPhone(phone)) {
                    Toast.makeText(RegisterActivity.this, "Numéro de téléphone invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crypte le mot de passe
                String hashedPassword = PasswordUtils.hashPassword(password);
                // Insérer l'utilisateur dans la base de données
                long userId = dbHelper.addUser(name, surname, email, hashedPassword, fullPhone);

                if (userId != -1) {
                    Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}