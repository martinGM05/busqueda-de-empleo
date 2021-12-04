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
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Db.DbPeoppleAceppted;
import com.example.proyectoappnativa.Db.DbPostulation;
import com.example.proyectoappnativa.Db.DbPostulationAcepted;
import com.example.proyectoappnativa.Fragments.Enterprise.PeoppleAceptedFragment;
import com.example.proyectoappnativa.Fragments.Peopple.PostulationsAcceptedFragment;
import com.example.proyectoappnativa.Fragments.Peopple.PostulationFullFragment;
import com.example.proyectoappnativa.Fragments.Peopple.detailPostulationFullFragment;
import com.example.proyectoappnativa.Fragments.SettingsFragment;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Fragments.Enterprise.PostulationFragment;
import com.example.proyectoappnativa.Fragments.ProfileFragment;
import com.example.proyectoappnativa.Fragments.Enterprise.detailPostulationFragment;
import com.example.proyectoappnativa.Fragments.Enterprise.detailProfileFragment;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.Interfaces.IComunicationFragmentApplications;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Locale;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbUsers;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Models.User;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        IComunicFragmentPostulation, IComunicationFragmentApplications {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View headerView;
    private ActionBarDrawerToggle toggle;
    private String id;
    private TextView navUsername;
    private ImageView imageUser;
    private Task<DocumentSnapshot> data;
    private FirebaseAuth mAuth;
    private fireService firebase = new fireService();
    private User usuario = new User();
    private Fragment detailPostulation;
    private detailProfileFragment detailProfileFragment;
    private String typeUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language();
        setContentView(R.layout.activity_home);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        toolbar();

    }


    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void language(){
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = prefs.getString("My_Languague", String.valueOf(Context.MODE_PRIVATE));
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void toolbar(){
        typeUser = getIntent().getStringExtra(getString(R.string.typeUser));
        if(typeUser.equals(getString(R.string.CiudadanoType))){
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFullFragment()).commit();
            navigationView.inflateMenu(R.menu.drawer_menu2);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFragment()).commit();
            navigationView.inflateMenu(R.menu.drawer_menu);
        }
        headerView = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.postulaciones);
        setSupportActionBar(toolbar);
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
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DbPeoppleAceppted dbPeoppleAceppted = new DbPeoppleAceppted(this);
        DbPostulation dbPostulation = new DbPostulation(this);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.loginData), MODE_PRIVATE);
        id = sharedPreferences.getString(getString(R.string.userId), String.valueOf(MODE_PRIVATE));
        if(!id.isEmpty()){
            if(Tools.isConnection(this)){
                getInfoUser(id);
                dbPeoppleAceppted.onUpgradePeoppleAcepted(db);
                dbPostulation.onUpgradePostulationDb(db);
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

    public void getInfoUser(String id){
        data = firebase.getInfo(getString(R.string.UsuariosFirebase), id);
        data.addOnCompleteListener(documentSnapshot -> {
           if(documentSnapshot.getResult().exists()){
                usuario.setId(documentSnapshot.getResult().getString("id"));
                usuario.setName(documentSnapshot.getResult().getString("name"));
                usuario.setDescription(documentSnapshot.getResult().getString("description"));
                usuario.setEmail(documentSnapshot.getResult().getString("email"));
                usuario.setType(documentSnapshot.getResult().getString("type"));
                usuario.setPhone(documentSnapshot.getResult().getString("phone"));
                usuario.setImageURL(documentSnapshot.getResult().getString("imageURL"));

                DbHelper dbHelper = new DbHelper(this);
                List<User> userData = dbHelper.getUserData(id);
                if(userData.size() > 0) {
                    navUsername.setText(usuario.getName());
                    Glide.with(this).load(usuario.getImageURL()).into(imageUser);
                }else{
                    DbUsers dbUsers = new DbUsers(this);
                    long uid = dbUsers.insertarUsuario(usuario.getId(), usuario.getName(), usuario.getEmail(), usuario.getDescription(), usuario.getType(), usuario.getPhone(),usuario.getImageURL());
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
        DbHelper dbHelper = new DbHelper(HomeActivity.this);
        DbPostulation dbPostulation = new DbPostulation(this);
        DbPeoppleAceppted dbPeoppleAceppted = new DbPeoppleAceppted(this);
        DbUsers dbUsers = new DbUsers(this);
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
            firebase.deleteToken(this, id);
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.loginData), MODE_PRIVATE);
            sharedPreferences.edit().remove(getString(R.string.userId)).apply();
            startActivity(new Intent(this, AuthActivity.class));
            dbPostulation.onUpgradePostulationDb(dbHelper.getWritableDatabase());
            dbPeoppleAceppted.onUpgradePeoppleAcepted(dbHelper.getWritableDatabase());
            dbUsers.onUpgrade(dbHelper.getWritableDatabase(), dbHelper.DATABASE_VERSION, dbHelper.DATABASE_VERSION + 1);
            finish();
        }else{
            firebase.deleteToken(this, id);
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.loginData), MODE_PRIVATE);
            sharedPreferences.edit().remove(getString(R.string.userId)).apply();
            startActivity(new Intent(this, AuthActivity.class));
            dbPostulation.onUpgradePostulationDb(dbHelper.getWritableDatabase());
            dbPeoppleAceppted.onUpgradePeoppleAcepted(dbHelper.getWritableDatabase());
            dbUsers.onUpgrade(dbHelper.getWritableDatabase(), dbHelper.DATABASE_VERSION, dbHelper.DATABASE_VERSION + 1);
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
        switch (item.getItemId()){
            case R.id.nav_home:
                if(Tools.isConnection(this)){
                    getInfoUser(id);
                }else{
                    getInfoUserOffline(id);
                }
                if (typeUser.equals(getString(R.string.CiudadanoType))){
                    ft.replace(R.id.content, new PostulationFullFragment()).commit();
                }else {
                    ft.replace(R.id.content, new PostulationFragment()).commit();
                }
                break;
            case R.id.nav_profile:
                ft.replace(R.id.content, new ProfileFragment()).commit();
                break;
            case R.id.nav_postulation:
                ft.replace(R.id.content, new PostulationsAcceptedFragment()).commit();
                break;
            case R.id.peoppleAccepted:
                ft.replace(R.id.content, new PeoppleAceptedFragment()).commit();
        }
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.itemLogout:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle(R.string.title_sesion);
                builder.setMessage(R.string.textCerrarSesion);
                builder.setPositiveButton(R.string.textOk, (dialog, which) -> {
                    signOut();
                });
                builder.setNegativeButton(R.string.textCancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
                return true;
            case R.id.itemSettings:
                ft.replace(R.id.content, new SettingsFragment()).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void sendPostulation(Postulation postulation) {
        if(typeUser.equals(getString(R.string.CiudadanoType))){
            detailPostulation = new detailPostulationFullFragment();
        }else{
            detailPostulation = new detailPostulationFragment();
        }
        Bundle bundleEnvio = new Bundle();
        bundleEnvio.putSerializable(getString(R.string.postulationBundle), postulation);
        detailPostulation.setArguments(bundleEnvio);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailPostulation).addToBackStack(null).commit();
    }

    @Override
    public void sendPostulationOff(PostulationOff postulationOff) {
        if(typeUser.equals(getString(R.string.CiudadanoType))){
            detailPostulation = new detailPostulationFullFragment();
        }else{
            detailPostulation = new detailPostulationFragment();
        }
        Bundle bundleEnvio = new Bundle();
        bundleEnvio.putSerializable(getString(R.string.postulationBundle), postulationOff);
        detailPostulation.setArguments(bundleEnvio);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailPostulation).addToBackStack(null).commit();
    }

    @Override
    public void sendApplications(User user) {
        detailProfileFragment = new detailProfileFragment();
        Bundle bundleProfile = new Bundle();
        bundleProfile.putSerializable(getString(R.string.objectProfile), user);
        detailProfileFragment.setArguments(bundleProfile);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailProfileFragment).addToBackStack(null).commit();
    }
}