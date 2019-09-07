public class Keyword {
    private String value;
    private String type = "KEYWORD";

    public Keyword(String value) {
        this.value = value;
    }

    public String toString() {
        return "KEYWORD: " + value;
    }
}