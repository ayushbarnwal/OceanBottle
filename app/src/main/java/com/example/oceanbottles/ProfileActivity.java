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

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference myRef;
    StorageReference storageReference;
    FirebaseDatabase database;

    ImageView profilePic;
    EditText personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        personName = findViewById(R.id.personName);
        profilePic = findViewById(R.id.profile_picture);
        Button uploadPicName = findViewById(R.id.update);


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,33);
            }
        });

        uploadPicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(personName.getText().toString())){
                    Toast.makeText(ProfileActivity.this,"please Enter your Name",Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String , Object> obj = new HashMap<>();
                    obj.put("Name",personName.getText().toString());
                    database.getReference().child("MyUsers").child(auth.getCurrentUser().getUid()).updateChildren(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent i = new Intent(ProfileActivity.this,MainActivity.class);
                            startActivity(i);
                        }
                    });
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData()!=null){       //if user selected any image then getdata will give path
            Uri sFile = data.getData();
            profilePic.setImageURI(sFile);

            storageReference = FirebaseStorage.getInstance().getReference().child("UserProfilePic")
                    .child(auth.getCurrentUser().getUid());
            storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            User user = new User(personName.getText().toString(),uri.toString());
                            HashMap<String , Object> obj = new HashMap<>();     // to update on firebase
                            obj.put("profilePic",uri.toString());
                            obj.put("Name",personName.getText().toString());
                            database.getReference().child("MyUsers").child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(obj);
                            Toast.makeText(ProfileActivity.this,"Profile Pic Updated",Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });


        }

    }
}