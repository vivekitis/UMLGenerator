import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by vivek on 05-01-2017.
 * Input Panel for User.
 */
class InputEditor extends JFrame{

    private int length=0;
    private SentenceArray sentences;
    private String FULLSTOP=".";
    private JTextArea textArea;
    private Container contentPane;
    private JMenuBar jMenuBar;
    private Component vstrut,hstrut;
    private Keywords keywords;
    private JPanel centre;
    private Tokenizer tokenizer;
    private JScrollPane textScroll;
    private Font font;
    private int fontSize;
    private InputEditor(){
        super("untitled");
        sentences=new SentenceArray();
        setSize(800,800);
        keywords=Keywords.getInstance();
        textArea=new JTextArea();
        textScroll=new JScrollPane(textArea);
        contentPane=getContentPane();
        textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
        textArea.setLineWrap(true);
        font=textArea.getFont();
        fontSize=font.getSize();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Document d=e.getDocument();
                //System.out.println("Len"+d.getLength());
                try {
                    length=d.getLength();
                    if(d.getText(length-1,1).equals(FULLSTOP)){
                        int start=sentences.getSentenceStart(sentences.getSentenceCount());
                        String s=d.getText(start,length-start-1);
                        sentences.add(length-1);
                        System.out.println(s);
                        tokenizer.analyzeSentence(s);
                        SwingUtilities.invokeLater(()->textArea.insert("",length));
                    }
                }
                catch (BadLocationException e1) {e1.printStackTrace();}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Document d=e.getDocument();
                length=d.getLength();
                //int offset=e.getOffset(),i=1;
                //System.out.println("Offset "+offset+" Length "+length);
                /*if(offset<length) {
                    while (offset < sentences.getSentenceStart(sentences.getSentenceCount() - i)) {
                        i++;
                        //tokenizer.removeRelation(sentences.getSentenceCount()-1);
                        //sentences.shift();
                    }
                    tokenizer.removeRelation(sentences.getSentenceCount()-i+1);
                }
                else */
                while(length<(sentences.getSentenceStart(sentences.getSentenceCount()))) {
                    tokenizer.removeLastRelation();
                    //tokenizer.analyzeSentence();
                    sentences.remove();
                }
                //System.out.println("Len"+length);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        jMenuBar=new JMenuBar();
        vstrut=Box.createVerticalStrut(10);
        hstrut=Box.createHorizontalStrut(10);
        JMenu file=new JMenu("File");
        JMenuItem jMenu=new JMenuItem("close");
        jMenu.addActionListener(e -> System.exit(0));
        jMenu.setAccelerator(KeyStroke.getKeyStroke('X', KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK));
        file.add(jMenu);
        jMenuBar.add(file);
        JMenu fontMenu=new JMenu("Font Size");
        JMenuItem sizes=new JMenuItem("+10");
        sizes.addActionListener(e -> textArea.setFont(font.deriveFont(fontSize+10)));
        fontMenu.add(sizes);
        sizes=new JMenuItem("+15");
        sizes.addActionListener(e -> textArea.setFont(font.deriveFont(fontSize+15)));
        fontMenu.add(sizes);
        sizes=new JMenuItem("+20");
        sizes.addActionListener(e -> textArea.setFont(font.deriveFont(fontSize+20)));
        fontMenu.add(sizes);
        sizes=new JMenuItem("+25");
        sizes.addActionListener(e -> textArea.setFont(font.deriveFont(fontSize+25)));
        fontMenu.add(sizes);
        jMenuBar.add(fontMenu);
        textArea.setMinimumSize(new Dimension(500,750));
        centre=new JPanel(new GridBagLayout());
    }
    static InputEditor getInstance(){return new InputEditor();}

    void showEditor(Tokenizer tokenizer) {
        this.tokenizer=tokenizer;
        contentPane.add(vstrut,BorderLayout.NORTH);
        contentPane.add(hstrut,BorderLayout.WEST);
        GridBagConstraints bagConstraints=new GridBagConstraints();
        bagConstraints.fill=GridBagConstraints.BOTH;
        bagConstraints.weightx=0.67;
        bagConstraints.weighty=0;
        centre.add(textScroll,bagConstraints);
        bagConstraints.weightx=0.33;
        bagConstraints.weighty=1;
        centre.add(keywords.getScroll(),bagConstraints);
        contentPane.add(centre,BorderLayout.CENTER);
        contentPane.add(vstrut,BorderLayout.SOUTH);
        contentPane.add(hstrut, BorderLayout.EAST);
        setJMenuBar(jMenuBar);
        setVisible(true);
    }
}

class SentenceArray{
    private ArrayList<Integer> starts;
    private int sentence_count;

    SentenceArray(){
        starts=new ArrayList<>();
        sentence_count=0;
        starts.add(0);
    }

    void add(int end){
        sentence_count++;
        starts.add(end+1);
    }

    /**
     *
     * @return sentence_count
     */
    int getSentenceCount(){return sentence_count;}

    /**
     *
     * @param n sentence number
     * @return  starts.get(n)
     */
    int getSentenceStart(int n){return starts.get(n);}

    //String getSentence(int n){return sentences.get(n);}

    void remove() {
        System.out.println("Remove");
        starts.remove(sentence_count);
        sentence_count--;
    }

    /*void remove(int n){
        System.out.println("Remove");
        starts.remove(n);
        sentence_count--;
    }*/
}