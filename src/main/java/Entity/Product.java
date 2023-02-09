package Entity;

public class Product {
    private String code;
    private String name;
    private String productLine;
    private String description;
    private int quantityInStock;
    private int quantityOrder;
    private double price;
    private String imagePath;

    public Product() {}

    public Product(String code, String name, String productLine, double price, String imagePath) {
        this.code = code;
        this.name = name;
        this.productLine = productLine;
        this.price = price;
        this.imagePath = imagePath;
    }

    public Product(String code, String name, String productLine, double price, String imagePath, int quantityOrder) {
        this.code = code;
        this.name = name;
        this.productLine = productLine;
        this.price = price;
        this.imagePath = imagePath;
        this.quantityOrder = quantityOrder;
    }

    public Product(String code, String name, String productLine, double price, String imagePath, String description) {
        this.code = code;
        this.name = name;
        this.productLine = productLine;
        this.price = price;
        this.imagePath = imagePath;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCode() {
        return code;
    }

    public String getProductLine() {
        return productLine;
    }

    public int getQuantityOrder() {
        return quantityOrder;
    }

    public String getDescription() {
        return description;
    }
}
