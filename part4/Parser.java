
/* *** This file is given as part of the programming assignment. *** */
import java.util.ArrayList;

public class Parser {

	ArrayList<ArrayList<String>> symtab = new ArrayList<ArrayList<String>>();
	Generator gen = new Generator();
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
		Generator.begin();
		block();
		System.out.println("return 0;");
		System.out.println("}");
	}

	private void block() {
	
		ArrayList<String> scope = new ArrayList<String>();
		symtab.add(scope);
	//	System.out.println("{");
		declaration_list();
		statement_list();
		symtab.remove(symtab.size() - 1);
		//System.out.println("}");
		
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
		
		boolean prev = false;
		
		mustbe(TK.DECLARE);
		Generator.declare();
		if(!redeclaration())
		    System.out.print("x_" + (symtab.size()-1)+ tok.string);
		else {
			prev = true;
		}
		mustbe(TK.ID);

		while (is(TK.COMMA)) {
			mustbe(TK.COMMA);
			
			if(!redeclaration()){
				if(!prev)
				    Generator.comma();
				System.out.print("x_" + (symtab.size()-1)+ tok.string);
				prev = false;
			}
			else
			    prev = true;
			
			
			mustbe(TK.ID);
		}

		System.out.println(";");
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
		Generator.print_fcn();
		expr();

		Generator.right_paren();
		Generator.semicolon();
	}

	private void assignment() {
		ref_id();
		mustbe(TK.ASSIGN);
		Generator.assign();
		expr();
		System.out.println(";");
	}

	private void ref_id() {
		boolean found = false;
                boolean findAll = false;
                boolean checkTilde = false;
	        int num = 0;
		if (is(TK.TILDE)) {
                        
          
			mustbe(TK.TILDE);
			
			if (is(TK.NUM)) {
				// check this
				 num = Integer.parseInt(tok.string);

				mustbe(TK.NUM);
				// -------------scope level check--------------
				if (symtab.size() - 1 >= num) {

					for (String i : symtab.get(symtab.size() - 1 - num)) {
						if (tok.string.equals(i)) {
							found = true;
                                                        checkTilde = true;
						}

					} // check if variable exists in symbol table
				}

				if (found == false) {
					System.err.println("no such variable ~" + num + tok.string + " on line " + tok.lineNumber);
					System.exit(1);
				}

			} else {
				// check the variable in the global level
				for (String i : symtab.get(0)) {
					if (tok.string.equals(i)) {
						found = true;
                                                num = symtab.size() -1;
                                                checkTilde = true;
					}
				}

				if (found == false) {

					System.err.println("no such variable ~" + tok.string + " on line " + tok.lineNumber);
					System.out.println("}");
					System.exit(1);
				}

			}

		}
                
            if(checkTilde == false){
                loop:
		for (int x = symtab.size() - 1; x >= 0; x--) {
			for (String i : symtab.get(x)) {
				if (tok.string.equals(i)) {
					found = true;
                                        num = x;
                                     break loop;         
				}
			}

		}

		if (found == false) {
			System.err.println(tok.string + " is an undeclared variable on line " + tok.lineNumber);
			System.out.println("}");
			System.exit(1);
		}
             }

                if(found == true){
                 if(checkTilde == true){
		System.out.print("x_" + (symtab.size()- 1 - num) + tok.string);
               }else{
                System.out.print("x_" + (num) + tok.string);
               }
                }
		mustbe(TK.ID); // check what to do when hit nonterminal

	}

	private void do_fcn() {
		mustbe(TK.DO);
		Generator.do_fcn();
		guarded_command();
		mustbe(TK.ENDDO);
		Generator.end_do();
	}

	private void if_fcn() {
		mustbe(TK.IF);
		Generator.if_fcn();
		guarded_command();

		// Generator.left_bracket();
		while (is(TK.ELSEIF)) {
			mustbe(TK.ELSEIF);
			Generator.elseif_fcn();
			guarded_command();
			// Generator.left_bracket();
		}

		if (is(TK.ELSE)) {
			mustbe(TK.ELSE);
			Generator.else_fcn();
			block();
			Generator.right_bracket();
			;
		}

		mustbe(TK.ENDIF);

	}

	private void guarded_command() {
		expr();
		System.out.print("<=0");
		Generator.end_if();
		mustbe(TK.THEN);
		Generator.then();
		Generator.left_bracket();
		block();
		Generator.right_bracket();
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
			Generator.left_paren();
			expr();
			mustbe(TK.RPAREN);
			Generator.right_paren();
		}
		// if we see id or '~', go to ref_id() for further actions
		else if (is(TK.ID) || is(TK.TILDE)) {
			ref_id();
		} else {
			System.out.print(tok.string);
			mustbe(TK.NUM);
		}

	}

	private boolean addop() {
		if (is(TK.PLUS)) {
			mustbe(TK.PLUS);
			Generator.plus();
		} else if (is(TK.MINUS)) {
			mustbe(TK.MINUS);
			Generator.minus();
		} else {
			return false;
		}

		return true; // return true if we see '+' or '-'

	}

	private boolean multop() {
		if (is(TK.TIMES)) {
			mustbe(TK.TIMES);
			Generator.times();
		} else if (is(TK.DIVIDE)) {
			mustbe(TK.DIVIDE);
			Generator.divide();
		} else {
			return false;
		}
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

	private boolean redeclaration() {

		for (String i : symtab.get(symtab.size() - 1)) {
			if (tok.string.equals(i)) {
				System.err.println("redeclaration of variable " + tok.string);
				return true;
			}
		} // if redeclaration, print error

		symtab.get(symtab.size() - 1).add(tok.string);
		/*System.out.print("x_" + tok.string);
		if(flag)
		    Generator.comma(); */
		return false; // if not redeclaration

	}
}

// Generator
