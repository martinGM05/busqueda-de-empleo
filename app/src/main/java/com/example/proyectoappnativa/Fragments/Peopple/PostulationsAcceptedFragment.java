package com.example.proyectoappnativa.Fragments.Peopple;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Adapters.Offline.AdapterPostulationOf;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbPostulation;
import com.example.proyectoappnativa.Db.DbPostulationAcepted;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicFragmentPostulation;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostulationsAcceptedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostulationsAcceptedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostulationsAcceptedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostulationsAcceptedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostulationsAcceptedFragment newInstance(String param1, String param2) {
        PostulationsAcceptedFragment fragment = new PostulationsAcceptedFragment();
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

    Button btnAceptados;
    fireService firebase = new fireService();
    private Task<DocumentSnapshot> dataApplications, dataUser;
    private ArrayList<Postulation> listPostulations;
    private RecyclerView recycler;
    private AdapterPostulation adapter;
    private Activity activity;
    private IComunicFragmentPostulation interfaceComunicFragment;
    String keys = "";
    private ArrayList<PostulationOff> listPostulationsOff = new ArrayList<>();
    private AdapterPostulationOf adapterOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_postulations_accepted, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString(getString(R.string.userId), String.valueOf(Context.MODE_PRIVATE));


        listPostulations = new ArrayList<>();
        recycler = root.findViewById(R.id.recycleAcept);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (Tools.isConnection(requireActivity())){
            getPostulates(idUser);
        }else{
            getPostulatesOffline();
        }
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

    private void getPostulatesOffline(){
        DbHelper dbHelper = new DbHelper(getContext());
        List<PostulationOff> postulationAceptedOff = dbHelper.getPostulationsAceptedData();
        listPostulationsOff.addAll(postulationAceptedOff);
        adapterOff = new AdapterPostulationOf(listPostulationsOff, requireActivity());
        recycler.setAdapter(adapterOff);
    }

    private void getPostulates(String idUser){
        dataApplications = firebase.getInfo(getString(R.string.UsuariosFirebase),idUser);
        dataApplications.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> worksAccepted = (List<String>) document.get(getString(R.string.postulantesAceptedFirebase));
                        if (worksAccepted != null){
                            if (worksAccepted.size() > 0) {
                                for (String idWork : worksAccepted) {
                                    dataUser = firebase.getInfo(getString(R.string.PostulacionesFirebase), idWork);
                                    dataUser.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Postulation postulation = document.toObject(Postulation.class);
                                                    listPostulations.add(postulation);

                                                    DbHelper dbHelper = new DbHelper(requireActivity());
                                                    DbPostulationAcepted dbPostulationAcepted = new DbPostulationAcepted(requireContext());
                                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                    dbPostulationAcepted.onUpgradePostulationAceptedDb(db);

                                                    for(int i = 0; i < postulation.getKeywords().size(); i++){
                                                        keys += postulation.getKeywords().get(i);
                                                        if(i != postulation.getKeywords().size() - 1){
                                                            keys += ",";
                                                        }
                                                    }
                                                    long uid = dbPostulationAcepted.insertPostulationAcepted(
                                                            postulation.getId(),
                                                            postulation.getName(),
                                                            postulation.getCompany(),
                                                            postulation.getDescription(),
                                                            keys);
                                                } else {
                                                    Snackbar.make(requireView(), getString(R.string.dont_jobExist), Snackbar.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), getString(R.string.titleError), Toast.LENGTH_SHORT).show();
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
                                    });
                                }
                            }
                        }else{
                            Toast.makeText(getContext(), getString(R.string.dont_job_hasAccepted), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}