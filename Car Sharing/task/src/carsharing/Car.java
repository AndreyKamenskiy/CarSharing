package carsharing;

public class Car {
    private int id;
    private String name;
    private int companyId;


    public Car(int id, String name, int companyId) {
        this.name = name;
        this.id = id;
        this.companyId = companyId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCompanyId() {
        return companyId;
    }
}
