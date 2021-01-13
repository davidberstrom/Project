package com.axdav.messageapp.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.axdav.messageapp.Model.MyLatLng;
import com.axdav.messageapp.Model.User;
import com.axdav.messageapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*class used for getting position from the user, upload it to the database and retriving position from each user in the database and mark it on a map.
* if the a user isnt logged in it uses the last location. */
public class PositionsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment supportMapFragment;
    private DatabaseReference positionRef,friendsRef;
    private FirebaseUser currentUser;
    private LocationRequest locationRequest;
    private GoogleMap map;
    private String username;
    private final int LOCATION_REQUEST_CODE = 1000;
    private List<String> friendsUid = new ArrayList<>();
    private List<String> presence = new ArrayList<>();
    /*A callback function called each specified time it should update the users location*/
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if(locationResult != null){
                for(Location pos : locationResult.getLocations()){
                    positionRef.child(currentUser.getUid()).setValue(new MyLatLng(pos.getLatitude(),pos.getLongitude()));
                    retrivePosition();
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_positions, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        positionRef = FirebaseDatabase.getInstance().getReference("Position");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
       locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUid());
        //check if user already has permissions, else ask for permisson which then invokes the onRequestPerimssionsResult method.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           startLocationUpdates();
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }

        return view;
    }



    /*Called if the user has'nt accepted or declined location permissions
    and ask the user if it is granted or not */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationUpdates();
            }
        }
    }

    /*Stopping the location updates*/
    private void stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /*Starting the locationsupdates*/
    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
    }

    /*Called when fragment is stopped and no longer visible*/
    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    /*Function to get position from current user friends and the current user*/
    private void retrivePosition(){
        getFriendsId();
        positionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(map != null){
                    map.clear();
                }
                for(DataSnapshot snap : snapshot.getChildren()){
                    String uId = snap.getKey();
                    if(!friendsUid.isEmpty()) {
                        for (int i = 0; i < friendsUid.size(); i++) {
                            if (friendsUid.get(i).equals(snap.getKey()) || uId.equals(currentUser.getUid())) {
                                MyLatLng myLatLng = snap.getValue(MyLatLng.class);
                                LatLng latLng = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                updateUI(uId, markerOptions, latLng);
                            }
                        }
                    } else if(uId.equals(currentUser.getUid())){
                        MyLatLng myLatLng = snap.getValue(MyLatLng.class);
                        LatLng latLng = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        updateUI(currentUser.getUid(), markerOptions, latLng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(getContext(),"Failed to read from database, try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /*Called when the google map is ready to be used*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }

    /*Method which updates the UI by setting a marker for each user*/
    private void updateUI(final String uId, final MarkerOptions marker, final LatLng pos) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(uId.equals(currentUser.getUid())){
                    username = "Me";
                }else {
                    username = snapshot.getValue(User.class).getUsername();
                }
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon));
                marker.title(username);
                marker.position(pos);
                map.addMarker(marker);
                map.setOnMarkerClickListener(PositionsFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(getContext(),"Failed to read from database, try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
    /*Called when the fragment is active and ready for user interaction*/
    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    /*called each time a marker is clicked, then zooms the camera to that position*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng position = marker.getPosition();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,5));
        return false;
    }
    /*Method to retrive current user friends ids */
    private void getFriendsId(){
        if(!friendsUid.isEmpty()){
            friendsUid.clear();
        }
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        String id = s.getKey();
                        friendsUid.add(id);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(getContext(),"Failed to read from database, try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


}
