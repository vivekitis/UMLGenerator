import java.util.*;


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
                                    ,{"FEW","n"},{"SOME","n"}
                                    ,{"AT","m"},{"LEAST","m"},{"MOST","m"}
                                    ,{"ATLEAST","q-"},{"ATMOST","q+"}
                                    ,{"ONLY","k"},{"NONE","0"},{"OTHER","l"}
                                    ,{"BETWEEN","r"},{"FROM","r"}
                                    ,{"OR","o"},{"TO","o"}
                                    ,{"EACH","e"}};
    //Determiner Tree
    private Tree determinerTree;
    private EntityTree entityTree;
    //Array of Entities;
    private HashMap<String,Entity> entities;
    private HashMap<String,Integer> entityCount;
    private ArrayList<Relation> relations=new ArrayList<>();
    private Relation relation;
    private String[] words;
    private boolean ambiguous;
    private int currentWord;
    private int undefineCount=0;

    private Tokenizer(){
        determinerTree=new Tree();
        entityTree=new EntityTree();
        for(String [] a:array)
            determinerTree.insertWord(a[0],a[1]);
        entities=new HashMap<>();
        entityCount=new HashMap<>();
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
        sentence=sentence.trim();
        words = sentence.split(" ");
        relation=new Relation();
        ambiguous=false;

        for (currentWord = 0; currentWord < words.length;) {
            //Store Entity----------------------------------------------------------------------------------------------
            getPhrase();
            if(ambiguous) {
                //Undo Changes
                removeRelation(relation);
                return;
            }
            //Identity Relation-----------------------------------------------------------------------------------------
            String verb=getVerb();
            if(ambiguous) {
                //Undo Changes
                removeRelation(relation);
                return;
            }

            //Store Relation -------------------------------------------------------------------------------------------
            saveRelation(verb);

        }
        relations.add(relation);
        System.out.println(relation);
    }

    private void getPhrase() {
        switch (checkEntity()) {
            case 0:
                ambiguous = true;
                break;
            case 1:
                relation.addEntity(storeEntity(words[currentWord++]));
                if((relation.getRelation().size()>1)&&(relation.getType()!=3))
                    relation.setType(2);
                break;
            case 2:
                if ((relation.getType() == 0) || (relation.getType() == 3)) {
                    storeAttribute(words[currentWord++], relation.getEntity());
                    relation.setType(3);
                } else ambiguous = true;
                break;
        }
    }

    private String getVerb(){
        StringBuilder stringBuilder = new StringBuilder();
        while (!ambiguous&&(currentWord < words.length) && (checkEntity() == 0)) {
            stringBuilder.append(words[currentWord++]).append(" ");
        }
        return stringBuilder.toString();
    }

    private void saveRelation(String verb){
        switch (verb.trim()){
            case ""     :break;
            case "and"  :getPhrase();
                        if(ambiguous) {
                            removeRelation(relation);
                            return;
                        }
                        saveRelation(getVerb());
                        break;
            case "are"  :relation.setType(1);
                        break;
            default:    {
                    if((relation.getType()==0)||(relation.getType()==2)) {
                        //relation.setType(2);
                        relation.storeRelation(verb);
                    }
                    else ambiguous=true;
            }
        }
    }


    private void storeAttribute(String word, Entity entity) {
        if(ambiguous)
            return;
        if((entity==null)){
            Entity entity1=new Entity("N*N"+(++undefineCount));
            undefineCount++;
            entity1.addAttribute(word);
            relation.addEntity(entity1);
            entities.put("NAN",entity1);
        }
        else {
            entity.addAttribute(word);
            Keywords.addAttribute(new KeywordLabel(word),entity.name);
        }
        relation.addAttribute(word);
    }

    private Entity storeEntity(String word) {
        if(ambiguous)
            return null;
        Entity entity=relation.getEntity();
        if((entity!=null)&&(entity.name.startsWith("N*N"))){
            undefineCount--;
            entity.name=word;
            entityCount.replace(word,entityCount.get(word)+1);
            return entity;
        }
        if(!entities.containsKey(word)) {
            entities.put(word, new Entity(word));
            entityCount.put(word,1);
        }
        else entityCount.replace(word,entityCount.get(word)+1);
        Keywords.addClass(new KeywordLabel(word));
        return entities.get(word);
    }

    private int checkEntity() {
        //Check Determiner
        int determiner=findDeterminer();
        if(determiner!=-1) {
            currentWord++;
            if(determiner>0){
                if(currentWord<words.length+3) {
                    int _determiner = findDeterminer();
                    if(_determiner<-3){
                        currentWord++;
                        if(currentWord<words.length) {
                            int __determiner = findDeterminer();
                            if (__determiner < 0) ambiguous = true;
                            else currentWord++;
                        }
                        else ambiguous=true;
                    }
                    else if(_determiner!=-1) ambiguous=true;
                    else relation.addMultiplicity(String.valueOf(determiner));
                }
                else ambiguous=true;
            }
            else if(determiner<=0){
                if(determiner==0){
                    relation.addMultiplicity(String.valueOf(0));
                }
                else if(determiner==-2){
                    if(currentWord<words.length) {
                        int _determiner = findDeterminer();
                        if(_determiner>0) relation.addMultiplicity(String.valueOf(_determiner));
                        else ambiguous=true;
                    }
                    else ambiguous=true;
                }
                else if(determiner>-6){
                    if(currentWord<words.length) {
                        int _determiner=findDeterminer();
                        if(_determiner>0)
                            relation.addAttribute(determiner == -4 ? ("1.." + _determiner) : (_determiner + "..n"));
                        else ambiguous=true;
                    }
                    else ambiguous=true;
                 }
                else if (determiner==-7){
                    if(currentWord<words.length) {
                        int _determiner = findDeterminer();     //number
                        if(_determiner>0){
                            currentWord++;
                            if(currentWord<words.length) {
                                int __determiner = findDeterminer();    //to
                                if (__determiner != -6) ambiguous = true;
                                else {
                                    currentWord++;
                                    if(currentWord<words.length) {
                                        int ___determiner = findDeterminer();   //number
                                        relation.addMultiplicity(_determiner+".."+___determiner);
                                        currentWord++;
                                    }
                                    else ambiguous=true;
                                }
                            }
                            else ambiguous=true;
                        }
                        else if(_determiner!=-1) ambiguous=true;
                        else relation.addMultiplicity(String.valueOf(determiner));
                    }
                    else ambiguous=true;
                }
                else if(determiner==-8){
                    if(currentWord<words.length){
                        int _determiner=findDeterminer();
                        if(_determiner==-3) relation.storeRelation("r");
                        else relation.addMultiplicity(String.valueOf("1.."));
                    }
                }
            }
        }
        if(ambiguous)
            return 0;
        int j=0;
        while((j<words[currentWord].length())&&(words[currentWord].charAt(j)< 93) && (words[currentWord].charAt(j) > 64))
            j++;
        if(j==1)
            return 2;
        if(j==words[currentWord].length())
            return 1;
        return 0;
    }


    private int findDeterminer(){
        int temp;
        try {temp=Integer.parseInt(words[currentWord]);}
        catch (NumberFormatException e){
            String determiner = determinerTree.search(words[currentWord].toUpperCase());
            switch (determiner) {
                case "1":return 1;
                case "":return -1;
                case "n":return -8;
                case "k":return -2;
                case "0":return 0;
                case "l":return -3;
                case "m":currentWord++;
                        if(currentWord<words.length) {
                            if (words[currentWord].toUpperCase().equals("MOST")) return -4;
                            else if(words[currentWord].toUpperCase().equals("LEAST")) return -5;
                            return -1;
                        }
                        return -1;
                case "q-":return -5;
                case "q+":return -4;
                case "o":return -6;
                case "r":return -7;
                case "e":return -9;
                default:return -1;
            }
        }
        return temp;
    }

    void removeLastRelation() {
        try {removeRelation(relations.get(relations.size()-1));}
        catch (ArrayIndexOutOfBoundsException e){}
    }

    private void removeRelation(Relation r){
        if(r==null)
            return;
        ArrayList<Entity> e=r.getRelation();
        if(e.size()==1){
            Entity entity=e.get(0);
            r.getAttributes().forEach(s -> Keywords.removeAttributes(s+" "+entity.name));
            entity.removeAttributes(r.getAttributes());
            if(entityCount.get(entity.name)==1){
                Keywords.removeClass(entity.name);
                entityCount.remove(entity.name);
                entities.remove(entity.name);
            }
            else entityCount.replace(entity.name,entityCount.get(entity.name)-1);
        }
        else {
            for (Entity ent :e) {
                String s = ent.name;
                if (entityCount.get(s) == 1) {
                    entities.remove(s);
                    entityCount.remove(s);
                    Keywords.removeClass(s);
                    ent.getAttributes().forEach(s1 -> Keywords.removeAttributes(s1+" "+s));
                }
                else entityCount.replace(s,entityCount.get(s)-1);
            }
        }
        relations.remove(r);
        ambiguous=false;
    }

    void removeRelation(int i) {
        Relation r=relations.get(i);
        removeRelation(r);
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

class Tree{
    private DeterminerNode root;
    private String word;
    private String  val;
    void insertWord(String word,String val){
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

    String search(String word){
        DeterminerNode temp=root;
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
    private ArrayList<String> multiplicity=new ArrayList<>();
    private HashSet<String> attribute=new HashSet<>();


    private String relation="";

    void addEntity(Entity entity){if(entity!=null) arrayList.add(entity);}

    void addAttribute(String att){attribute.add(att);}

    void addMultiplicity(String mul){multiplicity.add(mul);}

    Entity getEntity() {return arrayList.size()>0?arrayList.get(arrayList.size()-1):null;}

    ArrayList<Entity> getRelation(){return arrayList;}

    void setType(int type){this.type=type;}

    int getType() {return type;}

    HashSet<String> getAttributes(){return attribute;}

    void storeRelation(String s){relation=s;}

    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        if(type==1){
            stringBuilder.append("Aggregation between : ");
            arrayList.forEach((s)->stringBuilder.append(s.name).append(" "));
        }
        else if(type==2){
            stringBuilder.append("Class ").append(arrayList.get(0).name)
                    .append("\nRelation ").append(relation)
                    .append("\nClass ").append(arrayList.get(1).name);
        }
        else {
            stringBuilder.append("Class ").append(arrayList.get(0).name).append("\nAttributes");
            attribute.forEach((s)->stringBuilder.append(" ").append(s));
        }
        return stringBuilder.toString();
    }
}