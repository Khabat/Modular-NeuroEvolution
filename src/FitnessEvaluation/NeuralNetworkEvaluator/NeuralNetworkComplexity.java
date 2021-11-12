/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator;

import FitnessEvaluation.FitnessFunction;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.SigmoidFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.ARFFHandler;

/**
 *
 * @author KHS
 */
public class NeuralNetworkComplexity implements FitnessFunction{
    Dataset dataSet_train;
    Dataset dataSet_test;
   // MathEvaluator math;
    org.nfunk.jep.JEP mathEvaluator;
    double penalty = 0;
    /**
     * in the case of regression true and otherwise (classification) False!!!
     * this is used for instance class value calculating
     */
    boolean regression;//
    public NeuralNetworkComplexity(){
      //  math=new MathEvaluator();
        mathEvaluator=new org.nfunk.jep.JEP();
        mathEvaluator.addFunction("sig", new SigmoidFunction());
        dataSet_train=null;
        regression=false;
     }
    
    public ArrayList<String> tokenize(String str){
        ArrayList<String> ret=new ArrayList<String>();

        int start=0;
        do{
            ret.add(str.substring(start, str.indexOf(")",start)+1));
            start=str.indexOf(")",start);
            start+=2;
        }while(start<str.length());
        return ret;
    }
    public void getFitness(Individual i) {

            ArrayList<String> phenotype=((NeuronsGEIndividual)i).getPhenotypeStrings();

            RemoveWhiteSpaces(phenotype);
            //ArrayList<String> phenotype2=tokenize(phenotype); //for conn. count
            //i.getFitness().setDouble(-phenotype2.size()); // returns the number of connections
            i.getFitness().setDouble(-connectionCount(phenotype, dataSet_test));

    }

     public static ArrayList<String> RemoveWhiteSpaces(ArrayList<String> in){
        for (int i=0;i<in.size();i++){
            in.set(i, RemoveWhiteSpace(in.get(i)));
        }
        return in;
    }

     public static String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }
    private int neuronCount(String phenotype){
        int n=0;
        for(int i=0;i<phenotype.length();i++){

            int ind=phenotype.indexOf("sig", i);
            if(ind==-1) break;
            n++;
            i=ind+3;
        }
        return n;
    }

    private int connectionCount(ArrayList<String> phenotype, Dataset data){
        
        FeedForwardNeuralNetwork ffnn=new FeedForwardNeuralNetwork(phenotype, data.get(0).noAttributes());
        
        return ffnn.connectionCount();
    }
//    private int connCount(String phenotype){
//        int n=0;
//
//
//        return n;
//    }


    private double matchCompleteStruct(ArrayList<String> neurons, int featureCount, double fractConnected){
        double n=0;
       
        //--
        ArrayList<Set> features=new ArrayList<Set>();
        //--
        for (String neu : neurons) {
            Set inputs=new HashSet();

            int start=0;
            do{
                start=neu.indexOf("x",start);
                if(start==-1)break;
                inputs.add(neu.substring(start, start+2));
                start+=2;
            }while(start<neu.length());

            features.add(inputs);
        }//--
        for (Set set : features) {
            //if((set.size()/((double) featureCount)) >= fractConnected)
             //   n++;
            n+=set.size();
            if(n/neurons.size()>=0.95){
                int x=0;
            }
        }
        //---
        return n;
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
                Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
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

    public boolean canCache() {
        return false;
    }


}
