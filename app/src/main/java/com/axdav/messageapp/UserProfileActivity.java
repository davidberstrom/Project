package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.axdav.messageapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    private ImageButton friendButton;
    private TextView username;
    private String usernameTxt,userId;
    private DatabaseReference friendReq,users;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean alreadyFriends;
    private List<String> friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        friendButton = findViewById(R.id.add_friend_btn);
        username = findViewById(R.id.profile_username);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        friendReq = FirebaseDatabase.getInstance().getReference("FriendRequests");
        users = FirebaseDatabase.getInstance().getReference("Users");
        userId = getIntent().getStringExtra("ID");
        usernameTxt = getIntent().getStringExtra("NAME");
        username.setText(usernameTxt);
        friends = new ArrayList<>();
        getFriends();
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFriendRequest();
            }
        });

    }

    private void sendFriendRequest() {
        friendReq.child(currentUser.getUid()).child("SentTo").setValue(userId);
        friendReq.child(userId).child("SentFrom").setValue(currentUser.getUid());
        friendButton.setVisibility(View.INVISIBLE);
    }
    private void getFriends(){
        DatabaseReference FriendsReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Friends");
        
        FriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    String s = snap.getValue(String.class);
                    friends.add(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}