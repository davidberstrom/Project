package com.axdav.messageapp.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.icu.text.Transliterator;
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
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PositionsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment supportMapFragment;
    private DatabaseReference positionRef;
    private FirebaseUser currentUser;
    private LocationRequest locationRequest;
    private GoogleMap map;
    private String username;

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if(locationResult != null){
                for(Location l : locationResult.getLocations()){
                    positionRef.child(currentUser.getUid()).setValue(new MyLatLng(l.getLatitude(),l.getLongitude()));
                    updateUI();
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
        locationRequest.setInterval(4000);
       locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //check if user already has permissions, else ask for permisson which then invokes the onRequestPerimssionsResult method.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           startLocationUpdates();
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }

        return view;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationUpdates();
            }
        }
    }

    private void stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void updateUI(){

        positionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(map != null){
                    map.clear();
                }
                for(DataSnapshot snap : snapshot.getChildren()){
                    MyLatLng myLatLng = snap.getValue(MyLatLng.class);
                    String uId = snap.getKey();
                    LatLng latLng = new LatLng(myLatLng.getLatitude(),myLatLng.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    getUsername(uId,markerOptions,latLng);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }

    private void getUsername(final String uId, final MarkerOptions marker, final LatLng pos) {
        Log.i("USERNAME", "getUsername: " + uId);
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(uId.equals(currentUser.getUid())){
                    username = "Me";
                }else {
                    username = snapshot.getValue(User.class).getUsername();
                }
                if(snapshot.getValue(User.class).getImageURL().equals("Default")){
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon));
                }
                marker.title(username);
                marker.position(pos);
                map.addMarker(marker);
                map.setOnMarkerClickListener(PositionsFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng position = marker.getPosition();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,5));
        return false;
    }
}
