import java.awt.*;
import java.util.*;

public class SyntacticalAnalyzer {

    private LinkedList<String> tokens;
    private String curToken;
    private String str;

    public SyntacticalAnalyzer(LinkedList<String> tokens) {

        this.tokens = tokens;
        tokens.add("$");

        program();
        accept();
    }

    private void accept()
    {

        if (curToken.equals("$"))
        {
            System.out.println("ACCEPT");
            System.exit(0);
        }
        else
        {
            reject();
        }
    }

    private void reject()
    {
        System.out.println("REJECT.");
        System.exit(1);
    }

    private void nextToken()
    {
        curToken = tokens.removeFirst();
    }
   /*
        Format is as follows:
        1. grammar rule
        a. first set
        b. follow set
    */

    // 1. program -> declaration declarationList2
    // a. program = { int void float }
    // b. program = { $ }

    private void program()
    {
        nextToken();

        if (curToken.matches("(int)|(void)|(float)"))
        {
            declaration();
            declaration_list();
        }
        else if (curToken.equals("$"))
            return;
        else
            reject();
    }

    // 2. declaration list -> declaration declarationList2 | empty
    // a. declaration list = { float int void ϵ }
    // b. declaration list = { $ }

    private void declaration_list()
    {
        if (curToken.matches("(int)|(void)|(float)"))
        {
            declaration();
            declaration_list();
        }
        else if (curToken.equals("$"))
        {
            return;
        }
        else
            reject();
    }

    // 3. declaration -> typeSpecifier ID declarationEnd
    // a. declaration = { float int void }
    // b. declaration = { $ float int void }

    private void declaration()
    {
        type_specifier();

        if (getType(curToken).equals("id"))
        {
            nextToken();
            if (curToken.equals("(") || curToken.equals(";") || curToken.equals("["))
                declaration_end();
            else
                reject();
        }
        else
            reject();
    }

    // 4. declarationEnd -> VDEnd | ( params ) compoundstmt
    // a. declarationEnd = { ( ; [ }
    // b. declarationEnd = { $ float int void }

    private void declaration_end()
    {
        if (curToken.equals("[") || curToken.equals(";"))
        {
                VDEnd();
        }
        else if (curToken.equals("("))
        {
            nextToken();

            if (curToken.matches("(int)|(void)|(float)"))
                params();
                if (curToken.equals(")"))
                {
                    nextToken();
                    if (curToken.equals("{"))
                        compound_stmt();
                    else
                        reject();
                }
            else
                reject();
        }
        else
            reject();
    }

    // 5. VDEnd -> ; | [ NUM ] ;
    // a. VDEnd = { ; [ }
    // b. VDEnd = { $ ( ; ID NUM float if int return void while { } }

    private void VDEnd()
    {
        if (curToken.equals(";"))
        {
            nextToken();
            return;
        }
        else if (curToken.equals("["))
        {
            nextToken();
            if (getType(curToken).equals("num"))
            {
                nextToken();
                if (curToken.equals("]"))
                {
                    nextToken();
                    if (curToken.equals(";"))
                    {
                        nextToken();
                        return;
                    }
                    else
                        reject();
                }
                else
                    reject();
            }
            else
                reject();
        }
        else
            reject();
    }

    // 6. typeSpecifier -> int | void | float
    // a. typeSpecifier = { float int void }
    // b. typeSpecifier = { ID }

    private void type_specifier()
    {
        if (curToken.matches("(int)|(void)|(float)"))
        {
            nextToken();
            return;
        }
        else
            reject();
    }

    // 7. params -> int ID paramEnd paramList2 | float ID paramEnd paramList2 | void PVEnd
    // a. params = { float int void }
    // b. params = { ) }

    private void params()
    {
        if (curToken.matches("(int)|(float)"))
        {
            nextToken();
            if (getType(curToken).equals("id"))
            {
                nextToken();
                if (curToken.equals("[") || curToken.equals(",") || curToken.equals(")")) {
                    param_end();
                    if (curToken.equals(",") || curToken.equals(")"))
                        param_list();
                    else
                        reject();
                }
                else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals("void"))
        {
            nextToken();
            if (getType(curToken).equals("id") || curToken.equals(")"))
                PVEnd();
            else
                reject();
        }
        else
            reject();
    }

    // 8. PVEnd -> ID paramEnd paramList2 | empty
    // a. PVEnd = { ID ϵ }
    // b. PVEnd = { ) }

    private void PVEnd()
    {
        if ((getType(curToken)).equals("id"))
        {
            nextToken();
            if (curToken.equals("[") || curToken.equals(",") || curToken.equals("}"))
            {
                param_end();
                if (curToken.equals(",") || curToken.equals("}"))
                    param_list();
                else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals(")"))
        {
            return;
        }
        else
            reject();
    }

    // 9. paramList2 -> , param paramList2 | empty
    // a. paramList2 = { , ϵ }
    // b. paramList2 = { ) }

    private void param_list()
    {
        if (curToken.equals(","))
        {
            nextToken();
            if (curToken.matches("(int)|(void)|(float)"))
            {
                param();
                if (curToken.equals(",") || curToken.equals(")"))
                    param_list();
                else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals(")"))
        {
            return;
        }
        else
            reject();
    }

    // 10. param -> typeSpecifier ID paramEnd
    // a. param = { float int void }
    // b. param = { ) , }

    private void param()
    {
        type_specifier();

        if (getType(curToken).equals("id"))
        {
            nextToken();
            if (curToken.equals("[") || curToken.equals(",") || curToken.equals(")"))
                param_end();
            else
                reject();
        }
        else
            reject();
    }

    // 11. paramEnd -> [ ] | empty
    // a. paramEnd = { [ ϵ }
    // b. paramEnd = { ) , }

    private void param_end()
    {
        if (curToken.equals("["))
        {
            nextToken();
            if (curToken.equals(("]")))
            {
                nextToken();
            }
            else
                reject();
        }
        else if (curToken.equals(")") || curToken.equals((",")))
        {
            return;
        }
        else
            reject();
    }

    // 12. compoundStmt -> { localDeclarations2 statementList2 }
    // a. compoundStmt = { { }
    // b. compoundStmt = { $ ( ; ID NUM else float if int return void while { } }

    private void compound_stmt()
    {
        if (curToken.equals("{"))
        {
            nextToken();
            if (getType(curToken).matches("(id)|(num)") || curToken.matches("(int)|(float)|(void)|(return)|(while)|(if)") || curToken.equals("(") || curToken.equals(";")
                || curToken.equals("{") || curToken.equals("}"))
            {
                local_declarations();
                if (curToken.equals(";") || curToken.equals("(") || curToken.equals("{") || curToken.equals("}")
                    || getType(curToken).matches("(id)|(num)") || curToken.matches("(if)|(return)|(while)"))
                        statement_list();
                else
                    reject();
            }
            else
                reject();

            if (curToken.equals("}"))
            {
                nextToken();
                return;
            }
            else
                reject();
        }
        else
            reject();
    }

    // 13. localDeclarations2 -> typeSpecifier ID VDEnd localDeclarations2 | empty
    // a. localDeclarations2 = { float int void ϵ	}
    // b. localDeclarations2 = { ( ; ID NUM if return while { } }

    private void local_declarations()
    {
        if (curToken.matches("(int)|(void)|(float)"))
        {
            type_specifier();
            if (getType(curToken).equals("id"))
            {
                nextToken();
                if (curToken.equals(";") || curToken.equals("["))
                {
                    VDEnd();
                    if (curToken.matches("(int)|(void)|(float)|(if)|(return)|(while)") || getType(curToken).matches("(id)|(num)") || curToken.equals("(")
                        || curToken.equals("{") || curToken.equals("}"))
                        local_declarations();
                    else
                        reject();
                }
                else
                    reject();
            }
            else
                reject();
        }
        else
            return;
    }

    // 14. statementList2 -> statement statementList2 | empty
    // a. statementList2 = { ( ; ID NUM if return while { ϵ }
    // b. statementList2 = { } }

    private void statement_list()
    {
        if (curToken.equals("(") || curToken.equals("{") || curToken.equals(";") || getType(curToken).matches("(id)|(return)")
        || curToken.matches("(while)|(return)|(if)"))
        {
            statement();
            if (curToken.equals(";") || curToken.equals("(") || curToken.equals("{") || curToken.equals("}")
                || getType(curToken).matches("(id)|(num)") || curToken.matches("(if)|(return)|(while)"))
                statement_list();
            else
                reject();
        }
        else if (curToken.equals("}"))
        {
            return;
        }
        else
            reject();
    }

    // 15. statement -> expressionStmt | compoundStmt | selectionStmt | iterationStmt | returnStmt
    // a. statement = {( ; ID NUM if return while { }
    // b. statement = { ( ; ID NUM else if return while { } }

    private void statement()
    {
        if (getType(curToken).matches("(num)|(id)") || curToken.equals(";") || curToken.equals("(")) {
            expression_stmt();
        }
        else if (curToken.equals("{")) {
            compound_stmt();
        }
        else if (curToken.equals("if")) {
            selection_stmt();
        }
        else if (curToken.equals("while")) {
        iteration_stmt();
        }
        else if (curToken.equals("return")) {
            return_stmt();
        }
        else
            reject();
    }

    // 16. expressionStmt -> expression ; | ;
    // a. expressionStmt = { ( ; ID NUM }
    // b. expressionStmt = { ( ; ID NUM else if return while { } }

    private void expression_stmt()
    {
        if (curToken.equals("(") || getType(curToken).matches("(id)|(num)"))
        {
            expression();
            if (curToken.equals(";"))
            {
                nextToken();
                return;
            }
            else
                reject();
        }
        else if (curToken.equals(";"))
        {
            nextToken();
            return;
        }
        else
            reject();
    }

    // 17. selectionStmt -> if ( expression ) statement SSEnd
    // a. selectionStmt = { if }
    // b. selectionStmt = { ( ; ID NUM else if return while { } }

    private void selection_stmt() {
        if (curToken.equals("if")) {
            nextToken();
            if (curToken.equals("(")) {
                nextToken();
                if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                    expression();
                    if (curToken.equals(")")) {
                        nextToken();
                        if (curToken.equals("(") || curToken.equals("{") || curToken.equals(";") || getType(curToken).matches("(id)|(num)") || curToken.matches("(return)|(else)|(if)|(while)")) {
                            statement();
                            if (curToken.equals("else") || curToken.equals(";") || curToken.equals("{") || curToken.equals("(") ||
                                curToken.equals("}") || curToken.matches("(if)|(return)|(else)|(while)") || getType(curToken).matches("(id)|(num)"))
                                SSEnd();
                            else
                                reject();
                        }
                        else
                            reject();
                    } else
                        reject();
                } else
                    reject();
            } else
                reject();
        }
    }

    // 18. SSEnd -> else statement | empty
    // a. SSEnd = { else ϵ }
    // b. SSEnd = { ( ; ID NUM else if return while { } }

    private void SSEnd()
    {
        if (curToken.equals("else"))
        {
            nextToken();
            if (curToken.equals("(") || curToken.equals("{") || curToken.equals(";") || getType(curToken).matches("(id)|(num)") ||
                curToken.matches("(if)|(return)|(while)"))
                statement();
            else
                reject();
        }
        else if (curToken.equals("(") || curToken.equals(";") || curToken.equals("{") || curToken.equals("}") || getType(curToken).matches("(num)|(id)") ||
                 curToken.matches("(if)|(while)|(return)|"))
        {
            return;
        }
        else
            reject();
    }

    // 19. iterationStmt -> while ( expression ) statement
    // a. iterationStmt = { while }
    // b. iterationStmt = { ( ; ID NUM else if return while { } }

    private void iteration_stmt()
    {
        if (curToken.equals("while"))
        {
            nextToken();
            if (curToken.equals("("))
            {
                nextToken();
                if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                    expression();
                    if (curToken.equals(")")) {
                        nextToken();
                        if (curToken.equals("(") || curToken.equals("{") || curToken.equals("}") || curToken.equals(";") || getType(curToken).matches("(id)|(num)") ||
                            curToken.matches("(if)|(return)|(while)"))
                            statement();
                        else
                            reject();
                    } else
                        reject();
                }
                else
                    reject();
            }
            else
                reject();
        }
        else
            reject();
    }

    // 20. returnStmt -> return RSEnd
    // a. returnStmt = { return }
    // b. returnStmt = { ( ; ID NUM else if return while { } }

    private void return_stmt()
    {
        if (curToken.equals("return"))
        {
            nextToken();
            if (curToken.equals(";") || curToken.equals("(") || getType(curToken).matches("(id)|(num)"))
                RSEnd();
            else
                reject();
        }
        else
            reject();
    }

    // 21. RSEnd -> ; | expression ;
    // a. RSEnd = {( ; ID NUM	}
    // b. RSEnd = { ( ; ID NUM else if return while { } }

    private void RSEnd()
    {
        if (curToken.equals(";")) {
            nextToken();
            return;
        }
        else if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
            expression();
            if (curToken.equals(";"))
            {
                nextToken();
            }
            else
                reject();
        }
        else
            reject();
    }

    // 22. expression -> ID expression2 | | ( expression ) expressionEnd | NUM expressionEnd
    // a. expression = { ( ID NUM	}
    // b. expression = { ) , ; ] }

    private void expression() {
        if (getType(curToken).equals("id")) {
            nextToken();
            if (curToken.equals("(") || curToken.equals("+") || curToken.equals("-") || curToken.equals("*") || curToken.equals("/") ||
                curToken.equals("[") || curToken.equals("<") || curToken.equals(">") || curToken.equals("=") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                curToken.equals(";") || curToken.equals(",") || curToken.equals(")") || curToken.equals("]"))
                expression2();
            else
                reject();
        }
        else if (curToken.equals("(")) {
            nextToken();
            if ((curToken.equals("(") || getType(curToken).matches("(id)|(num)"))) {
                expression();
                if (curToken.equals(")")) {
                    nextToken();
                    if (curToken.equals("+") || curToken.equals("-") || curToken.equals("*") || curToken.equals("/") ||
                        curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                        curToken.equals(",") || curToken.equals(")") || curToken.equals("]") || curToken.equals(";"))
                    expression_end();
                    else
                        reject();
                } else
                    reject();
            }
            else
                reject();
        }
        else if (getType(curToken).equals("num")) {
            nextToken();
            if (curToken.equals("(") || curToken.equals("+") || curToken.equals("-") || curToken.equals("*") ||
                curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                curToken.equals(",") || curToken.equals("]") || curToken.equals(")") || curToken.equals("/") || curToken.equals(";"))
                expression_end();
            else
                reject();
        }
        else
            reject();
    }

    // 23. expression2 -> = expression | [ expression ] expression2End | expressionEnd | ( args ) expressionEnd
    // a. expression2 = { != ( * + - / < <= = == > >= [ ϵ	}
    // b. expression2 = { ) , ; ]	}

    private void expression2()
    {
        if (curToken.equals("=")) {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)"))
                expression();
            else
                reject();
        }
        else if (curToken.equals("["))
        {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                expression();
                if (curToken.equals("]")) {
                    nextToken();
                    if (curToken.equals(",") || curToken.equals(";") || curToken.equals("+") || curToken.equals("-") ||
                        curToken.equals("*") || curToken.equals("/") || curToken.equals(">") || curToken.equals("<") ||
                        curToken.equals("]") || curToken.equals("=") || curToken.equals(")") || curToken.matches("(!=)|(==)|(<=)|(>=)"))
                        expression2_end();
                    else
                        reject();
                } else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")"))
        {
            expression_end();
        }
        else if (curToken.equals("("))
        {
            nextToken();
            if (curToken.equals(")") || curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                args();

                if (curToken.equals(")")) {
                    nextToken();
                    if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                            curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                            curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")"))
                        expression_end();
                    else
                        reject();
                } else
                    reject();
            }
            else
                reject();
        }
        else
            reject();
    }

    // 24. expression2End -> = expression | expressionEnd
    // a. expression2End = { != * + - / < <= = == > >= ϵ }
    // b. expression2End = { ) , ; ] }

    private void expression2_end()
    {
        if (curToken.equals("=")) {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)"))
                expression();
            else
                reject();
        }
        else if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")")) {
            expression_end();
        }
        else
            reject();
    }


    // 25. expressionEnd -> term2 addExpression2 SEEnd
    // a. expressionEnd = { != * + - / < <= == > >= ϵ	}
    // b. expressionEnd = { ) , ; ] }

    private void expression_end()
    {
        if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
            curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
            curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")"))
        {
            term2();
            if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                    curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                    curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")"))
            {
                add_expression();
                if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                        curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                        curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")"))
                    SEEnd();
                else
                    reject();
            }
            else
                reject();
        }
        else
            reject();
    }

    // 26. SEEnd -> relop factor term2 addExpression2| empty
    // a. SEEnd = { != < <= == > >= ϵ	}
    // b. SEEnd = { ) , ; ] }

    private void SEEnd()
    {
        if (curToken.matches("(<=)|(>=)|(==)|(!=)") || curToken.equals("<") || curToken.equals(">"))
        {
            relop();

            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)"))
            {
                factor();
                if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                    curToken.equals(")") || curToken.equals(",") || curToken.equals(";") || curToken.equals("<") ||
                    curToken.equals(">") || curToken.equals("]") || curToken.matches("(<=)|(>=)|(==)|(!=)"))
                {
                    term2();
                    if (curToken.equals("+") || curToken.equals("-") ||
                        curToken.equals("<") || curToken.equals(">") || curToken.matches("(<=)|(>=)|(==)|(!=)") ||
                        curToken.equals(";") || curToken.equals(",") || curToken.equals("]") || curToken.equals(")"))
                    {
                        add_expression();
                    }
                    else
                        reject();
                }
                else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals(")") || curToken.equals(",") || curToken.equals(";") || curToken.equals("]")) {
            return;
        }
        else
            reject();
    }

    // 27. relop -> <= | < | > | >= | == | !=
    // a. relop = { != < <= == > >= }
    // b. relop = { ( ID NUM }

    private void relop()
    {
        if (curToken.matches("(<=)|(<)|(>)|(>=)|(==)|(!=)"))
            nextToken();
        else
            reject();
    }

    // 28. addExpression2 -> addop factor term2 addExpression2 | empty
    // a. addExpression2 = { + - ϵ }
    // b. addExpression2 = { != ) , ; < <= == > >= ] }

    private void add_expression()
    {
        if (curToken.equals("+") || curToken.equals("-")) {
            addop();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                factor();
                if (curToken.equals("*") || curToken.equals("/") || curToken.equals("-") || curToken.equals("+") ||
                        curToken.equals(";") || curToken.equals(",") || curToken.equals("<") || curToken.equals(">") ||
                        curToken.equals(")") || curToken.equals("]") || curToken.matches("(<=)|(>=)|(==)|(!=)")) {
                    term2();
                    if (curToken.equals(",") || curToken.equals("+") || curToken.equals("-") ||
                        curToken.equals("<") || curToken.equals(">") || curToken.equals("]") ||
                        curToken.equals(";") || curToken.equals(")") || curToken.matches("(<=)|(>=)|(==)|(!=)")) {
                        add_expression();
                    }
                    else
                        reject();
                }
                else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals(",") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") ||
                    curToken.equals("<") || curToken.equals(">") || curToken.equals("]") ||
                    curToken.equals(";") || curToken.equals(")") || curToken.matches("(<=)|(>=)|(==)|(!=)")) {
            return;
        }
        else
            reject();
    }

    // 29. addop -> + | -
    // a. addop = { + - }
    // b. addop = { ( ID NUM }

    private void addop()
    {
        if (curToken.equals("+") || curToken.equals("-"))
            nextToken();
        else
            reject();
    }

    // 30. term2 -> mulop factor term2 | empty
    // a. term2 = { * / ϵ	}
    // b. term2 = { != ) + , - ; < <= == > >= ] }

    private void term2()
    {
        if (curToken.equals("*") || (curToken.equals("/")))
        {
            mulop();
            if (curToken.equals("(") || (getType(curToken).matches("(id)|(num)")))
            {
                factor();
                if (curToken.equals("*") || (curToken.equals("/")) || curToken.equals("+") || curToken.equals("-") ||
                    curToken.equals("<") || curToken.equals(">") || curToken.equals(",") ||
                    curToken.matches("(!=)|(==)|(<=)|(>=)") || curToken.equals("]") || curToken.equals(")"))
                {
                    term2();
                }
            }
            else if (curToken.equals("*") || (curToken.equals("/")) || curToken.equals("+") || curToken.equals("-") ||
                    curToken.equals("<") || curToken.equals(">") || curToken.equals(",") ||
                    curToken.matches("(!=)|(==)|(<=)|(>=)") || curToken.equals("]") || curToken.equals(")"))
                return;
            else
                reject();
        }
    }

    // 31. mulop -> * | /
    // a. mulop = { * / }
    // b. mulop = { ( ID NUM }

    private void mulop()
    {
        if (curToken.equals("*") || (curToken.equals("/")))
        {
            nextToken();
        }
        else
            reject();
    }

    // 32. factor -> ( expression ) | ID IDEnd | NUM
    // a. factor = { ( ID NUM	}
    // b. factor = { != ) * + , - / ; < <= == > >= ] }

    private void factor()
    {
        if (curToken.equals("("))
        {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                expression();
                if (curToken.equals(")")) {
                    nextToken();
                    return;
                } else
                    reject();
            }
            else
                reject();
        }
        else if (getType(curToken).equals(("id")))
        {
            nextToken();
            if (curToken.equals("(") || curToken.equals("[") || curToken.equals("]") || curToken.equals(";") ||
                curToken.equals(")") || curToken.equals("/") || curToken.equals(">") || curToken.equals("<") ||
                curToken.equals("-") || curToken.equals("+") || curToken.equals(",") || curToken.equals("*") ||
                curToken.matches("(<=)|(>=)|(==)|(!=)"))
            {
                IDEnd();
            }
            else
                reject();
        }
        else if (getType(curToken).equals("num"))
        {
            nextToken();
            return;
        }
        else
            reject();
    }

    // 33.IDEnd -> [ expression ] | ( args ) | empty
    // a. IDEnd = {( [ ϵ }
    // b. IDEnd = { != ) * + , - / ; < <= == > >= ] }

    private void IDEnd()
    {
        if (curToken.equals("["))
        {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                expression();

                if (curToken.equals("]")) {
                    nextToken();
                    return;
                } else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals("(")) {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)") || curToken.equals(")"))
            {
                args();
                if (curToken.equals(")")) {
                    nextToken();
                    return;
                }
                else
                    reject();
            }
            else
                reject();
        }
        else if (curToken.equals("*") || curToken.equals("/") || curToken.equals("+") || curToken.equals("-") || curToken.equals(",")
                || curToken.equals(";") || curToken.equals("<") || curToken.equals(">")
                || curToken.equals(")") || curToken.equals("]") || curToken.matches("(<=)|(>=)|(==)|(!=)"))
        {
            return;
        }
        else
            reject();
    }

    // 34. args -> expression argList2 | empty
    // a. args = { ( ID NUM ϵ	}
    // b. args = { ) }

    private void args()
    {
        if (curToken.equals("(") || getType(curToken).matches("(id)|(num)"))
        {
            expression();
            if (curToken.equals(")") || curToken.equals(","))
                arg_list();
            else
                reject();
        }
        else if (curToken.equals(")"))
        {
            return;
        }
        else
            reject();
    }

    // 35. argList2 -> , expression argList2 | empty
    // a. argList2 = { , ϵ }
    // b. argList2 = { ) }

    private void arg_list()
    {
        if (curToken.equals(",")) {
            nextToken();
            if (curToken.equals("(") || getType(curToken).matches("(id)|(num)")) {
                expression();
                if (curToken.equals(",") || curToken.equals(")"))
                    arg_list();
                else
                    reject();
            } else
                reject();
        }
        else if (curToken.equals(")")) {
            return;
        }
        else
            reject();
    }

    private String getType(String str)
    {
        if (str.matches("(int)|(float)|(void)|(else)|(if)|(return)|(while)"))
            return "kw";
        else if (str.matches("[A-Za-z]*"))
            return "id";
        else if (str.matches("\\d+(\\.\\d+)?(E(\\+|-)?\\d+)?"))
            return "num";

        return str;
    }
}