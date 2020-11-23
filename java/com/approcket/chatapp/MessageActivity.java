package com.approcket.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.approcket.chatapp.Adapter.MessageAdapter;
import com.approcket.chatapp.Model.Chat;
import com.approcket.chatapp.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView circleImageView;
    TextView textView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;
    ImageButton btn_send;
    EditText edt_message;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        btn_send=findViewById(R.id.send_btn);
        edt_message=findViewById(R.id.text_send);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        intent=getIntent();
        final String userId=intent.getStringExtra("userId");
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=edt_message.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(firebaseUser.getUid(),userId,msg);
                    //Toast.makeText(MessageActivity.this, "message send", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MessageActivity.this, "You cant send message", Toast.LENGTH_SHORT).show();
                }
                edt_message.setText("");
            }
        });

        circleImageView=findViewById(R.id.profileimages);
        textView=findViewById(R.id.username1);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                textView.setText(user.getUsername());
                if(user.getImagesUrl().equals("default"))
                {
                    circleImageView.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Glide.with(MessageActivity.this).load(user.getImagesUrl()).into(circleImageView);
                }
                readMessage(firebaseUser.getUid(),userId,user.getImagesUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(final String sender, String receiver, String message)
    {
        final String userId=intent.getStringExtra("userId");
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(!snapshot.exists())
            {
                chatRef.child("id").setValue(userId);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(final String myid,final String userid,final String imageurl)
    {
        mchat=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}