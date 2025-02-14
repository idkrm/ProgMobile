package iut.dam.powerhome;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class HabitatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habitat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView list = (ListView) findViewById(R.id.listHab);

        List<Habitat> items = new ArrayList<>();

        Appliance aspi = new Appliance(1, "Aspirateur","SuperAspi", 50);
        Appliance fer = new Appliance(2, "Fer", "SuperFer",10);
        Appliance machine = new Appliance(3, "Machine", "SuperMachine",100);
        Appliance clim = new Appliance(4, "Climatiseur", "SuperClim",120);

        List<Appliance> listAp = new ArrayList<>();
        listAp.add(aspi);
        listAp.add(fer);
        items.add(new Habitat(1, "Lucie Yan", 5, 100, listAp));

        listAp.add(machine);
        items.add(new Habitat(2, "Lucas Lablanche", 1, 100, listAp));

        listAp.remove(aspi);
        listAp.remove(machine);
        items.add(new Habitat(3, "Irene Xu", 1, 100, listAp));


        HabitatAdapter adapter = new HabitatAdapter(HabitatActivity.this, R.layout.item_habitat, items);

        list.setAdapter(adapter);
    }
}