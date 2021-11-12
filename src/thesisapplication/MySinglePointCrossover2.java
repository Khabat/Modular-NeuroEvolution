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

/*
 * SinglePointCrossover.java
 *
 * Created on March 5, 2007, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package thesisapplication;

import Operator.Operations.*;
import Individuals.GEChromosome;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.Constants;
import Util.Random.RandomNumberGenerator;

import java.util.List;
import java.util.Properties;

/**
 * Single point crossover.
 * @author Blip
 */
public class MySinglePointCrossover2 extends CrossoverOperation {

    protected boolean fixedCrossoverPoint = true;
    protected boolean codonsUsedSensitive;
    public int competingConvention=0;
    /**
     * Creates a new instance of SinglePointCrossover
     * @param m random number generator
     * @param prob crossover probability
     */
    @SuppressWarnings({"SameParameterValue"})
    public MySinglePointCrossover2(RandomNumberGenerator m, double prob) {
        super(prob, m);
    }

    /**
     * New instance
     * @param m random number generator
     * @param p properties
     */
    public MySinglePointCrossover2(RandomNumberGenerator m, Properties p) {
        super(m, p);
        this.setProperties(p);
    }

    /**
     * Set properties
     *
     * @param p object containing properties
     */
    @Override
    public void setProperties (Properties p) {
        super.setProperties(p);
        String value;
        boolean b = false;
        String key;
        try {
            key = Constants.FIXED_POINT_CROSSOVER;
            value = p.getProperty(key);
            if (value != null) {
                if (value.equals(Constants.TRUE)) {
                    b = true;
                }
            }
        } catch (Exception e) {
            System.out.println(e + " using default: " + b);
        }
        this.fixedCrossoverPoint = b;
        //CodonUsed sensitive
        b = false;
        key = Constants.CODONS_USED_SENSITIVE;
        value = p.getProperty(key);
        if (value != null) {
            if (value.equals(Constants.TRUE)) {
                b = true;
            }
        }
        this.codonsUsedSensitive = b;

    }

    public void doOperation(Individual operands) {}
    
    /**
     * Performes crossover on the 2 first individuals in the incoming list.
     * Depending on the crossover probability.
     * @param operands Individuals to crossover
     **/
    public void doOperation(List<Individual> operands) {
        NeuronsGEIndividual p1 = (NeuronsGEIndividual) operands.get(0);
        NeuronsGEIndividual p2 = (NeuronsGEIndividual) operands.get(1);
        //if(myUtil.hasEqualNeuronTopology(p1, p2)) this.competingConvention++;
        if (rand.nextBoolean(this.probability)) {
            if (rand.nextBoolean()) {
                makeNewChromosome(p1, p2);
            } else {
                makeNewChromosome(p2, p1);
            }
           p1.invalidate();
           p2.invalidate();            
        }
         
        
        
    }
    protected void makeNewChromosome(NeuronsGEIndividual p1, NeuronsGEIndividual p2){
        int min_len=(p1.getGenotype().size()<p2.getGenotype().size())
                                    ?(p1.getGenotype().size()):(p2.getGenotype().size());
        int chrom_len=p1.getGenotype().get(0).getLength();
        int xpoint=rand.nextInt(min_len*chrom_len);
        int neuron_index=-1;

        for(int i=0,j=0;i<xpoint;i++,j++){
            if(i%chrom_len==0){
                neuron_index++;
                j=0;
            }

            int temp=((NeuronsGEChromosome)p1.getGenotype().get(neuron_index)).data[j];
            ((NeuronsGEChromosome)p1.getGenotype().get(neuron_index)).data[j]=
                    ((NeuronsGEChromosome)p2.getGenotype().get(neuron_index)).data[j];
            ((NeuronsGEChromosome)p2.getGenotype().get(neuron_index)).data[j]=temp;
        }
        //if(this.fixedCrossoverPoint){
//        GEChromosome tmp;
//        for(int i=0;i<xpoint;i++){
//            tmp=(GEChromosome) p1.getGenotype().get(i);
//            p1.getGenotype().set(i, p2.getGenotype().get(i));
//            p2.getGenotype().set(i, tmp);
//        }
        //}
    }

    /**
     * Chech is the crossover point is fixed
     * @return true if crossover point is fixed
     */
    public boolean isFixedCrossoverPoint() {
        return fixedCrossoverPoint;
    }

    /**
     * Set crossover point to be fixed (same on both chromsomes) or not fixed
     * @param fixedCrossoverPoint crossverpoint fixation
     */
    @SuppressWarnings({"SameParameterValue"})
    public void setFixedCrossoverPoint(boolean fixedCrossoverPoint) {
        this.fixedCrossoverPoint = fixedCrossoverPoint;
    }
       
}
