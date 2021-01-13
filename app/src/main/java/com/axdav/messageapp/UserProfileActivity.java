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

/*Activity which displays an users profile*/
public class UserProfileActivity extends AppCompatActivity {
    private Button addFriendButton, acceptFriendButton, declineFriendButton, cancelFriendButton, removeFriendButton;
    private TextView username, description, testText;
    private String usernameTxt,userId;
    private DatabaseReference friendReq,users,friendsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private User currentProfile, currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        username = findViewById(R.id.profile_username);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        friendReq = FirebaseDatabase.getInstance().getReference("FriendRequests");
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends");
        users = FirebaseDatabase.getInstance().getReference("Users");
        if(getIntent().getStringExtra("NAME") != null)
            usernameTxt = getIntent().getStringExtra("NAME");
        else
            initializeMyProfile();
        description = findViewById(R.id.profile_text);
        username.setText(usernameTxt);
        initializeFields();
    }

    private void initializeMyProfile(){
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    User u = snap.getValue(User.class);
                    if(u.getUserId().equals(firebaseUser.getUid())){
                        usernameTxt = u.getUsername();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void console(String s){
        Log.i("Info", s);
    }

    private void initializeFields(){
        addFriendButton = findViewById(R.id.add_friend_btn);
        acceptFriendButton = findViewById(R.id.accept_friend_btn);
        declineFriendButton = findViewById(R.id.decline_friend_btn);
        cancelFriendButton = findViewById(R.id.cancel_friend_btn);
        removeFriendButton = findViewById(R.id.remove_friend_btn);
        testText = findViewById(R.id.test_text);
        addFriendButton.setVisibility(View.VISIBLE);
        acceptFriendButton.setVisibility(View.INVISIBLE);
        declineFriendButton.setVisibility(View.INVISIBLE);
        cancelFriendButton.setVisibility(View.INVISIBLE);
        removeFriendButton.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        testText.setVisibility(View.INVISIBLE);

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
                    if(getIntent().getStringExtra("NAME") == null){
                        currentProfile = currentUser;
                        addFriendButton.setVisibility(View.INVISIBLE);
                        testText.setVisibility(View.VISIBLE);
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
                    if(snapshot.getValue() == null){
                        cancelFriendButton.setVisibility(View.INVISIBLE);
                        acceptFriendButton.setVisibility(View.INVISIBLE);
                        declineFriendButton.setVisibility(View.INVISIBLE);
                        addFriendButton.setVisibility(View.VISIBLE);
                    }
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
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        DatabaseReference friends = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null){
                    removeFriendButton.setVisibility(View.INVISIBLE);
                    description.setVisibility(View.INVISIBLE);
                }
                for(DataSnapshot snap : snapshot.getChildren()){
                    if(snap.getKey().equals(currentProfile.getUserId())){
                        description.setVisibility(View.VISIBLE);
                        addFriendButton.setVisibility(View.INVISIBLE);
                        removeFriendButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClickAdd(View view){
        friendReq.child(firebaseUser.getUid()).child(userId).setValue("sender");
        friendReq.child(userId).child(firebaseUser.getUid()).setValue("receiver");
        addFriendButton.setVisibility(View.INVISIBLE);
        cancelFriendButton.setVisibility(View.VISIBLE);
    }

    public void onClickAccept(View view){
        acceptFriendButton.setVisibility(View.INVISIBLE);
        declineFriendButton.setVisibility(View.INVISIBLE);

        friendsRef.child(currentUser.getUserId()).child(currentProfile.getUserId()).setValue("friends");
        friendsRef.child(currentProfile.getUserId()).child(currentUser.getUserId()).setValue("friends");

        friendReq.child(currentUser.getUserId()).child(currentProfile.getUserId()).removeValue();
        friendReq.child(currentProfile.getUserId()).child(currentUser.getUserId()).removeValue();
    }

    public void onClickDecline(View view){
        acceptFriendButton.setVisibility(View.INVISIBLE);
        declineFriendButton.setVisibility(View.INVISIBLE);
        addFriendButton.setVisibility(View.VISIBLE);

        friendReq.child(currentUser.getUserId()).child(currentProfile.getUserId()).removeValue();
        friendReq.child(currentProfile.getUserId()).child(currentUser.getUserId()).removeValue();
    }

    public void onClickCancel(View view) {
        friendReq.child(currentUser.getUserId()).child(currentProfile.getUserId()).removeValue();
        friendReq.child(currentProfile.getUserId()).child(currentUser.getUserId()).removeValue();

        cancelFriendButton.setVisibility(View.INVISIBLE);
        addFriendButton.setVisibility(View.VISIBLE);
    }

    public void onClickRemove(View view){
        addFriendButton.setVisibility(View.VISIBLE);
        removeFriendButton.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        friendsRef.child(currentUser.getUserId()).child(currentProfile.getUserId()).removeValue();
        friendsRef.child(currentProfile.getUserId()).child(currentUser.getUserId()).removeValue();
    }
}

//currentUserFriends = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Friends");
//        currentProfileFriends = FirebaseDatabase.getInstance().getReference("Users").child(currentProfile.getUserId()).child("Friends");