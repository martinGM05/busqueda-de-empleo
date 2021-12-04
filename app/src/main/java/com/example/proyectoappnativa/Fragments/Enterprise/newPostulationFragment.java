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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Fragments.Peopple.PostulationFullFragment;
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
import java.util.Objects;

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
    private EditText editText;
    private FloatingActionButton fabSave, fabGallery, fabCamera;
    private TextInputEditText namePostulation, description, keywords;
    private fireService firebase = new fireService();
    private Task<DocumentSnapshot> user;
    private double lat=19.0413,lon=-98.2062;
    private CircleImageView imagePostulation;
    private int TAKE_PHOTO = 1;
    private int CHOOSE_IMAGE = 200;
    private Uri imagenUri;
    private short type = 0, photo = 0;
    private Postulation dataPostulation = new Postulation();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public newPostulationFragment() {
        // Required empty public constructor
    }

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
            postulation = (Postulation) dataPostulation.getSerializable(getString(R.string.dataPostulation));
            assignInformation(postulation);
            type = 1;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapPostulation);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Places.initialize(this.requireContext(), getString(R.string.google_maps_key));
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent= new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(requireContext());
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
            if(Objects.requireNonNull(keywords.getText()).toString().isEmpty()){
                keywords.setText(keyword);
            }else{
                keywords.setText(keywords.getText().toString()+","+keyword);
            }
        });
        Glide.with(requireActivity()).load(postulation.getImage()).into(imagePostulation);
        lat = postulation.getLat();
        lon = postulation.getLont();
        dataPostulation = postulation;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == CHOOSE_IMAGE){
            imagenUri = data.getData();
            imagePostulation.setImageURI(imagenUri);
        }else if(resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get(getString(R.string.data));
            imagenUri = getImageUri(requireContext(), imgBitmap);
            imagePostulation.setImageBitmap(imgBitmap);
        }
        if(requestCode==100 && resultCode== RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());
            lat = Objects.requireNonNull(place.getLatLng()).latitude;
            lon = Objects.requireNonNull(place.getLatLng()).longitude;
            mMap.clear();
            LatLng position = new LatLng(lat,lon);
            mMap.setMinZoomPreference(15);
            mMap.addMarker(new MarkerOptions().position(position).title(place.getAddress())
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
                markerOptions.title(latLng.latitude + " ; "+ latLng.longitude);
                lat = latLng.latitude;
                lon = latLng.longitude;
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);
            }
        });
    }
    

    public void savePostulation(short type){
        for(EditText editText : new EditText[]{namePostulation, description, keywords}){
            if(TextUtils.isEmpty(editText.getText().toString())){
                editText.setError(getString(R.string.emptyField));
                return;
            }
        }
        if(type == 0){
            if(imagenUri == null){
                AlertDialog.showAlertDialog(requireActivity(), getString(R.string.titleError), getString(R.string.insertImage));
                return;
            }
            Postulation postulation = new Postulation();
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.loginData), Context.MODE_PRIVATE);
            String id = sharedPreferences.getString(getString(R.string.userId), "");
            DbHelper dbHelper = new DbHelper(getContext());
            List<User> userData = dbHelper.getUserData(id);
            if(userData.size() > 0) {
                for (User user : userData) {
                    postulation.setCompany(user.getName());
                }
            }
            postulation.setName(String.valueOf(namePostulation.getText()));
            postulation.setDescription(String.valueOf(description.getText()));
            postulation.setLat(lat);
            postulation.setLont(lon);
            String[] categories = Objects.requireNonNull(keywords.getText()).toString().split(",");
            List<String> keywordsList = Arrays.asList(categories);
            postulation.setKeywords(keywordsList);
            firebase.createDocumentPostulation(requireActivity() ,postulation, imagenUri);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFragment()).commit();

        }else{
            dataPostulation.setName(String.valueOf(namePostulation.getText()));
            dataPostulation.setDescription(String.valueOf(description.getText()));
            dataPostulation.setLat(lat);
            dataPostulation.setLont(lon);
            firebase.updatePostulation(requireActivity(), dataPostulation, imagenUri, photo);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostulationFragment()).commit();
        }
    }

    @SuppressWarnings("deprecation")
    public void tomarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
            startActivityForResult(intent, TAKE_PHOTO);
        }
    }

    @SuppressWarnings("deprecation")
    public void seleccionaImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, CHOOSE_IMAGE);
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, getString(R.string.title), null);
        return Uri.parse(path);
    }
}