package parser.ast;

//추상클래스 QutableNode에 구현할 인터페이스에 대한 사항들 구현
abstract class QuotableNodeImpl implements QuotableNode{
	//quoted의 디폴드 값은 false
	boolean quoted = false;

	//setQuoted 값을 false -> true로 바꿔준다
    @Override
    public void setQuoted() {
        this.quoted = true;
    }

    //quoted 자체를 리턴해준다(true 나 false 값을 리턴함)-> 현재 Quoted 값을 알 수 있음
    @Override
    public boolean isQuoted() {
        return quoted;
    }
}
