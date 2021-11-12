/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.Genotype;
import Individuals.NeuronsGEIndividual;
import Individuals.Phenotype;
import Individuals.Populations.Population;
import Individuals.Populations.SimplePopulation;
import Operator.Operations.ReplacementOperation;
import Operator.SimpleReplacementStrategy;
import Util.Random.RandomNumberGenerator;
import Util.Statistics.StatCatcher;

/**
 *
 * @author khs
 */
public class ReplacementStrategy extends SimpleReplacementStrategy {
    MyStateCatcher stats=null;
    public ReplacementStrategy(RandomNumberGenerator rng, Population incPop, ReplacementOperation rO, StatCatcher stats){
	super(rng, incPop, rO);
        this.stats=(MyStateCatcher) stats;
    }

    /**
     * New instance
     */
    public ReplacementStrategy(){
	super();
    }

    @Override
     public void perform() {

	//this.incomingPopulation.sort();
	//this.population.sort();
        //System.out.println("ip:"+this.incomingPopulation);
	//        System.out.println("op:"+this.population);
	/*
	 * If the incomming population is greater the the number of
	 * individuals that will be replaced in the original population
	 * (to create the new population) the incoming population needs
	 * to be reduced by the size difference between the incoming
	 * and the original population.
	 */

//        if(this.incomingPopulation.size()>this.replacementOperation.getReplacementSize()) {
//            int size = this.incomingPopulation.size()-this.replacementOperation.getReplacementSize();
//            this.replacementOperation.doOperation(this.incomingPopulation.getAll(), size);
//        }

        //System.out.println("t-ip:"+this.incomingPopulation);
        /*
	 * If Generational (incoming population size is the same as
	 * original population size) then Clear the original
	 * population. Else rank the original population and remove
	 * the worst (the number removed is the replacement size)
	 */
        stats.set_afterMutation(this.incomingPopulation);
        //  JOptionPane.showMessageDialog(null, gen.toString()+"\n"+phen.toString());
        
        


        //incomingPopulation.addAll(population);

        
        
//        int x=0;
//        Population inc=new SimplePopulation();
//        inc.addAll(incomingPopulation.getAll());
//
//        for(int i=0;i<inc.size();i++){
//            inc.get(i).setAge(1);
//        }
//        for(int i=0;i<inc.size();i++){
//            for(int j=i+1;j<inc.size();j++){
////                if(inc.get(i).getAge()==-1){
////                    System.out.print("hihi");
////                }
//                //if(isEqual((NeuronsGEIndividual)inc.get(i), (NeuronsGEIndividual)inc.get(j)) && i!=j){
//                if(inc.get(j).getAge()!=-1 && inc.get(i).equals(inc.get(j)) && i!=j ){
//
//                    inc.get(j).setAge(-1);
//                    x++;
//                }
//            }
//        }
//        System.out.println(x+"eq incom");
//        x=0;
//        for(int i=0;i<incomingPopulation.size();i++){
//            for(int j=0;j<population.size();j++){
//                if(isEqual((NeuronsGEIndividual)incomingPopulation.get(i), (NeuronsGEIndividual)population.get(j)) ){
//                    x++;
//                }
//            }
//        }
//
//        System.out.println(x+"pop equ");

        /**
         * gap
         */


//        replacementOperation.doOperation(incomingPopulation.getAll());
//        population.sort();
//        for(int i=0;i<replacementOperation.getReplacementSize();i++)
//            population.remove(population.get(population.size()-1));
//
//        population.addAll(incomingPopulation);
        //((SUSReplaceOperation)replacementOperation).sorter.rankBasedOnComplexity(incomingPopulation);

        //incomingPopulation.addAll(population);
        incomingPopulation.sort();
        replacementOperation.doOperation(incomingPopulation.getAll());
        population.sort();
               

        for(int i=0;i<replacementOperation.getReplacementSize();i++){
            population.remove(population.get(population.size()-1));
        }

        population.addAll(incomingPopulation);

//        population.clear();
//
//        population.addAll(incomingPopulation);

        //System.out.println("t-p:"+this.population);
	/*
	 * Add the incoming population to the original population to
	 * create the new population. The new population is guaranteed
	 * to have the same size as the original population since the
	 * incoming population is trimmed to replacement size as well
	 * as the original population has removed enough.
	 */

        this.incomingPopulation.clear();
        this.increaseAge(this.population.getAll());
        
        this.stats.set_afterReplacement(this.population);

    }
     
    private boolean isEqual(NeuronsGEIndividual i1, NeuronsGEIndividual i2){
        if(i1.getGenotype().size()!=i2.getGenotype().size() || i1.getAge()==-1 || i2.getAge()==-1)
            return false;
        if(i1.getFitness().getDouble()==i2.getFitness().getDouble())return true;
        for(int i=0;i<i1.getGenotype().size();i++){
            for(int j=0;j<i1.getGenotype().get(i).getLength();j++){
                if(((NeuronsGEChromosome)i1.getGenotype().get(i)).get(j)!=((NeuronsGEChromosome)i2.getGenotype().get(i)).get(j))
                    return false;
            }
        }
        return true;
    }
}
