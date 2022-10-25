package com.example.clonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.clonechat.AdapterStuff.ChatAdapter;
import com.example.clonechat.Models.MessageModel;
import com.example.clonechat.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity {

    private ActivityGroupChatBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private final ArrayList<MessageModel>messageModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setting the firebase instance and datbase.
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,GroupChatActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GroupChatActivity.this);
        binding.recyclerChat.setLayoutManager(linearLayoutManager);
        binding.recyclerChat.setHasFixedSize(true);
        binding.recyclerChat.setAdapter(chatAdapter);

        //fetching the group chats from the firebase.
        database.getReference()
                .child("GROUP-CHAT")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                MessageModel model = dataSnapshot.getValue(MessageModel.class);
                                messageModels.add(model);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.backToChatBtn.setOnClickListener(v->{
            Intent mainFragment = new Intent(GroupChatActivity.this,MainActivity.class);
            startActivity(mainFragment);
            finish();
        });


        binding.chatSendBtn.setOnClickListener(v->{
            String template = binding.chatEt.getText().toString().trim();
            final MessageModel group_mess_model = new MessageModel(Objects.requireNonNull(auth.getUid()),template);
            group_mess_model.setTime(new Date().getTime());
            binding.chatEt.setText("");

            database.getReference().child("GROUP-CHAT")
                    .push()
                    .setValue(group_mess_model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("group", " Success added ");
                        }
                    });
        });

    }
}