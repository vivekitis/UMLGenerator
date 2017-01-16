package knowledgeBase;

import java.util.HashMap;


/**
 * Created by vivek on 15-01-2017.
 * Convert Text to Number
 */
public class NumberConverter {
    private static String[] array = new String[]
                    {"ZERO","A","ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE","TEN"
                    ,"ELEVEN","TWELVE","THIRTEEN","FOURTEEN","FIFTEEN","SIXTEEN","SEVENTEEN","EIGHTEEN"
                    ,"NINETEEN","TWENTY","THIRTY","FORTY","FIFTY","SIXTY","SEVENTY","EIGHTY","NINETY"};
    private static int[] value=new int[]{0,1,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,30,40,50,60,70,80,90};
    private static String[] array2=new String[]{"HUNDRED","THOUSAND","LAKH","MILLION","CRORE"};
    private static int[] value2=new int[]{100,1000,100000,1000000,10000000};
    private static HashMap<String,Integer> add= new HashMap<>();
    private static HashMap<String,Integer> mul= new HashMap<>();
    private NumberConverter(){
        for(int i=0;i<array.length;i++)
            add.put(array[i],value[i]);
        for(int i=0;i<array2.length;i++)
            mul.put(array2[i],value2[i]);
    }
    public static NumberConverter getInstance(){return new NumberConverter();}

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

    public int check(String word) {
        word=word.toUpperCase();
        return add.get(word)!=null?1:mul.get(word)!=null?2:0;
    }
}