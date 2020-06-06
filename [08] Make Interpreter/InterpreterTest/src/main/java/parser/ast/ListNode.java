package parser.ast;


public interface ListNode extends QuotableNode {

	//빈 ListNode를 생성한다
    static ListNode EMPTYLIST = new ListNode() {
        
    	//생성시 car와 cdr은 null로 설정해준다
    	@Override
        public Node car() {
            return null;
        }

        @Override
        public ListNode cdr() {
            return null;
        }

        //ListNode 생성시 setQuotedIn은 head는 car로(Node), tail은 cdr로(ListNode로 형변환해서) 세팅
        @Override
        public ListNode setQuotedIn(){
            return ListNode.cons(setQuotedInside(this.car()), (ListNode)setQuotedInside(this.cdr()));
        }

        //위에서 쓰인 setQuotedInside() 함수에 대한 것
        @Override
        public Node setQuotedInside(Node node){
            if (node instanceof QuotableNode) { // case QuatableNode
            	//조건이 true라면 node를 QuotableNode로 형변환 해주고 Quoted = true 세팅해주기
            	((QuotableNode) node).setQuoted();
                return node;
            }
            else if(node instanceof ListNode) { // case ListNode
            	//Node가 ListNode일 경우 car, cdr로 나눠주기
                setQuotedInside(((ListNode) node).car());
                setQuotedInside(((ListNode) node).cdr());
            }
            return node;
            //마지막에 노드 리턴해주기
        }
        @Override
        public void setQuoted(){ }
        @Override
        public boolean isQuoted(){
            return false;
        }
    };
    
    //end listNode 생성
    static ListNode ENDLIST = new ListNode() {

    	//car과 cdr은 null로 설정
        @Override
        public Node car() {
            return null;
        }

        @Override
        public ListNode cdr() {
            return null;
        }

        @Override
        public ListNode setQuotedIn(){
            return ListNode.cons(setQuotedInside(this.car()), (ListNode)setQuotedInside(this.cdr()));
        }

        @Override
        public Node setQuotedInside(Node node){
        	//instanceof 연산자 : 참조 변수가 참고하고 있는 인스턴스의 실제 타입을 알아보기 위해 사용
        	//연산의 결과로 boolean값 ture, false 둘중 하나가 반환된다.
            if (node instanceof QuotableNode) { // case QuatableNode
                ((QuotableNode) node).setQuoted();
                return node;
            }
            else if(node instanceof ListNode) { // case ListNode
                setQuotedInside(((ListNode) node).car());
                setQuotedInside(((ListNode) node).cdr());
            }
            return node;
        }

        @Override
        public void setQuoted(){ }
        @Override
        public boolean isQuoted(){
            return false;
        }
    };

    //cons에 대한 것, head와 tail 세팅
    //head와 tail을 합치도록 하는 메소드
    static ListNode cons(Node head, ListNode tail) {
        return new ListNode() {
        	
        	//car는 head이고, cdr은 tail이다, 인자 세팅해주기
            @Override
            public Node car() {
                return head;
            }

            @Override
            public ListNode cdr() {
                return tail;
            }

            @Override
            public ListNode setQuotedIn(){
                return ListNode.cons(setQuotedInside(this.car()), (ListNode)setQuotedInside(this.cdr()));
            }

            //다른 부분과 동일하게 진행
            @Override
            public Node setQuotedInside(Node node){
                if (node instanceof QuotableNode) { // case QuatableNode
                    ((QuotableNode) node).setQuoted();
                    return node;
                }
                else if(node instanceof ListNode) { // case ListNode
                	setQuotedInside(((ListNode) node).car());
                    setQuotedInside(((ListNode) node).cdr());
                }
                return node;
            }

            public void setQuoted(){}
            //다른 부분!!!
            @Override
            public boolean isQuoted(){
            	//head를 car로 설정하고
                Node head = this.car();
                //head가 ListNode로 형변환 가능하다면
                while(head instanceof ListNode){
                    head = ((ListNode) head).car();
                    //head는 head의 car(head)로 설정
                }
                //true나 false를 반환한다
                return ((QuotableNodeImpl)head).isQuoted();
            }
        };
    }

    Node car();
    ListNode cdr();
    ListNode setQuotedIn();
    Node setQuotedInside(Node node);
}