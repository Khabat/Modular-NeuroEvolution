/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Operator.CrossoverModule;
import Operator.Operations.CrossoverOperation;
import Util.Random.RandomNumberGenerator;
import Util.Statistics.StatCatcher;

/**
 *
 * @author KHS
 */
public class MyCrossoverModule extends CrossoverModule{
    MyStateCatcher stats=null;
    public MyCrossoverModule(RandomNumberGenerator m, CrossoverOperation xOver,StatCatcher st){
        
        super(m,xOver);
        this.stats=(MyStateCatcher) st;
    }

    @Override
    public void perform() {
        ((MySinglePointCrossover2)super.getOperation()).competingConvention=0;
        super.perform();
        stats.set_competingConvention(((MySinglePointCrossover2)super.getOperation()).competingConvention);
   }
    

}
