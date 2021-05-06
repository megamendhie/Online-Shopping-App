package interfaces;

import models.CartProduct;
import models.Product;

public interface CartOperationListener {
    void onAdd(Product product);
    void onRemove(CartProduct cartProduct);
    void increaseQuantity(CartProduct product);
    void deceaseQuantity(CartProduct product);
}
