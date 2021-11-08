package Firebase;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoappnativa.AuthActivity;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import Models.User;

public class fireService{


    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User userModel = new User();


    public void createUser(RegisterActivity context, String email, String name, String password, String description, String type){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String userId = mAuth.getCurrentUser().getUid();
                    userModel.setId(userId);
                    userModel.setName(name);
                    userModel.setEmail(email);
                    userModel.setDescription(description);
                    userModel.setType(type);
                    createDocument(context);
                }
            }
        });
    }


    public void createDocument(Activity context){
        db.collection("Usuarios")
                .document(userModel.getId()).set(userModel).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, R.string.userCreated, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, R.string.userCreatedError, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

}
