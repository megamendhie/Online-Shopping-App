package com.sqube.desantosdirectory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import models.User;
import utils.FirebaseUtil;

import static models.Commons.EMAIL;
import static models.Commons.PASSWORD;
import static models.Commons.PROFILE;
import static models.Commons.USERS;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private final int GOOGLE_SIGN_IN = 123;
    private FirebaseUser user;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final Gson gson = new Gson();

    private String firstName;
    private String lastName;
    private String email;

    private EditText edtFirstName, edtLastName, edtEmail, edtPassword;
    private Button btnSignup, btnGoogle;
    private ProgressBar prgbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        edtFirstName = findViewById(R.id.edtFname);
        edtLastName = findViewById(R.id.edtLname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        prgbar = findViewById(R.id.prgSignup);
        btnSignup = findViewById(R.id.btnSignup); btnSignup.setOnClickListener(this);
        btnGoogle = findViewById(R.id.btnGoogle); btnGoogle.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignup:
                signUpWithEmail();
                break;
            case R.id.btnGoogle:
                enableViews(false);
                loginWithGoogle();
                break;
        }
    }

    private void loginWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, GOOGLE_SIGN_IN);
    }

    private void signUpWithEmail() {
        firstName = edtFirstName.getText().toString();
        lastName = edtLastName.getText().toString();
        email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if(firstName.isEmpty()||firstName.length()<3){
            edtFirstName.setError("Enter first name");
            return;
        }
        if(lastName.isEmpty()||lastName.length()<3){
            edtLastName.setError("Enter last name");
            return;
        }
        if(email.isEmpty()){
            edtEmail.setError("Enter your email");
            return;
        }
        if(password.isEmpty()){
            edtPassword.setError("Enter password");
            return;
        }
        if(password.length()<6){
            edtPassword.setError("Password too short");
            return;
        }
        enableViews(false);
        FirebaseUtil.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    enableViews(true);
                    if(task.isSuccessful()){
                        editor = prefs.edit();
                        editor.putString(EMAIL, email);
                        editor.putString(PASSWORD, password);
                        editor.apply();
                        user = task.getResult().getUser();
                        Snackbar.make(btnSignup, "SUCCESSFUL", Snackbar.LENGTH_SHORT).show();
                        saveUserData();
                    }
                    else
                        Snackbar.make(btnSignup, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                });
    }

    private void saveUserData() {
        String userId = user.getUid();
        User newUser = new User(firstName, lastName, email, userId);
        FirebaseUtil.getDatabase().collection(USERS).document(userId).set(newUser).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                String json = gson.toJson(newUser);
                editor = prefs.edit();
                editor.putString(PROFILE, json);
                editor.apply();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void enableViews(boolean enable) {
        btnSignup.setEnabled(enable);
        btnGoogle.setEnabled(enable);
        prgbar.setVisibility(enable? View.GONE:View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authenticateWithGoogle(account);
                Log.i("LoginActivity", "onActivityResult: account Retrieved successfully");
            } catch (ApiException e) {
                //enableViews("Cannot login at this time");
                e.printStackTrace();
            }
        }
    }

    public void authenticateWithGoogle(GoogleSignInAccount account){
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseUtil.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        user = FirebaseUtil.getAuth().getCurrentUser();
                        String userId = user.getUid();
                        verifyUser(userId);
                    }
                    else{
                        final String message = "Login unsuccessful. " + task.getException().getMessage();
                        Snackbar.make(btnGoogle, message, Snackbar.LENGTH_SHORT).show();
                        FirebaseUtil.getAuth().signOut();
                        mGoogleSignInClient.signOut();
                    }
                });
    }

    private void verifyUser(String userId) {
        FirebaseUtil.getDatabase().collection(USERS).document(userId).get().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                if (task.getResult() == null || !task.getResult().exists()) {
                    String displayName = user.getDisplayName();
                    String[] names = displayName.split(" ");
                    String firstName = names[0];
                    String lastName = names[1];
                    User newUser = new User(firstName, lastName, user.getEmail(), userId);

                    FirebaseUtil.getDatabase().collection(USERS).document(userId).set(newUser).addOnCompleteListener(this, task2 -> {
                        if (task.isSuccessful()) {
                            String json = gson.toJson(newUser);
                            editor = prefs.edit();
                            editor.putString(PROFILE, json);
                            editor.apply();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
                else if(task.getResult().exists()){
                    User user = task.getResult().toObject(User.class);
                    String json = gson.toJson(user);
                    editor = prefs.edit();
                    editor.putString(PROFILE, json);
                    editor.apply();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }
}