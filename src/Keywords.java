import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.security.Key;
import java.util.HashMap;

/**
 * Created by vivek on 07-01-2017.
 * Utility for the keyword panel
 */
class Keywords extends JPanel{
    private static JPanel Ambiguous;
    private static HashMap<String,KeywordLabel> AmbiguousIndex;
    private static JPanel Classes;
    private static HashMap<String,KeywordLabel> ClassIndex;
    private static JPanel Attributes;
    private static HashMap<String,KeywordLabel> AttributeIndex;
    private static JPanel Aggregations;
    private static HashMap<String,KeywordLabel> AggregationIndex;
    private TitledBorder title;
    private Keywords(){
        super(new GridBagLayout());
        GridBagConstraints gridBagConstraints=new GridBagConstraints();
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.weightx=0;
        gridBagConstraints.weighty=1;
        setMinimumSize(new Dimension(250,750));
        Classes=new JPanel();
        title = BorderFactory.createTitledBorder("Classes");
        Classes.setBorder(title);
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=2;
        add(Classes,gridBagConstraints);
        Attributes = new JPanel();
        title = BorderFactory.createTitledBorder("Attributes");
        Attributes.setBorder(title);
        gridBagConstraints.gridy=1;
        add(Attributes,gridBagConstraints);
        Ambiguous = new JPanel();
        title = BorderFactory.createTitledBorder("Ambiguous");
        Ambiguous.setBorder(title);
        gridBagConstraints.gridy=2;
        gridBagConstraints.weightx=0.5;
        gridBagConstraints.gridwidth=1;
        add(Ambiguous,gridBagConstraints);
        Aggregations = new JPanel();
        title=BorderFactory.createTitledBorder("Aggregations");
        Aggregations.setBorder(title);
        gridBagConstraints.gridx=1;
        add(Aggregations,gridBagConstraints);
        setVisible(true);
        AmbiguousIndex=new HashMap<>();
        AttributeIndex=new HashMap<>();
        ClassIndex=new HashMap<>();
        AggregationIndex=new HashMap<>();
    }

    static void addClass(KeywordLabel keywordLabel){
        if(!ClassIndex.containsKey(keywordLabel.toString())) {
            Classes.add(keywordLabel);
            ClassIndex.put(keywordLabel.toString(), keywordLabel);
            Classes.revalidate();
        }
    }

    static void addAttribute(KeywordLabel keywordLabel) {
        if(!AttributeIndex.containsKey(keywordLabel.toString())) {
            Attributes.add(keywordLabel);
            AttributeIndex.put(keywordLabel.toString(), keywordLabel);
            Attributes.revalidate();
        }
    }

    static Keywords getInstance(){return new Keywords();}

    static void removeClass(String s) {
        Classes.remove(ClassIndex.get(s));
        ClassIndex.remove(s);
        Classes.revalidate();
    }

    static void removeAttributes(String s){
        Attributes.remove(AttributeIndex.get(s));
        AttributeIndex.remove(s);
        Attributes.revalidate();
    }
}

class KeywordLabel extends JTextArea{
    private String label;
    private int type;

    KeywordLabel(String label,int type){
        super(label,1,label.length());
        setOpaque(false);
        setEditable(false);
        this.label=label;
        switch (type){
            case 1:Keywords.addClass(this);
                break;
            case 2:Keywords.addAttribute(this);
                break;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}