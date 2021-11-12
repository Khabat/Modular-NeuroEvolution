//package ec_grammaticalevolution4nn;
package FitnessEvaluation.xOr;
import Individuals.Individual;
//import Util.MathEvaluator;
import FitnessEvaluation.FitnessFunction;
import java.util.Properties;
import org.nfunk.jep.JEP;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */

public class xOrFitnessEvaluation implements FitnessFunction{
    org.nfunk.jep.JEP mathEvaluator;
    int dataset[][];

    public xOrFitnessEvaluation() {
        mathEvaluator=new JEP();
        mathEvaluator.addFunction("sig", new Util.SigmoidFunction());
        dataset=new int [4][];
        for(int i=0;i<dataset.length;i++){
            dataset[i]=new int[3];
        }
        dataset[0][0]=0;
        dataset[0][1]=0;
        dataset[0][2]=0;
        dataset[1][0]=0;
        dataset[1][1]=1;
        dataset[1][2]=1;
        dataset[2][0]=1;
        dataset[2][1]=0;
        dataset[2][2]=1;
        dataset[3][0]=1;
        dataset[3][1]=1;
        dataset[3][2]=0;

    }
    protected String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }
    public void getFitness(Individual i) {
         try{
            String phenotype = i.getPhenotype().getString();
            phenotype=RemoveWhiteSpace(phenotype);
          
           // m.setExpression(phenotype);
            double fitness = 0;
            double temp = 0;
            for (int j = 0; j < dataset.length; j++) {

                mathEvaluator.addVariable("x1", dataset[j][0]);
                mathEvaluator.addVariable("x2", dataset[j][1]);
                mathEvaluator.parseExpression(phenotype);
                temp = mathEvaluator.getValue();
                temp -= dataset[j][2];
                fitness += (temp * temp) ;
            }
            i.getFitness().setDouble((fitness*fitness));
        }catch(Exception e){
            i.getFitness().setDouble(Double.MIN_VALUE);
        }
        catch(Error r){
            i.getFitness().setDouble(Double.MIN_VALUE);
        }
    }

    public boolean canCache() {
        return false;
    }

    public void setProperties(Properties p) {
       
    }

}
