package com.example.oceanbottles.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.GroupChatDetailActivity;
import com.example.oceanbottles.Model.User;
import com.example.oceanbottles.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class GroupChatMemberAdapter extends RecyclerView.Adapter<GroupChatMemberAdapter.ViewHolder> {

    ArrayList<User> list = new ArrayList<>();
    Context context;

    public GroupChatMemberAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_group_chat_member,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        Log.e("Tag",user.getName());
        Glide.with(context).load(user.getProfilePic()).into(holder.group_user);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView group_user;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_user = itemView.findViewById(R.id.group_user_pic);
        }
    }

}
