package iut.dam.powerhome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.Appliance;
import entities.Habitat;
import iut.dam.powerhome.adapters.HabitatAdapter;
import iut.dam.powerhome.R;

public class HabitatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infle le layout pour le fragment
        View rootView = inflater.inflate(R.layout.fragment_habitat, container, false);

        // Gérer les insets pour Edge-to-Edge UI
        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Référencer le ListView et l'adapter
        ListView list = rootView.findViewById(R.id.listHab);
        List<Habitat> items = new ArrayList<>();

        // Création des Appliances et Habitats
        Appliance aspi = new Appliance(1, "Aspirateur", "SuperAspi", 50);
        Appliance fer = new Appliance(2, "Fer", "SuperFer", 10);
        Appliance machine = new Appliance(3, "Machine", "SuperMachine", 100);
        Appliance clim = new Appliance(4, "Climatiseur", "SuperClim", 120);

        items.add(new Habitat(1, "Lucie Yan", 5, 100, new ArrayList<>(Arrays.asList(aspi, fer))));
        items.add(new Habitat(2, "Lucas Lablanche", 2, 100, new ArrayList<>(Arrays.asList(aspi, fer, machine))));
        items.add(new Habitat(3, "Irene Xu", 1, 100, new ArrayList<>(Arrays.asList(fer))));
        items.add(new Habitat(4, "Priscilla Chen", 10, 100, new ArrayList<>(Arrays.asList(clim))));

        // Assigner l'adaptateur à la liste
        HabitatAdapter adapter = new HabitatAdapter(getActivity(), R.layout.item_habitat, items);
        list.setAdapter(adapter);

        return rootView;  // Retourne la vue pour le fragment
    }
}
