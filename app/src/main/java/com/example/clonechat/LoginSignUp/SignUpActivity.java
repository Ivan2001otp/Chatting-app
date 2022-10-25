package com.example.clonechat.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clonechat.MainActivity;
import com.example.clonechat.Models.User;
import com.example.clonechat.R;
import com.example.clonechat.Utils.Utility;
import com.example.clonechat.databinding.ActivitySignUpBinding;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
private ActivitySignUpBinding binding;
private FirebaseAuth auth;
private FirebaseDatabase database;
private ProgressDialog progressDialog;
private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        auth  = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SignUpActivity.this,gso);


        Objects.requireNonNull(getSupportActionBar()).hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Creating space for you !");



        binding.btnSignUp.setOnClickListener(v->{

            Utility.hideSoftKeyboard(v);
//            progressDialog.show();



            if(!binding.etEmailSignUp.getText().toString().isEmpty() &&
            !binding.etPasswordSignIn.getText().toString().isEmpty() &&
        !binding.etNameSignUp.getText().toString().isEmpty() &&(binding.femaleTick.isChecked() || binding.maleTick.isChecked()) ){



                auth.createUserWithEmailAndPassword(binding.etEmailSignUp.getText().toString().trim(),
                        binding.etPasswordSignIn.getText().toString().trim())
                        .addOnCompleteListener(SignUpActivity.this,result->{
                            progressDialog.show();

                            if(result.isSuccessful()){
                                User user = new User(binding.etNameSignUp.getText().toString(),
                                        binding.etPasswordSignIn.getText().toString(),
                                        binding.etEmailSignUp.getText().toString());

                                if(binding.maleTick.isChecked() && !binding.femaleTick.isChecked()){
                                    user.setMaleBoolean(true);
                                    String id = Objects.requireNonNull(result.getResult().getUser()).getUid();
                                    database.getReference().child("USERS").child(id).setValue(user);
                                    Toast.makeText(SignUpActivity.this,"Success",Toast.LENGTH_SHORT)
                                            .show();
                                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else if(binding.femaleTick.isChecked() && !binding.maleTick.isChecked()){
                                    user.setFemaleBoolean(true);
                                    String id = Objects.requireNonNull(result.getResult().getUser()).getUid();
                                    database.getReference().child("USERS").child(id).setValue(user);

                                    Toast.makeText(SignUpActivity.this,"Success",Toast.LENGTH_SHORT)
                                            .show();

                                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }else{
                                Toast.makeText(SignUpActivity.this,result.getException().getMessage(),Toast.LENGTH_SHORT)
                                        .show();
                            }
                        progressDialog.dismiss();
                        });
            }
            else{
                Toast.makeText(SignUpActivity.this,"NULL String",Toast.LENGTH_SHORT)
                        .show();
            }

        });

        binding.btnGoogle.setOnClickListener(v->{
            signUp();
        });



       binding.AlreadyHaveAccountTv.setOnClickListener(v->{
           Intent login_intent = new Intent(SignUpActivity.this,SignInActivity.class);
           startActivity(login_intent);
           finish();
       });



    }

    final int RC_SIGN_IN = 69;

    private void signUp() {

        Intent googleIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try{
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        }catch (ApiException e){
            Log.d("tag", "onActivityResult: "+e.getMessage());
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

                            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(SignUpActivity.this,"Success with Google",Toast.LENGTH_SHORT)
                                    .show();
                        }else{

                        }
                        progressDialog.dismiss();
                    }
                });
    }
}

