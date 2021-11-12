/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Operator.Operations.Operation;
import Operator.Operations.SelectionOperation;
import Operator.SelectionScheme;
import Util.Random.RandomNumberGenerator;
import Util.Statistics.StatCatcher;

/**
 *
 * @author KHS
 */
public class MultiObjectiveSelectionScheme extends SelectionScheme {
    MyStateCatcher stats;
    MSE_Complexity_Ranking sorter;
    public MultiObjectiveSelectionScheme(RandomNumberGenerator rng, int size, Operation op,StatCatcher stats){
        super(rng, size, op);
        this.stats=(MyStateCatcher) stats;
    }

    /** Creates a new instance of SelectionScheme
     * @param rng random number generator
     * @param op operation
     */
    public MultiObjectiveSelectionScheme(RandomNumberGenerator rng, Operation op ,StatCatcher stats){
        super(rng, ((SelectionOperation)op).getSize(), op);
        this.stats=(MyStateCatcher) stats;

    }

    public void setSorter(MSE_Complexity_Ranking sorter){
        this.sorter=sorter;
        

    }


    @Override
    public void perform(){
        
        this.stats.set_beforeParentSelection(population);
        super.population.sort();
        sorter.rankBasedOnComplexity(super.population.getAll());
        super.perform();
        this.stats.set_afterParentSelection(this.destinationPopulation);
        
    }
}
