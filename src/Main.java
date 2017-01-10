import java.util.Scanner;

/**
 * Created by vivek on 05-01-2017.
 */
public class Main {
    public static void main(String args[]){
        Scanner scanner=new Scanner(System.in);
        String s;
        Tokenizer tokenizer=Tokenizer.getInstance();
        do{
            s=scanner.next();
            System.out.println(Tokenizer.determinerTree.search(s));
        }
        while (!s.equals("-1"));
        /*
        InputEditor inputEditor=InputEditor.getInstance();
        inputEditor.showEditor();
        Keywords keywords=Keywords.getInstance();
        */
    }
}
