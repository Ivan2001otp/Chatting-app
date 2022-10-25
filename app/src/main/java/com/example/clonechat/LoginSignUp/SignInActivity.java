package com.example.clonechat.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.clonechat.MainActivity;
import com.example.clonechat.Models.User;
import com.example.clonechat.R;
import com.example.clonechat.Utils.Utility;
import com.example.clonechat.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
private ActivitySignInBinding binding;
private ProgressDialog progressDialog;
private FirebaseAuth auth;
private FirebaseDatabase database;

private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Objects.requireNonNull(getSupportActionBar()).hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Opening your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this,gso);




        binding.btnGoogle.setOnClickListener(v->{
            Log.d("tag", "signin");
            signIn();
        });

            binding.btnSignIn.setOnClickListener(v->{
                Utility.hideSoftKeyboard(v);

                if(!binding.etEmailSignIn.getText().toString().isEmpty() &&
                        !binding.etPasswordSignIn.getText().toString().isEmpty())
                {
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(binding.etEmailSignIn.getText().toString(),
                            binding.etPasswordSignIn.getText().toString().trim())
                            .addOnCompleteListener(this,task->{
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Toast.makeText(SignInActivity.this, "Authenticated", Toast.LENGTH_SHORT).show();



                                    Intent main_intent = new Intent(SignInActivity.this,MainActivity.class);

                                    startActivity(main_intent);
                                    finish();
                                }else{
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(this,"Please provide the credentials to Login !",Toast.LENGTH_SHORT)
                            .show();
                }
            });

            binding.CreateAnAccTv
                    .setOnClickListener(v->{
                        Intent RegisterIntent = new Intent(SignInActivity.this,SignUpActivity.class);
                        startActivity(RegisterIntent);
                        finish();
                    });

        }

        final int RC_SIGN_IN = 69;

        private void signIn(){
            Intent intent = mGoogleSignInClient.getSignInIntent();
            Log.d("tag", "intent activated");
            startActivityForResult(intent,RC_SIGN_IN);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == RC_SIGN_IN){
                    Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try{
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        firebaseAuthWithGoogle(account.getIdToken());
                    }catch(ApiException e){
                        Log.d("tag", "but catch error"+e.getMessage());

                    }
                }
        }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        progressDialog.show();
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                //logic
                                //extracting the user information from google Api.
                                User current_user = new User();
                                current_user.setId(Objects.requireNonNull(user).getUid());
                                current_user.setEmail(user.getEmail());
                                current_user.setName(user.getDisplayName());
                                current_user.setProfilePic(Objects.requireNonNull(user.getPhotoUrl()).toString());
//                                user.getMetadata().

                                database.getReference().child("USERS").child(user.getUid()).setValue(current_user);

                                Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SignInActivity.this,"Success with Google",Toast.LENGTH_SHORT)
                                        .show();
                            }else{
                                Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT)
                                        .show();

                            }
                            progressDialog.dismiss();
                    }
                });
    }
}
