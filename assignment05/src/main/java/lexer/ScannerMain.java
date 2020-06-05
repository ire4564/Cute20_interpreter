package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ScannerMain {
	
	static List<String>result = new LinkedList<>();
	
    public static final void main(String... args) throws Exception {
        ClassLoader cloader = ScannerMain.class.getClassLoader();
        File file = new File(cloader.getResource("lexer/as04.txt").getFile());
        testTokenStream(file);  
 
        //make file about result!
        try {
        	FileOutputStream output = new FileOutputStream("hw04.txt");
        	String str = "";
        	
        	for(int i=0; i<result.size(); i++) {
        		str += result.get(i) + "\n";
        	}
        	byte[] by = str.getBytes();
        	output.write(by);
        	output.close();
        	
        } catch(Exception e) {
        	e.getStackTrace();
        }
    }
    
    // use tokens as a Stream 
    private static void testTokenStream(File file) throws FileNotFoundException {	
        Stream<Token> tokens = Scanner.stream(file);
        tokens.map(ScannerMain::toString).forEach(System.out::println);
    }    
    
    private static String toString(Token token) {
    	String results = String.format("%-3s: %s", token.type().name(), token.lexme());
    	result.add(results);
        return results;
    }
    
}
