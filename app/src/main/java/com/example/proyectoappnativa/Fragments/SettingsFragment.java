package com.example.proyectoappnativa.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.HomeActivity;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    SwitchMaterial swSpain, swEnglish, swFranc;
    Button btnSave;
    String languague = "";
    private SharedPreferences sharedPref;
    String id;
    String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPref = requireActivity().getSharedPreferences(getString(R.string.SettingsShared), Context.MODE_PRIVATE);
        String languageSave = sharedPref.getString(getString(R.string.My_LanguagueShared), String.valueOf(Context.MODE_PRIVATE));

        swSpain = root.findViewById(R.id.swSpain);
        swEnglish = root.findViewById(R.id.swEnglis);
        swFranc = root.findViewById(R.id.swFrances);
        btnSave = root.findViewById(R.id.btnSaveLanguague);

        if(languageSave != null){
            if(languageSave.equals(getString(R.string.codeSpain))){
                swSpain.setChecked(true);
                languague = getString(R.string.codeSpain);
            }else if(languageSave.equals(getString(R.string.codeEnglish))){
                swEnglish.setChecked(true);
                languague = getString(R.string.codeEnglish);
            }else if(languageSave.equals(getString(R.string.codeFrench))){
                swFranc.setChecked(true);
                languague = getString(R.string.codeFrench);
            }
        }

        swSpain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swSpain.isChecked()) {
                    swEnglish.setChecked(false);
                    swFranc.setChecked(false);
                }
            }
        });
    
        swEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swEnglish.isChecked()) {
                    swSpain.setChecked(false);
                    swFranc.setChecked(false);
                }
            }
        });

        swFranc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swFranc.isChecked()) {
                    swSpain.setChecked(false);
                    swEnglish.setChecked(false);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swSpain.isChecked()) {
                    languague = "es";
                } else if (swEnglish.isChecked()) {
                    languague = "en";
                } else if (swFranc.isChecked()) {
                    languague = "fr";
                }
                changeLanguage(languague);
            }
        });


        return root;
    }

    public void changeLanguage(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        requireActivity().getBaseContext().getResources().updateConfiguration(config, requireActivity().getBaseContext().getResources().getDisplayMetrics());
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.SettingsShared), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.My_LanguagueShared), language);
        editor.apply();

        sharedPref = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
        id = sharedPref.getString(getString(R.string.userId), String.valueOf(Context.MODE_PRIVATE));

        DbHelper dbHelper = new DbHelper(requireActivity());
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
                type = user.getType();
            }
        }

        Intent intent = new Intent(requireActivity(), HomeActivity.class);
        intent.putExtra(getString(R.string.typeUser), type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

   

}