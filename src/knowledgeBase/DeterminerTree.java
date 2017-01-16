package knowledgeBase;

/**
 * Created by vivek on 15-01-2017.
 * Determiner KnowledgeBase
 */
public class DeterminerTree {
    //List if Determiners
    private static String[][] array={{"THE","1"},{"A","1"},{"AN","1"}
            ,{"ANY","n"},{"ALL","n"},{"EVERY","n"}
            ,{"FEW","n"},{"SOME","n"}
            ,{"AT","m"},{"LEAST","m"},{"MOST","m"}
            ,{"ATLEAST","q-"},{"ATMOST","q+"}
            ,{"ONLY","k"},{"NONE","0"},{"OTHER","l"}
            ,{"BETWEEN","r"},{"FROM","r"}
            ,{"OR","o"},{"TO","o"}
            ,{"EACH","e"}};

    private DeterminerNode root;
    private String word;
    private String  val;

    private DeterminerTree(){
        for(String [] a:array)
            insertWord(a[0],a[1]);
    }

    public static DeterminerTree getInstance(){return new DeterminerTree();}

    private void insertWord(String word,String val){
        if((word==null)||(word.equals("")))
            return;
        this.word=word;
        this.val=val;
        root=insert(root,0);
    }

    private DeterminerNode insert(DeterminerNode temp, int i) {
        if(temp==null)
            temp = new DeterminerNode(word.charAt(i));
        if(temp.data>word.charAt(i))
            temp.left=insert(temp.left, i);
        else if(temp.data<word.charAt(i))
            temp.right=insert(temp.right, i);
        else {
            if(i!=word.length()-1)
                temp.equal=insert(temp.equal, ++i);
            else temp.val = val;
        }
        return temp;
    }

    public String search(String word) {
        DeterminerNode temp = root;
        for (int i = 0; i < word.length(); ) {
            if (temp != null) {
                if (temp.data == word.charAt(i)) {
                    if (++i == word.length())
                        break;
                    temp = temp.equal;
                } else if (temp.data < word.charAt(i))
                    temp = temp.right;
                else temp = temp.left;
            } else return "";
        }
        return temp.val;
    }
}

class DeterminerNode{
    DeterminerNode left=null,equal=null,right=null;
    String val="";
    char data;
    DeterminerNode(char data){
        this.data=data;
    }
}
