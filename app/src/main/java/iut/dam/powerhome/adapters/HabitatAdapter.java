package iut.dam.powerhome.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entities.Appliance;
import entities.Habitat;
import iut.dam.powerhome.R;

public class HabitatAdapter extends ArrayAdapter<Habitat> {
    Activity activity;
    int itemResourceId;
    List<Habitat> items = new ArrayList<>();

    public HabitatAdapter(Activity act, int id, List<Habitat> list) {
        super(act, id, list);

        this.activity = act;
        this.itemResourceId = id;
        this.items = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View layout = convertView;
        if(convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResourceId, parent, false);
        }

        // recupere les views
        TextView nameTV = (TextView) layout.findViewById(R.id.nomHabitant);
        TextView etageTV = (TextView) layout.findViewById(R.id.etage);
        TextView nbEquip = (TextView) layout.findViewById(R.id.nb_equip);

        // set les infos (nom, etage et nombre d'equipement)
        nameTV.setText(items.get(position).getResidentName());
        etageTV.setText(String.valueOf(items.get(position).getFloor()));
        nbEquip.setText(items.get(position).getAppliances().size() > 1 ?
                items.get(position).getAppliances().size() + " équipements" :
                items.get(position).getAppliances().size() + " équipement");

        // recupere le layout des images des equipements
        LinearLayout imgEquip = (LinearLayout) layout.findViewById(R.id.listeEquip);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(50, 50);
        layoutparams.setMargins(0,5,10,0);

        imgEquip.removeAllViews(); // suppr les anciennes images

        for (Appliance a : items.get(position).getAppliances()) {
            ImageView img = new ImageView(this.getContext()); // cree dynamiquement une imageview

            // choisis l'image correspondant a l'equipement
            switch (a.getName()) {
                case "Aspirateur":
                    img.setImageResource(R.drawable.aspi);
                    break;
                case "Fer à repasser":
                    img.setImageResource(R.drawable.repasser);
                    break;
                case "Climatiseur":
                    img.setImageResource(R.drawable.climatiseur);
                    break;
                case "Machine à laver":
                    img.setImageResource(R.drawable.machine);
                    break;
            }
            imgEquip.addView(img, layoutparams); // rajoute l'image dans le layout
        }

        layout.setOnClickListener(v ->
                Toast.makeText(HabitatAdapter.this.getContext(), items.get(position).getResidentName(),
                        Toast.LENGTH_SHORT).show()
        );
        return layout;
    }
}
