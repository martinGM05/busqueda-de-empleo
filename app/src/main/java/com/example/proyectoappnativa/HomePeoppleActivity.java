package com.example.proyectoappnativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbUsers;
import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.Entidades.User;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Fragments.PostulationFullFragment;
import com.example.proyectoappnativa.Fragments.detailPostulationFragment;
import com.example.proyectoappnativa.Fragments.detailPostulationFullFragment;
import com.example.proyectoappnativa.Fragments.detailProfileFragment;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class HomePeoppleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IComunicFragmentPostulation {

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    View headerView;
    ActionBarDrawerToggle toggle;

    String idUser;
    TextView navUsername;
    ImageView imageUser;
    Task<DocumentSnapshot> data;
    RequestQueue request;
    fireService firebase = new fireService();
    User usuario = new User();
    FirebaseAuth mAuth;

    PostulationFragment listPostulation;
    detailPostulationFullFragment detailPostulation;

    com.example.proyectoappnativa.Fragments.detailProfileFragment detailProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_peopple);

/*
        listPostulation = new PostulationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content2, listPostulation).commit();
*/
        mDrawerLayout = findViewById(R.id.drawer_layout2);
        navigationView = (NavigationView)findViewById(R.id.nav_view2);
        headerView = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar2);

        setTitle(R.string.postulaciones);
        setSupportActionBar(toolbar);
        toggle = setUpDrawerToogle();
        mDrawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        navUsername = (TextView) headerView.findViewById(R.id.navUsername2);
        imageUser = (ImageView) headerView.findViewById(R.id.imageP2);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        String id = sharedPreferences.getString("userId", String.valueOf(MODE_PRIVATE));
        idUser = id;
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
        DbHelper dbHelper = new DbHelper(this);
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
        getInfoUser(idUser);
        switch (item.getItemId()){
            case R.id.nav_home2:
                 ft.replace(R.id.content, new PostulationFullFragment()).commit();
                break;
            case R.id.nav_profile2:
                ft.replace(R.id.content, new ProfileFragment()).commit();
                break;
            case R.id.signOut2:
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
        }
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            sharedPreferences.edit().remove("userId").apply();
            startActivity(new Intent(this, AuthActivity.class));
            DbHelper dbHelper = new DbHelper(this);
            dbHelper.onUpgrade( dbHelper.getWritableDatabase(), DbHelper.DATABASE_VERSION, DbHelper.DATABASE_VERSION+1);
            finish();
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
            sharedPreferences.edit().remove("userId").apply();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    @Override
    public void sendPostulation(Postulation postulation) {
        detailPostulation = new detailPostulationFullFragment();
        Bundle bundleEnvio = new Bundle();
        bundleEnvio.putSerializable("postulation", postulation);
        detailPostulation.setArguments(bundleEnvio);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailPostulation).addToBackStack(null).commit();
    }
}