package lexer;

import java.util.Optional;

class TransitionOutput {
	private final State nextState; //다음 상태
	private final Optional<Token> token; //토큰

	static TransitionOutput GOTO_START = new TransitionOutput(State.START); //시작 상태로 가라
	static TransitionOutput GOTO_ACCEPT_ID = new TransitionOutput(State.ACCEPT_ID); //id
	static TransitionOutput GOTO_ACCEPT_INT = new TransitionOutput(State.ACCEPT_INT); //integer
	static TransitionOutput GOTO_SIGN = new TransitionOutput(State.SIGN); //부호
	static TransitionOutput GOTO_SHARP = new TransitionOutput(State.SHARP); //# boolean
	static TransitionOutput GOTO_FAILED = new TransitionOutput(State.FAILED); //실패
	static TransitionOutput GOTO_EOS = new TransitionOutput(State.EOS); //종료
	
	static TransitionOutput GOTO_MATCHED(TokenType type, String lexime) {
		return new TransitionOutput(State.MATCHED, new Token(type, lexime));
	}
	static TransitionOutput GOTO_MATCHED(Token token) {
		return new TransitionOutput(State.MATCHED, token);
	}
	
	//���� ���� �ٲٱ�
	TransitionOutput(State nextState, Token token) {
		this.nextState = nextState;
		this.token = Optional.of(token);
	}
	
	TransitionOutput(State nextState) {
		this.nextState = nextState;
		this.token = Optional.empty();
	}
	
	State nextState() {
		return this.nextState;
	}
	
	Optional<Token> token() {
		return this.token;
	}
}