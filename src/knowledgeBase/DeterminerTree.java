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

    //root of determiner tree
    private DeterminerNode root;

    private String word;
    private String  val;

    private DeterminerTree(){
        for(String [] a:array)
            insertWord(a[0],a[1]);
    }

    /**
     * Utility function to get instance of DeterminerTree
     * @return new Instance of DeterminerTree
     */
    public static DeterminerTree getInstance(){return new DeterminerTree();}

    /**
     * Driver function that check parameters before calling insert function.
     * does not execute insert if word is either null or zero.
     * @param word determiner to store
     * @param val value of determiner
     */
    private void insertWord(String word,String val){
        if((word==null)||(word.equals("")))
            return;
        this.word=word;
        this.val=val;
        root=insert(root,0);
    }

    /**
     * Recursive function to insert a new Determiner into tree
     * @param temp parent node
     * @param i i<sup>th</sup> position of current determiner string
     * @return Node inserted containing the determiner
     */
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

    /**
     * Searche if a given word is a determiner or not
     * @param word string to search
     * @return value of determiner matching word or 0 if word is not found in tree
     */
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

/**
 * Stores Node information for Determiner
 */
class DeterminerNode{
    //pointers to subsequent nodes
    DeterminerNode left=null,equal=null,right=null;

    //value of determiner if present
    String val="";

    //data of node, i.e. value of character at position i in string s
    char data;

    /**
     *
     * @param data character value at position i of string s
     */
    DeterminerNode(char data){
        this.data=data;
    }
}
