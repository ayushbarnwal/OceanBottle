package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.example.oceanbottles.Adapter.GroupUserSelectionAdapter;
import com.example.oceanbottles.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database= FirebaseDatabase.getInstance();

    Toolbar toolbar;
    RecyclerView userList_recyclerView;
    ArrayList<User> list = new ArrayList<>();
    GroupUserSelectionAdapter adapter;

    String groupName;
    String groupPic;

    FloatingActionButton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Members");

        auth = FirebaseAuth.getInstance();

        fb = findViewById(R.id.floatingActionButton2);

        groupName = getIntent().getStringExtra("groupName");
        groupPic = getIntent().getStringExtra("groupPic");

        userList_recyclerView = findViewById(R.id.user_list_recycler_view);
        adapter = new GroupUserSelectionAdapter(list,UserListActivity.this,groupName,groupPic);
        userList_recyclerView.setAdapter(adapter);
        userList_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchUserList();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserListActivity.this, GroupChatDetailActivity.class);
                startActivity(i);
            }
        });

    }

    private void fetchUserList() {

        database.getReference().child("MyUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserId(dataSnapshot.getKey());
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getUid()))
                        list.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}