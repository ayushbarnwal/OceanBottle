package com.example.oceanbottles.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");

    ArrayList<Message> messageModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public MessageAdapter(ArrayList<Message> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public MessageAdapter(ArrayList<Message> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewHolder(view);
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

        if(holder.getClass() == SenderViewHolder.class){
            if(messageModel.getPhotoUrl()!=null){
                ((SenderViewHolder)holder).sender_msg.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sender_time.setText(simpleDateFormat.format(messageModel.getTimestamp()));
                Glide.with(context).load(messageModel.getPhotoUrl()).into(((SenderViewHolder)holder).senderFile);

            }else{
                ((SenderViewHolder)holder).senderFile.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sender_time.setText(simpleDateFormat.format(messageModel.getTimestamp()));
                ((SenderViewHolder)holder).sender_msg.setText(messageModel.getMessage());
            }
        }else{
            if(messageModel.getPhotoUrl()!=null){
                ((ReceiverViewHolder)holder).receiver_msg.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receiver_time.setText(simpleDateFormat.format(messageModel.getTimestamp()));
                Picasso.get().load(messageModel.getPhotoUrl()).into(((ReceiverViewHolder)holder).receiverFile);
            }
            else{
                ((ReceiverViewHolder)holder).receiverFile.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receiver_time.setText(simpleDateFormat.format(messageModel.getTimestamp()));
                ((ReceiverViewHolder)holder).receiver_msg.setText(messageModel.getMessage());
            }
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid()+recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        String receiverRoom = recId + FirebaseAuth.getInstance().getUid();
                                        database.getReference().child("chats").child(receiverRoom)
                                                .child(messageModel.getMessageId())
                                                .setValue(null);
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                return false;
            }
        });

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
        TextView receiver_msg,receiver_time;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverFile = itemView.findViewById(R.id.receiver_file);
            receiver_msg = itemView.findViewById(R.id.receiver_text);
            receiver_time = itemView.findViewById(R.id.reciever_time);
        }
    }

}
