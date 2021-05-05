package roomdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.CartProduct;

@Database(entities = {CartProduct.class}, version = 1, exportSchema = false)
public abstract class DeSantosDb extends RoomDatabase {

    public abstract CartDao cartDao();
    private static volatile DeSantosDb INSTANCE;
    private static final int numberOfThreads = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(numberOfThreads);

    static DeSantosDb getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (DeSantosDb.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DeSantosDb.class, "desantos_db")
                            .addCallback(deSantosDbCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback deSantosDbCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
