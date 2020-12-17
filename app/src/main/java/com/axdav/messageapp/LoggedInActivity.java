package com.axdav.messageapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.axdav.messageapp.Fragments.FindUserFragment;
import com.axdav.messageapp.Fragments.FriendsFragment;
import com.axdav.messageapp.Fragments.PositionsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoggedInActivity extends AppCompatActivity {
    FirebaseUser currUser;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager page = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tab = (TabLayout)findViewById(R.id.tabs);
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        pageAdapter adapt = new pageAdapter(getSupportFragmentManager());
        page.setAdapter(adapt);
        tab.setupWithViewPager(page);
        setSupportActionBar(toolbar);

        Toast toast  = Toast.makeText(this,"Welcome"+currUser.toString(),Toast.LENGTH_SHORT);


    }



    private class pageAdapter extends FragmentPagerAdapter{

        public pageAdapter(FragmentManager f){
            super(f);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new FriendsFragment();
                case 1:
                    return new PositionsFragment();
                case 2:
                    return new FindUserFragment();
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return getResources().getText(R.string.friends);
                case 1:
                    return getResources().getText(R.string.positions);
                case 2:
                    return getResources().getText(R.string.find_users);
            }
            return null;
        }
    }
}
