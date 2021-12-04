package com.example.proyectoappnativa.Fragments.Enterprise;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.proyectoappnativa.Adapters.AdapterApplications;
import com.example.proyectoappnativa.Adapters.AdapterPeoppleAcepted;
import com.example.proyectoappnativa.Adapters.Offline.AdapterPostulationOf;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Db.DbPeoppleAceppted;
import com.example.proyectoappnativa.Db.DbPostulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.HomeActivity;
import com.example.proyectoappnativa.Interfaces.IComunicationFragmentApplications;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.PeoppleAcepted;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeoppleAceptedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeoppleAceptedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PeoppleAceptedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeoppleAceptedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeoppleAceptedFragment newInstance(String param1, String param2) {
        PeoppleAceptedFragment fragment = new PeoppleAceptedFragment();
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

    private RecyclerView recycler;
    private ArrayList<PeoppleAcepted> listPeopple;
    private ArrayList<PeoppleAcepted> listPeppleOffline = new ArrayList<>();
    private IComunicationFragmentApplications interfaceComunicaFragments;
    private fireService firebase = new fireService();
    private Activity activity;
    private String id, name;
    private Task<QuerySnapshot> dataUser;
    private Task<DocumentSnapshot> dataPeopple;
    FloatingActionButton fab;
    private static final int REQUEST_CALL = 1;
    AdapterPeoppleAcepted listAdapter;
    DbPeoppleAceppted dbPeoppleAceppted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_peopple_acepted, container, false);
        // pedir permiso de llamada
        recycler = root.findViewById(R.id.recyclePeoppleAcepted);
        listPeopple = new ArrayList<>();
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
        id = sharedPreferences.getString(getString(R.string.userId), String.valueOf(Context.MODE_PRIVATE));
        DbHelper dbHelper = new DbHelper(requireActivity());
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
                name = user.getName();
            }
        }

        dbPeoppleAceppted = new DbPeoppleAceppted(requireContext());


        if (Tools.isConnection(requireActivity())){
            dbPeoppleAceppted.onUpgradePeoppleAcepted(dbHelper.getWritableDatabase());
            getApplications();
            SQLiteDatabase db = dbPeoppleAceppted.getWritableDatabase();
            dbPeoppleAceppted.onUpgradePeoppleAcepted(db);
        }else{
            getApplicationsOffline();
        }
        return root;
    }

    private void getApplicationsOffline(){
        DbHelper dbHelper = new DbHelper(getContext());
        List<PeoppleAcepted> peoppleAcepteds = dbHelper.getPeoppleAcepted();
        listPeppleOffline.addAll(peoppleAcepteds);
        listAdapter = new AdapterPeoppleAcepted(listPeppleOffline, requireActivity());
        recycler.setAdapter(listAdapter);
        registerForContextMenu(recycler);
    }


    public void getApplications(){
        dataUser = firebase.getPeoppleAcepted(requireActivity(), name);
        dataUser.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Postulation postulation = documentSnapshot.toObject(Postulation.class);
                        if (postulation.getPostulantes_aceptados() != null) {
                            for (String id : postulation.getPostulantes_aceptados()) {
                                dataPeopple = firebase.getInfo(getString(R.string.UsuariosFirebase), id);
                                dataPeopple.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        PeoppleAcepted user = documentSnapshot.toObject(PeoppleAcepted.class);
                                        listPeopple.add(new PeoppleAcepted(user.getId(), user.getName(), user.getPhone(), postulation.getName(), user.getImageURL()));

                                        long uid = dbPeoppleAceppted.insertPeopple(
                                                user.getId(),
                                                user.getName(),
                                                postulation.getName(),
                                                user.getPhone());
                                        listAdapter = new AdapterPeoppleAcepted(listPeopple, requireActivity());
                                        recycler.setAdapter(listAdapter);
                                        registerForContextMenu(recycler);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}