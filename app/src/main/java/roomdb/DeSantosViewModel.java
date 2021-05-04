package roomdb;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import models.CartProduct;

public class DeSantosViewModel extends AndroidViewModel {
    private final CartRepository repository;
    private final LiveData<List<CartProduct>> cartProducts;

    public DeSantosViewModel(Application application) {
        super(application);
        repository = new CartRepository(application);
        cartProducts = repository.getAllCartProducts();
    }

    LiveData<List<CartProduct>> getCartProducts(){
        return cartProducts;
    }

    public void addToCart(CartProduct cartProduct){
        repository.addToCart(cartProduct);
    }

    public void deleteFromCart(String docId){
        repository.deleteFromCart(docId);
    }
}
