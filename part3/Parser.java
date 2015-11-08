
/* *** This file is given as part of the programming assignment. *** */
import java.util.ArrayList;

public class Parser {

    ArrayList<ArrayList<String>> symtab = new ArrayList<ArrayList<String>>();
    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token

    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;

    Parser(Scan scanner) {

        this.scanner = scanner;
        scan();
        program();
        if (tok.kind != TK.EOF)
            parse_error("junk after logical end of program");
    }

    private void program() {
        block();
    }

    private void block() {
        ArrayList<String> scope = new ArrayList<String>();
        symtab.add(scope);
        declaration_list();
        statement_list();
        symtab.remove(symtab.size() - 1);
    }

    private void declaration_list() {
        // below checks whether tok is in first set of declaration.
        // here, that's easy since there's only one token kind in the set.
        // in other places, though, there might be more.
        // so, you might want to write a general function to handle that.
        while (is(TK.DECLARE)) {
            declaration();
        }
    }

    private void statement_list() {
        while (statement()) {

        }
    }

    private void declaration() {
        // redeclartion check

        mustbe(TK.DECLARE);
        redeclaration();
        mustbe(TK.ID);

        while (is(TK.COMMA)) {
            mustbe(TK.COMMA);
            redeclaration();
            mustbe(TK.ID);
        }
        // end of redeclaration check
    }

    private boolean statement() {
        if (is(TK.PRINT))
            print();
        else if (is(TK.DO))
            do_fcn();
        else if (is(TK.IF))
            if_fcn();
        else if (is(TK.TILDE) || is(TK.ID))
            assignment();
        else
            return false;
        return true;
    }

    private void print() {
        mustbe(TK.PRINT);
        expr();
    }

    private void assignment() {
        ref_id();
        mustbe(TK.ASSIGN);
        expr();
    }

    private void ref_id() {
        boolean found = false;
        if (is(TK.TILDE)) {

            mustbe(TK.TILDE);

            if (is(TK.NUM)) {
                // check this
                String num = tok.string;

                mustbe(TK.NUM);
                // -------------scope level check--------------
                if (symtab.size() - 1 >= Integer.parseInt(num)) {

                    for (String i : symtab.get(symtab.size() - 1 - Integer.parseInt(num))) {
                        if (tok.string.equals(i)) {
                            found = true;
                        }

                    }
                }

                if (found == false) {
                    System.err.println("no such variable ~" + num + tok.string + " on line " + tok.lineNumber);
                    System.exit(0);
                }

            } else {
                // check the variable in the global level
                for (String i : symtab.get(0)) {
                    if (tok.string.equals(i)) {
                        found = true;
                    }
                }

                if (found == false) {
                    System.err.println("no such variable ~" + tok.string + " on line " + tok.lineNumber);
                    System.exit(0);
                }

            }

        }

        for (int x = symtab.size() - 1; x >= 0; x--) {
            for (String i : symtab.get(x)) {
                if (tok.string.equals(i)) {
                    found = true;
                }
            }

        }

        if (found == false) {
            System.err.println(tok.string + " is an undeclared variable on line " + tok.lineNumber);
            System.exit(0);
        }

        mustbe(TK.ID); // check what to do when hit nonterminal
    }

    private void do_fcn() {
        mustbe(TK.DO);
        guarded_command();
        mustbe(TK.ENDDO);
    }

    private void if_fcn() {
        mustbe(TK.IF);
        guarded_command();
        while (is(TK.ELSEIF)) {
            mustbe(TK.ELSEIF);
            guarded_command();
        }

        if (is(TK.ELSE)) {
            mustbe(TK.ELSE);
            block();
        }

        mustbe(TK.ENDIF);

    }

    private void guarded_command() {
        expr();
        mustbe(TK.THEN);
        block();
    }

    private void expr() {
        term();
        while (addop()) { // while we see '+' or '-', do...
            term();
        }
    }

    private void term() {
        factor();
        while (multop()) { // while we see '*' or '/', do...
            factor();
        }

    }

    private void factor() {
        if (is(TK.LPAREN)) {
            mustbe(TK.LPAREN);
            expr();
            mustbe(TK.RPAREN);
        }
        // if we see id or '~', go to ref_id() for further actions
        else if (is(TK.ID) || is(TK.TILDE))
            ref_id();
        else
            mustbe(TK.NUM);
    }

    private boolean addop() {
        if (is(TK.PLUS))
            mustbe(TK.PLUS);
        else if (is(TK.MINUS))
            mustbe(TK.MINUS);
        else
            return false;
        return true; // return true if we see '+' or '-'

    }

    private boolean multop() {
        if (is(TK.TIMES))
            mustbe(TK.TIMES);
        else if (is(TK.DIVIDE))
            mustbe(TK.DIVIDE);
        else
            return false;
        return true; // return true if we see '*' or '/'
    }

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
        if (tok.kind != tk) {
            System.err.println("mustbe: want " + tk + ", got " + tok);
            parse_error("missing token (mustbe)");
        }
        scan();
    }

    private void parse_error(String msg) {
        System.err.println("can't parse: line " + tok.lineNumber + " " + msg);
        System.exit(1);
    }

    private void redeclaration() {
        boolean flag = false;
        for (String i : symtab.get(symtab.size() - 1)) {
            if (tok.string.equals(i)) {
                System.out.println("redeclaration of variable " + tok.string);
                flag = true;
            }
        }

        if (flag == false) {
            symtab.get(symtab.size() - 1).add(tok.string);
        }
    }
}




