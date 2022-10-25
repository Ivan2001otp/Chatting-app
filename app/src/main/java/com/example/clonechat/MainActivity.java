package com.example.clonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clonechat.AdapterStuff.FragmentsAdapter;
import com.example.clonechat.LoginSignUp.SignInActivity;
import com.example.clonechat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT)
                        .show();
                Intent SettingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(SettingsIntent);
                finish();
                break;

            case R.id.groupChat:
                Intent groupChatIntent = new Intent(MainActivity.this, GroupChatActivity.class);
                startActivity(groupChatIntent);
                finish();
                break;

            case R.id.Logout:
                auth.signOut();
                Intent returnIntent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(returnIntent);
                finish();
                break;
        }

        return true;
    }
}