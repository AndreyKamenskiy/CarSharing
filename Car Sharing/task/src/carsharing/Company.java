package carsharing;

public class Company {

    // to avoid null
    public static Company emptyCompany = new Company("", -1);

    private String name;
    private int id;

    public Company(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
