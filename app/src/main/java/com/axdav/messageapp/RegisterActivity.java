package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import com.axdav.messageapp.Model.User;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView email,username,password;
    Button reg_button;
    FirebaseDatabase database;
    DatabaseReference myRef;
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

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passTxt = password.getText().toString();
                String emailTxt = email.getText().toString();
                String userTxt = username.getText().toString();
                if(passTxt.isEmpty() || emailTxt.isEmpty() || userTxt.isEmpty()){
                    Toast empty = Toast.makeText(RegisterActivity.this,"feilds cannot be empty",Toast.LENGTH_SHORT);
                    empty.show();
                } else{
                    createAcc(userTxt,emailTxt,passTxt);
                }
            }
        });

    }

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
                    Toast failed = Toast.makeText(RegisterActivity.this,"Authentication failed",Toast.LENGTH_SHORT);
                    failed.show();

                }
            }
        });
    }
}