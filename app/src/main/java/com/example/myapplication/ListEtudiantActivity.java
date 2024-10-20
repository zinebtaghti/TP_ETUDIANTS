package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.adapter.EtudiantAdapter;
import com.example.myapplication.beans.Etudiant;
import com.example.myapplication.beans.SwipeDismissListViewTouchListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEtudiantActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.homme) {
            performSearch("homme");
            return true;
        } else if (itemId == R.id.femme) {
            performSearch("femme");
            return true;
        }

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private ListView listViewEtudiants;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiantList;
    private List<Etudiant> originalEtudiantList;

    private SearchView searchView;
    private RequestQueue requestQueue;
    private String loadUrl = "http://10.0.2.2/backend/ws/loadEtudiant.php";
    private String loadUrldelete = "http://10.0.2.2/backend/ws/delete.php";
    private String updateUrl="http://10.0.2.2/backend/ws/updateetudiant.php";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onResume() {
        super.onResume();
        loadEtudiants();
    }
    private void performSearch(String query) {
        List<Etudiant> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(originalEtudiantList);
        } else if (query.equals("homme")) {
            for (Etudiant etudiant : originalEtudiantList) {
                if (etudiant.getSexe().equals("Masculin")) {
                    filteredList.add(etudiant);
                }
            }
        } else if (query.equals("femme")) {
            for (Etudiant etudiant : originalEtudiantList) {
                if (etudiant.getSexe().equals("Feminin")) {
                    filteredList.add(etudiant);
                }
            }
        } else {
            for (Etudiant etudiant : originalEtudiantList) {
                if (etudiant.getNom().toLowerCase().contains(query.toLowerCase()) ||
                        etudiant.getPrenom().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(etudiant);
                }
            }
        }

        etudiantList.clear();
        etudiantList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiant);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        originalEtudiantList = new ArrayList<>();
        getSupportActionBar().setHomeButtonEnabled(true);
        // Gérer les événements de navigation ici, par exemple :
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.addstudent) {
                Intent intent = new Intent(ListEtudiantActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Pour vider la pile d'activités

                startActivity(intent);

                ListEtudiantActivity.this.finish();
                return true;
            }
            return false;
        });
        listViewEtudiants = findViewById(R.id.listViewEtudiants);
        etudiantList = new ArrayList<>();
        adapter = new EtudiantAdapter(this, etudiantList);
        listViewEtudiants.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        loadEtudiants();

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(listViewEtudiants, new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int position) {

                        Etudiant etudiant = etudiantList.get(position);
                        showModifyDialog(etudiant);                  }
                    @Override
                    public void onOtherAction(ListView listView, int position) {
                        int id=etudiantList.get(position).getId();

                        showDeleteConfirmationDialog(position,id);

                        adapter.notifyDataSetChanged();

                    }
                });

        listViewEtudiants.setOnTouchListener(touchListener);



    }
    private void showModifyDialog(final Etudiant etudiant) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_modify_etudiant, null);
        dialogBuilder.setView(dialogView);

        final EditText editNom = dialogView.findViewById(R.id.editNom);
        final EditText editPrenom = dialogView.findViewById(R.id.editPrenom);
        final Spinner spinnerVille = dialogView.findViewById(R.id.spinnerVille);
        final RadioGroup radioGroupSexe = dialogView.findViewById(R.id.radioGroupSexe);

        editNom.setText(etudiant.getNom());
        editPrenom.setText(etudiant.getPrenom());


        // Configuration des RadioButtons pour le sexe
        if (etudiant.getSexe().equalsIgnoreCase("masculin")) {
            radioGroupSexe.check(R.id.radioMasculin);
        } else {
            radioGroupSexe.check(R.id.radioFeminin);
        }

        dialogBuilder.setTitle("Modifier l'étudiant");
        dialogBuilder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                etudiant.setNom(editNom.getText().toString());
                etudiant.setPrenom(editPrenom.getText().toString());
                etudiant.setVille(spinnerVille.getSelectedItem().toString());

                int selectedId = radioGroupSexe.getCheckedRadioButtonId();
                RadioButton radioButton = dialogView.findViewById(selectedId);
                etudiant.setSexe(radioButton.getText().toString());

                updateEtudiant(etudiant);
            }
        });
        dialogBuilder.setNegativeButton("Annuler", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void updateEtudiant(final Etudiant etudiant) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ListEtudiantActivity.this, "Étudiant mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        loadEtudiants(); // Recharger la liste après la mise à jour
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListEtudiantActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                params.put("nom", etudiant.getNom());
                params.put("prenom", etudiant.getPrenom());
                params.put("ville", etudiant.getVille());
                params.put("sexe", etudiant.getSexe());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
    private void deleteEtudiant(int id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loadUrldelete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<List<Etudiant>>(){}.getType();
                        List<Etudiant> etudiants = new Gson().fromJson(response, type);
                        etudiantList.clear();
                        etudiantList.addAll(etudiants);

                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }

                }

                ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", id + "");
                 return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    private void loadEtudiants() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<List<Etudiant>>(){}.getType();
                        List<Etudiant> etudiants = new Gson().fromJson(response, type);
                        etudiantList.clear();
                        originalEtudiantList.clear();
                        etudiantList.addAll(etudiants);
                        originalEtudiantList.addAll(etudiants);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        requestQueue.add(stringRequest);

    }




    private void showDeleteConfirmationDialog(final int position,int id) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir supprimer cet étudiant ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteEtudiant(id))
                .setNegativeButton("Non", null)
                .show();
    }
    @Override
    public void onBackPressed() {
        // Fermer le menu si ouvert, sinon effectuer la fonction de retour arrière habituelle
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




}