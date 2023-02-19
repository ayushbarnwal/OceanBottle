package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SyncStats;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.Adapter.UserListAdapter;
import com.example.oceanbottles.Fragment.GroupChatFragment;
import com.example.oceanbottles.Fragment.PrivateChatFragment;
import com.example.oceanbottles.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database= FirebaseDatabase.getInstance();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ImageView logInUserImage;
    TextView logInUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        //Initializing Widgets

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.tool_bar);


        if(auth.getCurrentUser()!=null)
        setProfilePicAndNameInNavigationView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Private Chat");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getFragmentManager().findFragmentById(androidx.appcompat.R.id.action_bar_container) == null) {
            loadfragment(new PrivateChatFragment());;
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.navigation_logOut){
                   auth.signOut();
                   Intent i = new Intent(MainActivity.this,LogInActivity.class);
                   startActivity(i);

                }else if(id==R.id.navigation_profile){

                }else if(id==R.id.navigation_user_name){

                }else if(id==R.id.navigation_group_chat){getSupportActionBar().setTitle("Group Chat");loadfragment(new GroupChatFragment());}
                else if(id==R.id.navigation_private_chat){getSupportActionBar().setTitle("Private Chat");loadfragment(new PrivateChatFragment());}

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            Intent i = new Intent (MainActivity.this,LogInActivity.class);
            startActivity(i);
        }

    }

    private void setProfilePicAndNameInNavigationView() {
        View header = navigationView.getHeaderView(0);
        logInUserName = (TextView) header.findViewById(R.id.navigation_user_name);
        logInUserImage = (ImageView) header.findViewById(R.id.navigation_profile);
        database.getReference().child("MyUsers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                logInUserName.setText(user.getName());
                Log.e("TAG",user.getProfilePic());
 //               logInUserImage.setImageURI(Uri.parse(user.getProfilePic()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void loadfragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }
}