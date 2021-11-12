/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import FitnessEvaluation.FitnessFunction;
import FitnessEvaluation.NeuralNetworkEvaluator.NeuralNetworkFitnessFunction;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Operator.Operations.FitnessEvaluationOperation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Khabat
 */
public class MyFitnessEvaluationOperation extends FitnessEvaluationOperation {
    public MyStateCatcher stats;
    int maxNeurons=20;
    double penalty_coef;

    private Runner ranners[];
    private Thread thrds[];
    FitnessFunction ff[];

    public MyFitnessEvaluationOperation(FitnessFunction ff[], MyStateCatcher s) {
        super(ff[0]);
        stats=s;
        //maxNeurons=((NeuralNetworkFitnessFunction)super.getFitnessFunction()).getMaxNeuron();

         this.ff=ff;

        ranners=new Runner[ff.length];
        thrds = new Thread[ff.length];
        
        for(int i=0;i<ranners.length;i++){
            ranners[i]=new Runner();
            thrds[i]=new Thread(ranners[i]);
        }

       
    }
//    public void doOperation(List<Individual> operands,boolean lastGeneration){
//        if(lastGeneration)
//            super.doOperation(operands);
//    }
    
    private static double meanOFmse(List<Individual> operands) {
        double mean = 0, temp;
        int valids = 0;
        for (int i = 0; i < operands.size(); i++) {
            temp = operands.get(i).getFitness().getDouble();

            if (!Double.isNaN(temp) && !Double.isInfinite(temp) && temp != BasicFitness.DEFAULT_FITNESS)
            {
                mean += temp;
                valids++;
            }
        }
        return mean / valids;
    }
    
    private static double meanOfneurons(List<Individual> operands){
        double mean=0,temp;
        int valids=0;
        for(int i=0;i<operands.size();i++){
            temp = operands.get(i).getFitness().getDouble();

            if (!Double.isNaN(temp) && !Double.isInfinite(temp) && temp != BasicFitness.DEFAULT_FITNESS) {
            
                mean+=((NeuronsGEIndividual) operands.get(i)).get_neurons_count();
                valids++;
            }
        }
        return mean/valids;
    }

    /*
     * this function based on the current penalty coefficient udates the fitness of every individual
     */
    private static void updateFiness(List<Individual> operands, double coefficient){
        double mse;
        int neurons;
        for(int i=0;i<operands.size();i++){
            mse=operands.get(i).getFitness().getDouble();
            if(!Double.isNaN(mse) && !Double.isInfinite(mse) && mse != BasicFitness.DEFAULT_FITNESS)
            {
                neurons=((NeuronsGEIndividual)operands.get(i)).get_neurons_count();
                
                operands.get(i).getFitness().setDouble(mse + coefficient * neurons );
                //operands.get(i).getFitness().setDouble(mse);
            }
        }
    }

    @Override
        public void doOperation(List<Individual> operands) {

        for(int i=0;i<ranners.length;i++){
            ranners[i]=new Runner();
            ranners[i].fitnessFunction = ff[i];
            thrds[i]=new Thread(ranners[i]);
        }
        //--
        Iterator<Individual> iIt = operands.iterator();
        //Operation adds codons
        int i=0;
        while(iIt.hasNext()) {

            Individual ind=iIt.next();

            ranners[i%ranners.length].inds.add(ind);
            i++;
        }
        //--
        for(Thread trd:thrds){
            trd.start();
        }
        //--
        try {
            for (Thread trd : thrds) {
                trd.join();
            }
        } catch (InterruptedException ex) {
            //Logger.getLogger(FitnessEvaluationOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class Runner implements Runnable {

        List<Individual> inds=new ArrayList<Individual>();
        FitnessFunction fitnessFunction=null;

        public void run() {
            Iterator<Individual> iIt = inds.iterator();
            //Operation adds codons

            while (iIt.hasNext()) {

                Individual ind = iIt.next();
                doOperation(ind, fitnessFunction);
            }
        }
    }

    public void doOperation(Individual operand , FitnessFunction ff) {
        //	System.out.println(this.getClass().getName()+".doOperation("+operand+") ENTRY");
        operand.setEvaluated(false);
        if (!operand.isEvaluated()) {
            if (operand.getGenotype() == null && operand.getPhenotype() != null) {
                // This can happen when individual's phenotype is constructed directly,
                // eg by NGramEDAReproductionOperation. No need to map. Can't interact with cache.
                if (operand.isValid()) {
                    ff.getFitness(operand);
                } else {
                    operand.getFitness().setDefault();
                }
                return;
            }

            //Map individual
            operand.map(0);

            boolean cache = ff.canCache();
            //cache = false;
            if (cache == false // Short-circuit, won't getFitnessFromCache if cache==false
                    || getFitnessFromCache(operand) == false) {
                if (operand.isValid()) {
                    ff.getFitness(operand);
                } else {
                    operand.getFitness().setDefault();
                }
                if (cache == true) {
                    addFitnessToCache(operand);
                }
            }
            if (this.evaluateEverytime) {
                operand.setEvaluated(false);
            } else {
                operand.setEvaluated(true);
            }
        }
    //	System.out.println(this.getClass().getName()+".doOperation("+operand+") EXIT");
    //	System.out.println(this.getClass().getName()+".doOperation("+operand+") fit:"+operand.getFitness().getDouble());
    }
    
    

}
