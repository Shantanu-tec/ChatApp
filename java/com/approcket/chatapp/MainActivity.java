package com.approcket.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseAuth firebaseAuth;
    private EditText edt1, edt2,edt3;
    private Button btn1;
    private TextView txt, forgot;
    private String email, password, reset,username;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, Home.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt1 = findViewById(R.id.email);
        edt3 = findViewById(R.id.username);
        edt2 = findViewById(R.id.pass);
        btn1 = findViewById(R.id.btn);
        txt = findViewById(R.id.txt);
        forgot = findViewById(R.id.forgot);
        firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        //FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        signup();

        txt = findViewById(R.id.txt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

    }

    public void signup() {

        progressBar = findViewById(R.id.progress);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edt1.getText().toString();
                password = edt2.getText().toString();
               username=edt3.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if (!email.isEmpty() && !password.isEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                        HashMap<String,String> map=new HashMap<>();
                                        map.put("id",userId);
                                        map.put("username",username);
                                        map.put("imagesurl","default");


                                        databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Intent intent=new Intent(MainActivity.this,Home.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Something went Wrong",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                } else {
                    if (email.isEmpty()) {
                        edt1.setError("email is empty");
                    }
                    if (password.isEmpty()) {
                        edt2.setError("pass is empty");
                    }
                }
            }
        });
    }
}