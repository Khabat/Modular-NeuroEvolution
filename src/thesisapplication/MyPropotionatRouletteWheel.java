/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.Individual;
import Operator.Operations.ProportionalRouletteWheel;
import Util.Random.RandomNumberGenerator;
import java.util.List;

/**
 *
 * @author KHS
 */
public class MyPropotionatRouletteWheel extends ProportionalRouletteWheel {

    public MyPropotionatRouletteWheel(int size, RandomNumberGenerator rng) {
        super(size, rng);
    }

    /**
     * New instance
     */
    public MyPropotionatRouletteWheel() {
        super();
    }

    /**
     * Min fitness is the best fitness.
     * Subtracts the fitness from the fitness sum and divides by the fitness sum
     * Store the accumulated probabilities in the accProbs array
     * @param operands Individuals to take into account
     ***/
    @Override
    protected void calculateAccumulatedFitnessProbabilities(List<Individual> operands) {
        super.calculateAccumulatedFitnessProbabilities(operands);
        this.sumFit=1;//it was a mistake

    }

    void setSize(int i) {
        this.size=i;
    }

}
