package parser.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import parser.ast.*;
import parser.ast.BinaryOpNode.BinType;
import parser.ast.FunctionNode.FunctionType;
import lexer.Scanner;
import lexer.ScannerMain;
import lexer.Token;
import lexer.TokenType;

public class CuteParser {
	private Iterator<Token> tokens;
	private static Node END_OF_LIST = new Node() {}; //새로 추가된 부분
	
	public CuteParser(String file) {
		try {
			tokens = Scanner.scan(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Token getNextToken() {
		if (!tokens.hasNext())
			return null;
		return tokens.next();
	}

	public Node parseExpr() {
		Token t = getNextToken();
		if (t == null) {
			System.out.println("No more token");
			return null;
		}
		TokenType tType = t.type();
		String tLexeme = t.lexme();

		switch (tType) {
		case ID:
			IdNode idNode = new IdNode(tLexeme);
			return idNode;
		case INT:
			IntNode intNode = new IntNode(tLexeme);
			if (tLexeme == null)
				System.out.println("???");
			return intNode;

		// BinaryOpNode에 대해 작성
		// +, -, /, * 가 해당
		case DIV: 
			BinaryOpNode divNode = new BinaryOpNode(tType);
			return divNode;
		case EQ:
			BinaryOpNode eqNode = new BinaryOpNode(tType);
			return eqNode;
		case MINUS:
			BinaryOpNode minNode = new BinaryOpNode(tType);
			return minNode;
		case GT:
			BinaryOpNode gtNode = new BinaryOpNode(tType);
			return gtNode;
		case PLUS:
			BinaryOpNode plusNode = new BinaryOpNode(tType);
			//plusNode.setValue(tType);
			return plusNode;
		case TIMES:
			BinaryOpNode timeNode = new BinaryOpNode(tType);
			return timeNode;
		case LT:
			BinaryOpNode ltNode = new BinaryOpNode(tType);
			return ltNode;
		
			/*
			내용 채우기
			*/

		// FunctionNode에 대하여 작성
		// 키워드가 FunctionNode에 해당
		case ATOM_Q:
			FunctionNode atNode = new FunctionNode(tType);
			return atNode;
		case CAR:
			FunctionNode carNode = new FunctionNode(tType);
			return carNode;
		case CDR:
			FunctionNode cdrNode = new FunctionNode(tType);
			return cdrNode;
		case COND:
			FunctionNode condNode = new FunctionNode(tType);
			return condNode;
		case CONS:
			FunctionNode consNode = new FunctionNode(tType);
			return consNode;
		case DEFINE:
			FunctionNode defineNode = new FunctionNode(tType);
			return defineNode;
		case EQ_Q:
			FunctionNode eqqNode = new FunctionNode(tType);
			return eqqNode;
		case LAMBDA:
			FunctionNode lambNode = new FunctionNode(tType);
			return lambNode;
		case NOT:
			FunctionNode notNode = new FunctionNode(tType);
			return notNode;
		case NULL_Q:
			FunctionNode nullNode = new FunctionNode(tType);
			return nullNode;
			

	    //parse[2] : 새로 구현된 BooleanNode Case
		// BooleanNode에 대하여 작성
		case FALSE:
			return BooleanNode.FALSE_NODE;
		case TRUE:
			return BooleanNode.TRUE_NODE;
		
		// case L_PAREN일 경우와 case R_PAREN일 경우에 대해서 작성
		// L_PAREN일 경우 parseExprList()를 호출하여 처리하기
			
		//parse[2] : 새로 구현된 L_PAREN, R_PAREN, Case
		case L_PAREN:
			//내용 채우기
			return parseExprList();

		case R_PAREN:
			return END_OF_LIST;
			
		//parser[2] : 새로 추가된 APOSTROTHE '/일때 처리
		case APOSTROPHE:
			//새로운 Quote 관련 노드를 생성한다
			QuoteNode quoteNode = new QuoteNode();
			Node QuotedNode = parseExpr(); 
			//QuotedNode가 ListNode에 대응될 경우
			if(QuotedNode instanceof ListNode) {
				//ListNode로 형변환한 다음, head와 tail 생성하여 넣어주기(초기값 null)
				((ListNode) QuotedNode).setQuotedIn();
				//listNode를 생성해서, '/ + car + cdr를 각각 head와 tail로 넣어준다
				ListNode listNode = ListNode.cons(QuotedNode, ListNode.ENDLIST);
				ListNode new_listNode = ListNode.cons(quoteNode,listNode);
				
				return new_listNode;
			}
			//QuoteNode가 QutableNode에 대응되면
			else if(QuotedNode instanceof QuotableNode) {
				//quoted 값을 true로 세팅해주고
				((QuotableNode) QuotedNode).setQuoted();
				//li를 만들어서 합치기,
				//처음에 QuotedNode 그리고 마지막에 ENDLIST(car, cdr 모두 null)를 설정해준다
				ListNode li = ListNode.cons(QuotedNode, ListNode.ENDLIST);
				ListNode listNode = ListNode.cons(quoteNode, li);
				return listNode;
			}
			
			
		default:
			System.out.println("Parsing Error!");
			return null;
		}
	}

	// List의 value를 생성하는 메소드
	private ListNode parseExprList() {
			Node head = parseExpr();
			if (head == null)
				return null;
			if (head == END_OF_LIST) // if next token is RPAREN
				return ListNode.ENDLIST; 
			
			ListNode tail = parseExprList(); 
			if (tail == null) return null;
				return ListNode.cons(head, tail);
		}
}
