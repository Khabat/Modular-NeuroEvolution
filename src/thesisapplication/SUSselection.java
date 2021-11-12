/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Operator.Operations.SelectionOperation;
import Util.Constants;
import Util.Random.RandomNumberGenerator;
import Util.Random.Stochastic;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Khabat
 */
public class SUSselection extends SelectionOperation implements Stochastic{

    protected RandomNumberGenerator rng;
    protected double[] accProbs;
    double selection_preaseure;
    public SUSselection(int size, RandomNumberGenerator rng,double selection_preasure) {
        super(size);
        this.rng=rng;
        this.selection_preaseure=selection_preasure;
        
    }

    public SUSselection() {
        super();
        
    }

    @Override
    public void doOperation(Individual operand) {

    }

    /**
     * we assume that operands are ranked so we dont do it here,
     * @param operands
     * by Khabat
     */
    @Override
    public void doOperation(List<Individual> operands) {
//        this.minFit=operands.get(operands.size()-1).getFitness().getDouble();
//        calculateFitnessSum(operands);
        if (accProbs==null){// to  invoke it only one time
            calculateAccumulatedFitnessProbabilities(operands.size());
        }
        stochasticSampling(operands);
        
    }

    public RandomNumberGenerator getRNG() {
        return this.rng;
    }

    public void setRNG(RandomNumberGenerator m) {
        this.rng = m;
    }

    protected void calculateAccumulatedFitnessProbabilities(int size) {
        accProbs = new double[size];
        //minFit means max MSE value
        int mu=accProbs.length;
        int pos=mu;
        double s=this.selection_preaseure;

        accProbs[0]=(2-s)/mu+2*(pos-1)*(s-1)/(mu*(mu-1));

        for(int i=1;i<mu;i++){
            pos=mu-i;
            accProbs[i]=(2-s)/mu+2*(pos-1)*(s-1)/(mu*(mu-1));
            accProbs[i]=accProbs[i-1]+accProbs[i];
        }
    }

    @Override
    public void setProperties(Properties p){
        super.setProperties(p);
        String key=Constants.SELECTION_PREASURE;
        double value=0;
        try{
            value=Double.valueOf(p.getProperty(key, "1.7"));
        }catch(Exception exp){
            value=1.5;
        }

        this.selection_preaseure=value;
    }

    private void stochasticSampling(List<Individual> operands) {
        double prob;
        Individual selected;
        this.selectedPopulation.clear();
        //draw first double number
        prob=rng.nextDouble()/operands.size();
        int cnt = 0;
        while(this.selectedPopulation.size()<super.getSize()) {
            
            while(cnt < operands.size() && this.accProbs[cnt] < prob) {
                cnt=(cnt+1)%operands.size();
            }
            if(cnt >= operands.size()) {
		//     System.out.println("Doh:"+cnt);
                cnt = operands.size() - 1;
                //If the selction with the roulette fails, take the last individual
            }
            selected = operands.get(cnt);
            //selected.getFitness();
            this.selectedPopulation.add(selected);

            //update probablity
            prob += 1.0f / operands.size();
            if (prob >= 1) {
                prob -= 1;
            }
        }

    }

    void setSize(int i) {
        this.size=i;
    }

    public static void rankPopulation(List<Individual> operands) {
        Collections.sort(operands);
    }
}
