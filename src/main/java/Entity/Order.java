package Entity;

public class Order {
    private int orderNumber;
    private String orderDate;
    private String shippedDate;
    private String status;
    private double totalPayment;
    private Address address;

    public Order(int orderNumber, String orderDate, String shippedDate, String status, double totalPayment, Address address) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.shippedDate = shippedDate;
        this.status = status;
        this.totalPayment = totalPayment;
        this.address = address;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public Address getAddress() {
        return address;
    }

    public int getOrderNumber() {
        return orderNumber;
    }
}
