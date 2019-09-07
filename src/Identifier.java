public class Identifier {
    private String value;
    private String type = "IDENTIFIER";

    public Identifier(String value) {
        this.value = value;
    }

    public String toString() {
        return "IDENTIFIER: " + value;
    }
}