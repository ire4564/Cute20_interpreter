package lexer;


public enum TokenType {
	INT,
	ID, 
	TRUE, FALSE, NOT,
	PLUS, MINUS, TIMES, DIV,   //special character
	LT, GT, EQ, APOSTROPHE,    //special character
	L_PAREN, R_PAREN,QUESTION, //special character
	DEFINE, LAMBDA, COND, QUOTE,
	CAR, CDR, CONS,
	ATOM_Q, NULL_Q, EQ_Q; 
	
	static TokenType fromSpecialCharactor(char ch) {
		switch ( ch ) {
		//나머지 special character에 대해 토큰을 반환하도록 작성
			case '+': 
				return PLUS;
			case '-':
				return MINUS;
			case '*':
				return TIMES;
			case '/':
				return DIV;
			case '<':
				return LT;
			case '>':
				return GT;
			case '=':
				return EQ;
			case '\'':
				return APOSTROPHE;
			case '(':
				return L_PAREN;
			case ')':
				return R_PAREN;
			case '?':
				return QUESTION;
			default:
				throw new IllegalArgumentException("unregistered char: " + ch);
		}
	}
}
