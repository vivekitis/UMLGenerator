import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.security.Key;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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
    private static int ColorCount=12;
    private static Color[] Colors=new Color[ColorCount];
    private static boolean[] SelectedColor=new boolean[ColorCount];
    private static Random random;
    private Keywords(){
        super(new GridBagLayout());
        random=new Random(System.currentTimeMillis());
        Colors[0]=Color.orange;
        Colors[1]=Color.cyan;
        Colors[2]=Color.blue;
        Colors[3]=Color.darkGray;
        Colors[4]=Color.gray;
        Colors[5]=Color.green;
        Colors[6]=Color.lightGray;
        Colors[7]=Color.yellow;
        Colors[8]=Color.magenta;
        Colors[9]=Color.red;
        Colors[10]=Color.pink;
        Colors[11]=Color.white;
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

    static void addClass(KeywordLabel keywordLabel) {
        if (!ClassIndex.containsKey(keywordLabel.toString())) {
            int i = random.nextInt(ColorCount);
            while (SelectedColor[i])
                i=random.nextInt(ColorCount);
            SelectedColor[i] = true;
            keywordLabel.setColor(i);
            keywordLabel.setBackground(Colors[i]);
            Classes.add(keywordLabel);
            ClassIndex.put(keywordLabel.toString(), keywordLabel);
            Classes.revalidate();
        }
    }

    static void addAttribute(KeywordLabel keywordLabel, String name) {
        if (!AttributeIndex.containsKey(keywordLabel.toString()+name)) {
            keywordLabel.setBackground(Colors[ClassIndex.get(name).getColor()]);
            Attributes.add(keywordLabel);
            AttributeIndex.put(keywordLabel.toString()+name, keywordLabel);
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

class KeywordLabel extends JLabel{
    private String label;
    private int color;

    KeywordLabel(String label){
        super(label);
        setOpaque(true);
        this.label=label;
    }

    @Override
    public String toString() {
        return label;
    }

    void setColor(int color) {
        this.color = color;
    }

    int getColor() {
        return color;
    }
}