/*
Grammatical Evolution in Java
Release: GEVA-v1.2.zip
Copyright (C) 2008 Michael O'Neill, Erik Hemberg, Anthony Brabazon, Conor Gilligan 
Contributors Patrick Middleburgh, Eliott Bartley, Jonathan Hugosson, Jeff Wrigh

Separate licences for asm, bsf, antlr, groovy, jscheme, commons-logging, jsci is included in the lib folder. 
Separate licence for rieps is included in src/com folder.

This licence refers to GEVA-v1.2.

This software is distributed under the terms of the GNU General Public License.


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
/>.
*/

package Operator;

import Individuals.Individual;
import Individuals.Populations.Population;
import Individuals.Populations.SimplePopulation;
import Operator.Operations.CreationOperation;
import Operator.Operations.Operation;
import Util.Random.RandomNumberGenerator;
import Util.Constants;

import java.util.Iterator;
import java.util.Properties;

import Exceptions.BadParameterException;
import Individuals.GEChromosome;
import Individuals.NeuronsGEIndividual;

/**
 * Initialiser has a CreationOperation and is used to create a population.
 * The population is created by init(). 
 * perform() calls the operation to add codons to the population.
 * The constructor calls createIndividual() to generate a population.
 */
public class Initialiser extends SourceModule implements Creator{
    
    protected CreationOperation operation;
    
    /**
     * Initialiser creates the population
     * @param rng random number generator
     * @param size size
     * @param op creation operation
     */
    public Initialiser(RandomNumberGenerator rng, int size, CreationOperation op) {
        super(rng, size);
        this.operation = op;
        this.init();
    }
    
    /**
     * Initialiser creates the population
     * @param rng random number generator
     * @param op creation operation
     * @param p properties
     */
        public Initialiser(RandomNumberGenerator rng, CreationOperation op, Properties p) {
        super(rng, p);
        this.operation = op;
        this.init();
    }

    /** Creat ne instance */
    public Initialiser() {
        super();
    }
    
    /**
     * Creates the population and the individuals
     **/
    public void init() {
        this.population = new SimplePopulation();
        for(int i=0; i<size; i++) {
            this.population.add(this.operation.createIndividual());
        }
    }
    
    public void setProperties(Properties p) {
        int value  = Integer.parseInt(Constants.DEFAULT_POPULATION_SIZE);
        String key = Constants.POPULATION_SIZE;
        try {
            value = Integer.parseInt(p.getProperty(key));
            if(value < 1) {
                throw new BadParameterException(key);
            }
        } catch(Exception e) {            
            p.setProperty(key, Constants.DEFAULT_POPULATION_SIZE);
            System.out.println(e+" using default: "+Constants.DEFAULT_POPULATION_SIZE);
        }
        setSize(value);
    }
    
    public Population getPopulation() {
        return this.population;
    }
    
    /**
     * Calls the operation to add codons to the individuals in the population
     **/
    public void perform() {
        Iterator<Individual> iIt = this.population.iterator();
        //Operation adds codons

        while(iIt.hasNext()) {
            Individual ind=iIt.next();
            this.operation.doOperation(ind);
            //FIXME!!
            Individual ind2=iIt.next();
            //mutate((NeuronsGEIndividual)ind,(NeuronsGEIndividual)ind2);

        }
    }
    private void mutate(NeuronsGEIndividual ind1, NeuronsGEIndividual ind2){
        //ind2.getGenotype().clear();

        for(int i=0;i<ind1.getGenotype().size();i++){
            GEChromosome ch=(GEChromosome) ind1.getGenotype().get(i);
            GEChromosome ch2=(GEChromosome) ind2.getGenotype().get(i);
            
            for(int j=0;j<ch.size();j++){
                ch2.set(j,ch.get(j));
                //--
            }
        }

        //---
        //---
        
        int hammingLimit = 1;
        int hammingDidtance=0;
        //----
        while(hammingDidtance<hammingLimit){
            NeuronsGEIndividual ind=(NeuronsGEIndividual)ind1;
            int usedGenes= ind.getUsedCodons()[0];
            int position_of_mutation = rng.nextInt(1000);

            int index=0;
            for(int i=0;i<ind.getGenotype().size() && index<=position_of_mutation;i++){

                GEChromosome ch=(GEChromosome) ind2.getGenotype().get(i);
                
                for(int j=0;j<ch.size() && index<=position_of_mutation ;j++ , index++){
                    if(index==position_of_mutation){
                        int current_value = ch.get(j);
                        
                        int next_value=perturbLastBit(current_value);
                        
                        if(hammingDistance(current_value, next_value)+hammingDidtance<=hammingLimit)
                        {
                            hammingDidtance += hammingDistance(current_value, next_value);
                            ch.set(j, next_value);
                        }
                    }
                }
            }
        }
        if(rng.nextDouble()<0.02){
            //ind2.addNewNeuron(rng);
        }

    }
    private int hammingDistance(int x, int y){
        int xor = x ^ y;

        int bits=0;
        while(xor>0){
            bits= bits + (xor & 1);

            xor = xor>>1;
        }
        return bits;
    }

    private int perturbBits(int x, int bits){
        //returns x with at last {\it bits} number of bit changes
        int res=0;
        int temp=0;

        for(int i=0; i<bits;i++){
            int rand=rng.nextInt(8);
            rand = (int) Math.pow(2, rand);
            temp=temp | rand;
        }

        res= x^temp;
        return res;

    }

    private int perturbLastBit(int x){
        //returns x with at last {\it bits} number of bit changes
        return x^1;
    }

    public void setOperation(Operation op) {
        this.operation = (CreationOperation)op;
    }
    
    public Operation getOperation() {
        return this.operation;
    }
}