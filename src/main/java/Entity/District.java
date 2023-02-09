package Entity;

public class District {
    private int id;
    private String name;
    int provinceId;

    public District() {
    }

    public District(int id, String name, int provinceId) {
        this.id = id;
        this.name = name;
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public String getName() {
        return name;
    }
}
