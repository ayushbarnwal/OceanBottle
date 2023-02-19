package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.Adapter.MessageAdapter;
import com.example.oceanbottles.Model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatDetailActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    FirebaseAuth auth;
    DatabaseReference myRef;
    StorageReference storageReference;

    ImageView back_btn,profile_pic,send_msg,select_file;
    EditText write_msg;
    TextView user_name;
    String senderRoom,receiverRoom;
    String senderId;
    ScrollView chatScroll;

    RecyclerView msg_recycler_view;
    final ArrayList<Message> messageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        auth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        changeColorOfTopBar();

        msg_recycler_view = findViewById(R.id.chat_recycler_view);

        chatScroll =findViewById(R.id.chat_scroll);
        back_btn = findViewById(R.id.back_btn);
        profile_pic=findViewById(R.id.profile_image);
        send_msg=findViewById(R.id.send_msg);
        select_file=findViewById(R.id.select_file);
        write_msg=findViewById(R.id.msg_type);
        user_name=findViewById(R.id.name);

        Intent i = getIntent();
        String recId = i.getStringExtra("userId");
        senderId = auth.getCurrentUser().getUid();
        Glide.with(this).load(i.getStringExtra("profilePic")).into(profile_pic);
        user_name.setText(i.getStringExtra("userName"));

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(i);
            }
        });


        final MessageAdapter messageAdapter = new MessageAdapter(messageList, ChatDetailActivity.this,recId);
        msg_recycler_view.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msg_recycler_view.setLayoutManager(layoutManager);

        chatScroll.fullScroll(View.FOCUS_DOWN);

        senderRoom = senderId+recId;
        receiverRoom = recId+senderId;

        myRef.child("chats")
                .child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Message model = snapshot1.getValue(Message.class);
                    model.setMessageId(snapshot1.getKey());
                    messageList.add(model);
                }
                messageAdapter.notifyDataSetChanged();   //in order to see msg at run time... if we not do this then msg seen only when we press back button
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgTyped = write_msg.getText().toString();
                if(!TextUtils.isEmpty(msgTyped)){
                    final Message model = new Message(senderId,msgTyped);
                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                    model.setTimestamp(new Date().getTime());
                    write_msg.setText(" ");

                    myRef.child("chats")                //to store msg
                            .child(senderRoom)
                            .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {          //push will allote node to different msgs
                        @Override
                        public void onSuccess(Void unused) {
                            myRef.child("chats").child(receiverRoom).push()
                                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });


                        }
                    });
                }
                chatScroll.fullScroll(View.FOCUS_DOWN);
            }
        });


        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu(ChatDetailActivity.this,select_file);
                popUp.setOnMenuItemClickListener(ChatDetailActivity.this);
                popUp.inflate(R.menu.send_document_selection_menu);
                popUp.show();
            }
        });


    }

    private void changeColorOfTopBar() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.blueCOlor));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.select_photo:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,33);
            case R.id.select_files:
                Intent i1 = new Intent();
                i1.setType("application/pdf");
                i1.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(i1,35);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 33 && resultCode == RESULT_OK){
            if(data.getData()!=null){
                Uri sFile = data.getData();

                storageReference = FirebaseStorage.getInstance().getReference().child("ChatFiles").child(sFile.getLastPathSegment());        //to upload the image in storage here we given uid so when user update his profile pic then it will get overridden in storage.... so if we want to create different id for different image uploaded then use push method
                storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ChatDetailActivity.this,"file Uploaded",Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Calendar time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                                Message model = new Message(senderId,uri.toString(),new Date().getTime());
//                             HashMap<String , Object> obj = new HashMap<>();     // to update on firebase
//                             obj.put("file",uri.toString());
//                             obj.put("uid",model.getuId());
//                             obj.put("timestamp",model.getTimestamp());
                                myRef.getDatabase().getReference().child("chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        myRef.getDatabase().getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ChatDetailActivity.this,"file Uploaded in databse",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

            }
        }
            if(requestCode==35 && resultCode==RESULT_OK){
                if(data.getData()!=null){
                    Uri sFile = data.getData();

                    storageReference = FirebaseStorage.getInstance().getReference().child("ChatFiles").child(sFile.getLastPathSegment());        //to upload the image in storage here we given uid so when user update his profile pic then it will get overridden in storage.... so if we want to create different id for different image uploaded then use push method
                    storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ChatDetailActivity.this,"file Uploaded",Toast.LENGTH_SHORT).show();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Calendar time = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                                    Message model = new Message(senderId,uri.toString(),new Date().getTime());
                                    HashMap<String , Object> obj = new HashMap<>();     // to update on firebase
                                    obj.put("pdfUrl",uri.toString());
                                    obj.put("uid",model.getuId());
                                    obj.put("timestamp",model.getTimestamp());
                                    myRef.getDatabase().getReference().child("chats").child(receiverRoom).push().setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            myRef.getDatabase().getReference().child("chats").child(senderRoom).push().setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(ChatDetailActivity.this,"file Uploaded in databse",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            }



    }

}