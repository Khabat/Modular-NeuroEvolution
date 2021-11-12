/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Operator.MutationOperator;
import Operator.Operations.MutationOperation;
import Util.Random.RandomNumberGenerator;
import Util.Statistics.StatCatcher;

/**
 *
 * @author KHS
 */
public class MyMutationOperator extends MutationOperator {
     MyStateCatcher stats;
     public MyMutationOperator(RandomNumberGenerator rng, MutationOperation op,StatCatcher stats) {
        super(rng,op);
        this.stats=(MyStateCatcher) stats;

    }

    @Override
    public void perform() {
        stats.set_afterXover(this.population);
        super.perform();
    }


}
