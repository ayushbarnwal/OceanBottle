package com.example.oceanbottles.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oceanbottles.Adapter.UserListAdapter;
import com.example.oceanbottles.MainActivity;
import com.example.oceanbottles.Model.User;
import com.example.oceanbottles.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrivateChatFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database= FirebaseDatabase.getInstance();

    Toolbar toolbar;
    RecyclerView recyclerView;
    UserListAdapter adapter;

    ArrayList<User> list = new ArrayList<>();

    public PrivateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_private_chat, container, false);

        auth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new UserListAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        fetchUserList();





        return view;
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