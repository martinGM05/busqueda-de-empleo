package com.example.proyectoappnativa.Fragments.Enterprise;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectoappnativa.Adapters.Offline.AdapterPostulationOf;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbPostulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Models.Postulation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PostulationFragment extends Fragment {


    private ArrayList<Postulation> listPostulations;
    private ArrayList<PostulationOff> listPostulationsOff = new ArrayList<>();
    private RecyclerView recycler;
    private Activity activity;
    private IComunicFragmentPostulation interfaceComunicFragment;
    private fireService firebase = new fireService();
    private Task<QuerySnapshot> data;
    private AdapterPostulation adapter;
    private AdapterPostulationOf adapterOff;
    private String idPostulation = "";
    private FloatingActionButton fab;
    String keys = "";
    // Inicializar una lista vacia
    private List<String> listEmpty = new ArrayList<>();
    Postulation postulation = new Postulation();
    private boolean online;

    public PostulationFragment() {
    }

    public static PostulationFragment newInstance(String param1, String param2) {
        PostulationFragment fragment = new PostulationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_postulation, container, false);
        fab = root.findViewById(R.id.fabApplicationsAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(online){
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new newPostulationFragment()).commit();
                }else{
                    Snackbar.make(v, R.string.dont_conection, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        listPostulations = new ArrayList<>();
        recycler = root.findViewById(R.id.recyclerId);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        DbHelper dbHelper = new DbHelper(requireContext());
        DbPostulation dbPostulation = new DbPostulation(requireContext());

        if(online){
            dbPostulation.onUpgradePostulationDb(dbHelper.getWritableDatabase());
            getPostulation();
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler);
        }else{
            getPostulationOffline();
        }
        return root;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(Tools.isConnection(requireActivity())){
            online = true;
        }else{
            online = false;
        }
        if(context instanceof  Activity){
            this.activity = (Activity) context;
            interfaceComunicFragment = (IComunicFragmentPostulation) this.activity;
        }
    }

    public void getPostulationOffline(){
        DbHelper dbHelper = new DbHelper(getContext());
        List<PostulationOff> prueba = dbHelper.getPostulationsData();
        for(PostulationOff postulation : prueba){
            listPostulationsOff.add(postulation);
        }
        adapterOff = new AdapterPostulationOf(listPostulationsOff, requireActivity());
        recycler.setAdapter(adapterOff);
    }



    private void getPostulation(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(getString(R.string.userId), "");
        DbHelper dbHelper = new DbHelper(getContext());
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
                data = firebase.getPostulationFirebase(requireActivity(), user.getName());
                data.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Postulation postulation = document.toObject(Postulation.class);
                                listPostulations.add(postulation);
                                idPostulation = postulation.getId();

                                for(int i = 0; i < postulation.getKeywords().size(); i++){
                                    keys += postulation.getKeywords().get(i);
                                    if(i != postulation.getKeywords().size() - 1){
                                        keys += ",";
                                    }                                
                                }
                                DbPostulation dbPostulation = new DbPostulation(requireContext());
                                long uid = dbPostulation.insertPostulation(
                                        postulation.getId(),
                                        postulation.getName(),
                                        postulation.getCompany(),
                                        postulation.getDescription(),
                                        keys);
                            }
                            adapter = new AdapterPostulation(listPostulations, getActivity());
                            recycler.setAdapter(adapter);
                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                  interfaceComunicFragment.sendPostulation(listPostulations.get(recycler.getChildAdapterPosition(v)));
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void removePostulation(String idPostulation){
        firebase.deletePostulationFirebase(requireActivity(), idPostulation);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            listPostulations.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
            removePostulation(idPostulation);
        }
    };
}