/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import FitnessEvaluation.NeuralNetworkEvaluator.FeedForwardNeuralNetwork;
import FitnessEvaluation.NeuralNetworkEvaluator.FeedForwardNeuralNetworkMultiClass;
import FitnessEvaluation.NeuralNetworkEvaluator.NeuralNetworkFitnessFunction;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.SigmoidFunction;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
/**
 *
 * @author Administrator
 */
public class MSEMultiClass {
    //org.nfunk.jep.JEP mathEvaluator=null;
    public MSEMultiClass(){
//        mathEvaluator=new JEP();
//        mathEvaluator.addFunction("sig", new SigmoidFunction());
    }
//    public static double calculate(Dataset data, String phenotype,boolean regression){
//        org.nfunk.jep.JEP mathEvaluator=new JEP();
//        mathEvaluator.addFunction("sig", new SigmoidFunction());
//        double temp=0;
//        double squareErrors=0;
//        phenotype=RemoveWhiteSpace(phenotype);
//
//        for (Instance  instance : data) {
//            for(int j=0;j<data.noAttributes();j++){
//                    mathEvaluator.removeVariable("x"+String.valueOf(j+1));
//                    mathEvaluator.addVariable("x"+String.valueOf(j+1),instance.value(j));
//                }
//                mathEvaluator.parseExpression(phenotype);
//                temp=mathEvaluator.getValue();
//                if(!regression)
//                    temp-=data.classIndex(instance.classValue());
//                else
//                    temp-=Double.valueOf(String.valueOf(instance.classValue())).doubleValue();
//                squareErrors+= (temp*temp);
//        }
//        return squareErrors/data.size();
//    }


    protected String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }
    protected void RemoveWhiteSpaces(ArrayList<String> in){
        for (int i=0;i<in.size();i++){
            in.set(i, RemoveWhiteSpace(in.get(i)));
        }
    }

//    public ArrayList<Node> createNN_from_Neurons(ArrayList<String> strs,Instance ins){
//        ArrayList<Node> ret=new ArrayList<Node>(strs.size());
//        for(int i=0;i<ins.noAttributes();i++){
//            mathEvaluator.addVariable("x"+String.valueOf(i+1),ins.value(i));
//        }
//        int n=1;
//        for (String str : strs) {
//            try {
//                ret.add(mathEvaluator.parse(str));
//                mathEvaluator.addVariable("n"+n++,0);
//            } catch (ParseException ex) {
//                Logger.getLogger(MSEMultiClass.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return ret;
//    }
//    public double getOutput (ArrayList<Node> neurons,Instance input) {
//        for (int j = 0; j < input.noAttributes(); j++) {
//           mathEvaluator.addVariable("x" + String.valueOf(j + 1), input.value(j));
//        }
//        double out[]=new double[neurons.size()];
//
//        for(int i=0;i<neurons.size();i++){
//            Node node=neurons.get(i);
//            try {
//                out[i] = Double.valueOf(mathEvaluator.evaluate(node).toString()).doubleValue();
//            } catch (ParseException ex) {
//                //Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            mathEvaluator.addVariable("n" + String.valueOf(i+ 1), out[i]);
//        }
//
//        double ret=0;
//        for(int i=0;i<out.length;i++)
//            ret+=out[i];
//        //now we trasmit it from sigmoid function
//        Stack s=new Stack();
//        s.push(ret);
//        try {
//            new SigmoidFunction().run(s);
//        } catch (ParseException ex) {
//            //Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return Double.valueOf(s.pop().toString());
//    }

    public double calculate(Individual i, Dataset data, boolean regression) {
        ArrayList<String> phenotype=((NeuronsGEIndividual)i).getPhenotypeStrings();
        RemoveWhiteSpaces(phenotype);
        return get_fitness(phenotype, data,regression);
    }

    private int biggestIndex(double []values){
        double v=values[0];
        int index=0;

        for(int i=1;i<values.length;i++){
            if(values[i]>v){
                v=values[i];
                index=i;
            }
        }
        return index;
    }
    
    public double get_fitness(ArrayList<String> phenotype, Dataset data, boolean regression) {
        double ret = 0;
        try {
           double fitness = 0;
            double temp[]=null;
            Node n = null;
            //ArrayList<Node> nodes = createNN_from_Neurons(phenotype, data.get(0));
            FeedForwardNeuralNetworkMultiClass ffnn=new FeedForwardNeuralNetworkMultiClass(phenotype, data.get(0).noAttributes(),data.classes().size());
            //ind.set_Weights_Sum(ffnn.sumOfWeights());
            for (Instance instance : data) {
                //temp = getOutput(nodes, instance);
                ArrayList<Double> values=(ArrayList<Double>) instance.values();
                temp=ffnn.feedForwad(values);
                int output_class=biggestIndex(temp);
                double diff=0;
               // temp*=(data.classes().size()-1);
               // temp=0.25;
                if (!regression) {

                    if(output_class - data.classIndex(instance.classValue())==0)
                        diff=0;
                    else
                        diff=1;

                }
                else {
                    diff = Math.log(temp[data.classIndex(instance.classValue())]);

                }
                fitness += diff;
            }
            ret = fitness / data.size();
        } catch (Exception exp) {
            System.out.println( "getFitness Exception: "+exp.getMessage());
            //Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, exp);
        } catch (Error er) {
            //System.out.println("getFitness Error: "+er.p);
            er.printStackTrace();
        }
        return ret;
    }
}
