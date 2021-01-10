package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.axdav.messageapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    private Button addFriendButton, acceptFriendButton, declineFriendButton, cancelFriendButton;
    private TextView username;
    private String usernameTxt,userId;
    private DatabaseReference friendReq,users;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private User currentProfile, currentUser;
    private List<String> currentProfilefriends,currentUserFriends;
    private boolean keepRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        username = findViewById(R.id.profile_username);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        friendReq = FirebaseDatabase.getInstance().getReference("FriendRequests");
        users = FirebaseDatabase.getInstance().getReference("Users");
        usernameTxt = getIntent().getStringExtra("NAME");
        username.setText(usernameTxt);
        currentUserFriends = new ArrayList<>();
        currentProfilefriends = new ArrayList<>();
        initializeButtons();
    }

    private void sendFriendRequest() {
        friendReq.child(firebaseUser.getUid()).child(userId).setValue("sender");
        friendReq.child(userId).child(firebaseUser.getUid()).setValue("receiver");
        addFriendButton.setVisibility(View.INVISIBLE);
    }
    private void getFriends(){
        DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUserId());
       DatabaseReference currentProfileReference = FirebaseDatabase.getInstance().getReference("Friends").child(currentProfile.getUserId());

       currentUserReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot snap : snapshot.getChildren()){
                 currentUserFriends.add(snap.getKey());
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        currentProfileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    currentProfilefriends.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initializeButtons(){
        addFriendButton = findViewById(R.id.add_friend_btn);
        acceptFriendButton = findViewById(R.id.accept_friend_btn);
        declineFriendButton = findViewById(R.id.decline_friend_btn);
        cancelFriendButton = findViewById(R.id.cancel_friend_btn);
        addFriendButton.setVisibility(View.VISIBLE);
        acceptFriendButton.setVisibility(View.INVISIBLE);
        declineFriendButton.setVisibility(View.INVISIBLE);
        cancelFriendButton.setVisibility(View.INVISIBLE);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    if(user.getUsername().equals(usernameTxt)){
                        userId = snap.getKey();
                        currentProfile = user;
                    }
                    if(user.getUserId().equals(firebaseUser.getUid())){
                        currentUser = user;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference currentUserInFriendRequests = friendReq.child(firebaseUser.getUid());
        if(currentUserInFriendRequests != null){
            currentUserInFriendRequests.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getKey().equals(userId)){
                            if(snap.getValue().equals("sender")){
                                addFriendButton.setVisibility(View.INVISIBLE);
                                acceptFriendButton.setVisibility(View.INVISIBLE);
                                declineFriendButton.setVisibility(View.INVISIBLE);
                                cancelFriendButton.setVisibility(View.VISIBLE);
                            } else if(snap.getValue().equals("receiver")){
                                addFriendButton.setVisibility(View.INVISIBLE);
                                acceptFriendButton.setVisibility(View.VISIBLE);
                                declineFriendButton.setVisibility(View.VISIBLE);
                                cancelFriendButton.setVisibility(View.INVISIBLE);
                            } else {
                                addFriendButton.setVisibility(View.VISIBLE);
                                acceptFriendButton.setVisibility(View.INVISIBLE);
                                declineFriendButton.setVisibility(View.INVISIBLE);
                                cancelFriendButton.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast toast = Toast.makeText(this, "Somethink went wronk", Toast.LENGTH_LONG);
            toast.show();
        }


    }

    public void onClickAdd(View view){
        Log.i("CREATION", "Added " + userId);
        addFriendButton.setVisibility(View.INVISIBLE);
        cancelFriendButton.setVisibility(View.VISIBLE);
        sendFriendRequest();
    }

    public void onClickAccept(View view){
        Log.i("CREATION", "Accepted " + userId + " friend request");
        acceptFriendButton.setVisibility(View.INVISIBLE);
        declineFriendButton.setVisibility(View.INVISIBLE);

        getFriends();
        currentProfilefriends.add(currentUser.getUserId());
        currentUserFriends.add(currentProfile.getUserId());


    }

    public void onClickDecline(View view){
        Log.i("CREATION", "Declined " + userId + " friend request");
        acceptFriendButton.setVisibility(View.INVISIBLE);
        declineFriendButton.setVisibility(View.INVISIBLE);
        addFriendButton.setVisibility(View.VISIBLE);
    }

    public void onClickCancel(View view){
        Log.i("CREATION", "Canceled " + userId + " friend request");
        cancelFriendButton.setVisibility(View.INVISIBLE);
        addFriendButton.setVisibility(View.VISIBLE);
    }
}

//currentUserFriends = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Friends");
//        currentProfileFriends = FirebaseDatabase.getInstance().getReference("Users").child(currentProfile.getUserId()).child("Friends");