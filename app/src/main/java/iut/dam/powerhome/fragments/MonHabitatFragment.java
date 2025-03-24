package iut.dam.powerhome.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import iut.dam.powerhome.R;

public class MonHabitatFragment extends Fragment {
    private TextView nomPersonne, prenomPersonne, adressePersonne, telPersonne;
    private TextView etagePersonne, areaPersonne, appliancePersonne;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mon_habitat, container, false);

        // Initialisation des TextView
        nomPersonne = view.findViewById(R.id.nompersonne);
        prenomPersonne = view.findViewById(R.id.prenompersonne);
        adressePersonne = view.findViewById(R.id.adressepersonne);
        telPersonne = view.findViewById(R.id.telpersonne);
        etagePersonne = view.findViewById(R.id.etagepersonne);
        areaPersonne = view.findViewById(R.id.areapersonne);
        appliancePersonne = view.findViewById(R.id.appliancepersonne);

        // Récupérer les infos de connexion
        sharedPreferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        if (!email.isEmpty() && !password.isEmpty()) {
            fetchUserData(email, password);
        }

        return view;
    }

    private void fetchUserData(String email, String password) {
        String url = "http://192.168.1.67/ecopower/getUserData.php";

        Ion.with(this)
                .load(url)
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
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
                                JSONObject userData = jsonResponse.getJSONObject("data");

                                // Afficher les données de base
                                nomPersonne.setText(userData.getString("lastname"));
                                prenomPersonne.setText(userData.getString("firstname"));
                                adressePersonne.setText(userData.getString("email"));
                                telPersonne.setText(userData.getString("phone_number"));

                                // Afficher les données habitat
                                if (userData.has("floor")) {
                                    etagePersonne.setText(String.valueOf(userData.getInt("floor")));
                                }
                                if (userData.has("area")) {
                                    areaPersonne.setText(userData.getInt("area") + "m²");
                                }

                                // Afficher les appareils
                                if (userData.has("appliances")) {
                                    JSONArray appliances = userData.getJSONArray("appliances");
                                    StringBuilder sb = new StringBuilder();

                                    for (int i = 0; i < appliances.length(); i++) {
                                        JSONObject appliance = appliances.getJSONObject(i);
                                        sb.append("• ")
                                                .append(appliance.getString("name"))
                                                .append(" (")
                                                .append(appliance.getString("reference"))
                                                .append(") - ")
                                                .append(appliance.getInt("wattage"))
                                                .append("W\n");
                                    }

                                    if (sb.length() > 0) {
                                        appliancePersonne.setText(sb.toString());
                                    } else {
                                        appliancePersonne.setText("Aucun appareil enregistré");
                                    }
                                } else {
                                    appliancePersonne.setText("Aucune information d'appareils");
                                }
                            } else {
                                Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            Log.e("MonHabitatFragment", "Erreur JSON: " + ex.getMessage());
                            Toast.makeText(getContext(), "Erreur de données", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}