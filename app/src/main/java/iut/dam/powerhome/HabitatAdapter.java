package iut.dam.powerhome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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

        TextView nameTV = (TextView) layout.findViewById(R.id.nomHabitant);
        ImageView imageIM = (ImageView) layout.findViewById(R.id.img_equip);
        ImageView imageIM2 = (ImageView) layout.findViewById(R.id.img_equip2);
        ImageView imageIM3 = (ImageView) layout.findViewById(R.id.img_equip3);
        ImageView imageIM4 = (ImageView) layout.findViewById(R.id.img_equip4);
        TextView etageTV = (TextView) layout.findViewById(R.id.etage);
        TextView nbEquip = (TextView) layout.findViewById(R.id.nb_equip);


        nameTV.setText(items.get(position).residentName);
        etageTV.setText(String.valueOf(items.get(position).floor));
        nbEquip.setText(items.get(position).appliances.size() > 1 ?
                items.get(position).appliances.size() + " équipements" :
                items.get(position).appliances.size() + " équipement");


        for (int i = 0; i < items.get(position).appliances.size(); i++) {
            Appliance appliance = items.get(position).appliances.get(i);
            if (i == 0 && appliance.getName().equals("Aspirateur")) {
                imageIM.setImageResource(R.drawable.aspi);
            } else if (i == 1 && appliance.getName().equals("Fer")) {
                imageIM2.setImageResource(R.drawable.repasser);
            } else if (i == 2 && appliance.getName().equals("Climatiseur")) {
                imageIM4.setImageResource(R.drawable.climatiseur);
            } else if (i == 3 && appliance.getName().equals("Machine")) {
                imageIM3.setImageResource(R.drawable.machine);
            }
        }
        return layout;
    }
}
