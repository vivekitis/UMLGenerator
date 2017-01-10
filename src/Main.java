import java.util.Scanner;

/**
 * Created by vivek on 05-01-2017.
 */
public class Main {
    public static void main(String args[]){
        Scanner scanner=new Scanner(System.in);
        String s;
        Tokenizer tokenizer=Tokenizer.getInstance();
        InputEditor inputEditor=InputEditor.getInstance();
        inputEditor.showEditor(tokenizer);
        Keywords keywords=Keywords.getInstance();
    }
}
