package com.example.oceanbottles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.Model.Message;
import com.example.oceanbottles.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GroupMessageAdapter extends RecyclerView.Adapter{

    FirebaseAuth auth = FirebaseAuth.getInstance();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");

    ArrayList<Message> messageModels;
    Context context;
    String LogInUserName;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public GroupMessageAdapter(ArrayList<Message> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

//    public GroupMessageAdapter(ArrayList<Message> messageModels, Context context, String LogInUserName) {
//        this.messageModels = messageModels;
//        this.context = context;
//        this.LogInUserName = LogInUserName;
//    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new GroupMessageAdapter.SenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.group_sample_receiver,parent,false);
            return new GroupMessageAdapter.ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModels.get(position).getuId().equals(auth.getCurrentUser().getUid()))return SENDER_VIEW_TYPE;
        else return RECEIVER_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message messageModel = messageModels.get(position);

        if(holder.getClass() == GroupMessageAdapter.SenderViewHolder.class){
            if(messageModel.getPhotoUrl()!=null){
                ((GroupMessageAdapter.SenderViewHolder)holder).sender_msg.setVisibility(View.GONE);
                ((GroupMessageAdapter.SenderViewHolder)holder).sender_time.setText(simpleDateFormat.format(messageModel.getTimestamp()));
                Glide.with(context).load(messageModel.getPhotoUrl()).into(((GroupMessageAdapter.SenderViewHolder)holder).senderFile);

            }else{
                ((GroupMessageAdapter.SenderViewHolder)holder).senderFile.setVisibility(View.GONE);
                ((GroupMessageAdapter.SenderViewHolder)holder).sender_time.setText(simpleDateFormat.format(messageModel.getTimestamp()));
                ((GroupMessageAdapter.SenderViewHolder)holder).sender_msg.setText(messageModel.getMessage());
            }
        }else{
            if(messageModel.getPhotoUrl()!=null){
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiver_msg.setVisibility(View.GONE);
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiver_time.setText(String.valueOf(simpleDateFormat.format(messageModel.getTimestamp())));
                Picasso.get().load(messageModel.getPhotoUrl()).into(((GroupMessageAdapter.ReceiverViewHolder)holder).receiverFile);
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiver_name.setText(messageModel.getName());
            }else{
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiverFile.setVisibility(View.GONE);
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiver_time.setText(String.valueOf(simpleDateFormat.format(messageModel.getTimestamp())));
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiver_msg.setText(messageModel.getMessage());
                ((GroupMessageAdapter.ReceiverViewHolder)holder).receiver_name.setText(messageModel.getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        ImageView senderFile;
        TextView sender_msg,sender_time;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderFile = itemView.findViewById(R.id.sender_file);
            sender_msg = itemView.findViewById(R.id.sender_text);
            sender_time = itemView.findViewById(R.id.sender_time);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        ImageView receiverFile;
        TextView receiver_msg,receiver_time,receiver_name;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverFile = itemView.findViewById(R.id.imageView6);
            receiver_msg = itemView.findViewById(R.id.textView11);
            receiver_time = itemView.findViewById(R.id.textView12);
            receiver_name = itemView.findViewById(R.id.textView10);
        }
    }
}
