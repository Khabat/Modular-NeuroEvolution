/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.GEChromosome;
import Individuals.Phenotype;

/**
 *
 * @author sky
 */
public class NeuronsGEChromosome extends GEChromosome{
    Phenotype phenotype;
    public boolean isValid;
    
    public NeuronsGEChromosome() {
        super();
        setMaxChromosomeLength(1000);
        phenotype=new Phenotype();
    }

    public NeuronsGEChromosome(int size){
        super(size);
        setMaxChromosomeLength(1000);
        phenotype=new Phenotype();
    }

    public NeuronsGEChromosome(int size, int[] data){
        super(size, data);
        setMaxChromosomeLength(1000);
        phenotype=new Phenotype();
    }

    public NeuronsGEChromosome(NeuronsGEChromosome c) {
        super(c);
        phenotype=new Phenotype(c.getPhenotype());
        setMaxChromosomeLength(1000);
        this.isValid=c.isValid;
    }

    public Phenotype getPhenotype(){
        return phenotype;
    }   
}
