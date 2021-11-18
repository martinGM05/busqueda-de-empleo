package com.example.proyectoappnativa.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_detail_postulation, container, false);

        textName = (TextView) vista.findViewById(R.id.namePostulation);
        textDescription = (TextView) vista.findViewById(R.id.descriptionPostulation);

        Bundle objectPostulation = getArguments();
        Postulation postulation = null;

        if(objectPostulation != null){
            postulation = (Postulation) objectPostulation.getSerializable("object");
            assignInformation(postulation);
        }

        return vista;
    }

    public void assignInformation(Postulation postulation) {
        textName.setText(postulation.getName());
        textDescription.setText(postulation.getDescription());
    }
}