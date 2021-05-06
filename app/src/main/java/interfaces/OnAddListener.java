package interfaces;

import models.Category;
import models.Product;

public interface OnAddListener {
    void onComplete(boolean editable);
    void onEdit(Category category, boolean delete);
}
