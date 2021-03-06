package com.example.proyectoappnativa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterApplications extends RecyclerView.Adapter<AdapterApplications.ApplicationsViewHolder> implements View.OnClickListener {

    ArrayList<User> applications;
    private View.OnClickListener listener;
    Activity activity;
    String idUser;
    fireService firebase = new fireService();
    Task<DocumentSnapshot> data;
    Postulation postulation = new Postulation();

    public AdapterApplications(ArrayList<User> applications, Activity activity) {
        this.applications = applications;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterApplications.ApplicationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applications_list_layout, null, false);
        view.setOnClickListener(this);
        return new AdapterApplications.ApplicationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterApplications.ApplicationsViewHolder holder, int position) {
        User user = applications.get(position);
        holder.textName.setText(user.getName());
        holder.textDescription.setText(user.getEmail());
        Glide.with(activity).load(user.getImageURL()).into(holder.ivPhoto);
        idUser = user.getId();
        holder.user = user;
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }

    public class ApplicationsViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDescription;
        CircleImageView ivPhoto;
        Button btnUsers;
        User user;
        private SharedPreferences sharedPref;
        private StorageReference mStorageRef;

        public ApplicationsViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.namePeopple);
            textDescription = (TextView) itemView.findViewById(R.id.txtPostulation);
            ivPhoto = (CircleImageView) itemView.findViewById(R.id.ivPeoppleAcepted);
            btnUsers = (Button) itemView.findViewById(R.id.btnApplications);
            sharedPref = activity.getSharedPreferences(activity.getString(R.string.idPostulation), Context.MODE_PRIVATE);
            String idPostulacion = sharedPref.getString(activity.getString(R.string.idPostulation), String.valueOf(Context.MODE_PRIVATE));
            getInfoPostulation(idPostulacion);


            btnUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendNotificationTo(user.getId());
                    apply(idPostulacion, user.getId());
                }
            });

        }
    }

    private void getInfoPostulation(String id){
        data = firebase.getInfo(activity.getString(R.string.PostulacionesFirebase), id);
        data.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Postulation dataPostulation = document.toObject(Postulation.class);
                        postulation.setCompany(dataPostulation.getCompany());
                        postulation.setName(dataPostulation.getName());
                        postulation.setPostulantes_aceptados(dataPostulation.getPostulantes_aceptados());
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.notExist), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, activity.getString(R.string.titleError), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apply(String idPostulation, String id){
        firebase.applyPostulate(activity, idPostulation, id);
    }

    private void sendNotificationTo(String id){
        RequestQueue myRequest = Volley.newRequestQueue(activity.getApplicationContext());
        JSONObject json = new JSONObject();
        data = firebase.getInfo(activity.getString(R.string.UsuariosFirebase), id);
        data.addOnCompleteListener(documentSnapshot -> {
            String token;
            token = documentSnapshot.getResult().getString(activity.getString(R.string.tokenFCM));
            try{
                json.put("to", token);
                JSONObject notification = new JSONObject();
                notification.put("titulo", postulation.getCompany());
                notification.put("detalle", activity.getString(R.string.jobAceptNoti) + " " +postulation.getName());
                json.put("data", notification);
                String URL = "https://fcm.googleapis.com/fcm/send";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json,null, null){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("content-type", "application/json");
                        header.put("authorization", activity.getString(R.string.keyFCM));
                        return header;
                    }
                };
                myRequest.add(request);
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }
}
