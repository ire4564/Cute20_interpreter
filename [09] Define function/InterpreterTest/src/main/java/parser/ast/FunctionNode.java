package parser.ast;

import lexer.TokenType;

import java.util.HashMap;
import java.util.Map;


public class FunctionNode extends QuotableNodeImpl implements ValueNode {
    public FunctionType funcType;

    //타입을 판단해서 fType에 세팅해주는 함수
    public FunctionNode(TokenType tType) {
        FunctionType fType = FunctionType.getFunctionType(tType);
        funcType = fType;
    }

    //이 funcType의 이름을 나타내주는 toString 메소드
    @Override
    public String toString() {
        return funcType.name();
    }

    //각각의 타입을 열거하여 알맞은 타입별로 리턴을 해준다!
    public enum FunctionType {
        DEFINE {
            TokenType tokenType() {
                return TokenType.DEFINE;
            }
        },
        LAMBDA {
            TokenType tokenType() {
                return TokenType.LAMBDA;
            }
        },
        COND {
            TokenType tokenType() {
                return TokenType.COND;
            }
        },
        NOT {
            TokenType tokenType() {
                return TokenType.NOT;
            }
        },
        CDR {
            TokenType tokenType() {
                return TokenType.CDR;
            }
        },
        CAR {
            TokenType tokenType() {
                return TokenType.CAR;
            }
        },
        CONS {
            TokenType tokenType() {
                return TokenType.CONS;
            }
        },
        EQ_Q {
            TokenType tokenType() {
                return TokenType.EQ_Q;
            }
        },
        NULL_Q {
            TokenType tokenType() {
                return TokenType.NULL_Q;
            }
        },
        ATOM_Q {
            TokenType tokenType() {
                return TokenType.ATOM_Q;
            }
        };

        private static Map<TokenType, FunctionType> fromTokenType = new HashMap<TokenType, FunctionType>();

        static {
            for (FunctionType fType : FunctionType.values()) {
                fromTokenType.put(fType.tokenType(), fType);
            }
        }

        static FunctionType getFunctionType(TokenType tType) {
            return fromTokenType.get(tType);
        }

        abstract TokenType tokenType();

    }

}