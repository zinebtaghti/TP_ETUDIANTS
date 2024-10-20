package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.beans.Etudiant;

import java.util.List;

public class EtudiantAdapter extends ArrayAdapter<Etudiant> {

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        super(context, 0, etudiants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Etudiant etudiant = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_etudiant, parent, false);
        }

        // Lookup view for data population
        TextView textViewNom = convertView.findViewById(R.id.textViewNom);
        TextView textViewPrenom = convertView.findViewById(R.id.textViewPrenom);
        TextView textViewVille = convertView.findViewById(R.id.textViewVille);
        ImageView image=convertView.findViewById(R.id.imageViewPhoto);

        // Populate the data into the template view using the data object
        textViewNom.setText(etudiant.getNom());
        textViewPrenom.setText(etudiant.getPrenom());
        if (etudiant.getImage() != null && !etudiant.getImage().isEmpty()) {
            Glide.with(convertView.getContext())
                    .load("http://10.0.2.2/backend/"+etudiant.getImage())
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .error(R.drawable.baseline_account_circle_24)
                    .into(image);
        } else {
            image.setImageResource(R.drawable.baseline_account_circle_24);
        }
        textViewVille.setText(etudiant.getVille());

        // Return the completed view to render on screen
        return convertView;
    }
}