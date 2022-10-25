package com.example.clonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.clonechat.AdapterStuff.ChatAdapter;
import com.example.clonechat.Models.MessageModel;
import com.example.clonechat.Utils.Utility;
import com.example.clonechat.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import okhttp3.internal.cache.DiskLruCache;

public class ChatDetailActivity extends AppCompatActivity {
    private ActivityChatDetailBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private final ArrayList<MessageModel> messageModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //hide the actionbar

        Objects.requireNonNull(getSupportActionBar()).hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        String senderUid_fromIntent = auth.getUid();
        String receiverUid = getIntent().getStringExtra("userChatId");
        String profilePicUrl = getIntent().getStringExtra("chatProfile");
        String userName = getIntent().getStringExtra("userName");
        boolean female_boolean = getIntent().getBooleanExtra("female_gender", true);
        boolean male_boolean = getIntent().getBooleanExtra("male_gender", true);


        final String senderRoom = senderUid_fromIntent + receiverUid;
        final String receiverRoom = receiverUid + senderUid_fromIntent;


        //setting the adapter
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this,receiverUid);
        //setting the layout
        LinearLayoutManager linearLayoutManagerForChatRecycler = new LinearLayoutManager(ChatDetailActivity.this);
        binding.recyclerChat.setLayoutManager(linearLayoutManagerForChatRecycler);
        binding.recyclerChat.setHasFixedSize(true);
        binding.recyclerChat.setAdapter(chatAdapter);


        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                MessageModel model = dataSnapshot.getValue(MessageModel.class);
                                Objects.requireNonNull(model).setMessage_id(dataSnapshot.getKey());
                                Log.d("tag_message", "onModelChanged: " + Objects.requireNonNull(model).getSms_message());
                                messageModels.add(model);
                            }

                        }
                        chatAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//setting the values obtained from the main-chat intent runtime


        binding.userNameChat.setText(userName.trim());
        //setting the img
        if (female_boolean) {
            Picasso.get().load(profilePicUrl).placeholder(R.drawable.woman_img).into(binding.chatProfile);
        } else if (male_boolean) {
            Picasso.get().load(profilePicUrl).placeholder(R.drawable.man_img).into(binding.chatProfile);
        }


        binding.backToChatBtn.setOnClickListener(view ->
        {   //setting the intent to return to the chat activity from chat detailActivity.
            Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        binding.chatSendBtn.setOnClickListener(v -> {
            String temp_mes = binding.chatEt.getText().toString().trim();
            final MessageModel model = new MessageModel(Objects.requireNonNull(senderUid_fromIntent), temp_mes);
            model.setTime(new Date().getTime());
            binding.chatEt.setText("");

            database.getReference().child("chats")
                    .child(senderRoom)
                    .push()
                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    database.getReference().child("chats")
                            .child(receiverRoom)
                            .push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
            });
        });


//         database.getReference().child("chats")
//                 .child(senderRoom)
//                 .addValueEventListener(new ValueEventListener() {
//                     @Override
//                     public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
//                                MessageModel model = snapshot1.getValue(MessageModel.class);
//                                messageModels.add(model);
//                            }
//                     }
//
//                     @Override
//                     public void onCancelled(@NonNull DatabaseError error) {
//
//                     }
//                 });

/*
         database.getReference().child("chats")
                 .child(senderRoom)
                 .addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                             chat_arrayList.clear();
                         for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            //return all the snapshot until all the children are empty in the node.
                            MessageModel model_chat = snapshot.getValue(MessageModel.class);


                            Log.d("db-tag", "onDataChange collecting all the chats: ");

                                chat_arrayList.add(model_chat);

                             Log.d("db-tag", "onDataChange collecting all the chats: "+ Objects.requireNonNull(model_chat).getSms_message().toString());

                         }

                         for(MessageModel m : chat_arrayList){
                             Log.d("allData", "the data is :  "+m.getUnique_Uid());
                         }

                        chatAdapter.notifyDataSetChanged();
                     }


                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });*/


     /*       binding.chatSendBtn.setOnClickListener(v->{
            String message = binding.chatEt.getText().toString().trim();
                Log.d("tag", "send chat btn: "+auth.getUid());
                assert senderUid_fromIntent != null;
                final MessageModel chat_model = new MessageModel(senderUid_fromIntent, message);
            chat_model.setTime(new Date().getTime());

            binding.chatEt.setText("");

            //this is done in p.o.v of sender
            database.getReference().child("chats")
                    .child(senderRoom)
                    .push()
                    .setValue(chat_model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //this is done in p.o.v of receiver
                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .push()
                                    .setValue(chat_model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                        }
                    });

        });*/
    }
}