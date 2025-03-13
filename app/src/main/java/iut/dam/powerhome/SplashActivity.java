package iut.dam.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Récupérer l'image du logo
        ImageView logoImageView = findViewById(R.id.logo);

        // Créer une animation de zoom
        ScaleAnimation zoomAnimation = new ScaleAnimation(
                0.0f, 1.0f, // Zoom de 0% à 100% (X)
                0.0f, 1.0f, // Zoom de 0% à 100% (Y)
                Animation.RELATIVE_TO_SELF, 0.5f, // Le point de pivot pour l'animation (milieu de l'image)
                Animation.RELATIVE_TO_SELF, 0.5f);
        zoomAnimation.setDuration(700); // Durée de l'animation
        zoomAnimation.setFillAfter(true); // Maintenir la taille après l'animation
        zoomAnimation.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()); // Effet d'accélération et décélération

        // Démarrer l'animation
        logoImageView.startAnimation(zoomAnimation);

        // Lancer MainActivity après l'animation
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Logique avant le début de l'animation
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Ne rien faire ici
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}