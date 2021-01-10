package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.axdav.messageapp.Adapters.NotificationAdapter;
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

/*class used to display friendrequest and retreving them from the database*/
public class NotificationActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference friendRequests, allUsers;
    FirebaseUser currentUser;
    List<User> friendRequestArray;
    Toolbar toolbar;
    private RecyclerView view;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        friendRequests = FirebaseDatabase.getInstance().getReference("FriendRequests");
        allUsers = FirebaseDatabase.getInstance().getReference("Users");
        friendRequestArray = new ArrayList<User>();
        toolbar = findViewById(R.id.toolbar);
        view = findViewById(R.id.friend_request_recycler_view);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        setTitle("Notifications");
        getNotifications();
    }
    /*method to retrive friendrequest from the database
    * and setting the views adapter to represent each requests in the view*/
    private void getNotifications() {
        if (friendRequests != null) {
            DatabaseReference myFriendRequests = friendRequests.child(currentUser.getUid());
            if(myFriendRequests != null) {
                myFriendRequests.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap : snapshot.getChildren()){
                            if(snap.getValue(String.class).equals("receiver")){
                                allUsers.child(snap.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        friendRequestArray.add(user);
                                        adapter = new NotificationAdapter(friendRequestArray);
                                        adapter.notifyDataSetChanged();
                                        view.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
}