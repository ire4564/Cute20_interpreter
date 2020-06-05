package parser.ast;

//interface: 반드시 구현해야 할 메소드 기재
public interface QuotableNode extends Node {
    public void setQuoted(); 
    public boolean isQuoted();
}
