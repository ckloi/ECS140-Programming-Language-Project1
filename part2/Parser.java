/* *** This file is given as part of the programming assignment. *** */

public class Parser {


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
    if( tok.kind != TK.EOF )
        parse_error("junk after logical end of program");
    }

    private void program() {
    block();
    }

    private void block(){
    declaration_list();
    statement_list();
    }

    private void declaration_list() {
    // below checks whether tok is in first set of declaration.
    // here, that's easy since there's only one token kind in the set.
    // in other places, though, there might be more.
    // so, you might want to write a general function to handle that.
        while( is(TK.DECLARE) ) {
            declaration();
        }
    }

    

    private void statement_list() {
        while(statement()){
            
        }
    }
    
    private void declaration() {
        mustbe(TK.DECLARE);
        mustbe(TK.ID);
        while( is(TK.COMMA) ) {
            mustbe(TK.COMMA);
            mustbe(TK.ID);
        }
    }

    private boolean statement() {
        if(is(TK.PRINT))
            print();
        else if(is(TK.DO))
            do_fcn();
        else if(is(TK.IF))
            if_fcn();
        else if(is(TK.TILDE) || is(TK.ID))
            assignment();
        else
            return false;
        return true;
   }

    private void print(){
        mustbe(TK.PRINT);
        expr();        
    }
    
    private void assignment(){
        ref_id();
        mustbe(TK.ASSIGN);
        expr();
    }
    
    private void ref_id(){
        if(is(TK.TILDE)){
            mustbe(TK.TILDE);
            if(is(TK.NUM)) {
                // check this
                mustbe(TK.NUM);
            }
        }
        mustbe(TK.ID); // check what to do when hit nonterminal
    }
    
    private void do_fcn(){
        mustbe(TK.DO);
        guarded_command();
        mustbe(TK.ENDDO);
    }
    
    private void if_fcn(){
        mustbe(TK.IF);
        guarded_command();
        while ( is(TK.ELSEIF)){
            mustbe(TK.ELSEIF);
            guarded_command();
        }
                
        if(is(TK.ELSE)){
            mustbe(TK.ELSE);
            block();
        }

        mustbe(TK.ENDIF);
        
    }
    
    private void guarded_command(){
        expr();
        mustbe(TK.THEN);
        block();
    }

    private void expr(){
        term();
        while(addop()){    //while we see '+' or '-', do...
            term();
        }   
    }
    
    private void term(){
        factor();
        while(multop()){    //while we see '*' or '/', do...
            factor();
        }
               
    }
    
    private void factor(){
        if( is(TK.LPAREN)){
            mustbe(TK.LPAREN);
            expr();
            mustbe(TK.RPAREN);
        }
        //if we see id or '~', go to ref_id() for further actions
        else if( is(TK.ID) || is(TK.TILDE))  
            ref_id();
        else
            mustbe(TK.NUM);   
    }

    private boolean addop() {
        if( is(TK.PLUS) )
            mustbe(TK.PLUS);
        else if( is(TK.MINUS))
            mustbe(TK.MINUS);
        else
            return false;
        return true;   //return true if we see '+' or '-'
               
    }
    
    private boolean multop() {
        if( is(TK.TIMES) )
            mustbe(TK.TIMES);
        else if( is(TK.DIVIDE))
            mustbe(TK.DIVIDE);
        else
            return false;
        return true;   //return true if we see '*' or '/'
    }

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
    if( tok.kind != tk ) {
        System.err.println( "mustbe: want " + tk + ", got " +
                    tok);
        parse_error( "missing token (mustbe)" );
    }
    scan();
    }

    private void parse_error(String msg) {
    System.err.println( "can't parse: line "
                + tok.lineNumber + " " + msg );
    System.exit(1);
    }
}



