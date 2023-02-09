package Entity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private int cartNumber;
    private int totalQuantity;
    private List<Product> cartProducts;

    public ShoppingCart() {}

    public ShoppingCart(int cartNumber) {
        this.cartNumber = cartNumber;
        totalQuantity = 0;
        cartProducts = new ArrayList<>();
    }

    public void addQuantity(int plus) {
        totalQuantity += plus;
    }

    public void setTotalQuantity(int quantity) {
        totalQuantity = quantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}
