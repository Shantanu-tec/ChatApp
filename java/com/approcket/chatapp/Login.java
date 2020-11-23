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

public class Login extends AppCompatActivity {
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseAuth firebaseAuth;
    private EditText edt1, edt2,edt3;
    private Button btn1;
    private TextView txt, forgot;
    private String email, password, reset;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt1 = findViewById(R.id.email);
        edt2 = findViewById(R.id.pass);
        btn1 = findViewById(R.id.btn);
        txt = findViewById(R.id.txt);
        forgot = findViewById(R.id.forgot);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        login();

        txt = findViewById(R.id.txt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });

    }

    public void login() {

        progressBar = findViewById(R.id.progress);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edt1.getText().toString();
                password = edt2.getText().toString();
               // username=edt3.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if (!email.isEmpty() && !password.isEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent=new Intent(Login.this,Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(Login.this, "Something went Wrong",
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


    public void reset(View view) {
        final EditText resetemail = new EditText(view.getContext());
        final AlertDialog.Builder progressdialog = new AlertDialog.Builder(view.getContext());
        progressdialog.setTitle("Reset Password");
        progressdialog.setMessage("Enter registered email to send reset link");
        progressdialog.setView(resetemail);

        progressdialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reset = resetemail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(reset).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Login.this, "Link is send to your registered email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        progressdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        progressdialog.create().show();
    }
}