package com.example.proyectoappnativa.Fragments.Peopple;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Adapters.Offline.AdapterPostulationOf;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbPostulation;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostulationFullFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostulationFullFragment extends Fragment implements SearchView.OnQueryTextListener {

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

    private ArrayList<Postulation> listPostulations;
    private RecyclerView recycler;
    private fireService firebase = new fireService();
    private Task<QuerySnapshot> dataPostulations;
    private String idPostulation = "";
    private AdapterPostulation adapter;
    private Activity activity;
    private IComunicFragmentPostulation interfaceComunicFragment;
    private SearchView svSearch;
    String keys = "";
    private ArrayList<PostulationOff> listPostulationsOff = new ArrayList<>();
    private AdapterPostulationOf adapterOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_postulation_full, container, false);
        svSearch = root.findViewById(R.id.svPostulation);
        listPostulations = new ArrayList<>();
        recycler = root.findViewById(R.id.recyclePostulations);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if(Tools.isConnection(requireActivity())){
            getPostulations();
        }else{
            getPostulationOffline();
        }

        initListener();
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

    private void getPostulationOffline(){
        DbHelper dbHelper = new DbHelper(getContext());
        List<PostulationOff> prueba = dbHelper.getPostulationsData();
        for(PostulationOff postulation : prueba){
            listPostulationsOff.add(postulation);
        }
        adapterOff = new AdapterPostulationOf(listPostulationsOff, requireActivity());
        recycler.setAdapter(adapterOff);
    }

    private void getPostulations() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString(getString(R.string.userId), String.valueOf(Context.MODE_PRIVATE));

        DbPostulation dbPostulation = new DbPostulation(requireActivity());
        SQLiteDatabase db = dbPostulation.getWritableDatabase();
        dbPostulation.onUpgradePostulationDb(db);

        dataPostulations = firebase.getPostulations(requireActivity());
        dataPostulations.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Postulation postulation = document.toObject(Postulation.class);
                        //Toast.makeText(requireActivity(), postulation.getName(), Toast.LENGTH_SHORT).show();
                        if(postulation.getPostulantes_aceptados() != null){
                            if(!postulation.getPostulantes_aceptados().contains(idUser)){
                                listPostulations.add(postulation);
                                for(int i = 0; i < postulation.getKeywords().size(); i++){
                                    keys += postulation.getKeywords().get(i);
                                    if(i != postulation.getKeywords().size() - 1){
                                        keys += ",";
                                    }
                                }
                                long uid = dbPostulation.insertPostulation(
                                        postulation.getId(),
                                        postulation.getName(),
                                        postulation.getCompany(),
                                        postulation.getDescription(),
                                        keys);
                            }
                        }else{
                            listPostulations.add(postulation);
                            for(int i = 0; i < postulation.getKeywords().size(); i++){
                                keys += postulation.getKeywords().get(i);
                                if(i != postulation.getKeywords().size() - 1){
                                    keys += ",";
                                }
                            }
                            long uid = dbPostulation.insertPostulation(
                                    postulation.getId(),
                                    postulation.getName(),
                                    postulation.getCompany(),
                                    postulation.getDescription(),
                                    keys);
                        }
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

    private void initListener(){
        svSearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(Tools.isConnection(requireActivity())){
            adapter.filter(newText);
        }else{
            adapterOff.filter(newText);
        }
        return false;
    }
}