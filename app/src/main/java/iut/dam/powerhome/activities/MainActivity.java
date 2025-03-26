package iut.dam.powerhome.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import iut.dam.powerhome.R;
import iut.dam.powerhome.fragments.HabitatFragment;
import iut.dam.powerhome.fragments.MonHabitatFragment;
import iut.dam.powerhome.fragments.NotificationsFragment;
import iut.dam.powerhome.fragments.PreferenceFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle toggle;
    private FragmentManager fm;
    private DrawerLayout drawer;
    private NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawer = findViewById(R.id.drawer);
        nav = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fm = getSupportFragmentManager();
        nav.setNavigationItemSelectedListener(this);
        nav.getMenu().performIdentifierAction(R.id.menu_monhabitat, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toggle.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment = null;
        int titleResId = 0;

        if (itemId == R.id.menu_habitats) {
            fragment = new HabitatFragment();
            titleResId = R.string.habitats;
        } else if (itemId == R.id.menu_monhabitat) {
            fragment = new MonHabitatFragment();
            titleResId = R.string.my_habitat;
        } else if (itemId == R.id.menu_notif) {
            fragment = new NotificationsFragment();
            titleResId = R.string.mes_notifications;
        } else if (itemId == R.id.menu_preference) {
            fragment = new PreferenceFragment();
            titleResId = R.string.mes_pr_f_rences;
        } else if (itemId == R.id.menu_deco) {
            handleLogout();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }else if(itemId == R.id.menu_about){
            aboutDialog();
            return true;
        } else {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        if (fragment != null) {
            fm.beginTransaction()
                    .replace(R.id.contentFL, fragment)
                    .commit();

            setActionBarTitle(titleResId);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setActionBarTitle(@StringRes int titleResId) {
        if (getSupportActionBar() != null) {
            String title = getString(titleResId);
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.WHITE),
                    0,
                    spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            getSupportActionBar().setTitle(spannableString);
        }
    }

    private void aboutDialog(){
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            String message = getString(R.string.app_name) + "\n\n"
                    + "Version : " + version + "\n\n"
                    + "Développeuses :\n    - Priscilla Chen\n    - Irène Xu\n    - Lucie Yan\n\n"
                    + "Merci d'utiliser notre application !";

            new AlertDialog.Builder(this)
                    .setTitle("À propos")
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.vrailogo)
                    .show();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
