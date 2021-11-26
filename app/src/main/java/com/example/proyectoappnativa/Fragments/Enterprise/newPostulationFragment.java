package com.example.proyectoappnativa.Fragments.Enterprise;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.ToolsCarpet.AlertDialog;
import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    FloatingActionButton fabSave, fabGallery, fabCamera;
    TextInputEditText namePostulation, description, keywords;
    fireService firebase = new fireService();
    Task<DocumentSnapshot> user;
    double lat=19.0413,lon=-98.2062;
    CircleImageView imagePostulation;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;
    Uri imagenUri;
    short type = 0, photo = 0;
    Postulation dataPostulation = new Postulation();

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


        Bundle dataPostulation = getArguments();
        Postulation postulation = null;

        editText= root.findViewById(R.id.edit_text);
        keywords = root.findViewById(R.id.keyWords);
        fabSave = root.findViewById(R.id.fabSavePostulation);
        namePostulation = root.findViewById(R.id.namePostulation);
        description = root.findViewById(R.id.descriptionPostulation);
        imagePostulation = root.findViewById(R.id.cvImagePostulation);
        fabGallery = root.findViewById(R.id.fabPictureWithImage);
        fabCamera = root.findViewById(R.id.fabPictureWithCamera);

        if(dataPostulation != null){
            postulation = (Postulation) dataPostulation.getSerializable("dataPostulation");
            assignInformation(postulation);
            type = 1;
        }


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapPostulation);
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
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePostulation(type);
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
                photo = 1;
            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionaImagen();
                photo = 1;
            }
        });

        return root;
    }

    public void assignInformation(Postulation postulation) {
        namePostulation.setText(postulation.getName());
        description.setText(postulation.getDescription());
        postulation.getKeywords().forEach(keyword -> {
            if(keywords.getText().toString().isEmpty()){
                keywords.setText(keyword);
            }else{
                keywords.setText(keywords.getText().toString()+","+keyword);
            }
        });
        Glide.with(getActivity()).load(postulation.getImage()).into(imagePostulation);
        lat = postulation.getLat();
        lon = postulation.getLont();
        dataPostulation = postulation;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == SELEC_IMAGEN){
            imagenUri = data.getData();
            imagePostulation.setImageURI(imagenUri);
        }else if(resultCode == Activity.RESULT_OK && requestCode == 1){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            imagenUri = getImageUri(requireContext(), imgBitmap);
            imagePostulation.setImageBitmap(imgBitmap);
        }


        if(requestCode==100 && resultCode== RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());
            //Toast.makeText(getContext(),String.valueOf(place.getLatLng().longitude),Toast.LENGTH_SHORT).show();
            lat=place.getLatLng().latitude;
            lon=place.getLatLng().longitude;
            mMap.clear();
            LatLng position = new LatLng(lat,lon);
            mMap.setMinZoomPreference(15);
            mMap.addMarker(new MarkerOptions().position(position).title(""+place.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(15);
        LatLng position = new LatLng(lat,lon);
        mMap.addMarker(new MarkerOptions().position(position).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(""+latLng.latitude + " ; "+ latLng.longitude);
                lat = latLng.latitude;
                lon = latLng.longitude;
                //Toast.makeText(getContext(),""+String.valueOf(latLng),Toast.LENGTH_SHORT).show();
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);
            }
        });
    }
    

    public void savePostulation(short type){
        if(type == 0){
            Postulation postulation = new Postulation();
            // GET id for sharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginData", getContext().MODE_PRIVATE);
            String id = sharedPreferences.getString("userId", "");
            DbHelper dbHelper = new DbHelper(getContext());
            List<User> userData = dbHelper.getUserData(id);
            if(userData.size() > 0) {
                for (User user : userData) {
                    postulation.setCompany(user.getName());
                }
            }
            if(TextUtils.isEmpty(namePostulation.getText()) || TextUtils.isEmpty(description.getText())){
                AlertDialog.alertEmptyFields(getActivity());
            }else{
                postulation.setName(String.valueOf(namePostulation.getText()));
                postulation.setDescription(String.valueOf(description.getText()));
                postulation.setLat(lat);
                postulation.setLont(lon);
                String[] categories = keywords.getText().toString().split(",");
                List<String> keywordsList = Arrays.asList(categories);
                postulation.setKeywords(keywordsList);
                firebase.createDocumentPostulation(getActivity(), postulation, imagenUri);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFragment()).commit();
            }
        }else{
            dataPostulation.setName(String.valueOf(namePostulation.getText()));
            dataPostulation.setDescription(String.valueOf(description.getText()));

            dataPostulation.setLat(lat);
            dataPostulation.setLont(lon);
            firebase.updatePostulation(getActivity(), dataPostulation, imagenUri, photo);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFragment()).commit();
        }
    }

    @SuppressWarnings("deprecation")
    public void tomarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
            startActivityForResult(intent, 1);
        }
    }

    @SuppressWarnings("deprecation")
    public void seleccionaImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, SELEC_IMAGEN);
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



}