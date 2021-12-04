package com.example.proyectoappnativa.Fragments.Peopple;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.maps.android.PolyUtil;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailPostulationFullFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailPostulationFullFragment extends Fragment implements OnMapReadyCallback {

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

    private TextView textName, textDescription;
    private CircleImageView cvImage;
    private FloatingActionButton fabAddApplications;
    private Postulation dataPostulation = new Postulation();
    private final fireService firebase = new fireService();
    private String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_postulation_full, container, false);
        textName = root.findViewById(R.id.tvNamePostulation);
        textDescription = root.findViewById(R.id.tvDescriptionPostulation);
        cvImage = root.findViewById(R.id.cvImagePostulation);
        fabAddApplications = root.findViewById(R.id.fabApplicationsAdd);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(getString(R.string.userId), String.valueOf(Context.MODE_PRIVATE));

        Bundle objectPostulation = getArguments();
        Postulation postulation = null;

        if(objectPostulation != null){
            postulation = (Postulation) objectPostulation.getSerializable(getString(R.string.postulationBundle));
            assignInformation(postulation);
        }

        fabAddApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addApplications(dataPostulation.getId(), idUser);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapPostulation);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        return root;
    }

    private void addApplications(String idPostulation, String idUser){
        PostulationFullFragment listPostulation = new PostulationFullFragment();
        try {
            firebase.addApplications(requireActivity(), idPostulation, idUser);
        }catch (Exception err){
            Snackbar.make(requireActivity().findViewById(R.id.content), R.string.titleError, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(requireContext().getColor(R.color.secondaryDarkColor)).show();
        }
    }



    public void assignInformation(Postulation postulation) {
        textName.setText(postulation.getName());
        textDescription.setText(postulation.getDescription());
        Glide.with(this).load(postulation.getImage()).into(cvImage);
        dataPostulation = postulation;
    }

    GoogleMap map;
    Boolean actualPosition = true;
    JSONObject jso;
    Double longitudOrigen, latitudOrigen;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(location -> {
            if (actualPosition){
                latitudOrigen = location.getLatitude();
                longitudOrigen = location.getLongitude();
                actualPosition=false;
                LatLng positionWork = new LatLng(dataPostulation.getLat(),dataPostulation.getLont());
                map.addMarker(new MarkerOptions().position(positionWork).title(dataPostulation.getName()));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitudOrigen,longitudOrigen)).zoom(17).bearing(90).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                String url =getString(R.string.urlMaps)+latitudOrigen+","+longitudOrigen+getString(R.string.destinationMaps)+dataPostulation.getLat()+","+dataPostulation.getLont();
                RequestQueue queue = Volley.newRequestQueue(requireActivity());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jso = new JSONObject(response);
                            trazarRuta(jso);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);
            }
        });
    }

    private void trazarRuta(JSONObject jso) {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jso.getJSONArray(getString(R.string.routes));
            for (int i=0; i<jRoutes.length();i++){
                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray(getString(R.string.legs));
                for (int j=0; j<jLegs.length();j++){
                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray(getString(R.string.steps));
                    for (int k = 0; k<jSteps.length();k++){
                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k))
                                .get(getString(R.string.polyline))).get(getString(R.string.points));
                        List<LatLng> list = PolyUtil.decode(polyline);
                        map.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(5));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}