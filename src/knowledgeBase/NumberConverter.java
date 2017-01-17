package knowledgeBase;

import java.util.HashMap;


/**
 * Created by vivek on 15-01-2017.
 * Convert Text to Number
 */
public class NumberConverter {
    // Array of number that can be added directly to compute value

    private static String[] array = new String[]
                    {"ZERO","A","ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE","TEN"
                    ,"ELEVEN","TWELVE","THIRTEEN","FOURTEEN","FIFTEEN","SIXTEEN","SEVENTEEN","EIGHTEEN"
                    ,"NINETEEN","TWENTY","THIRTY","FORTY","FIFTY","SIXTY","SEVENTY","EIGHTY","NINETY"};
    // Integer Value of array to add

    private static int[] value=new int[]{0,1,1,2,3,4,5,6,7,8
                                        ,9,10,11,12,13,14,15
                                        ,16,17,18,19,20,30
                                        ,40,50,60,70,80,90};

    // Array of number that are used as multiplicands to compute value
    private static String[] array2=new String[]{"HUNDRED","THOUSAND","LAKH","MILLION","CRORE"};

    //// Integer  value of of multiplicands
    private static int[] value2=new int[]{100,1000,100000,1000000,10000000};

    //Hash map to of add values
    private static HashMap<String,Integer> add= new HashMap<>();

    //Hash map of multiplicands
    private static HashMap<String,Integer> mul= new HashMap<>();

    //Initialize hash maps
    private NumberConverter(){
        for(int i=0;i<array.length;i++)
            add.put(array[i],value[i]);
        for(int i=0;i<array2.length;i++)
            mul.put(array2[i],value2[i]);
    }

    /**
     * @return new instance of Number Converter
     */
    public static NumberConverter getInstance(){return new NumberConverter();}

    /**
     * used to calculate the value of some algebric operation between number and word
     * operation can be attitive or multiplicative
     * @param number temporary number to perform operation on
     * @param word key used to retrieve numerical equivalent of word.
     * @return number*multiplicand or number+multiplicand based of operator stored
     */
    public int calculate(int number, String word){
        word=word.toUpperCase();
        Integer n=add.get(word);
        if(n==null){
            n=mul.get(word);
            if(n==null)
                return -1;
            else return number*n;
        }
        return number+n;
    }

    /**
     * @param word key used to check if value in hash map
     * @return  1 if key is additive, 2 if key is multiplicand, 0 otherwise
     */
    public int check(String word) {
        word=word.toUpperCase();
        return add.get(word)!=null?1:mul.get(word)!=null?2:0;
    }
}