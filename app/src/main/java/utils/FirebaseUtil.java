package utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseUtil {
    private static FirebaseAuth auth;
    private static FirebaseFirestore database;
    private static FirebaseStorage storage;
    private static FirebaseDatabase realtimeDb;


    public static void initFirebase(){
            setAuth(FirebaseAuth.getInstance());
            setDatabase(FirebaseFirestore.getInstance());
            setRealtimeDb(FirebaseDatabase.getInstance());
            setStorage(FirebaseStorage.getInstance());
    }

    public static FirebaseAuth getAuth() {
        if(auth==null)
            initFirebase();
        return auth;
    }

    private static void setAuth(FirebaseAuth auth) {
        FirebaseUtil.auth = auth;
    }

    public static FirebaseFirestore getDatabase() {
        if(database==null)
            initFirebase();
        return database;
    }

    private static void setDatabase(FirebaseFirestore database) {
        FirebaseUtil.database = database;
    }

    public static FirebaseStorage getStorage() {
        if(storage==null)
            initFirebase();
        return storage;
    }

    private static void setStorage(FirebaseStorage storage) {
        FirebaseUtil.storage = storage;
    }

    public static FirebaseDatabase getRealtimeDb() {
        if(realtimeDb==null)
            initFirebase();
        return realtimeDb;
    }

    public static void setRealtimeDb(FirebaseDatabase realtimeDb) {
        FirebaseUtil.realtimeDb = realtimeDb;
    }
}
