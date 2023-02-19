package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText mEmail,mPassword;
        Button signUp;
        TextView againLogIn;

        mEmail=findViewById(R.id.sign_up_email_id);
        mPassword=findViewById(R.id.sign_up_password);
        againLogIn=findViewById(R.id.log_in_again);
        signUp=findViewById(R.id.sign_up);

        auth = FirebaseAuth.getInstance();

        againLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this,LogInActivity.class);
                startActivity(i);
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailId = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(TextUtils.isEmpty(emailId) || TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpActivity.this,"Please fill the credentials",Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userId = firebaseUser.getUid();

                                myRef = FirebaseDatabase.getInstance().getReference("MyUsers").
                                        child(userId);

                                HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("UserId",userId);

                                myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent i = new Intent(SignUpActivity.this,ProfileActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });

                            }else{
                                Toast.makeText(SignUpActivity.this,"Invalid EmailId or Password",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        });

    }
}