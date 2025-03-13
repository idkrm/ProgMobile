package iut.dam.powerhome;

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

public class LoginActivity extends AppCompatActivity {
    private Button creerCompte;
    private Button connecter;
    private EditText mail;
    private EditText mdp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        creerCompte=findViewById(R.id.creerCompte);
        creerCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        connecter=findViewById(R.id.connexion);
        mail=findViewById(R.id.mail);
        mdp=findViewById(R.id.mdp);

        connecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this,MainActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Mail", mail.getText().toString());
                bundle.putSerializable("Mdp", mdp.getText().toString());
                intent.putExtras(bundle);

                if(mail.getText().toString().equals("abcd") && mdp.getText().toString().equals("EFGH")){
                        startActivity(intent);
                }
                else{
                    Toast errorToast = Toast.makeText(LoginActivity.this, "Erreur, identifiant ou mot de passe incorrect !", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
//                startActivity(intent);
            }
        });
    }

}