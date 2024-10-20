package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.beans.Etudiant;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private EditText nom;
    private EditText prenom;
    private  Bitmap bitmap;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private ImageButton addpic;
    private CircleImageView image;
    final int code=999;
    RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/backend/ws/createEtudiant.php";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view1);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.list) {
                Intent intent = new Intent(MainActivity.this, ListEtudiantActivity.class);
                // Ajouter ces flags pour contrôler le comportement de la pile d'activités
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        ville = (Spinner) findViewById(R.id.ville);
        add = (Button) findViewById(R.id.add);
        m = (RadioButton) findViewById(R.id.m);
        addpic=findViewById(R.id.add_photo_button);
        f = (RadioButton) findViewById(R.id.f);
        image=findViewById(R.id.profile_image);
        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        code
                );
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view == add) {

                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.POST,
                            insertUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("respone", response);
                            Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                            Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                            for(Etudiant e : etudiants){
                                Log.d("etudiant", e.toString());
                            }
                            Toast.makeText(MainActivity.this, "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show();

                            // Lancer l'activité ListEtudiantActivity
                            Intent intent = new Intent(MainActivity.this, ListEtudiantActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Pour vider la pile d'activités
                            startActivity(intent);

                            // Optionnel : fermer l'activité courante
                            MainActivity.this.finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("erroe",error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            String sexe = "";
                            if(m.isChecked())
                                sexe = "Masculin";
                            else
                                sexe = "Feminin";
                            String imageData=imageToString(bitmap);
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("image",imageData);
                            params.put("nom", nom.getText().toString());
                            params.put("prenom", prenom.getText().toString());
                            params.put("ville", ville.getSelectedItem().toString());
                            params.put("sexe", sexe);
                            return params;
                        }
                    };
                    requestQueue.add(request);
                }
            }
        });




    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==code){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"select image"),code);
            }
            else{
                Toast.makeText(MainActivity.this,"                                                                                                                                                                                                                                                                                                                                                  Error add image",Toast.LENGTH_LONG).show();
            }
            return;
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==code && resultCode==RESULT_OK && data!=null){
            Uri filepath=data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imagebyte=outputStream.toByteArray();
        String encodedimage= Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encodedimage;

    }
}