/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Exceptions.BadParameterException;
import Individuals.Chromosome;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.FitnessPackage.Fitness;
import Individuals.GEChromosome;
import Individuals.Genotype;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Individuals.Phenotype;
import Mapper.GEGrammar;
import Util.Constants;
import Util.Random.RandomNumberGenerator;
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class MyRandomInitialiser extends Operator.Operations.RandomInitialiser {
    int num_of_neurons;
    public MyRandomInitialiser(RandomNumberGenerator rng, GEGrammar g, int initChromSize) {
        super(rng, g, initChromSize);
    }

    /**
     * New instance
     * @param rng random number generator
     * @param g grammatical evolution grammar
     * @param p properties
     */
    public MyRandomInitialiser(RandomNumberGenerator rng, GEGrammar g, Properties p) {
        super(rng, g, p);
    }

     /**
     * Set an integer chromsome of initChromSize filled with random integers
     * in the incoming individual.
     * We override this method to limitb codon values to 0..255
     * @param operand Individual to get the new chromosome
     **/
    @Override
    public void doOperation(Individual operand) {
        
        for (Chromosome c : operand.getGenotype()) {

            int[] chr = new int[c.getLength()];
            for (int i = 0; i < c.getLength(); i++) {
                chr[i] = rng.nextInt(256);
            }
            ((GEChromosome)c).setAll(chr);            
        }
    }

    @Override
    public Individual createIndividual() {
        NeuronsGEGrammar gram = new NeuronsGEGrammar((NeuronsGEGrammar)this.grammar);
        Phenotype phenotype = new Phenotype();
        //int num_neurons=rng.nextInt(this.num_of_neurons-1)+2;
        int num_neurons=this.num_of_neurons;//FixMe!!
        Genotype genotype = new Genotype(num_neurons);
        
        for(int i=0;i<num_neurons;i++){
            int[] codons = new int[this.initChromSize];
            NeuronsGEChromosome chrom = new NeuronsGEChromosome(this.initChromSize, codons);
            //Set the maximum chromosome length
            chrom.setMaxChromosomeLength(gram.getMaxChromosomeLengthByDepth());
            genotype.add(chrom);        
        }
        
        Fitness fitness = new BasicFitness();
        //this.doOperation(ind);

        NeuronsGEIndividual individual = new NeuronsGEIndividual(gram, phenotype, genotype, fitness);
        return individual;
    }
    
    @Override
    public void setProperties(Properties p){
        super.setProperties(p);

        String key=Constants.NUM_NEURONS;
        int value=-1;
        try {
            value = Integer.parseInt(p.getProperty(key));
            if (value < 1) {
                throw new BadParameterException(key);
            }
        } catch (Exception e) {
            System.out.println(e + " for " + key + " using default: " + value);
        }
        this.num_of_neurons=value;
    }

}
