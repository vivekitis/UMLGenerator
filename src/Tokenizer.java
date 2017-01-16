import knowledgeBase.DeterminerTree;
import knowledgeBase.NumberConverter;

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


    //Determiner Tree
    private DeterminerTree determinerTree;
    private EntityTree entityTree;

    private ArrayList<Relation> relations=new ArrayList<>();
    private Relation relation;
    private String[] words;
    private boolean ambiguous;
    private int currentWord;
    private int undefineCount=0;
    private NumberConverter numberConverter;

    private Tokenizer(){
        determinerTree=DeterminerTree.getInstance();
        entityTree=new EntityTree();
        numberConverter=NumberConverter.getInstance();
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
    boolean analyzeSentence(String sentence) {
        sentence=sentence.trim();
        words=splitSentence(sentence);
        //words = sentence.split(" ");
        relation=new Relation();
        ambiguous=false;

        for (currentWord = 0; currentWord < words.length;) {
            //Store Entity----------------------------------------------------------------------------------------------
            getPhrase();
            if(ambiguous) {
                //Undo Changes
                removeRelation(relation);
                return ambiguous;
            }
            //Identity Relation-----------------------------------------------------------------------------------------
            String verb=getVerb();
            if(ambiguous) {
                //Undo Changes
                removeRelation(relation);
                return ambiguous;
            }

            //Store Relation -------------------------------------------------------------------------------------------
            saveRelation(verb);
            if(ambiguous) {
                //Undo Changes
                removeRelation(relation);
                return ambiguous;
            }

        }
        relations.add(relation);
        System.out.println(relation);
        return ambiguous;
    }

    private String[] splitSentence(String sentence) {
        ArrayList<String> temp=new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<sentence.length();i++){
            char c=sentence.charAt(i);
            if(c==' '){
                temp.add(stringBuilder.toString());
                stringBuilder.delete(0,stringBuilder.length());
                while ((i<sentence.length())&&(sentence.charAt(i)==' '))
                    i++;
                i--;
            }
            else stringBuilder.append(c);
        }
        if(stringBuilder.length()>0)
            temp.add(stringBuilder.toString());
        String[] temp2=new String[temp.size()];
        return temp.toArray(temp2);
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
                    storeAttribute(words[currentWord++], relation.getLastEntity());
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


    private void storeAttribute(String word, String entity) {
        if(ambiguous)
            return;
        if(entity==null){
            Entity entity1=new Entity("N*N"+(++undefineCount));
            undefineCount++;
            entity1.addAttribute(word);
            relation.addEntity(entity1.name);
            entityTree.insertWord("N*N",entity1);
        }
        else {
            EntityNode entityNode=entityTree.search(entity);
            entityNode.entity.addAttribute(word);
            Keywords.addAttribute(new KeywordLabel(word),entityNode.entity.name);
        }
        relation.addAttribute(word);
    }

    private String storeEntity(String word) {
        if(ambiguous)
            return null;
        EntityNode entity=entityTree.search(word);
        if(entity==null||entity.entity==null)
            entityTree.insertWord(word, new Entity(word));
        else if(entity.entity.name.startsWith("N*N")){
            undefineCount--;
            entity.entity.name=word;
            entity.count++;
            return entity.entity.name;
        }
        else entityTree.search(word).count++;
        Keywords.addClass(new KeywordLabel(word));
        return word;
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
        if(currentWord>=words.length)
            return 0;
        while((j<words[currentWord].length())&&(words[currentWord].charAt(j)< 93) && (words[currentWord].charAt(j) > 64))
            j++;
        if(j==1)
            return 2;
        if(j==words[currentWord].length())
            return 1;
        return 0;
    }


    private int findDeterminer(){
        int temp=0;
        try {temp=Integer.parseInt(words[currentWord]);}
        catch (Exception e){
            while (currentWord<words.length){
                int n=0;
                int add=numberConverter.check(words[currentWord]);
                if(add==0)
                    break;
                else if(add==1){
                    n=numberConverter.calculate(n,words[currentWord++]);
                    while ((currentWord<words.length)&&(numberConverter.check(words[currentWord])==2))
                        n=numberConverter.calculate(n,words[currentWord++]);
                    temp+=n;
                }
                else temp=numberConverter.calculate(1,words[currentWord++]);
            }
            if(temp!=0) {
                System.out.println(temp);
                currentWord--;
                return temp;
            }
            else if(currentWord<words.length){
                String determiner = determinerTree.search(words[currentWord].toUpperCase());
                switch (determiner) {
                    case "1":
                        return 1;
                    case "":
                        return -1;
                    case "n":
                        return -8;
                    case "k":
                        return -2;
                    case "0":
                        return 0;
                    case "l":
                        return -3;
                    case "m":
                        currentWord++;
                        if (currentWord < words.length) {
                            if (words[currentWord].toUpperCase().equals("MOST")) return -4;
                            else if (words[currentWord].toUpperCase().equals("LEAST")) return -5;
                            return -1;
                        }
                        return -1;
                    case "q-":
                        return -5;
                    case "q+":
                        return -4;
                    case "o":
                        return -6;
                    case "r":
                        return -7;
                    case "e":
                        return -9;
                    default:
                        return -1;
                }
            }
            else return -1;
        }
        return temp;
    }

    void removeLastRelation() {
        if(relations.size()>0)
            removeRelation(relations.get(relations.size()-1));
    }

    private void removeRelation(Relation r){
        if(r==null)
            return;
        ArrayList<String> e=r.getRelation();
        if(e.size()==1){
            EntityNode entity=entityTree.search(e.get(0));
            r.getAttributes().forEach(s -> Keywords.removeAttributes(s+" "+entity.entity.name));
            entity.entity.removeAttributes(r.getAttributes());
            if(entity.count==1){
                Keywords.removeClass(entity.entity.name);
                entityTree.remove(entity.entity.name);
            }
            else if(--entity.count==0) entity.entity=null;
        }
        else {
            for (String  entityName :e) {
                EntityNode ent=entityTree.search(entityName);
                String s = ent.entity.name;
                if (ent.count == 1) {
                    Keywords.removeClass(s);
                    ent.entity.getAttributes().forEach(s1 -> Keywords.removeAttributes(s1+" "+s));
                    entityTree.remove(s);
                }
                else if(--ent.count==0) ent.entity=null;
            }
        }
        relations.remove(r);
        ambiguous=false;
    }

    /*void removeRelation(int i) {
        Relation r=relations.get(i);
        removeRelation(r);
    }*/
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
    private ArrayList<String> arrayList=new ArrayList<>();
    private ArrayList<String> multiplicity=new ArrayList<>();
    private HashSet<String> attribute=new HashSet<>();


    private String relation="";

    void addEntity(String  entity){if(entity!=null) arrayList.add(entity);}

    void addAttribute(String att){attribute.add(att);}

    void addMultiplicity(String mul){multiplicity.add(mul);}

    String getLastEntity() {return arrayList.size()>0?arrayList.get(arrayList.size()-1):null;}

    ArrayList<String> getRelation(){return arrayList;}

    void setType(int type){this.type=type;}

    int getType() {return type;}

    HashSet<String> getAttributes(){return attribute;}

    void storeRelation(String s){relation=s;}

    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        if(type==1){
            stringBuilder.append("Aggregation between : ");
            arrayList.forEach((s)->stringBuilder.append(s).append(" "));
        }
        else if(type==2){
            stringBuilder.append("Class ").append(arrayList.get(0))
                    .append("\nRelation ").append(relation)
                    .append("\nClass ").append(arrayList.get(1));
        }
        else {
            stringBuilder.append("Class ").append(arrayList.get(0)).append("\nAttributes");
            attribute.forEach((s)->stringBuilder.append(" ").append(s));
        }
        return stringBuilder.toString();
    }
}