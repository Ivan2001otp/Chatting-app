package com.example.clonechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.clonechat.Models.User;
import com.example.clonechat.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
private ActivitySettingsBinding binding;
private FirebaseAuth auth;
private FirebaseDatabase database;
private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.backBtn.setOnClickListener(v->{
            Intent returnIntent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(returnIntent);
            finish();
        });


        binding.selectUserImgSettings.setOnClickListener(v->{

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("images/*");//for all types we use */*

            startActivityForResult(intent,33);
        });


        binding.saveSettings.setOnClickListener(v->{

            String username = binding.userNameTvSettings.getText().toString().trim();
            String status = binding.statusTvSettings.getText().toString().trim();

            if(!username.isEmpty() && !status.isEmpty()){
                    //pull the edit text and set into the firebase.
                    HashMap<String ,Object>hashMap = new HashMap<>();
                    hashMap.put("name",username);
                    hashMap.put("status",status);
                    //updates the username and status.
                    database.getReference().child("USERS").
                            child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .updateChildren(hashMap);
            }else{
                Toast.makeText(SettingsActivity.this,"Please set your username and status",Toast.LENGTH_SHORT)
                        .show();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       try{
           if(Objects.requireNonNull(data).getData() != null){
               Uri sFile = data.getData();
               binding.settingsProfileImage.setImageURI(sFile);

               final StorageReference reference = storage.getReference().child("profile-pictures")
                       .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

               //showing the pic in the settings

               database.getReference().child("USERS").child(FirebaseAuth.getInstance().getUid())
                       .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               User user = snapshot.getValue(User.class);
                               Picasso.get()
                                       .load(Objects.requireNonNull(user).getProfilePic())
                                       .placeholder(R.drawable.contact)
                                       .into(binding.settingsProfileImage);

                               binding.userNameTvSettings.setText(user.getName());
                               binding.statusTvSettings.setText(user.getStatus());
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });


                       reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       //storing into the firebase storage
                                       database.getReference().child("USERS")
                                               .child(FirebaseAuth.getInstance().getUid())
                                               .child("profilePic")
                                               .setValue(uri.toString());
                                       Toast.makeText(SettingsActivity.this,"Profile Pic Updated !",Toast.LENGTH_SHORT)
                                               .show();
                                   }
                               });
                           }
                       });
                   }

               }catch(Exception e){
                   Log.d("tag-e", "onActivityResult: "+e.getMessage());
       }
    }
}