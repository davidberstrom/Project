package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

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
    private FirebaseAuth mAuth;
    private DatabaseReference friendRequests, allUsers;
    private FirebaseUser currentUser;
    private List<User> friendRequestArray;
    private Toolbar toolbar;
    private RecyclerView view;
    private NotificationAdapter adapter;
    private TextView empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        empty_view = findViewById(R.id.empty_view);
        friendRequests = FirebaseDatabase.getInstance().getReference("FriendRequests");
        allUsers = FirebaseDatabase.getInstance().getReference("Users");
        friendRequestArray = new ArrayList<User>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = findViewById(R.id.friend_request_recycler_view);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Notifications");
        getNotifications();

    }
    /*method to retrive friendrequest from the database
    * and setting the views adapter to represent each requests in the view*/
    private void getNotifications() {
        if (friendRequests != null) {
             adapter = new NotificationAdapter(friendRequestArray);
            DatabaseReference myFriendRequests = friendRequests.child(currentUser.getUid());
                myFriendRequests.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() == null){
                            view.setVisibility(View.GONE);
                            empty_view.setVisibility(View.VISIBLE);
                        }
                        for(final DataSnapshot snap : snapshot.getChildren()){
                            friendRequestArray.clear();
                            if(snap.getValue(String.class).equals("receiver")){
                                allUsers.child(snap.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        friendRequestArray.add(user);
                                        adapter.notifyDataSetChanged();
                                        Log.i("ADAPT", "onDataChange: " + view.getAdapter());
                                        view.setAdapter(adapter);
                                        empty_view.setVisibility(View.INVISIBLE);
                                        view.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast toast = Toast.makeText(NotificationActivity.this,"Failed to read from database, try again", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast toast = Toast.makeText(NotificationActivity.this,"Failed to read from database, try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
        } else {
            view.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }

    }
}