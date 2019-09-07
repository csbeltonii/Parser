       import java.util.*;
import java.io.*;

class LexicalAnalyzer {

    private String[] keywords;
    private char[] expressions;
    private Scanner lexer;
    private String currentLine;
    private int commentDepth;
    private int currentState;
    private char commentHold;
    private char operatorHold;
    private boolean singleLnCom;
    private ArrayList<Object> SymbolTable;
    private LinkedList<String> tokens;


    public LexicalAnalyzer(File input) throws IOException {

        keywords = getKeyWords();
        expressions = getExpressions();
        lexer = new Scanner(input);
        commentDepth = 0;
        currentState = 0;
        singleLnCom = false;
        SymbolTable = new ArrayList<Object>();
        tokens = new LinkedList<String>();

        while (lexer.hasNext())
        {
            currentLine = lexer.nextLine() + " ";

            //System.out.println("\nINPUT: " + currentLine + "\n");

            analyze(currentLine);
        }

        /*for (int i=0;i<tokens.size();i++)
            System.out.println(tokens.get(i));*/
    }

    private void analyze(String line) {

        StringBuilder lexeme = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            currentState = transition(currentState, line.charAt(i), lexeme);

            if (i == line.length() - 1) {
                singleLnCom = false;
            }
        }
    }

    private int transition(int curState, char sym, StringBuilder lx) {

        boolean loop = true;

        while (loop) {
            switch (curState) {
                case 0:

                    if (checkDepth())
                        curState = 1;
                    else
                        curState = 2;

                    if (singleLnCom) {
                        curState = 0;
                        loop = false;
                    }
                    break;
                case 1:

                    if (Character.isLetter(sym)) {
                        lx.append(sym);
                        curState = 3;
                        loop = false;
                    } else if (Character.isDigit(sym)) {
                        lx.append(sym);
                        curState = 4;
                        loop = false;

                    } else if (sym == '/') {
                        //System.out.println("going to state 5");
                        //System.out.println("symbol: " + sym);
                        commentHold = sym;
                        curState = 5;
                        loop = false;
                    } else if (isExpression(sym, expressions)) {
                        curState = 9;
                    }
                    else if (!Character.isLetterOrDigit(sym) && !Character.isWhitespace(sym))
                    {
                        curState = 0;
                        //System.out.println("ERROR: " + sym);
                        loop = false;
                    }
                    else {
                        curState = 0;
                        loop = false;
                    }

                    break;
                case 2:
                    if (sym == '*')
                    {
                        commentHold = sym;
                        curState = 6;
                        loop = false;
                    }
                    else if (sym == '/')
                    {
                        commentHold = sym;
                        curState = 7;
                        loop = false;
                    }
                    else
                    {
                        curState = 0;
                        loop = false;
                    }


                    break;
                case 3:
                    if (Character.isLetter(sym))
                    {
                        lx.append(sym);
                        curState = 3;
                        loop = false;
                    }
                    else if (Character.isDigit(sym))
                    {
                        if (!isKeyword(lx.toString(),keywords)) {
                            createIdentifier(lx.toString());
                            lx.setLength(0);
                            curState = 4;
                        }
                        else
                        {
                            createKeyword(lx.toString());
                            lx.setLength(0);
                            curState = 4;
                        }
                    }
                    else if (isExpression(sym, expressions))
                    {
                        if (!isKeyword(lx.toString(),keywords))
                        {
                            createIdentifier(lx.toString());
                            lx.setLength(0);
                            curState = 1;
                        }
                        else
                        {
                            createKeyword(lx.toString());
                            lx.setLength(0);
                            curState = 1;
                        }
                    }
                    else if ((sym == ' '))
                    {
                        if (!isKeyword(lx.toString(),keywords)) {
                            createIdentifier(lx.toString());
                            lx.setLength(0);
                            curState = 1;
                        }
                        else
                        {
                            createKeyword(lx.toString());
                            lx.setLength(0);
                            curState = 1;
                        }
                    }
                    else
                    {
                        curState = 8;
                    }

                    break;
                case 4:
                    if (Character.isDigit(sym))
                    {
                        lx.append(sym);
                        curState = 4;
                        loop = false;
                    }
                    else if (sym == '.' || sym == 'E')
                    {
                        lx.append(sym);
                        curState = 11;
                        loop = false;
                    }
                    else if (Character.isLetter(sym))
                    {
                        createNumber(lx.toString());
                        lx.setLength(0);
                        curState = 3;
                    }
                    else if (sym == ' ')
                    {
                        createNumber(lx.toString());
                        lx.setLength(0);
                        curState = 1;
                        loop = false;
                    }
                    else if (isExpression(sym, expressions))
                    {
                        createNumber(lx.toString());
                        lx.setLength(0);
                        curState = 1;
                    }
                    else
                    {
                        createNumber(lx.toString());
                        lx.setLength(0);
                        curState = 8;
                    }

                    break;
                case 5:
                    //System.out.println("here");

                    if (commentHold == '/' && sym == '*')
                    {
                        //System.out.println("Comment depth increment.");
                        commentDepth++;
                        curState = 0;
                        commentHold = ' ';
                        loop = false;
                    }
                    else if (commentHold == '/' && sym == '/')
                    {
                        curState = 0;
                        singleLnCom = true;
                        commentHold = ' ';
                    }
                    else if (Character.isLetterOrDigit(sym))
                    {
                        //System.out.println("Doing this.");
                        tokens.add(Character.toString(commentHold));
                        commentHold = ' ';

                        if (Character.isLetter(sym))
                        {
                            //System.out.println("Doing that.");
                            curState = 3;
                        }
                        else
                        {
                            //System.out.println("Doing something");
                            curState = 4;
                        }

                    }
                    else if (commentHold == '/' && sym == ' ')
                    {
                        tokens.add(Character.toString(commentHold));
                        operatorHold = ' ';
                        curState = 0;
                        loop = false;
                    }
                    else
                    {
                        tokens.add(Character.toString(commentHold));
                        commentHold = ' ';
                        //System.out.println(sym);
                        curState = 0;
                    }

                    break;
                case 6:
                    if (commentHold == '*' && sym == '/')
                    {
                        //System.out.println("Comment depth decrement.");
                        commentDepth--;
                        commentHold = ' ';
                        curState = 0;
                    }

                    loop = false;
                    break;
                case 7:
                    if (commentHold == '/' && sym == '*')
                    {
                        //System.out.println("Comment depth increment.");
                        commentHold = ' ';
                        commentDepth++;
                        curState = 0;
                    }
                    else {
                        commentHold = ' ';
                        curState = 2;
                    }
                    loop = false;

                    break;
                case 8:

                    if (sym == ' ' || isExpression(sym, expressions))
                    {
                        //System.out.println("ERROR: " + lx.toString());
                        System.out.println("REJECT.");
                        System.exit(1);
                        lx.setLength(0);
                        curState = 0;
                    }
                    else
                    {
                        lx.append(sym);
                    }

                    loop = false;

                    break;
                case 9:
                    if (sym == '<' || sym == '>' || sym == '=' || sym == '!')
                    {
                        operatorHold = sym;
                        curState = 10;
                        lx.append(sym);
                        //System.out.print(sym);
                    }
                    else
                    {
                        //System.out.println(sym);
                        tokens.add(Character.toString(sym));
                        lx.setLength(0);
                        curState = 1;
                    }

                    loop = false;
                    break;
                case 10:
                    if (sym == '=')
                    {
                        lx.append(sym);
                        //System.out.println(lx.toString());
                        tokens.add(lx.toString());
                        //System.out.println(sym);
                        operatorHold = ' ';
                        curState = 1;
                        lx.setLength(0);
                        loop = false;
                    }
                    else if (sym == ' ')
                    {
                        curState = 0;
                        //System.out.println(lx.toString());
                        tokens.add(lx.toString());
                        lx.setLength(0);
                        loop = false;
                    }
                    else if (Character.isLetter(sym))
                    {
                        //System.out.println(lx.toString());
                        tokens.add(lx.toString());
                        lx.setLength(0);
                        curState = 3;
                    }
                    else
                    {
                        tokens.add(lx.toString());
                        lx.setLength(0);
                        operatorHold = ' ';
                        curState = 4;
                    }
                    break;
                case 11:
                    if (Character.isDigit(sym))
                    {
                        lx.append(sym);
                        loop = false;
                    }
                    else if (sym == 'E' || sym == 'e')
                    {
                        lx.append(sym);
                        curState = 12;
                        loop = false;
                    }
                    else if (Character.isLetter(sym))
                    {
                        createFloat(lx.toString());
                        lx.setLength(0);
                        curState = 3;
                    }
                    else if (sym == ' ')
                    {
                        createFloat(lx.toString());
                        lx.setLength(0);
                        curState = 1;
                    }
                    else if (isExpression(sym,expressions))
                    {
                        createFloat(lx.toString());
                        lx.setLength(0);
                        curState = 1;
                    }
                    else
                    {
                        lx.append(sym);
                        curState = 8;
                        loop = false;
                    }
                    break;
                case 12:
                    if (sym == '+' || sym == '-')
                    {
                        lx.append(sym);
                        loop = false;
                    }
                    else if (sym == 'E')
                    {
                        lx.append(sym);
                        curState = 8;
                        loop = false;
                    }
                    else if (Character.isDigit(sym))
                    {
                        lx.append(sym);
                        loop = false;
                    }
                    else if (Character.isLetter(sym))
                    {
                        createFloat(lx.toString());
                        lx.setLength(0);
                        curState = 3;
                        loop = false;
                    }
                    else if (isExpression(sym,expressions))
                    {
                        createFloat(lx.toString());
                        lx.setLength(0);
                        curState = 1;
                    }
                    else if (sym == ' ')
                    {
                        createFloat(lx.toString());
                        curState = 1;
                        lx.setLength(0);
                        loop = false;
                    }
                    break;
            }
        }
        return curState;
    }

    private void createKeyword(String x) {
        Keyword kw = new Keyword(x);
        //SymbolTable.add(kw);
        //System.out.println(kw.toString());
        tokens.add(x);
    }

    private void createIdentifier(String x) {
        Identifier id = new Identifier(x);
        //SymbolTable.add(id);
        //System.out.println(id.toString());
        tokens.add(x);
    }

    private void createNumber(String x) {
        Number nm = new Number(x);
        //SymbolTable.add(nm);
        //System.out.println(nm.toString());
        tokens.add(x);
    }

    private void createFloat(String x) {
        Float fl = new Float(x);
        //SymbolTable.add(fl);
        //System.out.println(fl.toString());
        tokens.add(x);
    }

    private String[] getKeyWords() {
        String[] kw = {"if", "else", "int", "return", "void", "while", "float"};

        return kw;
    }

    private char[] getExpressions() {
        char[] exp = {'+', '-', '*', '/', '<', '>', '=', ';', ',', '(', ')', '}', '{', '!', '[', ']'};

        return exp;
    }

    private boolean checkDepth() {

        if (commentDepth == 0) {
            return true;
        }

        return false;
    }

    private boolean isKeyword(String lx, String[] kw) {
        for (int i=0;i<kw.length;i++) {
            if (lx.matches(kw[i])){
                return true;
            }
        }

        return false;
    }

    private boolean isExpression(char sym, char[] exp) {
        for (int i=0;i<exp.length;i++){
            if (exp[i] == sym)
            {
                return true;
            }
        }

        return false;
    }

    public LinkedList getTokens() {
        return tokens;
    }
}