package com.example.clonechat.UtilFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clonechat.AdapterStuff.UserRecylerView;
import com.example.clonechat.Models.User;
import com.example.clonechat.R;
import com.example.clonechat.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class ChatsFragment extends Fragment {
    private final ArrayList<User>list = new ArrayList<>();
    private FragmentChatsBinding binding;
    private FirebaseDatabase database;


    public ChatsFragment(){}

    @Override
    public void onStart() {
        super.onStart();
        binding.recyclerViewForChat.setHasFixedSize(true);
        binding.recyclerViewForChat.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database=FirebaseDatabase.getInstance();
        Log.d("tag", "onCreateView: oncreateview executed");
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater,container,false);

        UserRecylerView chatRecyclerViewAdapter = new UserRecylerView(list,getContext());

        binding.recyclerViewForChat.setAdapter(chatRecyclerViewAdapter);
        Log.d("tag", "onCreateView: "+list.size());
        //setting the linear layout manager

        database.getReference().child("USERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //to collect the data from the firebase we use
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                User dataUser = dataSnapshot.getValue(User.class);
                 //   Log.d("tag", "onDataChange: "+dataSnapshot.getValue());
                Objects.requireNonNull(dataUser).setId(dataSnapshot.getKey());

                    if(!(dataUser.getId().equals(FirebaseAuth.getInstance().getUid()))){
                        list.add(dataUser);
                    }

                }
                chatRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}