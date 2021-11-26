package com.example.proyectoappnativa.Fragments.Enterprise;

import android.app.Activity;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Adapters.AdapterApplications;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Interfaces.IComunicationFragmentApplications;
import com.example.proyectoappnativa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

    TextView textName, textDescription;
    CircleImageView ivPhoto;
    RecyclerView recycler;
    com.example.proyectoappnativa.Fragments.detailProfileFragment detailProfileFragment;
    Activity activity;
    IComunicationFragmentApplications interfaceComunicaFragments;
    fireService firebase = new fireService();
    Task<DocumentSnapshot> dataApplications, dataUser;
    String idDocument = "";
    AdapterApplications adapter;
    FloatingActionButton fabEdit;
    Bundle dataPostulation = new Bundle();
    Postulation postulationInfo = new Postulation();

    public detailPostulationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detailPostulationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static detailPostulationFragment newInstance(String param1, String param2) {
        detailPostulationFragment fragment = new detailPostulationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ArrayList<User> listApplications;

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
        View vista = inflater.inflate(R.layout.fragment_detail_postulation, container, false);

        ivPhoto = (CircleImageView) vista.findViewById(R.id.imagePostulationDetail);
        textName = (TextView) vista.findViewById(R.id.namePostulationDetail);
        fabEdit = vista.findViewById(R.id.fabEditPostulation);


        listApplications = new ArrayList<>();
        recycler = vista.findViewById(R.id.recyclePostulation);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle objectPostulation = getArguments();
        Postulation postulation = null;

        if(objectPostulation != null){
            postulation = (Postulation) objectPostulation.getSerializable("postulation");
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

        return vista;
    }

    private void editPostulation(){
        newPostulationFragment editPostulation = new newPostulationFragment();
        dataPostulation.putSerializable("dataPostulation", postulationInfo);
        editPostulation.setArguments(dataPostulation);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, editPostulation).commit();
    }


    public void assignInformation(Postulation postulation) {
        Glide.with(getActivity()).load(postulation.getImage()).into(ivPhoto);
        textName.setText(postulation.getName());
        idDocument = postulation.getId();
        postulationInfo = postulation;
    }

    public void fill(){


        listApplications.add(new User("1", "Martin Gonzalez", "mtn.gm05@gmail.com", "", "", ""));
        listApplications.add(new User("1", "Manuel Fransisco", "mtn.gm05@gmail.com", "", "", ""));

        AdapterApplications adapter = new AdapterApplications(listApplications, getActivity());
        recycler.setAdapter(adapter);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(getContext(), "Selecciona: " + listApplications.get(recycler.getChildAdapterPosition(v)).getName()
                ,Toast.LENGTH_LONG).show();*/
                interfaceComunicaFragments.sendApplications(listApplications.get(recycler.getChildAdapterPosition(v)));

            }
        });
    }

    public void getApplications(String idDocument){
        dataApplications = firebase.getApplicationsFirebase(idDocument);
        dataApplications.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        //Toast.makeText(activity, "Nombre: " + name, Toast.LENGTH_SHORT).show();
                        List<String> postulantes = (List<String>) document.get("postulantes");
                        if(postulantes != null){
                            for(String postulante : postulantes){
                                System.out.println("Postulante: " + postulante);
                                dataUser = firebase.getInfoUser(postulante);
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
        firebase.updateApplicationsFirebase(idDocument, lista);
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