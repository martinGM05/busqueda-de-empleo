package com.example.proyectoappnativa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.Entidades.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Entidades.Postulation;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PostulationFragment extends Fragment {


    ArrayList<Postulation> listPostulations;
    RecyclerView recycler;

    Activity activity;
    IComunicFragmentPostulation interfaceComunicFragment;
    fireService firebase = new fireService();
    Task<QuerySnapshot> data;
    AdapterPostulation adapter;
    String idPostulation = "";

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

    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_postulation, container, false);
       fab = root.findViewById(R.id.fabApplicationsAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new newPostulationFragment()).commit();
            }
        });

        listPostulations = new ArrayList<>();
        recycler = root.findViewById(R.id.recyclerId);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        getPostulation();

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  Activity){
            this.activity = (Activity) context;
            interfaceComunicFragment = (IComunicFragmentPostulation) this.activity;
        }
    }



    private void getPostulation(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginData", getContext().MODE_PRIVATE);
        String id = sharedPreferences.getString("userId", "");
        DbHelper dbHelper = new DbHelper(getContext());
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
                data = firebase.getPostulationFirebase(user.getName());
                data.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Postulation postulation = document.toObject(Postulation.class);
                                listPostulations.add(postulation);
                                idPostulation = postulation.getId();
                            }
                            adapter = new AdapterPostulation(listPostulations, getActivity());
                            recycler.setAdapter(adapter);
                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Toast.makeText(getContext(), "Selecciona: "+listPostulations.get(recycler.getChildAdapterPosition(v)).getName(), Toast.LENGTH_LONG).show();
                                    //firebase.getApplicationsFirebase(listPostulations.get(recycler.getChildAdapterPosition(v)).getId());
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
        firebase.deletePostulationFirebase(idPostulation);
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