public class Float {
    private String value;
    private String type = "FLOAT";

    public Float(String value)
    {
        this.value = value;
    }

    public String toString() {
        return "FLOAT: " + value;
    }
}