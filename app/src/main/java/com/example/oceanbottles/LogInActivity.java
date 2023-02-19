package com.example.oceanbottles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class LogInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();

        EditText email_id,password;
        Button loginBtn;
        TextView signUp;

        email_id = findViewById(R.id.email_id);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.log_in);
        signUp = findViewById(R.id.textView6);


        if(auth.getCurrentUser()!=null){
            Intent i = new Intent(LogInActivity.this,MainActivity.class);
            startActivity(i);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memailId = email_id.getText().toString();
                String mpassword = password.getText().toString();

                if(TextUtils.isEmpty(memailId) || TextUtils.isEmpty(mpassword)){
                    Toast.makeText(LogInActivity.this,"Please fill the credentials",Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(memailId, mpassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        String userId = user.getUid();
                                        Intent i = new Intent(LogInActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LogInActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

    }

}