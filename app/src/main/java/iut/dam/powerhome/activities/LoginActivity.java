package iut.dam.powerhome.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import iut.dam.powerhome.R;
import entities.User;
import iut.dam.powerhome.database.JsonDatabase;
import iut.dam.powerhome.utils.PasswordUtils;

public class LoginActivity extends AppCompatActivity {
    private Button creerCompte;
    private Button connecter;
    private EditText mail;
    private EditText mdp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        creerCompte = findViewById(R.id.creerCompte);
        creerCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        connecter = findViewById(R.id.connexion);
        mail = findViewById(R.id.mail);
        mdp = findViewById(R.id.mdp);

        connecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mail.getText().toString().trim();
                String password = mdp.getText().toString().trim();

                // Valider les champs
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lire les utilisateurs depuis le fichier JSON
                JsonDatabase jsonDatabase = new JsonDatabase(LoginActivity.this);
                List<User> users = jsonDatabase.readUsers();

                // Vérifier si l'utilisateur existe
                boolean isAuthenticated = false;
                for (User user : users) {
                    if (user.getEmail().equalsIgnoreCase(email) && PasswordUtils.checkPassword(password, user.getPassword())) {
                        isAuthenticated = true;
                        break;
                    }
                }

                // Rediriger l'utilisateur ou afficher un message d'erreur
                if (isAuthenticated) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Mail", email);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Erreur, identifiant ou mot de passe incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}