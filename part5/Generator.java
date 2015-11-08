

public class Generator {

	public static void begin() {
		System.out.println("#include<stdio.h>");
		System.out.println("int main() {");
	}

	public static void declare() {
		System.out.print("int ");
	}

	public static void print_fcn() {
		System.out.print("printf(\"%d\\n\",");
	}

	public static void if_fcn() {
		System.out.print("if(");
	}

	public static void then() {
	}

	public static void elseif_fcn() {
		System.out.print("else if(");
	}

	public static void else_fcn() {
		System.out.print("else{");
	}

	public static void end_if() {
		System.out.print(")");
	}

	public static void do_fcn() {
		System.out.print("while(");
	}

	public static void end_do() {
		//System.out.print(")");
	}

	public static void assign() {
		System.out.print("=");
	}

	public static void comma() {
		System.out.print(",");
	}

	public static void left_paren() {
		System.out.print("(");
	}

	public static void right_paren() {
		System.out.print(")");
	}
	public static void left_bracket() {
		System.out.print("{");
	}
	public static void right_bracket() {
		System.out.print("}");
	}

	public static void plus() {
		System.out.print("+");
	}

	public static void minus() {
		System.out.print("-");
	}

	public static void times() {
		System.out.print("*");
	}

	public static void divide() {
		System.out.print("/");
	}
	
	public static void printLine(){
		System.out.println();
	}
	
	public static void semicolon(){
	    System.out.println(";");
	}
	


}