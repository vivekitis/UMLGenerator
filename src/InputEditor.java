import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.util.ArrayList;

import static java.awt.GridBagConstraints.REMAINDER;

/**
 * Created by vivek on 05-01-2017.
 */
public class InputEditor extends JFrame{

    private int length=0;
    private SentenceArray sentences;
    private String FULLSTOP=".";
    private JTextArea textArea;
    private Container contentPane;
    private JMenuBar jMenuBar;
    private Component vstrut,hstrut;
    private Keywords keywords;
    private JPanel centre;
    private InputEditor(){
        super("untitled");
        sentences=new SentenceArray();
        setSize(800,800);
        keywords=Keywords.getInstance();
        textArea=new JTextArea();
        contentPane=getContentPane();
        textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Document d=e.getDocument();
                //System.out.println("Len"+d.getLength());
                try {
                    length=d.getLength();
                    if(d.getText(length-1,1).equals(FULLSTOP)){
                        int start=sentences.getSentenceStart(sentences.getSentenceCount());
                        sentences.add(d.getText(start,length-start-1),length-1);
                        System.out.println(sentences.getSentence(sentences.getSentenceCount()-1));
                        Runnable doHighlight = new Runnable() {
                            @Override
                            public void run() {
                                textArea.insert(" ",length);
                            }
                        };
                        SwingUtilities.invokeLater(doHighlight);
                    }
                }
                catch (BadLocationException e1) {e1.printStackTrace();}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Document d=e.getDocument();
                length=d.getLength();
                while(length<(sentences.getSentenceStart(sentences.getSentenceCount())))
                    sentences.remove();
                System.out.println("Len"+length);
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
        textArea.setMinimumSize(new Dimension(500,750));
        centre=new JPanel(new GridBagLayout());
    }
    static InputEditor getInstance(){
        return new InputEditor();
    }

    void showEditor() {
        contentPane.add(vstrut,BorderLayout.NORTH);
        contentPane.add(hstrut,BorderLayout.WEST);
        GridBagConstraints bagConstraints=new GridBagConstraints();
        bagConstraints.fill=GridBagConstraints.BOTH;
        bagConstraints.weightx=0.67;
        bagConstraints.weighty=0;
        centre.add(textArea,bagConstraints);
        bagConstraints.weightx=0.33;
        bagConstraints.weighty=1;
        centre.add(keywords,bagConstraints);
        contentPane.add(centre,BorderLayout.CENTER);
        contentPane.add(vstrut,BorderLayout.SOUTH);
        contentPane.add(hstrut, BorderLayout.EAST);
        setJMenuBar(jMenuBar);
        setVisible(true);
    }
}

class SentenceArray{
    private ArrayList<String> sentences;
    private ArrayList<Integer> starts;
    private ArrayList<Integer> ends;
    private int sentence_count;

    SentenceArray(){
        sentences=new ArrayList<>();
        starts=new ArrayList<>();
        ends=new ArrayList<>();
        sentence_count=0;
        starts.add(0);
    }

    void add(String s,int end){
        sentence_count++;
        sentences.add(s);
        starts.add(end+1);
        //System.out.println("Start "+starts.get(sentence_count));
        ends.add(end);
    }

    int getSentenceCount(){return sentence_count;}

    int getSentenceStart(int n){return starts.get(n);}

    String getSentence(int n){return sentences.get(n);}

    void remove() {
        System.out.println("Remove");
        starts.remove(sentence_count);
        sentence_count--;
        sentences.remove(sentence_count);
        ends.remove(sentence_count);
    }
}