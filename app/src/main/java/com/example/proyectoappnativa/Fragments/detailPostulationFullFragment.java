package com.example.proyectoappnativa.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailPostulationFullFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailPostulationFullFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public detailPostulationFullFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detailPostulationFullFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static detailPostulationFullFragment newInstance(String param1, String param2) {
        detailPostulationFullFragment fragment = new detailPostulationFullFragment();
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

    TextView textName, textDescription;
    CircleImageView cvImage;
    FloatingActionButton fabAddApplications;
    Postulation dataPostulation = new Postulation();
    fireService firebase = new fireService();
    String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_postulation_full, container, false);
        textName = (TextView) root.findViewById(R.id.tvNamePostulation);
        textDescription = root.findViewById(R.id.tvDescriptionPostulation);
        cvImage = root.findViewById(R.id.cvImagePostulation);
        fabAddApplications = root.findViewById(R.id.fabApplicationsAdd);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginData", getContext().MODE_PRIVATE);
         idUser = sharedPreferences.getString("userId", String.valueOf(getContext().MODE_PRIVATE));

        Bundle objectPostulation = getArguments();
        Postulation postulation = null;

        if(objectPostulation != null){
            postulation = (Postulation) objectPostulation.getSerializable("postulation");
            assignInformation(postulation);
        }

        fabAddApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addApplications(dataPostulation.getId(), idUser);
            }
        });

        return root;
    }

    private void addApplications(String idPostulation, String idUser){
        PostulationFullFragment listPostulation = new PostulationFullFragment();
        try {
            firebase.addApplications(idPostulation, idUser);
            Snackbar.make(requireActivity().findViewById(R.id.content), R.string.addApplications, Snackbar.LENGTH_LONG).setBackgroundTint(getContext().getColor(R.color.secondaryDarkColor)).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, listPostulation).commit();
        }catch (Exception err){
            Snackbar.make(requireActivity().findViewById(R.id.content), R.string.titleError, Snackbar.LENGTH_LONG).setBackgroundTint(getContext().getColor(R.color.secondaryDarkColor)).show();
        }
    }

    public void assignInformation(Postulation postulation) {
       textName.setText(postulation.getName());
       textDescription.setText(postulation.getDescription());
       Glide.with(this).load(postulation.getImage()).into(cvImage);
        dataPostulation = postulation;

    }


}