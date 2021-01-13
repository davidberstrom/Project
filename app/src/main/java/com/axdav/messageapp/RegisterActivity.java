package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.axdav.messageapp.Model.User;
import com.google.firebase.database.ValueEventListener;

/*class used to register a new user*/
public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView email,username,password;
    private Button reg_button;
    private FirebaseDatabase database;
    private DatabaseReference myRef,usernamesRef;
    private List<String> allUsernames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        reg_button = findViewById(R.id.reg_button);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        allUsernames = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(allUsernames != null)
                allUsernames.clear();

                for(DataSnapshot snap : snapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    allUsernames.add(user.getUsername().toLowerCase());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passTxt = password.getText().toString();
                String emailTxt = email.getText().toString();
                String userTxt = username.getText().toString();
                if(passTxt.isEmpty() || emailTxt.isEmpty() || userTxt.isEmpty()){
                    Toast empty = Toast.makeText(RegisterActivity.this,"fields cannot be empty",Toast.LENGTH_SHORT);
                    empty.show();
                } else if(allUsernames.contains(userTxt.toLowerCase())) {
                        Toast sameUserName = Toast.makeText(RegisterActivity.this,"Username already exists",Toast.LENGTH_SHORT);
                        sameUserName.show();
                }else{
                    createAcc(userTxt,emailTxt,passTxt);
                }
            }
        });

    }
    /*method to creata new account and starts the LoggedInActivity if succesful*/
    private void createAcc(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String userId = auth.getCurrentUser().getUid();
                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(username,userId));
                    Intent intent = new Intent(RegisterActivity.this,LoggedInActivity.class);
                    startActivity(intent);

                }else{
                    Toast failed = Toast.makeText(RegisterActivity.this,"Failed to create account",Toast.LENGTH_SHORT);
                    failed.show();
                }
            }
        });
    }
}