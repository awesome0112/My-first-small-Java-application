package Entity;

public class Baker extends Person {
    private int bakerNumber;
    private String userName;

    public Baker(int bakerNumber, String userName, String bakerName) {
        this.bakerNumber = bakerNumber;
        this.userName = userName;
        super.setFirstName(bakerName);
    }

    public int getBakerNumber() {
        return bakerNumber;
    }

    public String getUserName() {
        return userName;
    }
}
