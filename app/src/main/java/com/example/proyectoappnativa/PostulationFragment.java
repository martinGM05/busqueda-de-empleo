package com.example.proyectoappnativa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectoappnativa.Interfaces.IComunicFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Entidades.Postulation;

public class PostulationFragment extends Fragment {


    ArrayList<Postulation> listPostulations;
    RecyclerView recycler;

    Activity activity;
    IComunicFragment interfaceComunicFragment;

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
        //return inflater.inflate(R.layout.fragment_postulation, container, false);
        View root = inflater.inflate(R.layout.fragment_postulation, container, false);

       fab = root.findViewById(R.id.fab);
    
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Postulacion", Toast.LENGTH_SHORT).show();
                // abrir un nuevo fragment
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new newPostulationFragment()).commit();
            }
        });

        listPostulations = new ArrayList<>();
        recycler = root.findViewById(R.id.recyclerId);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        fillList();

        AdapterPostulation adapter = new AdapterPostulation(listPostulations);
        recycler.setAdapter(adapter);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Selecciona: "+listPostulations.get(recycler.getChildAdapterPosition(v)).getName(), Toast.LENGTH_LONG).show();
                interfaceComunicFragment.sendPostulation(listPostulations.get(recycler.getChildAdapterPosition(v)));
            }

        });

        return root;
    }

    private void fillList() {
        String[] names = {"John","Tim","Sam","Ben"};
       // listPostulations.add(new Postulation("1P", "Jardinero", "", "Se busca jardinero para patio de 10x10", 20.1, 20.6, {""}, ""));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  Activity){
            this.activity = (Activity) context;
            interfaceComunicFragment = (IComunicFragment) this.activity;
        }
    }
}