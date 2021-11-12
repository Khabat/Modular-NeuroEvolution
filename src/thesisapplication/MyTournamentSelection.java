/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Operator.Operations.TournamentSelect;
import Util.Random.RandomNumberGenerator;
import java.util.Collections;
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class MyTournamentSelection extends TournamentSelect{
    public MyTournamentSelection(int size, int tourSize, RandomNumberGenerator rand) {
        super(size, tourSize, rand);
    }

    /** Creates a new instance of TournamentSelect
     * @param rand random number generator
     * @param p properties
     */
    public MyTournamentSelection(RandomNumberGenerator rand, Properties p) {
       super(rand, p);
    }

    /**
     * New instantion
     */
    public MyTournamentSelection(){
        super();
    }
    /**
     * In Original TournamentSelect class it Selects a winner from the tournament and add to the selected population.
     * but we select 2 of best and add it to the pool (the 2 individual that are placed closed, do crossover in later action: 0&1 - 2&3 - 4&5- ..)
     * just like paper
     **/
    @Override
    public void selectFromTour() {
        Collections.sort(tour);
        this.selectedPopulation.add(tour.get(0).getIndividual().clone());
        this.selectedPopulation.add(tour.get(1).getIndividual().clone());   //just this line of code has been added
    }
}
