import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.security.Key;

/**
 * Created by vivek on 07-01-2017.
 */
public class Keywords extends JPanel{
    private JPanel Ambiguous;
    private JPanel Classes;
    private JPanel Attributes;
    private JPanel Aggregations;
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
    }

    static Keywords getInstance(){return new Keywords();}
}

class KeywordLabel extends JLabel{
    private String label;
    private int type;
    private int count=0;

    KeywordLabel(String label,int type){
        super(label);
        this.label=label;
        this.type=type;
        count++;
    }

    @Override
    public String toString() {
        return label;
    }
}
