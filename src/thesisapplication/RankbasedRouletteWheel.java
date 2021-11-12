package thesisapplication;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import Individuals.Individual;
import Operator.Operations.RouletteWheel;
import Util.Constants;
import Util.Random.RandomNumberGenerator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author khs
 */
public class RankbasedRouletteWheel extends RouletteWheel {
    //private double maxFit;//we use this to interprete MSE as fitness by subtructing it from maxFit
    double selection_preaseure;
    {accProbs=null;}
    public RankbasedRouletteWheel(int size, RandomNumberGenerator rng,double selection_preasure) {
        super(size, rng);
        this.selection_preaseure=selection_preasure;
    }

    /**
     * New instance
     */
    public RankbasedRouletteWheel() {
        super();
    }

    @Override
    protected void calculateAccumulatedFitnessProbabilities(List<Individual> operands) {

        accProbs = new double[operands.size()];
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
    protected void calculateFitnessSum(List<Individual> c) {
//        double sum = 0;
//        double tmp;
//        Iterator<Individual> itI = c.iterator();
//        //this.minFit = Double.MIN_VALUE;//THIS LINe HAS BEEN MODIFIED
//
//
//        while (itI.hasNext()) {
//            tmp = itI.next().getFitness().getDouble();
//            if (tmp > 1) {
//                this.smallFit = false;
//            }
//
//            sum += tmp;
//        }
//        this.sumFit = c.size() * this.minFit - sum;
        this.sumFit=1;
    }
    
    //we altered it: deleting the first line that ranked the population. because we assume that it is ranked earler
    @Override
    public void doOperation(List<Individual> operands) {
        //rankPopulation(operands);
        //calculateFitnessSum(operands);
        this.minFit=operands.get(operands.size()-1).getFitness().getDouble();
        calculateFitnessSum(operands);
        if(accProbs==null)calculateAccumulatedFitnessProbabilities(operands);//to invoke it once
        spinRoulette(operands);
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

    void setSize(int i) {
        this.size=i;
    }

    protected void spinRoulette(List<Individual> operands) {
        double prob;
        Individual selected;
        this.selectedPopulation.clear();

        while(this.selectedPopulation.size()<super.getSize()) {
            prob = rng.nextDouble()*this.sumFit;

            int cnt = 0;
            while(cnt < operands.size() && this.accProbs[cnt] < prob) {
                cnt++;
            }
            if(cnt >= operands.size()) {
		//     System.out.println("Doh:"+cnt);
                cnt = operands.size() - 1;
                //If the selction with the roulette fails, take the last individual
            }
            selected = operands.get(cnt).clone();
            this.selectedPopulation.add(selected);
        }
    }
}
