package iut.dam.powerhome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.Appliance;
import entities.Habitat;
import iut.dam.powerhome.adapters.HabitatAdapter;
import iut.dam.powerhome.R;

public class HabitatFragment extends Fragment {
    private ListView list;
    private HabitatAdapter adapter;
    private List<Habitat> habitats = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_habitat, container, false);

        // Gérer les insets
        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        list = rootView.findViewById(R.id.listHab);
        adapter = new HabitatAdapter(getActivity(), R.layout.item_habitat, habitats);
        list.setAdapter(adapter);

        loadHabitatsFromServer();

        return rootView;
    }

    private void loadHabitatsFromServer() {
        String url = "http://192.168.1.67/ecopower/getHabitats.php";

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            if (jsonResponse.getString("status").equals("success")) {
                                habitats.clear();

                                JSONArray data = jsonResponse.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject habitatJson = data.getJSONObject(i);

                                    List<Appliance> appliances = new ArrayList<>();
                                    JSONArray appliancesJson = habitatJson.getJSONArray("appliances");
                                    for (int j = 0; j < appliancesJson.length(); j++) {
                                        JSONObject applianceJson = appliancesJson.getJSONObject(j);
                                        appliances.add(new Appliance(
                                                applianceJson.getInt("id"),
                                                applianceJson.getString("name"),
                                                applianceJson.getString("reference"),
                                                applianceJson.getInt("wattage")
                                        ));
                                    }

                                    habitats.add(new Habitat(
                                            habitatJson.getInt("id"),
                                            habitatJson.getString("resident_name"),
                                            habitatJson.getInt("floor"),
                                            habitatJson.getDouble("area"),
                                            appliances
                                    ));
                                }

                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(getContext(), "Erreur de données", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }
                });
    }
}