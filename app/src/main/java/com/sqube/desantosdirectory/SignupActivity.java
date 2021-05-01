package com.sqube.desantosdirectory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import models.User;
import utils.FirebaseUtil;

import static models.Commons.EMAIL;
import static models.Commons.PASSWORD;
import static models.Commons.USERS;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGNUP= 321;
    private final static int RC_SIGN_IN = 123;
    private FirebaseUser user;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();

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
    }

    @Override
    public void onClick(View v) {
        enableViews(false);
        switch (v.getId()){
            case R.id.btnSignup:
                signUpWithEmail();
                break;
            case R.id.btnGoogle:
                LoginWithGoogle();
                break;
        }
    }

    private void LoginWithGoogle() {

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
}