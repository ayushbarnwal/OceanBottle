package com.example.oceanbottles.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oceanbottles.Adapter.GroupListAdapter;
import com.example.oceanbottles.GroupProfileActivity;
import com.example.oceanbottles.Model.GroupListModel;
import com.example.oceanbottles.Model.User;
import com.example.oceanbottles.ProfileActivity;
import com.example.oceanbottles.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupChatFragment extends Fragment {

    FloatingActionButton fb;
    RecyclerView groupListRecyclerView;
    GroupListAdapter adapter;
    ArrayList<GroupListModel> groupList = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public GroupChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);



        groupListRecyclerView = view.findViewById(R.id.group_recycler_view);
        adapter = new GroupListAdapter(groupList,getContext());
        groupListRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        groupListRecyclerView.setLayoutManager(linearLayoutManager);

        fetchGroupListData();


        fb = view.findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), GroupProfileActivity.class));
            }
        });


        return view;
    }

    private void fetchGroupListData() {
        myRef.getDatabase().getReference().child("MyUsers").child(auth.getCurrentUser().getUid()).child("Group")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            GroupListModel group = dataSnapshot.getValue(GroupListModel.class);
                            groupList.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}

// groupList.clear();
//         for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
//         GroupListModel group = dataSnapshot.getValue(GroupListModel.class);
//        groupList.add(group);
//        }
//        adapter.notifyDataSetChanged();