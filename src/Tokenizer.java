import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

/**
 * Created by vivek on 08-01-2017.
 */
public class Tokenizer {

    /**Return Tokenizer Instance
     *
     * @return instance of Tokenizer
     */
    static Tokenizer getInstance(){return new Tokenizer();}

    //List if Determiners
    private static String[][] array={{"THE","1"},{"A","1"},{"AN","1"}
                                    ,{"ANY","n"},{"ALL","n"},{"EVERY","n"}
                                    ,{"FEW","n"},{"SOME","n"},{"ATLEAST","k"},{"ATMOST","k"}
                                    ,{"ONLY","k"},{"NONE","0"},{"OTHER","l"}};
    //Determiner Tree
    static Tree determinerTree;

    //Array of Entities;
    private HashMap<String,Entity> entities;
    private Relation relation=new Relation();
    private String[] words;
    private Tokenizer(){
        determinerTree=new Tree();
        for(String [] a:array)
            determinerTree.insertWord(a[0],a[1]);
        entities=new HashMap<>();
    }

    /**
     * Analyzes the sentences and stores information contained in the sentence
     * First Check for Determiner-Entity Pair
     * Second Check for Relation
     * Third Check for Determiner-Entity/Attribute Pair
     * Repeat 2nd and 3rd.
     * if Order Breaks, Ambiguity Exists
     *
     * @param sentence sentence to analyze
     */
    void analyzeSentence(String sentence) {
        words = sentence.split(" ");
        boolean ambiguous = false, attached = false, conjuncion = false;
        for (int i = 0; i < words.length; i++) {

            //Store Multiplicity----------------------------------------------------------------------------------------

            int determiner = findDeterminer(words, i);
            if (determiner == -1)
                ambiguous = true;
            else if (determiner > 0) {
                //Multiplicity integer
            } else {
                //multiplicity
            }
            if (ambiguous)
                i++;

            //Store Entity----------------------------------------------------------------------------------------------
            switch (checkEntity(i)) {
                case 0:ambiguous = true;
                        break;
                case 1:relation.addEntity(storeEntity(words[i]));
                        break;
                case 2:if((relation.getType()==0)||(relation.getType()==3)) {
                        storeAttribute(words[i], relation.getEntity());
                        relation.setType(3);
                    }
                    else ambiguous=true;
                    break;
            }


            //Store Relation--------------------------------------------------------------------------------------------
            StringBuilder stringBuilder=new StringBuilder();
            while((findDeterminer(words,i)==-1)&&(checkEntity(i)==0)) {
                stringBuilder.append(words[i]).append(" ");
                i++;
            }
            String rel=stringBuilder.toString();
            if(rel.equals("and")){
                getPhrase(relation.getType(),i,ambiguous);
            }
            else if(rel.equals("are")){
                relation.setType(1);
            }
            else relation.storeRelation(stringBuilder.toString());

        }
    }

    private void getPhrase(int type, int i, boolean ambiguous) {
        int determiner = findDeterminer(words, i);
        if (determiner == -1)
            ambiguous = true;
        else if (determiner > 0) {
            //Multiplicity integer
        } else {
            //multiplicity
        }
        if (ambiguous)
            i++;

        //Store Entity----------------------------------------------------------------------------------------------
        switch (checkEntity(i)) {
            case 0:ambiguous = true;
                break;
            case 1:relation.addEntity(storeEntity(words[i]));
                break;
            case 2:if((relation.getType()==0)||(relation.getType()==3)) {
                storeAttribute(words[i], relation.getEntity());
                relation.setType(3);
            }
            else ambiguous=true;
                break;
        }


        //Store Relation--------------------------------------------------------------------------------------------
        StringBuilder stringBuilder=new StringBuilder();
        while((findDeterminer(words,i)==-1)&&(checkEntity(i)==0)) {
            stringBuilder.append(words[i]).append(" ");
            i++;
        }
    }

    private void storeAttribute(String word, Entity entity) {

    }

    private Entity storeEntity(String word) {
        return entities.computeIfAbsent(word, s -> entities.put(s,new Entity(s)));
    }

    private int checkEntity(int i) {
        //Check Determiner
        if(findDeterminer(words,i)==-1) {
            int j=0;
            while((words[i].charAt(j)< 93) && (words[i].charAt(j) > 64))
                j++;
            if(j==1)
                return 2;
            if(j==words[i].length())
                return 1;
            return 0;
        }
        return 0;
    }


    private int findDeterminer(String[] words, int i){
        int temp=-1;
        try {
            temp=Integer.parseInt(words[i]);
        }catch (NumberFormatException e){e.printStackTrace();};
        if (temp==-1) {
            String determiner = determinerTree.search(words[i]);
            switch (determiner) {
                case "1":return 1;
                case "":return -1;
                case "n":
                case "k":return -2;
                case "0":return 0;
                case "l":return -3;
            }
        }
        return temp;
    }
}

class Node{
    Node left=null,equal=null,right=null;
    String val="";
    char data;
    Node(char data){
        this.data=data;
    }
}

class Tree{
    private Node root;
    private String word;
    private String  val;
    void insertWord(String word,String val){
        if((word==null)||(word.equals("")))
            return;
        this.word=word;
        this.val=val;
        root=insert(root,0);
    }

    private Node insert(Node temp, int i) {
        if(temp==null)
            temp = new Node(word.charAt(i));
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

    String search(String word){
        Node temp=root;
        for(int i=0;i<word.length();){
            if(temp!=null) {
                if (temp.data == word.charAt(i)) {
                    if(++i==word.length())
                        break;
                    temp = temp.equal;
                }
                else if (temp.data < word.charAt(i))
                    temp = temp.right;
                else temp = temp.left;
            }
            else return "";
        }
        return temp.val;
    }

}

class Entity{
    private String name;
    private HashSet<String> attributes;
    Entity(String entity){
        name=entity;
        attributes=new HashSet<>();
    }

    boolean addAttribute(String att){
        return attributes.add(att);
    }

    int getSize(){
        return attributes.size();
    }
}

/**
 * type stores the type of relation
 * 1 : Aggregation
 * 2 : Association
 * 3 : Attribute (No Relation)
 */
class Relation{
    private int type=0;
    private ArrayList<Entity> arrayList=new ArrayList<>();
    private String relation="";
    void addEntity(Entity entity){arrayList.add(entity);}
    ArrayList<Entity> getRelation(){return arrayList;}
    void setType(int type){this.type=type;}
    Entity getEntity() {return arrayList.get(arrayList.size()-1);}
    int getType() {return type;}
    void storeRelation(String s){relation=s;}
}