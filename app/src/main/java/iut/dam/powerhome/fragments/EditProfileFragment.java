package iut.dam.powerhome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import iut.dam.powerhome.R;

public class EditProfileFragment extends Fragment {
    private Button btnSave;
    private Spinner spinnerPrefix;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonHabitatFragment fragment = new MonHabitatFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentFL, fragment);
                transaction.commit();
            }
        });

        spinnerPrefix = view.findViewById(R.id.prefixeTel);
        String[] items = new String[]{"+33", "+44", "+34"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinnerPrefix.setAdapter(adapter);
        return view;
    }
}