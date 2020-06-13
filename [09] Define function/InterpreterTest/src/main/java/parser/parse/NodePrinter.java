package parser.parse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import parser.ast.*;

public class NodePrinter {
    private final String OUTPUT_FILENAME = "output06.txt";
    private StringBuffer sb = new StringBuffer();
    private Node root;

    //ListNode, ' Node, Node에 대한 printNode 함수를 각각 overload 형식으로 작성하기!
    public NodePrinter(Node root) {
        this.root = root;
    }

    private void printList(ListNode listNode) {
    
    	if (listNode == ListNode.EMPTYLIST) {
		    	sb.append("( )");
		    	return;
		}
    	//오른쪽 괄호일 때 처리
		if (listNode == ListNode.ENDLIST) {
			   	return;
		}
		
		//	다음 노드로 넘어가기
		printNode(listNode.car());
		printList(listNode.cdr());
		
	}
    
  //car은 Node cdr은 list
    private void printNode(Node node) {
		if (node == null)
			   return;
		// 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오.
		if(node instanceof ListNode) {
			ListNode ln = (ListNode) node;
			
			if(ln.car() instanceof QuoteNode) {
				printNode(ln.car());
				printList(ln.cdr());
			}
		
			else if(ln.car() instanceof ListNode) {
				sb.append("( ");
				printList(ln);
				sb.append(" )");
				
			} else {
				sb.append("( ");
				printList(ln);
				sb.append(" )");
			}
			
		} else if(node instanceof QuoteNode) {
			sb.append(node);
				
    	} else {
			sb.append(" [" + node + "] ");
		}
		
	} 
		    	
		    	
    public void prettyPrint() {
		printNode(root);
		try (FileWriter fw = new FileWriter(OUTPUT_FILENAME);
			    			
	    PrintWriter pw = new PrintWriter(fw)) {
			pw.write(sb.toString());
			System.out.println("... " + sb.toString()); //쓰기 전 확인을 위해 추가함.
				    	
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
   