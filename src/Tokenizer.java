import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by vivek on 08-01-2017.
 * Used to break sentences to Tokens.
 */
class Tokenizer {

    /**Return Tokenizer Instance
     * @return instance of Tokenizer
     */
    static Tokenizer getInstance(){return new Tokenizer();}

    //List if Determiners
    private static String[][] array={{"THE","1"},{"A","1"},{"AN","1"}
                                    ,{"ANY","n"},{"ALL","n"},{"EVERY","n"}
                                    ,{"FEW","n"},{"SOME","n"},{"ATLEAST","k"},{"ATMOST","k"}
                                    ,{"ONLY","k"},{"NONE","0"},{"OTHER","l"}};
    //Determiner Tree
    private Tree determinerTree;

    //Array of Entities;
    private HashMap<String,Entity> entities;
    private HashMap<String,Integer> entityCount;
    private ArrayList<Relation> relations=new ArrayList<>();
    private Relation relation;
    private String[] words;
    private boolean ambiguous;
    private boolean attached;
    private boolean conjuncion;

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
        relation=new Relation();
        ambiguous=false;
        conjuncion=false;
        attached=false;
        for (int i = 0; i < words.length;) {
            //Store Entity----------------------------------------------------------------------------------------------
            switch (checkEntity(i)) {
                case 0:
                    ambiguous = true;
                    break;
                case 1:
                    relation.addEntity(storeEntity(words[i++]));
                    break;
                case 2:
                    if ((relation.getType() == 0) || (relation.getType() == 3)) {
                        storeAttribute(words[i++], relation.getEntity());
                        relation.setType(3);
                    } else ambiguous = true;
                    break;
            }


            //Identity Relation-----------------------------------------------------------------------------------------
            StringBuilder stringBuilder = new StringBuilder();
            while ((i < words.length) && (checkEntity(i) == 0)) {
                stringBuilder.append(words[i]).append(" ");
                i++;
            }
            String verb = stringBuilder.toString();

            //Store Relation -------------------------------------------------------------------------------------------

            switch (verb){
                case ""     :break;
                case "and"  :if (conjuncion) getPhrase(relation.getType(), i);
                            else {
                                conjuncion = true;
                                getPhrase(relation.getType(), i);
                               }
                            break;
                case "are"  :if (attached) {

                            } else {
                                attached = true;
                                relation.setType(1);
                            }
                            break;
                default:    relation.storeRelation(verb);
            }
        }
        relations.add(relation);
    }

    private void getPhrase(int type, int i) {

    }

    private void storeAttribute(String word, Entity entity) {
        if((entity==null)){
            Entity entity1=new Entity("NAN");
            entity1.addAttribute(word);
            relation.addEntity(entity1);
            entities.put("NAN",entity1);
        }
        else
            entity.addAttribute(word);
        relation.addAttribute(word);
    }

    private Entity storeEntity(String word) {
        Entity entity=relation.getEntity();
        if((entity!=null)&&(entity.name.equals("NAN"))){
            entity.name=word;
            entityCount.replace(word,entityCount.get(word)+1);
            return entity;
        }
        if(!entities.containsKey(word)) {
            entities.put(word, new Entity(word));
            entityCount.put(word,1);
        }
        else entityCount.replace(word,entityCount.get(word)+1);
        return entities.get(word);
    }

    private int checkEntity(int i) {
        //Check Determiner
        if(findDeterminer(words,i)==-1) {
            int j=0;
            while((j<words[i].length())&&(words[i].charAt(j)< 93) && (words[i].charAt(j) > 64))
                j++;
            if(j==1)
                return 2;
            if(j==words[i].length())
                return 1;
            return 0;
        }
        else {
            //Store Multiplicity
        }
        return 0;
    }


    private int findDeterminer(String[] words, int i){
        int temp=-1;
        try {
            temp=Integer.parseInt(words[i]);
        }catch (NumberFormatException e){System.out.println(words[i]);};
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

    void removeRelation() {
        Relation r=relations.get(relations.size()-1);
        ArrayList<Entity> e=r.getRelation();
        if(e.size()==1){
            Entity entity=e.get(0);
            entity.removeAttributes(r.getAttributes());
        }
        else {
            for (Entity ent :e) {
                String s = ent.name;
                if (entityCount.get(s) == 1) {
                    entities.remove(s);
                    entityCount.remove(s);
                }
                entityCount.replace(s,entityCount.get(s)-1);
            }
        }
        relations.remove(r);
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
    String name;
    private HashSet<String> attributes;
    Entity(String entity){
        name=entity;
        attributes=new HashSet<>();
    }

    boolean addAttribute(String att){return attributes.add(att);}

    void removeAttributes(HashSet<String> attributes) {this.attributes.removeAll(attributes);}

    int getSize(){return attributes.size();}
}


class Relation{
    /**
     * type stores the type of relation
     * 1 : Aggregation
     * 2 : Association
     * 3 : Attribute (No Relation).
     */
    private int type=0;

    /**
     * Stores The Entities in a relation
     */
    private ArrayList<Entity> arrayList=new ArrayList<>();
    private HashSet<String> attribute=new HashSet<>();

    private String relation="";

    void addEntity(Entity entity){arrayList.add(entity);}
    void addAttribute(String att){attribute.add(att);}

    Entity getEntity() {return arrayList.size()>0?arrayList.get(arrayList.size()-1):null;}

    ArrayList<Entity> getRelation(){return arrayList;}

    void setType(int type){this.type=type;}

    int getType() {return type;}

    HashSet<String> getAttributes(){return attribute;}
    void storeRelation(String s){relation=s;}
}