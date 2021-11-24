package com.example.proyectoappnativa.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Entidades.Postulation;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.util.List;

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
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapPostulation);
        mapFragment.getMapAsync(this);
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
        //latitude= postulation.getLat();
        //longitude=postulation.getLont();


    }

    GoogleMap map;
    Boolean actualPosition = true;
    JSONObject jso;
    Double longitudOrigen, latitudOrigen;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }




            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (actualPosition){
                    //Cordernadas en tiempo real
                    latitudOrigen = location.getLatitude();
                    longitudOrigen = location.getLongitude();
                    actualPosition=false;

                    //Aqui declaras las cordenadas del lugar predeterminado
                    LatLng posisicioTrabajo = new LatLng(dataPostulation.getLat(),dataPostulation.getLont());
                    //pones nombre si es que lo deseas
                    map.addMarker(new MarkerOptions().position(posisicioTrabajo).title(""+dataPostulation.getName()));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitudOrigen,longitudOrigen))      // Sets the center of the map to Mountain View
                            .zoom(17)
                            .bearing(90)// Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    //sustituyes las ultimas cordenadas que son del lugar que pusiste en posisicioTrabajo
                    String url ="https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyC0PZVZz6Pxy74Hal64-VtZSDOyYrqTMEE&origin="+latitudOrigen+","+longitudOrigen+"&destination="+dataPostulation.getLat()+","+dataPostulation.getLont();
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                jso = new JSONObject(response);
                                trazarRuta(jso);
                                Log.i("jsonRuta: ",""+response);
                                Log.i("UserLoginTask", "PASÉ POR AQUÍ");
                            } catch (JSONException e) {
                                Log.i("UserLoginTask", "Error men");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    queue.add(stringRequest);
                }
            }


        });

    }

    private void trazarRuta(JSONObject jso) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k<jSteps.length();k++){


                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        map.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(5));



                    }



                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}