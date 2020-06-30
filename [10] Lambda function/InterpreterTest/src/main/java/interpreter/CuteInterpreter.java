package interpreter;
import static parser.ast.ListNode.cons;
import static parser.ast.ListNode.EMPTYLIST; //편의를 위해서 import 해옴
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
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
	
	 HashMap<String, Node> map = new HashMap();
	 HashMap<String, Node> lambda_map = new HashMap();
	
	 public static void main(String[] args) {
		 CuteInterpreter interpreter = new CuteInterpreter();
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
				 //만약에 이미 선언된 것들중에 정의된 key가 존재한다면 -> val 가져오기
				 if(lookupTable(rootExpr.toString()) != null) {
					 rootExpr = lookupTable(rootExpr.toString());
					 return rootExpr;
				 } else {
					 return rootExpr;
				 }
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
	 
	 private Node runLambda(ListNode node, ListNode input) {
			Node parameter_Node = node.cdr().car();
			ListNode parameter = (ListNode) parameter_Node;
			//lambda hash map에 formeter parameter와 actual parameter를 key-value로 삽입
			lambda_map.put(parameter.car().toString(), input.car());
			Node lists = bindLamb((ListNode) node.cdr().cdr().car()); 
			return runList((ListNode) lists);
	}
	
	public ListNode bindLamb (ListNode list) {
		//( 다음에 나오는 첫번째 원소가 lambda일때, 두 번째 원소가 있을 경우, 없을 경우 판단
		Node temp = null; //operator
		if (list.cdr().car() == null) {
			//두 번째 원소가 없을 경우
			if (list.car() instanceof ListNode) {
				//다시 한번더 재귀
				temp = bindLamb((ListNode) list.car());
			} else {
				temp = list.car();
			}
			if (lambda_map.containsKey(temp.toString())) {
				//람다 테이블에 해당하는 키가 있나?
				Node getValue = lambda_map.get(temp.toString());//찾아온 값
				//함수 수행 후 바인딩한 부분을 테이블에서 제거
				lambda_map.remove(temp.toString());
				return cons(getValue, EMPTYLIST);
			} else {
				return cons(temp, EMPTYLIST);
			}
		}
		//빈 것이 아니고 ListNode일 때
		if(list.car() instanceof ListNode) {
			temp = bindLamb((ListNode) list.car());
		} else {
			temp = list.car(); //기호
		}
		ListNode matchThis = bindLamb((ListNode) list.cdr()); //넣어야 할 값
		if(lambda_map.containsKey(temp.toString())) {
			//해당 값 람다 맵에서 얻어오기
			Node getValue = lambda_map.get(temp.toString());
			//함수 수행 후 바인딩한 부분을 테이블에서 제거
			lambda_map.remove(temp.toString());
			return cons(getValue, matchThis);
		} else {
			return cons(temp, matchThis);
		}
		
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
		 if (list.car() instanceof ListNode) {
			 return runLambda((ListNode) list.car(), list.cdr());
		 }

		 return list;
	}
	
	//테이블안에 define된 변수와 value를 세팅해준다.
	private Node insertTable(String id, Node val) {
		//map을 이용하여 define된 변수 세팅하기
		map.put(id, val);
		return null;
	}
	
	//map 안에 define된 것들을 끌어와서 사용하기
	private Node lookupTable(String id) {
		if (map.containsKey(id.toString())) {
			Node val = map.get(id.toString());
			return val;
		} else {
			return null;
		}
	}
	
	 private Node runFunction(FunctionNode operator, ListNode operand) {
		 Node temp;
		 System.out.println(operand);
		 switch (operator.funcType) {
		 // CAR, CDR, CONS, NULL_Q…등 모든 function node에 대한 동작 구현
		 case LAMBDA: 
			 //lambda 구현
			 return cons(operator, operand);
		 case DEFINE:
			 //item2 구현하기 
			 Node id = operand.car(); //변수 이름
			 Node val = operand.cdr().car(); //할당할 값
			 if(val instanceof IdNode) {
				 //String이 들어왔을 때 처리(미완성)
				 val = map.get(val.toString());
			 }
			 if(val instanceof ListNode) {
				 //ListNode가 들어왔을 경우 처리
				 if (((ListNode) val).car() instanceof QuoteNode) {
					 	insertTable(id.toString(), ((ListNode) val).cdr().car());
					} else {
						temp = runExpr(val);
						insertTable(id.toString(), temp);
					}
			 } else {
					//일반적인 값이 들어왔을 때 처리
				 	insertTable(id.toString(),val);
			 }
			 System.out.println("define "  + id + " compelete!");
			
			 return null;
		 case CAR: //맨 처음 원소를 리턴
			 	if(!(operand.car() instanceof ListNode)) {
			 		if(operand.car() instanceof IdNode) {
			 			//변수일 경우 찾아오기
			 			Node results = lookupTable(operand.car().toString());
			 			
			 			if(results instanceof ListNode) {
			 				return ((ListNode) results).car();
			 			}
			 			
			 			return results;
			 			}
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
				 if(operand.car() instanceof IdNode) {
			 			//변수일 경우 찾아오기
					 	ListNode results = (ListNode)lookupTable(operand.car().toString());
					 	ListNode carval = results.cdr();
			 			QuoteNode quoteNode = new QuoteNode();
			 			ListNode li = ListNode.cons(carval, ListNode.ENDLIST);
			 			ListNode result = ListNode.cons(quoteNode, li);
			 			
			 			return result;
			 		}
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
			 ListNode tails = null; 
			 Node temps;
			 
			 Node heads = operand.car(); //head 부분
			 temps = operand.cdr().car(); //tail의 head부분
			 
			 if(temps instanceof ListNode) {
				 tails = (ListNode)temps;
				 tails = (ListNode) runList(tails.cdr());
			 }
			
			 //뒤의 부분이 IdNode일 경우
			 if(temps instanceof IdNode) {
				 ListNode take = (ListNode)lookupTable(temps.toString());
				 tails = take;
			 } 
			 
			 //앞의 부분이 IdNode일 경우
			 if(operand.car() instanceof IdNode) {
		 			//변수일 경우 찾아오기
		 			ListNode results = (ListNode)lookupTable(operand.car().toString());
		 			
		 			QuoteNode quoteNode = new QuoteNode();
		 			ListNode li = ListNode.cons((Node)results, tails);
		 			ListNode li2 = ListNode.cons(li, ListNode.ENDLIST);
		 			
		 			ListNode result = ListNode.cons(quoteNode, li2);
		 			
		 			return result;
		 	}
			 
			 if(heads instanceof ListNode) {
				 //car()가 만약에 ListNode라면 
				 ListNode headsList = (ListNode) heads; //형변환
				 if(headsList.car() instanceof QuoteNode) {
			 			Node next = runList(headsList.cdr());
			 			ListNode headsFinal = (ListNode) next;
			 		
			 			QuoteNode quoteNode = new QuoteNode();
			 			ListNode li = ListNode.cons((Node)headsFinal, tails);
			 			ListNode li2 = ListNode.cons(li, ListNode.ENDLIST);
			 			
			 			ListNode result = ListNode.cons(quoteNode, li2);
			 			
			 			return result;
			 	}
			 } else {
				 QuoteNode quoteNode = new QuoteNode();
				 ListNode li = ListNode.cons(heads, tails);
				 ListNode li2 = ListNode.cons(li, ListNode.ENDLIST);
				 ListNode result = ListNode.cons(quoteNode, li2);
		 		 return result;
			 }
			 
			 
		 case NULL_Q: //null인지 검사
			 if(operand.car() instanceof IdNode) {
		 			//변수일 경우 찾아오기
		 			Node results = lookupTable(operand.car().toString());
			 		ListNode nextList = (ListNode) results;
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
			 Node compareThis = null;
			 
			 if(operand.car() instanceof IdNode) {
				 compareThis = lookupTable(operand.car().toString());
				 
					QuoteNode quoteNode = new QuoteNode();
					ListNode li = ListNode.cons(compareThis, ListNode.ENDLIST);
					ListNode result = ListNode.cons(quoteNode, li);
						 
					compareThis = result;
					
			 } else {
				 compareThis = operand.car();
			 }
			 
			 if(!(compareThis instanceof ListNode)) {
				 if(compareThis instanceof QuoteNode) {
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
			 		return runFunction(operator, (ListNode) compareThis);
			 }
			 
		 case EQ_Q: //비교하여 같은 객체인지? 
			 	ListNode next1, next2;
			 	QuoteNode quoteNode = new QuoteNode();
			 	
			 	Node temp1 =  operand.car(); //head 부분
			 	Node temp2 =  operand.cdr().car(); //tail의 head부분
			 
			 	//변수라면, 변수가 아니면
			 	if(temp1 instanceof IdNode) {
			 		Node after = lookupTable(temp1.toString());
					ListNode result = ListNode.cons(after, ListNode.ENDLIST);
			 		next1 = result;
			 	} else {
			 		ListNode temp_1 = (ListNode)temp1;
			 		next1 = (ListNode) runList(temp_1.cdr());
			 	}
			 	
			 	if(temp2 instanceof IdNode) {
			 		Node after = lookupTable(temp2.toString());
					ListNode result = ListNode.cons(after, ListNode.ENDLIST);
			 		next2 = result;
			 	} else {
			 		ListNode temp_2 = (ListNode)temp2;
			 		next2 = (ListNode) runList(temp_2.cdr());
			 	}
			 	//각각의 비교할 객체를 추출하는 과정
			 
			 	 
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
			 Node compare = null;
			 //변수일 경우
			 if(tail instanceof IdNode) {
				 compare = lookupTable(tail.toString());
			 }
			 else {
				 compare = tail;
			 }
			 //우선 리스트로 변환 가능한지
			 if(compare instanceof ListNode) {
				 ListNode tailList = (ListNode) compare;
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
				 if(compare.toString().equals("#T")) {
					 	BooleanNode result = new BooleanNode(false);
						return result;
				 } else {
					//만약 원래 값이 false라면 -> true 반대로 출력
					 BooleanNode result = new BooleanNode(true);
					 return result;
				 }
			 }
		
		 case COND: //조건문
			 Node val1, val2; //식 판별
			 Node res1, res2; //결과 판별
			 Node cond1 = operand.car();
			 Node cond2 = operand.cdr().car();
	
			 ListNode cond1List = (ListNode) cond1;
			 ListNode cond2List = (ListNode) cond2;
			 //식 판별해야 함
			 
			 //각각의 식이 변수일 때 (조건식 처리)
			 if(cond1List.car() instanceof IdNode) {
				 val1 = lookupTable(cond1List.car().toString());
			 } else {
				 val1 = cond1List.car();
			 }
			 
			 if(cond2List.car() instanceof IdNode) {
				 val2 = lookupTable(cond2List.car().toString());
			 } else {
				 val2 = cond2List.car();
			 }
			 
			//각각의 결과 값이 변수일 때 (값 변수 처리)
			 if(cond1List.cdr().car() instanceof IdNode) {
				 res1 = lookupTable(cond1List.cdr().car().toString());
				 
			 } else {
				 res1 = cond1List.cdr().car();
			 }
			 
			 if(cond2List.cdr().car() instanceof IdNode) {
				 res2 = lookupTable(cond2List.cdr().car().toString());
			 } else {
				 res2 = cond2List.cdr().car();
			 }
			 
			 //식에 대한 처리
			 if(val1 instanceof ListNode) {
				 Node cond1Result = runBinary((ListNode)cond1List.car());
				 if(cond1Result.toString().equals("#T")) {
						 //true이면
						 return res1;
					 } 
			 } else {
				//그냥 Boolean 값일 경우
				 if(val1.toString().equals("#T")) {
					 //true이면
					 return res1;
				 }  
			 }
			 
			 if(val2 instanceof ListNode) {
				 Node cond2Result = runBinary((ListNode)cond2List.car());
				 if(cond2Result.toString().equals("#T")) {
						 //true이면
						 return res2;
					 } 
			 } else {
				//그냥 Boolean 값일 경우
				 if(val2.toString().equals("#T")) {
					 //true이면
					 return res2;
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
		 
		 Node next = runList(list.cdr());
		 ListNode nextList = (ListNode) next;
		 
		 Node num1 =  list.cdr().car();
		 Node num2 =  nextList.cdr().car();
		 
		 //만약 num1이나 num2가 idNode였다면
		 if(num1 instanceof IdNode) {
			 Node results = lookupTable(num1.toString());
			 if(results == null) {
				 System.out.println("error!");
				 return null;
			 }
			 num1 = results;
		 }

		 if(num2 instanceof IdNode) {
			 Node results = lookupTable(num2.toString());
			 if(results == null) {
				 System.out.println("error!");
				 return null;
			 }
			 num2 = results;
		 }
		 
		 switch (operator.binType) {
		 // +, -, *, = 등 모든 binary node에 대한 동작 구현
		 // +,-,/ 등에 대한 바이너리 연산 동작 구현
			 case PLUS:
				 if(num1 instanceof IntNode) {
					temp = (IntNode) num1;
					result = temp.getValue();
					
					temp = (IntNode) num2;
					result += temp.getValue();
					 
				 } else {
					 runBinary((ListNode)nextList.car());
					 
					 temp = (IntNode)num2;
					 result += temp.getValue();
				 }
				 
				 resultNode = new IntNode(String.valueOf(result));
				 return resultNode;
				
			 case MINUS:
				 if(num1 instanceof IntNode) {
						temp = (IntNode) num1;
						result = temp.getValue();
						
						temp = (IntNode) num2;
						result -= temp.getValue();
						 
					 } else {
						 runBinary((ListNode)nextList.car());
						 
						 temp = (IntNode)num2;
						 result -= temp.getValue();
					 }
					 
					 resultNode = new IntNode(String.valueOf(result));
					 return resultNode;
					 
			 case TIMES:
				 if(num1 instanceof IntNode) {
						temp = (IntNode) num1;
						result = temp.getValue();
						
						temp = (IntNode) num2;
						result *= temp.getValue();
						 
					 } else {
						 runBinary((ListNode)nextList.car());
						 
						 temp = (IntNode)num2;
						 result *= temp.getValue();
					 }
					 
					 resultNode = new IntNode(String.valueOf(result));
					 return resultNode;
					 
			 case DIV:
				 if(num1 instanceof IntNode) {
						temp = (IntNode) num1;
						result = temp.getValue();
						
						temp = (IntNode) num2;
						result /= temp.getValue();
						 
					 } else {
						 runBinary((ListNode)nextList.car());
						 
						 temp = (IntNode)num2;
						 result /= temp.getValue();
					 }
					 
					 resultNode = new IntNode(String.valueOf(result));
					 return resultNode;
					 
			 case LT: 
				 if(num1 instanceof IntNode) {
						temp = (IntNode)num1;
						int v1 = temp.getValue();
		
						temp = (IntNode)num2;
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
				 if(num1 instanceof IntNode) {
						temp = (IntNode)num1;
						int v1 = temp.getValue();
						
						temp = (IntNode)num2;
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
				 if(num1 instanceof IntNode) {
					 	temp = (IntNode)num1;
						int v1 = temp.getValue();
						
						temp = (IntNode)num2;
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
