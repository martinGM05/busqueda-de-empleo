package com.example.proyectoappnativa.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public detailProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static detailProfileFragment newInstance(String param1, String param2) {
        detailProfileFragment fragment = new detailProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    TextView textName, textDescription, textEmail;
    CircleImageView ivPhoto;
    FloatingActionButton fab;

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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_detail_profile, container, false);

        textName =  (TextView) root.findViewById(R.id.idNameProfile);
        textEmail = (TextView) root.findViewById(R.id.idEmailProfile);
        textDescription =  (TextView) root.findViewById(R.id.idDescriptionProfile);
        ivPhoto = (CircleImageView) root.findViewById(R.id.imagePostulationDetail);

        Bundle objectProfile = getArguments();
        User user = null;

        if(objectProfile != null){
            user = (User) objectProfile.getSerializable("objectProfile");
            assignInformation(user);
        }
        return root;
    }

    public void assignInformation(User user) {
        textName.setText(user.getName());
        textEmail.setText(user.getEmail());
        textDescription.setText(user.getDescription());
        Glide.with(this).load(user.getImageURL()).into(ivPhoto);
    }
}