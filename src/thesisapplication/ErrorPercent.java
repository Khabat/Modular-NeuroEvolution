/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;
import Util.SigmoidFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
//import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 *
 * @author Administrator
 */
public class ErrorPercent {
    protected static String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }

     public static double calculate(Dataset data, String phenotype){
        org.nfunk.jep.JEP mathEvaluator = new JEP();
        mathEvaluator.addFunction("sig", new SigmoidFunction());
        double out = 0;
        double errors = -1;
        double temp=0;
        phenotype = RemoveWhiteSpace(phenotype);
        Node n = null;
        for (Instance instance : data) {
            for (int j = 0; j < data.noAttributes(); j++) {
                //mathEvaluator.removeVariable("x" + String.valueOf(j + 1));
                mathEvaluator.addVariable("x" + String.valueOf(j + 1), instance.value(j));
            }
            try {
                if (errors == -1) {
                    n = mathEvaluator.parse(phenotype);
                    errors = 0;
                }
                out = Double.valueOf(mathEvaluator.evaluate(n).toString());
            } catch (ParseException ex) {
                Logger.getLogger(ErrorPercent.class.getName()).log(Level.SEVERE, null, ex);
            }
            temp =out- data.classIndex(instance.classValue());
            if(Math.abs(temp)<0.5)temp=0;
            else temp=1;
//            if(Math.abs(temp)<0.5 || (data.classIndex(instance.classValue())==0 && out<0)
//                    || ((data.classIndex(instance.classValue())==data.classes().size()-1)
//                            &&(out>data.classes().size()-1)) )
//                errors+=0;
//            else errors++;
            errors+=temp*temp;
           }
        return errors / data.size();
    }
     private static double binaryCovert(StringBuffer str, String strw) {
        double d=0;
        double r=0;
        double tmp=0;
        String str1="",str2="";
        try{
         str1=str.substring(0,str.indexOf("."));
         str2=str.substring(str.indexOf(".")+1,str.length());

        for(int j=0;j<str1.length();j++){
            if(str1.charAt(j)!='.'){
                if("a".equals(str1.charAt(j)+""))tmp=0;
                else tmp=1;
                d=d*2+ tmp;
            }
        }
        for(int j=0;j<str2.length();j++){
            if(str2.charAt(j)!='.'){
                if("a".equals(str2.charAt(j)+""))tmp=0;
                else tmp=1;
                r=r+ tmp*Math.pow(0.5, j+1);
            }
        }
        }catch(Exception exp){
            System.err.println(str+"\n"+strw);
        }

        return d+r;
    }
}
