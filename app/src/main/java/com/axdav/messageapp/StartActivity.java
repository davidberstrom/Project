package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class StartActivity extends AppCompatActivity {
    private TextView registerView;
    private FirebaseAuth auth;
    private TextView email,password;
    private Button signIn_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        registerView = findViewById(R.id.register_txtView);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
        signIn_btn = findViewById(R.id.signIn_btn);
        makeClickableTxtView();

        signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();
                if(emailTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast err = Toast.makeText(StartActivity.this,"Cannot login with empty account dickhead",Toast.LENGTH_SHORT);
                    err.show();
                }else{
                    Signin(emailTxt,passwordTxt);
                }
            }
        });

    }

    public void onStart(){
        super.onStart();
        FirebaseUser currUser = auth.getCurrentUser();
        ifLoggedIn(currUser);
    }

    private void makeClickableTxtView(){
        String text = "Not registered? Register";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);
            }
        };
        ss.setSpan(clickableSpan,16,24,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerView.setText(ss);
        registerView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void ifLoggedIn(FirebaseUser user){
        if(user!= null){
            email.setText(user.getEmail());
        }
    }

    private void Signin(String email, String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(StartActivity.this,LoggedInActivity.class);
                    startActivity(intent);
                }else{
                    Toast err = Toast.makeText(StartActivity.this,"WRONG USERNAME,PASSWORD OR USER DOSENT EXIST",Toast.LENGTH_SHORT);
                    err.show();
                }
            }
        });
    }
}
