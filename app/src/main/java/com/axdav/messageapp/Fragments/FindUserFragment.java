package com.axdav.messageapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.axdav.messageapp.Adapters.FindUsersAdapter;
import com.axdav.messageapp.LoggedInActivity;
import com.axdav.messageapp.Model.User;
import com.axdav.messageapp.R;
import com.axdav.messageapp.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FindUserFragment extends Fragment {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private FindUsersAdapter adapter;
    private List<User> allUsers;

    private View view;
    private Button addFriend_btn;

    public FindUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        allUsers = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        view = inflater.inflate(R.layout.fragment_find_user, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getAllUsers();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu,menu);

        MenuItem item = menu.findItem(R.id.search_friends);
        SearchView search = (SearchView) item.getActionView();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                //called when user press seatch button
                if (!TextUtils.isEmpty(query.trim())){

                    searchUser(query);
                }else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called when user press a letter

                if (!TextUtils.isEmpty(newText.trim())){

                    searchUser(newText);
                }else {
                    getAllUsers();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getActivity(),StartActivity.class));
                return true;
            case R.id.notification:
                //gör något -
        }
        return false;
    }



    private void searchUser(final String query){
        //search users and initiate and set adapter
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsers.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    if(user.getUserId() != currentUser.getUid()){
                        if(user.getUsername().toLowerCase().contains(query.toLowerCase())){
                            allUsers.add(user);
                            Log.i("CREATION", "TESSSTTTT 8");
                        }
                    }
                    adapter = new FindUsersAdapter(getContext(),allUsers);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getAllUsers(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsers.clear();
                for(DataSnapshot s : snapshot.getChildren()){
                    User user = s.getValue(User.class);
                    if(!user.getUserId().equals(currentUser.getUid())){
                        allUsers.add(user);
                    }
                }
                adapter = new FindUsersAdapter(getContext(),allUsers);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}