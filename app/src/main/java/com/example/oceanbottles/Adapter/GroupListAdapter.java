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
import com.example.oceanbottles.GroupChatDetailActivity;
import com.example.oceanbottles.Model.GroupListModel;
import com.example.oceanbottles.Model.User;
import com.example.oceanbottles.R;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    ArrayList<GroupListModel> list;
    Context context;

    public GroupListAdapter(ArrayList<GroupListModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new GroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GroupListModel groupListModel = list.get(position);
        Glide.with(context).load(groupListModel.getGroupProfilePic()).placeholder(R.drawable.user).into(holder.userprofilePic);
        holder.userName.setText(groupListModel.getGroupName());
        holder.lastSeen.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, GroupChatDetailActivity.class);
                i.putExtra("groupName", groupListModel.getGroupName());
                i.putExtra("groupProfilePic",groupListModel.getGroupProfilePic());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView userprofilePic;
        TextView userName,lastSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userprofilePic = itemView.findViewById(R.id.user_list_profile_picture);
            userName = itemView.findViewById(R.id.user_list_name);
            lastSeen = itemView.findViewById(R.id.last_seen_msg);
        }
    }


}
