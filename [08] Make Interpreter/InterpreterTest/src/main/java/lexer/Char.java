package lexer;


class Char {
	private final char value; //문자의 값
	private final CharacterType type; //문자 타입

	enum CharacterType { //열거형 문자 타입들
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
		//문자, 숫자, 특수기호, 공백, 문장의 끝
	}
	
	static Char of(char ch) { 
		return new Char(ch, getType(ch));
	}
	
	static Char end() { //min_value? end_of_stream?
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	}
	
	private Char(char ch, CharacterType type) { //생성자
		this.value = ch;
		this.type = type;
	}
	
	char value() { //value return 
		return this.value;
	}
	
	CharacterType type() { //type return 
		return this.type;
	}
	 
	//find types
	private static CharacterType getType(char ch) {
		//letter가 되는 조건식을 알맞게 채우기
		int code = (int)ch;
		if ( code >= 'a' && code <= 'z' || code >= 'A' && code <= 'Z' || code == '?') { 
			return CharacterType.LETTER;
		}
		 
		if ( Character.isDigit(ch) ) {
			return CharacterType.DIGIT;
		}
		
		switch ( ch ) {
			case '-': case '+': case '*': case '/':
			case '(': case ')':
			case '<': case '=': case '>':
			case '#': case '\'':
				return CharacterType.SPECIAL_CHAR;
		}
		
		if ( Character.isWhitespace(ch) ) {
			return CharacterType.WS;
		}
		
		throw new IllegalArgumentException("input=" + ch);
	}
}
