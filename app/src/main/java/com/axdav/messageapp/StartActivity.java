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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView registerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerView = findViewById(R.id.register_txtView);
        makeClickableTxtView();

        mAuth = FirebaseAuth.getInstance();

    }

    public void onStart(){
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
        updateUI(currUser);
    }

    private void makeClickableTxtView(){
        String text = "Not registered? Register";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
        ss.setSpan(clickableSpan,16,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerView.setText(ss);
        registerView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //hoppa direkt till inloggad skärm om användaren är inloggad
    public void updateUI(FirebaseUser currUser){
        if(currUser != null ){
            //display hello "name"
            //skicka direkt till inloggade skärmen
        }
    }
}
