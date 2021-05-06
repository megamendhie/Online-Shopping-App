package interfaces;

import models.Category;
import models.Product;

public interface OnAddProductListener {
    void onComplete(boolean editable);
    void onEdit(Product product, boolean delete);
}
