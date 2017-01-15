import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by vivek on 13-01-2017.
 * Entity
 */

class Entity {
    String name;
    private HashSet<String> attributes;
    Entity(String entity){
        name=entity;
        attributes=new HashSet<>();
    }

    boolean addAttribute(String att){return attributes.add(att);}

    void removeAttributes(HashSet<String> attributes) {this.attributes.removeAll(attributes);}

    HashSet<String> getAttributes(){return attributes;}

    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("Class ").append(name).append("\nAttribute");
        for(String att:attributes)
            stringBuilder.append(" ").append(att);
        return stringBuilder.toString();
    }

}
class EntityNode{
    EntityNode left=null,equal=null,right=null;
    char data;
    int count=0;
    Entity entity;

    EntityNode(char data){this.data = data;}

    Entity createEntity(String name){
        count=1;
        entity=new Entity(name);
        return entity;
    }
}

class EntityTree{
    EntityNode root;
    private String word;
    private String  val;

    void insertWord(String word,String val){
        if((word==null)||(word.equals("")))
            return;
        this.word=word;
        this.val=val;
        root=insert(root,0);
    }

    private EntityNode insert(EntityNode temp, int i) {
        if(temp==null)
            temp = new EntityNode(word.charAt(i));
        if(temp.data>word.charAt(i))
            temp.left=insert(temp.left, i);
        else if(temp.data<word.charAt(i))
            temp.right=insert(temp.right, i);
        else {
            if(i!=word.length()-1)
                temp.equal=insert(temp.equal, ++i);
            else if(temp.entity!=null)
                temp.count++;
            else temp.createEntity(val);
        }
        return temp;
    }

    Entity search(String word){
        EntityNode temp=root;
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
            else return null;
        }
        return temp.entity;
    }

    void remove(String word){
        EntityNode temp=root;
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
            else return;
        }
        if (temp.count<2) {
            temp.entity = null;
            temp.count = 0;
        }
        else temp.count--;
    }
}