package interpreter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import parser.ast.BinaryOpNode;
import parser.ast.BooleanNode;
import parser.ast.FunctionNode;
import parser.ast.IdNode;
import parser.ast.IntNode;
import parser.ast.ListNode;
import parser.ast.Node;
import parser.ast.QuoteNode;
import parser.parse.CuteParser;
import parser.parse.NodePrinter;
import parser.parse.ParserMain;

public class CuteInterpreter {
	 public static void main(String[] args) {
		 Scanner scan = new Scanner(System.in); //입력을 받기 위해 scanner 선언
		 ClassLoader cloader = ParserMain.class.getClassLoader();
		 
		 while(true) {
			 System.out.print("> "); //콘솔에 입력 출력
			 String program = scan.nextLine(); //알맞은 식을 입력받는다.
			 
			 if(program.equals("exit")) {
				 System.out.println("Program end...");
				 break;
			 }
		
			 CuteParser cuteParser = new CuteParser(program);
			 CuteInterpreter interpreter = new CuteInterpreter();
		 
			 Node parseTree = cuteParser.parseExpr();
			 Node resultNode = interpreter.runExpr(parseTree);
			 NodePrinter nodePrinter = new NodePrinter(resultNode);
			 nodePrinter.prettyPrint();
		 }
		 
	 }

	 private void errorLog(String err) {
		 	System.out.println(err);
	 }
	 //runExpr 
	 public Node runExpr(Node rootExpr) {
		 	 if (rootExpr == null)
		 		 return null;
			 if (rootExpr instanceof IdNode) //id 노드일때
				 return rootExpr;
			 else if (rootExpr instanceof IntNode) //int 노드일때
				 return rootExpr;
			 else if (rootExpr instanceof BooleanNode) //boolean 노드일때
				 return rootExpr;
			 else if (rootExpr instanceof ListNode) //리스트일때
				 return runList((ListNode) rootExpr);
			 else
				 errorLog("run Expr error"); //에러
			 	 return null;
	 }
	 
	private Node runList(ListNode list) {
		 list = (ListNode)stripList(list);
		 //빈 리스트라면 list를 리턴하기
		 if (list.equals(ListNode.EMPTYLIST))
			 	return list;
		 //FuctionNode
		 if (list.car() instanceof FunctionNode) {
			 if (list.car() instanceof QuoteNode) //Quote 내부의 리스트의 경우 계산하지 않음
					 return list;
		 else {
			 return runFunction((FunctionNode) list.car(), list.cdr());
		 	}
		 }
		 //binary operator가 있으면 계산
		 if (list.car() instanceof BinaryOpNode) {
			 if (list.car() instanceof QuoteNode) //Quote 내부의 리스트의 경우 계산하지 않음
				 return list;
			 else
				 return runBinary(list);
		 }
		 return list;
	}

	 private Node runFunction(FunctionNode operator, ListNode operand) {
		 switch (operator.funcType) {
		 // CAR, CDR, CONS, NULL_Q…등 모든 function node에 대한 동작 구현
		 case CAR: //맨 처음 원소를 리턴
			 	if(!(operand.car() instanceof ListNode)) {
			 		if(operand.car() instanceof QuoteNode) {
			 			Node next = runList(operand.cdr());
			 			ListNode nextList = (ListNode) next;
			 			Node carVal = nextList.car();
			 			
			 			return carVal;
			 		}
			 	} else {
			 		return runFunction(operator, (ListNode) operand.car());
			 	}
			 
		 case CDR: //맨 처음 원소를 제외한 나머지 list 리턴
			 if(!(operand.car() instanceof ListNode)) {
			 		if(operand.car() instanceof QuoteNode) {
			 			Node next = runList(operand.cdr());
			 			ListNode nextList = (ListNode) next;
			 			Node carVal = nextList.cdr();
			 			
			 			QuoteNode quoteNode = new QuoteNode();
			 			ListNode li = ListNode.cons(carVal, ListNode.ENDLIST);
			 			ListNode result = ListNode.cons(quoteNode, li);
			 			
			 			return result;
			 		}
			 	} else {
			 		return runFunction(operator, (ListNode) operand.car());
			 	}
			 
		 case CONS: //한 개의 head+tail -> 리스트 만듬
			 Node heads = operand.car(); //head 부분
			 ListNode tails = (ListNode) operand.cdr().car(); //tail의 head부분
			 
			 ListNode tailsFinal = (ListNode) runList(tails.cdr());
			 
			 if(heads instanceof ListNode) {
				 //car()가 만약에 ListNode라면 
				 ListNode headsList = (ListNode) heads; //형변환
				 if(headsList.car() instanceof QuoteNode) {
			 			Node next = runList(headsList.cdr());
			 			ListNode headsFinal = (ListNode) next;
			 		
			 			QuoteNode quoteNode = new QuoteNode();
			 			ListNode li = ListNode.cons((Node)headsFinal, tailsFinal);
			 			ListNode li2 = ListNode.cons(li, ListNode.ENDLIST);
			 			
			 			ListNode result = ListNode.cons(quoteNode, li2);
			 			
			 			return result;
			 	}
			 } else {
				 QuoteNode quoteNode = new QuoteNode();
				 ListNode li = ListNode.cons(heads, tailsFinal);
				 ListNode li2 = ListNode.cons(li, ListNode.ENDLIST);
				 ListNode result = ListNode.cons(quoteNode, li2);
		 		 return result;
			 }
			 
			 
		 case NULL_Q: //null인지 검사
			 if(!(operand.car() instanceof ListNode)) {
				 if(operand.car() instanceof QuoteNode) {
					 Node next = runList(operand.cdr());
			 		 ListNode nextList = (ListNode) next;
			 		 Node isValue = nextList.car();
			 		 
			 		 if(isValue == null) {
						 //operand 리스트가 비었다면 true 리턴
						 BooleanNode result = new BooleanNode(true);
						 return result;
					 } else {
						 BooleanNode result = new BooleanNode(false);
						 return result;
					 }
				 }
			 } else {
			 		return runFunction(operator, (ListNode) operand.car());
			 }

		 case ATOM_Q: //list가 아닌 atom인 경우 -> list인 경우 false
			 if(!(operand.car() instanceof ListNode)) {
				 if(operand.car() instanceof QuoteNode) {
					 ListNode nextList = operand.cdr();
			 		 if(nextList.car()== null) {
			 			 //만약 이 노드가 NULL이라면 => atom으로 취급한다
			 			 BooleanNode result = new BooleanNode(true);
						 return result;
			 		 } else if(nextList.car() instanceof ListNode) {
			 			 //만약 이 노드가 ListNode라면 == ( 라면
			 		     Node next = runList(operand.cdr());
						 ListNode nextCheck = (ListNode) next;
						 //한번 더 리스트 안에 체크, 원소가 있다면 리스트, 없으면 null 리스트 -> true
						 if(nextCheck.car() == null) {
							 BooleanNode result = new BooleanNode(true);
							 return result;
						 } else {
							 BooleanNode result = new BooleanNode(false);
							 return result;
						 }
			
			 		 } else {
			 			BooleanNode result = new BooleanNode(true);
						 return result;
			 		 }
				 }
			 } else {
			 		return runFunction(operator, (ListNode) operand.car());
			 }
			 
		 case EQ_Q: //비교하여 같은 객체인지? 
			 	ListNode temp1 = (ListNode) operand.car(); //head 부분
			 	ListNode temp2 = (ListNode) operand.cdr().car(); //tail의 head부분
			 
			 	//각각의 비교할 객체를 추출하는 과정
			 	ListNode next1 = (ListNode) runList(temp1.cdr());
			 	ListNode next2 = (ListNode) runList(temp2.cdr());
			 	 
			 	if(next1.cdr().car() != null || next2.cdr().car() != null) {
			 		//비교 대상이 List일 경우는 false
			 		BooleanNode result = new BooleanNode(false);
					return result;
			 	}
			 	if((next1.car().toString()).equals(next2.car().toString())) {
			 		//list가 아닐 경우 두 객체를 비교한다
						BooleanNode result = new BooleanNode(true);
						return result;
				} else {
					//그 이외에 아예 다를 경우는 false 
						BooleanNode result = new BooleanNode(false);
						return result;
				}
			 	
		 case NOT:
			 Node tail = operand.car();
			
			 //우선 리스트로 변환 가능한지
			 if(tail instanceof ListNode) {
				 ListNode tailList = (ListNode) tail;
				//식이 포함되어 있을 때
				 if(tailList.car() instanceof BinaryOpNode) {
					 Node oriResult = runBinary(tailList);
					 //만약 원래 값이 true라면 -> false 반대로 출력
					 if(oriResult.toString().equals("#T")) {
						 	BooleanNode result = new BooleanNode(false);
							return result;
					 } else {
						//만약 원래 값이 false라면 -> true 반대로 출력
						 BooleanNode result = new BooleanNode(true);
						 return result;
					 }
			 }
			 
			 //단순히 Boolean 값일 경우에	 
			 } else {
				 if(tail.toString().equals("#T")) {
					 	BooleanNode result = new BooleanNode(false);
						return result;
				 } else {
					//만약 원래 값이 false라면 -> true 반대로 출력
					 BooleanNode result = new BooleanNode(true);
					 return result;
				 }
			 }
		
		 case COND: //조건문
			 Node cond1 = operand.car();
			 Node cond2 = operand.cdr().car();
	
			 ListNode cond1List = (ListNode) cond1;
			 ListNode cond2List = (ListNode) cond2;
			 //식 판별해야 함
			 if(cond1List.car() instanceof ListNode) {
				 Node cond1Result = runBinary((ListNode)cond1List.car());
				 if(cond1Result.toString().equals("#T")) {
						 //true이면
						 return ((ListNode) cond1).cdr();
					 } 
			 } else {
				//그냥 Boolean 값일 경우
				 if(cond1List.car().toString().equals("#T")) {
					 //true이면
					 return cond1List.cdr();
				 }  
			 }
			 
			 if(cond2List.car() instanceof ListNode) {
				 Node cond2Result = runBinary((ListNode)cond2List.car());
				 if(cond2Result.toString().equals("#T")) {
						 //true이면
						 return ((ListNode) cond2).cdr();
					 } 
			 } else {
				//그냥 Boolean 값일 경우
				 if(cond2List.car().toString().equals("#T")) {
					 //true이면
					 return cond2List.cdr();
				 }  
			 }
		 default:
			 break;
	 }
	 return null;
	 }
	 
	 //리스트 조각내기
	 private Node stripList(ListNode node) {
		 if (node.car() instanceof ListNode && node.cdr().car() == null) {
			 Node listNode = node.car();
			 return listNode;
		 } else {
			 return node;
		 }
	 }
	 
	 int result = 0;
	 private Node runBinary(ListNode list) {
		 BinaryOpNode operator = (BinaryOpNode) list.car();
		 IntNode resultNode; IntNode temp; result = 0;
		 // 구현과정에서 필요한 변수 및 함수 작업 가능
		 switch (operator.binType) {
		 // +, -, *, = 등 모든 binary node에 대한 동작 구현
		 // +,-,/ 등에 대한 바이너리 연산 동작 구현
			 case PLUS:
				 if(list.cdr().car() instanceof IntNode) {
					temp = (IntNode) list.cdr().car();
					result = temp.getValue();
					
					Node next = runList(list.cdr());
					ListNode nextList = (ListNode) next;
					
					temp = (IntNode) nextList.cdr().car();
					result += temp.getValue();
					 
				 } else {
					 //또 다른 괄호가 있을 때
					 Node next = runList(list.cdr());
					 ListNode nextList = (ListNode) next;
					 runBinary((ListNode)nextList.car());
					 
					 temp = (IntNode) nextList.cdr().car();
					 result += temp.getValue();
				 }
				 
				 resultNode = new IntNode(String.valueOf(result));
				 return resultNode;
				
			 case MINUS:
				 if(list.cdr().car() instanceof IntNode) {
						temp = (IntNode) list.cdr().car();
						result = temp.getValue();
						
						Node next = runList(list.cdr());
						ListNode nextList = (ListNode) next;
						
						temp = (IntNode) nextList.cdr().car();
						result -= temp.getValue();
						 
					 } else {
						 //또 다른 괄호가 있을 때
						 Node next = runList(list.cdr());
						 ListNode nextList = (ListNode) next;
						 runBinary((ListNode)nextList.car());
						 
						 temp = (IntNode) nextList.cdr().car();
						 result -= temp.getValue();
					 }
					 
					 resultNode = new IntNode(String.valueOf(result));
					 return resultNode;
					 
			 case TIMES:
				 if(list.cdr().car() instanceof IntNode) {
						temp = (IntNode) list.cdr().car();
						result = temp.getValue();
						
						Node next = runList(list.cdr());
						ListNode nextList = (ListNode) next;
						
						temp = (IntNode) nextList.cdr().car();
						result *= temp.getValue();
						 
					 } else {
						 //또 다른 괄호가 있을 때
						 Node next = runList(list.cdr());
						 ListNode nextList = (ListNode) next;
						 runBinary((ListNode)nextList.car());
						 
						 temp = (IntNode) nextList.cdr().car();
						 result *= temp.getValue();
					 }
					 
					 resultNode = new IntNode(String.valueOf(result));
					 return resultNode;
					 
			 case DIV:
				 if(list.cdr().car() instanceof IntNode) {
						temp = (IntNode) list.cdr().car();
						result = temp.getValue();
						
						Node next = runList(list.cdr());
						ListNode nextList = (ListNode) next;
						
						temp = (IntNode) nextList.cdr().car();
						result /= temp.getValue();
						 
					 } else {
						 //또 다른 괄호가 있을 때
						 Node next = runList(list.cdr());
						 ListNode nextList = (ListNode) next;
						 runBinary((ListNode)nextList.car());
						 
						 temp = (IntNode) nextList.cdr().car();
						 result /= temp.getValue();
					 }
					 
					 resultNode = new IntNode(String.valueOf(result));
					 return resultNode;
					 
			 case LT: 
				 if(list.cdr().car() instanceof IntNode) {
						temp = (IntNode) list.cdr().car();
						int v1 = temp.getValue();
						
						Node next = runList(list.cdr());
						ListNode nextList = (ListNode) next;
						
						temp = (IntNode) nextList.cdr().car();
						int v2 = temp.getValue();
						
						//오른쪽 것이 더 크면
						if(v1 < v2) {
							BooleanNode result = new BooleanNode(true);
							return result;
						} else {
							BooleanNode result = new BooleanNode(false);
							return result;
						}
						 
					 } 
					
			 case GT:
				 if(list.cdr().car() instanceof IntNode) {
						temp = (IntNode) list.cdr().car();
						int v1 = temp.getValue();
						
						Node next = runList(list.cdr());
						ListNode nextList = (ListNode) next;
						
						temp = (IntNode) nextList.cdr().car();
						int v2 = temp.getValue();
						
						//왼쪽 것이 더 크면
						if(v1 > v2) {
							BooleanNode result = new BooleanNode(true);
							return result;
						} else {
							BooleanNode result = new BooleanNode(false);
							return result;
						}
						 
					 } 
			 case EQ:
				 if(list.cdr().car() instanceof IntNode) {
						temp = (IntNode) list.cdr().car();
						int v1 = temp.getValue();
						
						Node next = runList(list.cdr());
						ListNode nextList = (ListNode) next;
						
						temp = (IntNode) nextList.cdr().car();
						int v2 = temp.getValue();
						
						//왼쪽 것이 더 크면
						if(v1 == v2) {
							BooleanNode result = new BooleanNode(true);
							return result;
						} else {
							BooleanNode result = new BooleanNode(false);
							return result;
						}
						 
					 } 
			 default:
				 break;
			 }
		 return null;
	}
}
