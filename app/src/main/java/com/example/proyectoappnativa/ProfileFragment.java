package com.example.proyectoappnativa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.example.proyectoappnativa.Db.DbHelper;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Models.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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


    String idUser;
    FloatingActionButton fab;
    TextInputEditText inputName, inputDescription;
    Button btnTomarFoto, btnSeleccionarImagen;
    CircleImageView ivFoto;
    Uri imagenUri;
    FirebaseAuth mAuth;
    fireService firebase = new fireService();
    User usuario = new User();
    RequestQueue request;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_profile, container, false);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        fab = root.findViewById(R.id.fabSaveProfile);
        inputName = root.findViewById(R.id.inputName);
        inputDescription = root.findViewById(R.id.inputDescription);

        btnTomarFoto = root.findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = root.findViewById(R.id.btnSelectImage);

        ivFoto = root.findViewById(R.id.imageP);
        request = Volley.newRequestQueue(requireContext());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },0);
        }

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionaImagen();
            }
        });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserStatus();
    }

    public void checkUserStatus(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("userId", String.valueOf(Activity.MODE_PRIVATE));
        idUser = id;
        if(!id.isEmpty()){
            getInfoUserOffline(id);
/*            if(isConnection()){
                getInfoUser(id);
            }else{
                getInfoUserOffline(id);
            }  */
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
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

    private void cargarImage(String urlImage){
        String url = urlImage;
        url = url.replace(" ","%20");
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ivFoto.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.textErrorImagen, Toast.LENGTH_LONG);
            }
        });
        request.add(imageRequest);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == SELEC_IMAGEN){
            imagenUri = data.getData();
            ivFoto.setImageURI(imagenUri);
        }else if(resultCode == Activity.RESULT_OK && requestCode == 1){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            imagenUri = getImageUri(requireContext(), imgBitmap);
            ivFoto.setImageBitmap(imgBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void updateUser(){
        String name = inputName.getText().toString();
        String description = inputDescription.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(description)){
            AlertDialog.alertEmptyFields(getActivity());
        }else{
            try {
                firebase.updateUser(getActivity(), idUser, name, usuario.getEmail(),description, usuario.getType(),imagenUri);
                Snackbar.make(requireActivity().findViewById(R.id.content), R.string.userUpdated, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.secondaryDarkColor)).show();
            }catch (Exception err){
                Snackbar.make(requireActivity().findViewById(R.id.content), R.string.titleError, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.secondaryDarkColor)).show();
            }

        }
    }

    public void getInfoUserOffline(String id){
        DbHelper dbHelper = new DbHelper(getContext());
        List<User> userData = dbHelper.getUserData(id);
        if(userData.size() > 0) {
            for (User user : userData) {
               inputName.setText(user.getName());
               inputDescription.setText(user.getDescription());
               usuario.setType(user.getType());
               usuario.setEmail(user.getEmail());
              // cargarImage(user.getImageURL());
            }
        }
    }

}