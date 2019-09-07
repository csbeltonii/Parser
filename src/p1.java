import java.io.*;

public class p1 {

    public static void main(String[] args) throws IOException {

        File inputFile = new File(args[0]);

        LexicalAnalyzer partOne = new LexicalAnalyzer(inputFile);

        SyntacticalAnalyzer partTwo = new SyntacticalAnalyzer(partOne.getTokens());
    }
}