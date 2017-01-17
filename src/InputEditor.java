import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
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
    private JTextPane textPane;
    private Container contentPane;
    private JMenuBar menuBar;
    private Component vstrut,hstrut;
    private Keywords keywords;
    private Tokenizer tokenizer;
    private JScrollPane textScroll;
    private JSplitPane splitPane;
    private AbstractDocument document;
    private Font font;
    private int fontSize;
    private boolean update=false;
    private InputEditor(){
        super("untitled");
        sentences=new SentenceArray();
        setSize(800,800);
        keywords=Keywords.getInstance();
        textPane=new JTextPane();
        textScroll=new JScrollPane(textPane);
        contentPane=getContentPane();
        textPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        font=textPane.getFont();
        fontSize=font.getSize();
        document=(AbstractDocument)textPane.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(update)
                    return;
                //System.out.println("Len"+d.getLength());
                try {
                    length=document.getLength();
                    //System.out.println(length);
                    if(document.getText(length-1,1).equals(FULLSTOP)){
                        int start=sentences.getSentenceStart(sentences.getSentenceCount());
                        String s=document.getText(start,length-start-1);
                        //sentences.add(length);
                        tokenizer.analyzeSentence(s);
                        update=true;
                        SwingUtilities.invokeLater(() -> {
                            try {
                                document.remove(start, length - start);
                                int temp=insertString(tokenizer.getCurrentWord(),start,tokenizer.getWords());
                                sentences.add(temp);
                            }
                            catch (BadLocationException e1) {e1.printStackTrace();}
                        });
                        //System.out.println(start+" "+(length-start-1));

                    }
                }
                catch (BadLocationException e1) {e1.printStackTrace();}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (update)
                    return;
                //Document d=e.getDocument();
                length=document.getLength();
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
        menuBar=new JMenuBar();
        vstrut=Box.createVerticalStrut(10);
        hstrut=Box.createHorizontalStrut(10);
        JMenu file=new JMenu("File");
        JMenuItem jMenu=new JMenuItem("close");
        jMenu.addActionListener(e -> System.exit(0));
        jMenu.setAccelerator(KeyStroke.getKeyStroke('X', KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK));
        file.add(jMenu);
        menuBar.add(file);
        JMenu fontMenu=new JMenu("Font Size");
        JMenuItem sizes=new JMenuItem("+10");
        sizes.addActionListener(e -> textPane.setFont(font.deriveFont(fontSize+10)));
        fontMenu.add(sizes);
        sizes=new JMenuItem("+15");
        sizes.addActionListener(e -> textPane.setFont(font.deriveFont(fontSize+15)));
        fontMenu.add(sizes);
        sizes=new JMenuItem("+20");
        sizes.addActionListener(e -> textPane.setFont(font.deriveFont(fontSize+20)));
        fontMenu.add(sizes);
        sizes=new JMenuItem("+25");
        sizes.addActionListener(e -> textPane.setFont(font.deriveFont(fontSize+25)));
        fontMenu.add(sizes);
        menuBar.add(fontMenu);
        textPane.setMinimumSize(new Dimension(500,750));
        splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    }

    private int insertString(int currentWord, int start, String[] words) throws BadLocationException {
        StringBuilder stringBuilder=new StringBuilder();
        int offset=0,offset2=0;
        SimpleAttributeSet attributeSet=new SimpleAttributeSet();
        for(int i=0;i<currentWord;i++) {
            stringBuilder.append(" ").append(words[i]);
            offset+=(words[i].length()+1);
        }
        document.insertString(start,stringBuilder.toString(),attributeSet);
        stringBuilder.delete(0,offset);
        for (int i=currentWord;i<words.length;i++) {
            stringBuilder.append(" ").append(words[i]);
            offset2+=(words[i].length()+1);
        }

        StyleConstants.setUnderline(attributeSet,true);
        document.insertString(start+offset,stringBuilder.toString(),attributeSet);
        StyleConstants.setUnderline(attributeSet,false);
        document.insertString(start+offset+offset2,".",attributeSet);
        update=false;
        return start+offset+offset2;
    }

    static InputEditor getInstance(){return new InputEditor();}

    void showEditor(Tokenizer tokenizer) {
        this.tokenizer=tokenizer;
        contentPane.add(vstrut,BorderLayout.NORTH);
        contentPane.add(hstrut,BorderLayout.WEST);
        splitPane.setLeftComponent(textScroll);
        splitPane.setRightComponent(keywords.getScroll());
        contentPane.add(splitPane,BorderLayout.CENTER);
        contentPane.add(vstrut,BorderLayout.SOUTH);
        contentPane.add(hstrut, BorderLayout.EAST);
        setJMenuBar(menuBar);
        setVisible(true);
        splitPane.setDividerLocation(.66);
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