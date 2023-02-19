package com.example.oceanbottles.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.ChatDetailActivity;
import com.example.oceanbottles.Model.User;
import com.example.oceanbottles.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupUserSelectionAdapter extends RecyclerView.Adapter<GroupUserSelectionAdapter.ViewHolder> {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    ArrayList<User> list;
    Context context;
    String groupName;
    String groupPic;

    public GroupUserSelectionAdapter(ArrayList<User> list, Context context, String groupName, String groupPic) {
        this.groupName = groupName;
        this.groupPic = groupPic;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupUserSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new GroupUserSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        Glide.with(context).load(user.getProfilePic()).placeholder(R.drawable.user).into(holder.userprofilePic);
        holder.userName.setText(user.getName());
        //       holder.lastMessage.setText(user.getlastmsg());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add To Group")
                        .setMessage("Are you Sure You want to add this user")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                HashMap<String,Object> obj = new HashMap<>();
                                obj.put("userId",user.getUserId());
                                Log.e("Tag",user.getName());
                                myRef.getDatabase().getReference().child("GroupChatList").child(groupName).child("members").push().updateChildren(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                HashMap<String, Object> obj1 = new HashMap<>();
                                                obj1.put("groupName",groupName);
                                                obj1.put("groupProfilePic",groupPic);
                                                myRef.getDatabase().getReference().child("MyUsers").child(user.getUserId())
                                                        .child("Group").push().updateChildren(obj1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(context,"User added Successfully",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();

                return false;
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userprofilePic;
        TextView userName,lastSeenMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userprofilePic = itemView.findViewById(R.id.user_list_profile_picture);
            userName = itemView.findViewById(R.id.user_list_name);
            lastSeenMsg = itemView.findViewById(R.id.last_seen_msg);
        }
    }
}
