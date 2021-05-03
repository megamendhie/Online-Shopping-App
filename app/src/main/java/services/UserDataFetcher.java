package services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import models.User;

import static models.Commons.PROFILE;
import static models.Commons.USERS;

public class UserDataFetcher extends IntentService {
    FirebaseFirestore database;
    FirebaseAuth auth;
    SharedPreferences.Editor editor;
    private final String TAG = "UserDataFetcher";

    public UserDataFetcher() {
        super("UserDataFetcher");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onCreate: ");
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            if(firebaseAuth.getCurrentUser()==null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = prefs.edit();
                editor.putString(PROFILE, "");
                editor.apply();
            }
            else
                setUserData();
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    private void setUserData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        database = FirebaseFirestore.getInstance();

        final String userID = auth.getCurrentUser().getUid();

        database.collection(USERS).document(userID).addSnapshotListener((documentSnapshot, e) -> {
            Log.i(TAG, "onEvent: profile");
            if (documentSnapshot == null || !documentSnapshot.exists())
                return;

            //set user profile to SharePreference
            Gson gson = new Gson();
            Log.i(TAG, "onEvent: happended now");
            String json = gson.toJson(documentSnapshot.toObject(User.class));
            editor.putString(PROFILE, json);
            editor.apply();
        });
    }
}
