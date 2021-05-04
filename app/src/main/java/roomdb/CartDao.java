package roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.CartProduct;

@Dao
public interface CartDao {

    @Insert
    void addCart(CartProduct product);

    @Query("DELETE FROM cart_table")
    void deleteAll();

    @Query("DELETE FROM cart_table WHERE docId = :docId")
    void deleteCartById(String docId);

    @Query("SELECT * FROM cart_table ORDER BY createdAt ASC")
    LiveData<List<CartProduct>> getCart();
}
