package com.example.proyectoappnativa.Fragments.Enterprise;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Adapters.AdapterApplications;
import com.example.proyectoappnativa.Db.DbPeoppleAceppted;
import com.example.proyectoappnativa.Db.DbPostulation;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicationFragmentApplications;
import com.example.proyectoappnativa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailPostulationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailPostulationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView textName;
    private CircleImageView ivPhoto;
    private RecyclerView recycler;
    private Activity activity;
    private IComunicationFragmentApplications interfaceComunicaFragments;
    private fireService firebase = new fireService();
    private Task<DocumentSnapshot> dataApplications, dataUser;
    private String idDocument = "";
    private AdapterApplications adapter;
    private FloatingActionButton fabEdit;
    private Bundle dataPostulation = new Bundle();
    private Postulation postulationInfo = new Postulation();
    private ArrayList<User> listApplications;
    private SharedPreferences sharedPref;
    private StorageReference mStorageRef;


    public detailPostulationFragment() {
        // Required empty public constructor
    }

    public static detailPostulationFragment newInstance(String param1, String param2) {
        detailPostulationFragment fragment = new detailPostulationFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_postulation, container, false);

        ivPhoto = root.findViewById(R.id.imagePostulationDetail);
        textName = root.findViewById(R.id.namePostulationDetail);
        fabEdit = root.findViewById(R.id.fabEditPostulation);
        listApplications = new ArrayList<>();
        recycler = root.findViewById(R.id.recyclePostulation);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle objectPostulation = getArguments();
        Postulation postulation = null;

        if(objectPostulation != null){
            postulation = (Postulation) objectPostulation.getSerializable(getString(R.string.postulationBundle));
            assignInformation(postulation);
            getApplications(idDocument);
        }

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler);

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPostulation();
            }
        });

        return root;
    }

    private void editPostulation(){
        newPostulationFragment editPostulation = new newPostulationFragment();
        dataPostulation.putSerializable(getString(R.string.dataPostulation), postulationInfo);
        editPostulation.setArguments(dataPostulation);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, editPostulation).commit();
    }


    public void assignInformation(Postulation postulation) {
        Glide.with(requireActivity()).load(postulation.getImage()).into(ivPhoto);
        textName.setText(postulation.getName());
        idDocument = postulation.getId();
        postulationInfo = postulation;
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.idPostulation), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.idPostulation), idDocument);
        editor.apply();
    }

    public void getApplications(String idDocument){
        dataApplications = firebase.getInfo(getString(R.string.PostulacionesFirebase), idDocument);
        dataApplications.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> postulantes = (List<String>) document.get(getString(R.string.postulantesFirebaseField));
                        if(postulantes != null){
                            for(String postulante : postulantes){
                                dataUser = firebase.getInfo(getString(R.string.UsuariosFirebase), postulante);
                                dataUser.addOnCompleteListener(documentSnapshot -> {
                                    if(documentSnapshot.getResult().exists()){
                                        User user = documentSnapshot.getResult().toObject(User.class);
                                        listApplications.add(user);
                                    }
                                    adapter = new AdapterApplications(listApplications, getActivity());
                                    recycler.setAdapter(adapter);
                                    adapter.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            interfaceComunicaFragments.sendApplications(listApplications.get(recycler.getChildAdapterPosition(v)));
                                        }
                                    });
                                });
                            }
                        }else{
                            Toast.makeText(activity, getString(R.string.dont_postulates), Toast.LENGTH_SHORT).show();
                        }                        
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  Activity){
            this.activity = (Activity) context;
            interfaceComunicaFragments = (IComunicationFragmentApplications) this.activity;
        }
    }

    private void removeApplications(String idDocument){
        List<String> lista = new ArrayList<>();
        for(User user : listApplications){
            lista.add(user.getId());
        }
        firebase.updateApplicationsFirebase(requireActivity(), idDocument, lista);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            listApplications.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
            removeApplications(idDocument);
        }
    };

}