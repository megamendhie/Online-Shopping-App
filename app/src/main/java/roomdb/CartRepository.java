package roomdb;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import models.CartProduct;

public class CartRepository {
    private CartDao cartDao;
    private LiveData<List<CartProduct>> allCartProducts;

    public CartRepository(Application application){
        DeSantosDb deSantosDb = DeSantosDb.getDatabase(application);
        cartDao = deSantosDb.cartDao();
        allCartProducts = cartDao.getCart();
    }

    public LiveData<List<CartProduct>> getAllCartProducts() {
        return allCartProducts;
    }

    public void deleteFromCart(String docId){
        DeSantosDb.databaseWriteExecutor.execute(() -> cartDao.deleteCartById(docId));
    }

    public void addToCart(CartProduct cartProduct){
        DeSantosDb.databaseWriteExecutor.execute(() -> cartDao.addCart(cartProduct));
    }
}
