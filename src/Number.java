public class Number {
    private String value;
    private String type = "NUMBER";

    public Number(String value) {
        this.value = value;
    }

    public String toString() {
        return "NUMBER: " + value;
    }
}