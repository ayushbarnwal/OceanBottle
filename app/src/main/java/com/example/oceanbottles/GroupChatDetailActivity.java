package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oceanbottles.Adapter.GroupChatMemberAdapter;
import com.example.oceanbottles.Adapter.GroupMessageAdapter;
import com.example.oceanbottles.Model.Message;
import com.example.oceanbottles.Model.User;
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

public class GroupChatDetailActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference;

    ImageView grpProfilePicture,sendMsg,backBtn,select_file;
    TextView grpName;
    EditText msgTyped;
    RecyclerView groupChatRecyclerView;
    RecyclerView groupChatMemberRecyclerView;

    String GroupName;
    String GroupProfilePic;
    String LogInUserName;

    Toolbar toolbar;

    GroupMessageAdapter adapter;
    GroupChatMemberAdapter adapter1;

    ArrayList<Message> list = new ArrayList<>();
    ArrayList<User> list1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_detail);

        Intent i = getIntent();
        GroupName = i.getStringExtra("groupName");
        GroupProfilePic = i.getStringExtra("groupProfilePic");

        init();
        setSupportActionBar(toolbar);

        Glide.with(this).load(GroupProfilePic).into(grpProfilePicture);
        grpName.setText(GroupName);

        getLogInUserName();

        adapter1 = new GroupChatMemberAdapter(list1,this);
        groupChatMemberRecyclerView.setAdapter(adapter1);
        groupChatMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        fetchGroupChatMemberList();

        adapter = new GroupMessageAdapter(list,this);
        groupChatRecyclerView.setAdapter(adapter);
        groupChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(msgTyped.getText().toString()))
                saveDataToFirebase();
            }
        });

        fetchChatList();

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,33);
            }
        });

    }

    private void getLogInUserName() {
        myRef.getDatabase().getReference().child("MyUsers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LogInUserName = snapshot.child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchGroupChatMemberList() {

        myRef.getDatabase().getReference().child("GroupChatList").child(GroupName).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String userId = dataSnapshot.child("userId").getValue().toString();
                    Log.e("Tag",userId);
                    fetchUserProfilePic(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchUserProfilePic(String userId) {
        myRef.getDatabase().getReference().child("MyUsers").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.e("Tag",user.getName());
                list1.add(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchChatList() {

        myRef.getDatabase().getReference().child("GroupChatList").child(GroupName).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message model = dataSnapshot.getValue(Message.class);
                    model.setMessageId(dataSnapshot.getKey());
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveDataToFirebase() {
        String typedMsg = msgTyped.getText().toString();
        String senderId = auth.getCurrentUser().getUid();
        Calendar time = Calendar.getInstance();
        final Message model = new Message(senderId,typedMsg);
        model.setTimestamp(new Date().getTime());
        model.setName(LogInUserName);
        msgTyped.setText(" ");

        myRef.getDatabase().getReference().child("GroupChatList").child(GroupName).child("Messages").push().setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grp_chat_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.add_member) {
                Intent i = new Intent(GroupChatDetailActivity.this,UserListActivity.class);
                i.putExtra("groupName",GroupName);
                i.putExtra("groupPic",GroupProfilePic);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){
        grpProfilePicture = findViewById(R.id.grp_profile_image);
        sendMsg = findViewById(R.id.grp_send_msg);
        grpName = findViewById(R.id.grp_name);
        msgTyped = findViewById(R.id.grp_msg_type);
        backBtn = findViewById(R.id.grp_back_btn);
        select_file=findViewById(R.id.grp_select_file);

        groupChatRecyclerView = findViewById(R.id.group_chat_recycler_view);
        groupChatMemberRecyclerView = findViewById(R.id.group_chat_user_recycler_view);

        toolbar = findViewById(R.id.toolbar2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData()!=null){
            Uri sFile = data.getData();

            storageReference = FirebaseStorage.getInstance().getReference().child("GroupChatFiles").child(sFile.getLastPathSegment());        //to upload the image in storage here we given uid so when user update his profile pic then it will get overridden in storage.... so if we want to create different id for different image uploaded then use push method
            storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(GroupChatDetailActivity.this,"file Uploaded",Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String senderId = auth.getCurrentUser().getUid();
                            Message model = new Message(senderId,uri.toString(),new Date().getTime());
                            myRef.getDatabase().getReference().child("GroupChatList").child(GroupName).child("Messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    });
                }
            });

        }



    }

}