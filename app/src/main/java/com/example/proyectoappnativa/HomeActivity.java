package com.example.proyectoappnativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Fragments.PostulationFullFragment;
import com.example.proyectoappnativa.Fragments.detailPostulationFullFragment;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Fragments.Enterprise.PostulationFragment;
import com.example.proyectoappnativa.Fragments.ProfileFragment;
import com.example.proyectoappnativa.Fragments.Enterprise.detailPostulationFragment;
import com.example.proyectoappnativa.Fragments.detailProfileFragment;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.Interfaces.IComunicationFragmentApplications;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbUsers;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Models.User;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        IComunicFragmentPostulation, IComunicationFragmentApplications {

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    View headerView;
    ActionBarDrawerToggle toggle;

    String id;
    TextView navUsername;
    ImageView imageUser;
    Task<DocumentSnapshot> data;
    FirebaseAuth mAuth;
    fireService firebase = new fireService();
    User usuario = new User();

    Fragment detailPostulation;
    detailProfileFragment detailProfileFragment;
    String typeUser;
    PostulationFullFragment listPostulation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        typeUser = getIntent().getStringExtra("typeUser");
        if(typeUser.equals("Ciudadano")){
            listPostulation = new PostulationFullFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content, listPostulation).commit();

            mDrawerLayout = findViewById(R.id.drawer_layout);
            navigationView = (NavigationView)findViewById(R.id.nav_view);
            headerView = navigationView.getHeaderView(0);
            toolbar = findViewById(R.id.toolbar);

            setTitle(R.string.postulaciones);
            setSupportActionBar(toolbar);

            toggle = setUpDrawerToogle();
            mDrawerLayout.addDrawerListener(toggle);
            navigationView.inflateMenu(R.menu.drawer_menu2);
            navigationView.setNavigationItemSelectedListener(this);


            navUsername = (TextView) headerView.findViewById(R.id.navUsername);
            imageUser = (ImageView) headerView.findViewById(R.id.cvImagePostulation);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFragment()).commit();

            mDrawerLayout = findViewById(R.id.drawer_layout);
            navigationView = (NavigationView)findViewById(R.id.nav_view);
            headerView = navigationView.getHeaderView(0);
            toolbar = findViewById(R.id.toolbar);

            setTitle(R.string.postulaciones);
            setSupportActionBar(toolbar);

            toggle = setUpDrawerToogle();
            mDrawerLayout.addDrawerListener(toggle);
            navigationView.inflateMenu(R.menu.drawer_menu);
            navigationView.setNavigationItemSelectedListener(this);

            navUsername = (TextView) headerView.findViewById(R.id.navUsername);
            imageUser = (ImageView) headerView.findViewById(R.id.cvImagePostulation);
        }


        super.onStart();
    }

    private void toolbar(){
        /*if(type.equals("Ciudadano")){
            mDrawerLayout = findViewById(R.id.drawer_layout2);
            navigationView = (NavigationView)findViewById(R.id.nav_view2);
            headerView = navigationView.getHeaderView(0);
        }else{
            mDrawerLayout = findViewById(R.id.drawer_layout);
            navigationView = (NavigationView)findViewById(R.id.nav_view);
            headerView = navigationView.getHeaderView(0);
        }*/
        headerView = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.postulaciones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toggle = setUpDrawerToogle();
        mDrawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        imageUser = (ImageView) headerView.findViewById(R.id.cvImagePostulation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
    }


    public void checkUserStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", String.valueOf(MODE_PRIVATE));
        if(!id.isEmpty()){
            if(isConnection()){
                getInfoUser(id);
            }else{
                getInfoUserOffline(id);
            }
        }else{
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    public void getInfoUserOffline(String id){
        DbHelper dbHelper = new DbHelper(HomeActivity.this);
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
                navUsername.setText(user.getName());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public void getInfoUser(String id){
        data = firebase.getInfoUser(id);
        data.addOnCompleteListener(documentSnapshot -> {
           if(documentSnapshot.getResult().exists()){
                usuario.setId(documentSnapshot.getResult().getString("id"));
                usuario.setName(documentSnapshot.getResult().getString("name"));
                usuario.setDescription(documentSnapshot.getResult().getString("description"));
                usuario.setEmail(documentSnapshot.getResult().getString("email"));
                usuario.setType(documentSnapshot.getResult().getString("type"));
                usuario.setImageURL(documentSnapshot.getResult().getString("imageURL"));

                DbHelper dbHelper = new DbHelper(this);
                List<User> userData = dbHelper.getUserData(id);
                if(userData.size() > 0) {
                    navUsername.setText(usuario.getName());
                    Glide.with(this).load(usuario.getImageURL()).into(imageUser);
                }else{
                    DbUsers dbUsers = new DbUsers(this);
                    long uid = dbUsers.insertarUsuario(usuario.getId(), usuario.getName(), usuario.getEmail(), usuario.getDescription(), usuario.getType(), usuario.getImageURL());
                    if(uid > 0){
                        navUsername.setText(usuario.getName());
                        Glide.with(this).load(usuario.getImageURL()).into(imageUser);
                    }
                }
           }
        });
    }



    private void signOut() {
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
            firebase.deleteToken(id);
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            sharedPreferences.edit().remove("userId").apply();
            startActivity(new Intent(this, AuthActivity.class));
            DbHelper dbHelper = new DbHelper(HomeActivity.this);
            dbHelper.onUpgrade( dbHelper.getWritableDatabase(), DbHelper.DATABASE_VERSION, DbHelper.DATABASE_VERSION+1);
            finish();
        }else{
            firebase.deleteToken(id);
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            sharedPreferences.edit().remove("userId").apply();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    private ActionBarDrawerToggle setUpDrawerToogle() {
       return new ActionBarDrawerToggle(this,
               mDrawerLayout,
               toolbar,
               R.string.drawer_open,
               R.string.drawer_close
       );
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectItemNav(item);
        return true;
    }

    private void selectItemNav(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        getInfoUser(id);
        switch (item.getItemId()){
            case R.id.nav_home:
                if (typeUser.equals("Ciudadano")){
                    ft.replace(R.id.content, new PostulationFullFragment()).commit();
                }else {
                    ft.replace(R.id.content, new PostulationFragment()).commit();
                }
                break;
            case R.id.nav_profile:
                ft.replace(R.id.content, new ProfileFragment()).commit();
                break;
        }
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHelp:
                Toast.makeText(this, "Ayuda", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemLogout:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle(R.string.textSesion);
                builder.setMessage(R.string.textCerrarSesion);
                builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
                    signOut();
                });
                builder.setNegativeButton(R.string.textCancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void sendPostulation(Postulation postulation) {
        if(typeUser.equals("Ciudadano")){
            detailPostulation = new detailPostulationFullFragment();
        }else{
            detailPostulation = new detailPostulationFragment();
        }
        Bundle bundleEnvio = new Bundle();
        bundleEnvio.putSerializable("postulation", postulation);
        detailPostulation.setArguments(bundleEnvio);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailPostulation).addToBackStack(null).commit();
    }

    @Override
    public void sendApplications(User user) {
        detailProfileFragment = new detailProfileFragment();
        Bundle bundleProfile = new Bundle();
        bundleProfile.putSerializable("objectProfile", user);
        detailProfileFragment.setArguments(bundleProfile);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailProfileFragment).addToBackStack(null).commit();
    }
}