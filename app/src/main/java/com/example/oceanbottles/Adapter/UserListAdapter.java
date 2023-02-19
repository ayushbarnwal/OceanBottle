package com.example.oceanbottles.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.ChatDetailActivity;
import com.example.oceanbottles.Model.User;
import com.example.oceanbottles.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    ArrayList<User> list;
    Context context;

    public UserListAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {

        User user = list.get(position);
        Glide.with(context).load(user.getProfilePic()).placeholder(R.drawable.user).into(holder.userprofilePic);
        holder.userName.setText(user.getName());
 //       holder.lastMessage.setText(user.getlastmsg());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("profilePic", user.getProfilePic());
                intent.putExtra("userName", user.getName());
                context.startActivity(intent);

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
