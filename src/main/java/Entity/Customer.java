package Entity;

public class Customer extends Person {
    private int customerNumber;
    private String userName;
    private ShoppingCart cart;

    public Customer() {

    }

    public Customer(int customerNumber, String userName, String firstName, String lastName, String phoneNumber, int defaultAddressId) {
        super(firstName, lastName, phoneNumber);
        this.customerNumber = customerNumber;
        this.userName = userName;
        this.cart = new ShoppingCart(customerNumber);
        super.setDefaultAddress(new Address());
        super.getDefaultAddress().setId(defaultAddressId);
    }

    public String getFullName() {
        return super.getFirstName() + " " + super.getLastName();
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public void addQuantity(int plus) {
        cart.addQuantity(plus);
    }

    public void setTotalQuantity(int quantity) {
        cart.setTotalQuantity(quantity);
    }

    public int getCartTotalQuantity() {
        return cart.getTotalQuantity();
    }

    public String getUserName() {
        return userName;
    }


}
