package Entity;

public class Address {
    private Integer id;
    private String province;
    private String district;
    private String ward;
    private String specificAddress;

    public Address() {

    }

    public Address(String province, String district, String ward, String specificAddress) {
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.specificAddress = specificAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public String getWard() {
        return ward;
    }

    public String getSpecificAddress() {
        return specificAddress;
    }
}
