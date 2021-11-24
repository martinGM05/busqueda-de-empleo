package com.example.proyectoappnativa.Fragments;

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

import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostulationFullFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostulationFullFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostulationFullFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostulationFullFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostulationFullFragment newInstance(String param1, String param2) {
        PostulationFullFragment fragment = new PostulationFullFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArrayList<Postulation> listPostulations;
    RecyclerView recycler;
    fireService firebase = new fireService();
    Task<QuerySnapshot> dataPostulations;
    String idPostulation = "";
    AdapterPostulation adapter;
    Activity activity;
    IComunicFragmentPostulation interfaceComunicFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_postulation_full, container, false);

        listPostulations = new ArrayList<>();
        recycler = root.findViewById(R.id.recyclePostulations);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        
        getPostulations();
        
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.activity = (Activity) context;
            interfaceComunicFragment = (IComunicFragmentPostulation) this.activity;
        }
    }

    private void getPostulations() {
        dataPostulations = firebase.getPostulations();
        dataPostulations.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Postulation postulation = document.toObject(Postulation.class);
                        listPostulations.add(postulation);
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