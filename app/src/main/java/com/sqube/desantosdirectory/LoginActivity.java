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
import android.widget.TextView;

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

import utils.FirebaseUtil;

import static models.Commons.EMAIL;
import static models.Commons.PASSWORD;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogle;
    private ProgressBar prgbar;
    private final int CODE_SIGN_UP = 432;
    private final int GOOGLE_SIGN_IN = 123;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        TextView txtSignup = findViewById(R.id.txtSignUp); txtSignup.setOnClickListener(this);
        prgbar = findViewById(R.id.prgLogin);
        btnLogin = findViewById(R.id.btnLogin); btnLogin.setOnClickListener(this);
        btnGoogle = findViewById(R.id.btnGoogle); btnGoogle.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        edtEmail.setText(prefs.getString(EMAIL, ""));
        edtPassword.setText(prefs.getString(PASSWORD, ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtSignUp:
                startActivityForResult(new Intent(this, SignupActivity.class), CODE_SIGN_UP);
                break;
            case R.id.btnLogin:
                loginWithEmail();
                break;
            case R.id.btnGoogle:
                LoginWithGoogle();
                break;
        }
    }

    private void LoginWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, GOOGLE_SIGN_IN);
    }

    private void loginWithEmail() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

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
        FirebaseUtil.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            enableViews(true);
            if(task.isSuccessful()){
                editor = prefs.edit();
                editor.putString(EMAIL, email);
                editor.putString(PASSWORD, password);
                editor.apply();
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
            }
            else
                Snackbar.make(btnLogin, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void enableViews(boolean enable) {
        btnLogin.setEnabled(enable);
        btnGoogle.setEnabled(enable);
        prgbar.setVisibility(enable? View.GONE:View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_SIGN_UP && resultCode==RESULT_OK){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if (requestCode==GOOGLE_SIGN_IN){
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

    }
}