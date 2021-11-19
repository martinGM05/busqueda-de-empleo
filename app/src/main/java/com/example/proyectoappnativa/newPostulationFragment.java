package com.example.proyectoappnativa;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link newPostulationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class newPostulationFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GoogleMap mMap;

    EditText editText;
    TextView textView1, textView2;
    double lat=19.0413,lon=-98.2062;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public newPostulationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment newPostulationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static newPostulationFragment newInstance(String param1, String param2) {
        newPostulationFragment fragment = new newPostulationFragment();
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



        View root = inflater.inflate(R.layout.fragment_new_postulation, container, false);

        editText= root.findViewById(R.id.edit_text);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Places.initialize(this.getContext(), "AIzaSyC0PZVZz6Pxy74Hal64-VtZSDOyYrqTMEE");
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent= new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getContext());
                startActivityForResult(intent, 100);
            }
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode== RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());
            //textView1.setText(String.format("Locality name: %s", place.getName()));
            //textView2.setText(String.valueOf(place.getLatLng()));
            Toast.makeText(getContext(),String.valueOf(place.getLatLng().longitude),Toast.LENGTH_SHORT).show();
            lat=place.getLatLng().latitude;
            lon=place.getLatLng().longitude;
            mMap.clear();
            LatLng sydney = new LatLng(lat,lon);
            mMap.setMinZoomPreference(10);
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title(""+place.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }else if(resultCode== AutocompleteActivity.RESULT_ERROR){
            Status status= Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(),status.getStatusMessage()
                    ,Toast.LENGTH_SHORT).show();

        }}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(7);
        Toast.makeText(this.getContext(),""+lat,Toast.LENGTH_SHORT).show();
        //Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat,lon);
        //LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(""+latLng.latitude + " ; "+ latLng.longitude);
               // textView2.setText(String.valueOf(latLng));
                Toast.makeText(getContext(),""+String.valueOf(latLng),Toast.LENGTH_SHORT).show();
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);
            }
        });



    }
}