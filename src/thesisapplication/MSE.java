/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import FitnessEvaluation.NeuralNetworkEvaluator.FeedForwardNeuralNetwork;
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
public class MSE {
    org.nfunk.jep.JEP mathEvaluator=null;
    public MSE(){
        mathEvaluator=new JEP();
        mathEvaluator.addFunction("sig", new SigmoidFunction());
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

    public ArrayList<Node> createNN_from_Neurons(ArrayList<String> strs,Instance ins){
        ArrayList<Node> ret=new ArrayList<Node>(strs.size());
        for(int i=0;i<ins.noAttributes();i++){
            mathEvaluator.addVariable("x"+String.valueOf(i+1),ins.value(i));
        }
        int n=1;
        for (String str : strs) {
            try {
                ret.add(mathEvaluator.parse(str));
                mathEvaluator.addVariable("n"+n++,0);
            } catch (ParseException ex) {
                Logger.getLogger(MSE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    public double getOutput (ArrayList<Node> neurons,Instance input) {
        for (int j = 0; j < input.noAttributes(); j++) {
           mathEvaluator.addVariable("x" + String.valueOf(j + 1), input.value(j));
        }
        double out[]=new double[neurons.size()];

        for(int i=0;i<neurons.size();i++){
            Node node=neurons.get(i);
            try {
                out[i] = Double.valueOf(mathEvaluator.evaluate(node).toString()).doubleValue();
            } catch (ParseException ex) {
                //Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
            }
            mathEvaluator.addVariable("n" + String.valueOf(i+ 1), out[i]);
        }

        double ret=0;
        for(int i=0;i<out.length;i++)
            ret+=out[i];
        //now we trasmit it from sigmoid function
        Stack s=new Stack();
        s.push(ret);
        try {
            new SigmoidFunction().run(s);
        } catch (ParseException ex) {
            //Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.valueOf(s.pop().toString());
    }

    public double calculate(Individual i, Dataset data, boolean regression) {
        ArrayList<String> phenotype=((NeuronsGEIndividual)i).getPhenotypeStrings();
        RemoveWhiteSpaces(phenotype);
        return get_fitness(phenotype, data,regression);
    }

    public double get_fitness(ArrayList<String> phenotype, Dataset data, boolean regression) {
        double ret = 0;
        try {
            double fitness = 0;
            double temp;
            FeedForwardNeuralNetwork ffnn=new FeedForwardNeuralNetwork(phenotype, data.get(0).noAttributes());

            for (Instance instance : data) {
                //temp = getOutput(nodes, instance);
                ArrayList<Double> values=(ArrayList<Double>) instance.values();
                temp=ffnn.feedForwad(values);
                //temp *= (data.classes().size() - 1);
                
                if (!regression) {
                    //temp -= data.classIndex(instance.classValue());
                    int class_label=data.classIndex(instance.classValue());
                    if(class_label==9)
                        class_label=0;
                    else
                        class_label=1;
                    //--
                    //temp -= data.classIndex(instance.classValue());
                    
                    temp -= class_label;
                    if (Math.abs(temp) < 0.5|| temp==0.5 || class_label==1)//FIX ME remove the last condition
                         temp=0;
                    else temp=1;

                    
//                    double margin=0.5/(data.classes().size()-1);
//                    //double margin=0.5;
//                    if (Math.abs(temp) < margin || temp==margin ) {
//                        temp = 0;
//                    } else {
//                        temp = 1;
//                    }
                } else {
                    temp -= data.classIndex(instance.classValue());
                    // temp -= Double.valueOf(String.valueOf(instance.classValue()).trim()).doubleValue()/(data.classes().size()-1);
                }
                fitness += temp * temp;
            }

            
            ret = fitness / data.size();//fix me
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
