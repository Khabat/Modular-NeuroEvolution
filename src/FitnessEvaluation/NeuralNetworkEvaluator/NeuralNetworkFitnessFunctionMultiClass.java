/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator;

import FitnessEvaluation.FitnessFunction;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.SigmoidFunction;
//import ec_grammaticalevolution4nn.TwoChromosomeIndividual_EP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

import net.sf.javaml.tools.data.ARFFHandler;


import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import weka.core.pmml.Constant;

/**
 *
 * @author Administrator
 */
public class NeuralNetworkFitnessFunctionMultiClass implements FitnessFunction{
    Dataset dataSet_train;
    Dataset dataSet_test;
   // MathEvaluator math;
    org.nfunk.jep.JEP mathEvaluator;
    /**
     * in the case of regression true and otherwise (classification) False!!!
     * this is used for instance class value calculating
     */
    boolean regression;//
    double penalty;

    int connCount=0;
    public NeuralNetworkFitnessFunctionMultiClass(){
      //  math=new MathEvaluator();
        mathEvaluator =new org.nfunk.jep.JEP();
        mathEvaluator.addFunction("sig", new SigmoidFunction());
        dataSet_train =null;
        dataSet_test  =null;
        regression    =false;
     }
    public int getMaxNeuron(){
//       int inputs=this.dataSet_train.noAttributes();
//       int weightsOfNeuron= inputs+2;

//       return 2*(dataSet_train.size()/weightsOfNeuron+1);
       return dataSet_train.size();
   }
   public int getClassCount(){
       return dataSet_train.classes().size();
   }

    public static String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }
    public static ArrayList<String> RemoveWhiteSpaces(ArrayList<String> in){
        for (int i=0;i<in.size();i++){
            in.set(i, RemoveWhiteSpace(in.get(i)));
        }
        return in;
    }
    public void getFitness(Individual i) {
        
        ArrayList<String> phenotype=((NeuronsGEIndividual)i).getPhenotypeStrings();

        RemoveWhiteSpaces(phenotype);
        regression=true;
        penalty = 0.001;
        i.getFitness().setDouble(get_fitness((NeuronsGEIndividual)i,phenotype, dataSet_train)+penalty * connCount );
        //i.getFitness().setDouble(1);
        //i.getFitness().setDouble(get_fitness(phenotype, dataSet_train) );
        regression=false;
        ((NeuronsGEIndividual)i).validation=get_fitness((NeuronsGEIndividual)i, phenotype, dataSet_test);
        //((TwoChromosomeIndividual_EP)i).fitnessOnValidation.setDouble(get_fitness(phenotype, dataSet_test));
    }

//    public double getFit(Individual i, Dataset data){
//        ArrayList<String> phenotype=((NeuronsGEIndividual)i).getPhenotypeStrings();
//
//        RemoveWhiteSpaces(phenotype);
//        return (get_fitness(phenotype, data));
//
//    }
    public boolean canCache() {
        return false;
    }
    public void setProperties(Properties p) {
        String regressionOrNot, dataset_train_string, dataset_test_string, classIndex, penalty_coeff ;

        String key="isRegression";
        regressionOrNot=p.getProperty(key);

        key = "train_set";
        dataset_train_string = p.getProperty(key);

        key="class_index";
        classIndex=p.getProperty(key);

        key="test_set";
        dataset_test_string=p.getProperty(key);

        key=Util.Constants.PENALTY_COEFFICIENT;
        penalty_coeff=p.getProperty(key);
      
        if(regressionOrNot!=null && regressionOrNot.equals("true")){
            this.regression=true;
        }else this.regression=false;

        int classindex=0;
        if(classIndex==null || classIndex.trim().length()==0){
           classindex=2;
        }
        else{
            try{
            classindex =Integer.parseInt(classIndex);
            }catch(NumberFormatException ex){
                Logger.getLogger(NeuralNetworkFitnessFunctionMultiClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try{
        this.penalty=Double.valueOf(penalty_coeff);
        }catch(NullPointerException ex){
            //Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
            penalty=0;
        }
        try {
            dataSet_train = net.sf.javaml.tools.data.FileHandler.loadDataset(new File(dataset_train_string),classindex);
            dataSet_test = net.sf.javaml.tools.data.FileHandler.loadDataset(new File(dataset_test_string),classindex);
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkFitnessFunctionMultiClass.class.getName()).log(Level.SEVERE, null, ex);
        }
          }
    protected Dataset loadArffDataset(String path, int classIndex) throws FileNotFoundException{
        Dataset data = ARFFHandler.loadARFF(new File(path), classIndex);
        return data;
    }
    protected Dataset loadArffDataset(String path) throws FileNotFoundException{
        File f=new File(path);
        Dataset data = ARFFHandler.loadARFF(new File(path));
        return data;
    }

    public double get_fitness(NeuronsGEIndividual ind, ArrayList<String> phenotype, Dataset data) {
        if(data.size()==0)return 1;
        double ret = 0;
        try {
            double fitness = 0;
            double temp[]=null;
            Node n = null;
            //ArrayList<Node> nodes = createNN_from_Neurons(phenotype, data.get(0));
            FeedForwardNeuralNetworkMultiClass ffnn=new FeedForwardNeuralNetworkMultiClass(phenotype, data.get(0).noAttributes(),data.classes().size());
            connCount=ffnn.connectionCount();
            ind.set_Weights_Sum(ffnn.sumOfWeights());
            for (Instance instance : data) {
                //temp = getOutput(nodes, instance);
                ArrayList<Double> values=(ArrayList<Double>) instance.values();
                temp=ffnn.feedForwad(values);
                int output_class=biggestIndex(temp);
                double diff=0;
               // temp*=(data.classes().size()-1);
               // temp=0.25;
                if (!regression) {                    
                    if((output_class - data.classIndex(instance.classValue()))==0)
                        diff=0;
                    else
                        diff=1;                    
                }
                else {

                    diff = Math.log(temp[data.classIndex(instance.classValue())]);                    
                }

                fitness += diff;
                
            }
            if(!regression)
                ret = fitness / data.size();
            else
                ret= 1 - fitness/data.size();
        } catch (Exception exp) {
            //System.out.println( "getFitness Exception: "+exp.getMessage());
            Logger.getLogger(NeuralNetworkFitnessFunctionMultiClass.class.getName()).log(Level.SEVERE, null, exp);
        } catch (Error er) {
            //System.out.println("getFitness Error: "+er.p);
            er.printStackTrace();
        }
        
        return ret;
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
//                Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return ret;
//    }
//    public double getOutput(ArrayList<Node> neurons,Instance input) {
//        for (int j = 0; j < input.noAttributes(); j++) {
//           mathEvaluator.addVariable("x" + String.valueOf(j + 1), input.value(j));
//        }
//
//        double out[]=new double[neurons.size()];
//
//        for(int i=0;i<neurons.size();i++){
//            Node node=neurons.get(i);
//            try {
//                out[i] = Double.valueOf(mathEvaluator.evaluate(node).toString()).doubleValue();
//            } catch (ParseException ex) {
//                Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            mathEvaluator.addVariable("n" + String.valueOf(i+ 1), out[i]);
//        }
//
//        double ret=0;
//        for(int i=0;i<out.length;i++)
//            ret+=out[i];
//        //now we trasmit it from sigmoid function
////        Stack s=new Stack();
////        s.push(ret);
////        try {
////            new SigmoidFunction().run(s);
////        } catch (ParseException ex) {
////            Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return 1/(1+Math.pow(Math.E,-((Double) ret)));
//    }
}
