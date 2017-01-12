import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by vivek on 07-01-2017.
 * Utility for the keyword panel
 */
class Keywords extends JPanel{
    private static JScrollPane jScrollPane;
    private static JPanel Ambiguous;
    private static HashMap<String,KeywordLabel> AmbiguousIndex;
    private static JPanel Classes;
    private static HashMap<String,KeywordLabel> ClassIndex;
    private static JPanel Attributes;
    private static HashMap<String,KeywordLabel> AttributeIndex;
    private static JPanel Aggregations;
    private static HashMap<String,KeywordLabel> AggregationIndex;
    private static int ColorCount=11;
    private static Color[] Colors=new Color[ColorCount];
    private static boolean[] SelectedColor=new boolean[ColorCount];
    private static Random random;
    private Keywords(){
        super(new GridBagLayout());
        random=new Random(System.currentTimeMillis());
        Colors[0]=Color.orange;
        Colors[1]=Color.cyan;
        Colors[2]=Color.blue;
        Colors[3]=Color.white;
        Colors[4]=Color.gray;
        Colors[5]=Color.green;
        Colors[6]=Color.lightGray;
        Colors[7]=Color.yellow;
        Colors[8]=Color.magenta;
        Colors[9]=Color.red;
        Colors[10]=Color.pink;
        setMinimumSize(new Dimension(250,750));
        setPreferredSize(new Dimension(250,750));
        GridBagConstraints gridBagConstraints=new GridBagConstraints();
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.weightx=0;
        gridBagConstraints.weighty=1;
        Classes=new JPanel();
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=2;
        Classes.setBorder(BorderFactory.createTitledBorder("Classes"));
        add(Classes,gridBagConstraints);
        Attributes = new JPanel();
        gridBagConstraints.gridy=1;
        Attributes.setBorder(BorderFactory.createTitledBorder("Attributes"));
        add(Attributes,gridBagConstraints);
        Ambiguous = new JPanel();
        gridBagConstraints.gridy=2;
        gridBagConstraints.weightx=0.5;
        gridBagConstraints.gridwidth=1;
        Ambiguous.setBorder(BorderFactory.createTitledBorder("Ambiguous"));
        add(Ambiguous,gridBagConstraints);
        Aggregations = new JPanel();
        gridBagConstraints.gridx=1;
        Aggregations.setBorder(BorderFactory.createTitledBorder("Aggregations"));
        add(Aggregations,gridBagConstraints);
        setVisible(true);
        AmbiguousIndex=new HashMap<>();
        AttributeIndex=new HashMap<>();
        ClassIndex=new HashMap<>();
        AggregationIndex=new HashMap<>();
        jScrollPane=new JScrollPane(this);
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
            SwingUtilities.invokeLater(()->Classes.revalidate());
        }
    }

    static void addAttribute(KeywordLabel keywordLabel, String name) {
        if (!AttributeIndex.containsKey(keywordLabel.toString()+" "+name)) {
            keywordLabel.setBackground(Colors[ClassIndex.get(name).getColor()]);
            Attributes.add(keywordLabel);
            AttributeIndex.put(keywordLabel.toString()+" "+name, keywordLabel);
            SwingUtilities.invokeLater(()->Attributes.revalidate());
        }
    }

    static Keywords getInstance(){return new Keywords();}

    static void removeClass(String s) {
        Classes.remove(ClassIndex.get(s));
        ClassIndex.remove(s);
        SwingUtilities.invokeLater(()->{Classes.revalidate();Classes.repaint();});
    }

    static void removeAttributes(String s){
        Attributes.remove(AttributeIndex.get(s));
        AttributeIndex.remove(s);
        SwingUtilities.invokeLater(() -> {Attributes.revalidate();Attributes.repaint();});
    }

    JScrollPane getScroll(){return jScrollPane;}
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