package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.oceanbottles.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class GroupProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference myRef;
    StorageReference storageReference;
    FirebaseDatabase database;

    ImageView groupProfilePic;
    EditText groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        groupName = findViewById(R.id.groupName);
        groupProfilePic = findViewById(R.id.group_profile_picture);
        Button uploadGroupPicName = findViewById(R.id.group_update);

        groupProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(groupName.getText().toString())){
                    Toast.makeText(GroupProfileActivity.this,"please Enter Group Name First",Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i,33);
                }
            }
        });

        uploadGroupPicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User user = new User(groupName.getText().toString(),uri.toString());
                        HashMap<String , Object> obj = new HashMap<>();    // to update on firebase
                        obj.put("groupAdmin",auth.getCurrentUser().getUid());
                        obj.put("groupProfilePic",uri.toString());
                        obj.put("groupName",groupName.getText().toString());
                        database.getReference().child("GroupChatList").child(groupName.getText().toString())
                                .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                HashMap<String , Object> obj1 = new HashMap<>();
                                obj1.put("groupProfilePic",uri.toString());
                                obj1.put("groupName",groupName.getText().toString());
                                database.getReference().child("MyUsers").child(auth.getCurrentUser().getUid()).child("Group").push()
                                        .updateChildren(obj1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        HashMap<String , Object> obj2 = new HashMap<>();
                                        obj2.put("userId",auth.getCurrentUser().getUid());
                                        database.getReference().child("GroupChatList").child(groupName.getText().toString()).child("members").push().updateChildren(obj2)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent i = new Intent(GroupProfileActivity.this,MainActivity.class);
                                                        startActivity(i);
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData()!=null){       //if user selected any image then getdata will give path
            Uri sFile = data.getData();
            groupProfilePic.setImageURI(sFile);

            storageReference = FirebaseStorage.getInstance().getReference().child("GroupProfilePic");
            storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });


        }

    }

}