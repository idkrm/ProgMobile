package iut.dam.powerhome;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle toggle;
    private FragmentManager fm;
    private DrawerLayout drawer;
    private NavigationView nav;
    String urlString = "http://10.125.132.129/powerhome_server/getHabitats.php";
    ProgressDialog pDialog;

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
        nav.getMenu().performIdentifierAction(R.id.menu_habitats, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toggle.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        if (item.getItemId() == R.id.menu_habitats){
            fm.beginTransaction().replace(R.id.contentFL, new HabitatFragment()).commit();
        }
        else if (item.getItemId() == R.id.menu_monhabitat){
            fm.beginTransaction().replace(R.id.contentFL, new MonHabitatFragment()).commit();
        }
        else if (item.getItemId() == R.id.menu_requetes){
            fm.beginTransaction().replace(R.id.contentFL, new RequetesFragment()).commit();
        }else if (item.getItemId() == R.id.menu_param){
            fm.beginTransaction().replace(R.id.contentFL, new ParametresFragment()).commit();
        }
        else if (item.getItemId() == R.id.menu_deco){
            fm.beginTransaction().replace(R.id.contentFL, new InscriptionFragment()).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getRemoteHabitats() {
        String urlString = "http://10.125.132.129/powerhome/getHabitats.php";
        Ion.with(this)
                .load(urlString)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pDialog.dismiss();
                        if (result == null)
                            Log.d("TAG", "No response from the server!!!");
                        else {
                            // Traitement de result
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
