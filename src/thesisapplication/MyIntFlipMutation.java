/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.Chromosome;
import Individuals.GEChromosome;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.Random.RandomNumberGenerator;
import java.util.Properties;
import java.util.Random;



/**
 *
 * @author Administrator
 */
public class MyIntFlipMutation extends Operator.Operations.IntFlipMutation{

    public MyIntFlipMutation(double probability, RandomNumberGenerator rng){
        super(probability, rng);
    }
    public MyIntFlipMutation(RandomNumberGenerator rng, Properties prop){
        super(rng, prop);
    }


    static int gennrs=1;
    /**
     * According to this.probability a codon in the chromosome is
     * replaced with a new randomly chosen integer
     * We Override this method to limit codon values to 0..255
     * @param c input to mutate
     */
    Random r= new Random();
    private void doMutationGE(GEChromosome c,int usedCodons) {
         for(int i=0;i<c.getLength();i++) {
            if(this.rng.nextBoolean(this.probability)) {
                final int nextInt = Math.abs(rng.nextInt(256));//original methode use nextInt() with no parameter (Integer.Max_value default)
                c.set(i, nextInt);
            }
        }

        
        
//        if (this.rng.nextBoolean(this.probability)) {
//               addCoddon(c,usedCodons);
//        }
//        else if(this.rng.nextBoolean(this.probability)) {
//               delCoddon(c,usedCodons);
//        }
    }
    private void addCoddon(GEChromosome c,int usedCodons) {
        final int nextInt = Math.abs(rng.nextInt(256));//original methode use nextInt() with no parameter (Integer.Max_value default)
        int bound=(usedCodons>c.getLength()||usedCodons<=0)?(c.getLength()+1):(usedCodons);
        int position = this.rng.nextInt(bound);
        position=0;////FIXME!!!!!!!!!! It is temprary
        c.add(c.get(c.getLength()-1));
        int i;
        for (i = c.getLength()-1; i > position; i--) {
            c.set(i, c.get(i-1));
        }
        c.set(i, nextInt);
        
    }
    private void delCoddon(GEChromosome c,int usedCodons) {
        if(c.getLength()<10)return;

        int bound=(usedCodons>c.getLength()||usedCodons<=0)?(c.getLength()+1):(usedCodons);

        int position = this.rng.nextInt(bound);
        position=0;////FIXME!!!!!!!!!! It is temprary
        GEChromosome newC = new NeuronsGEChromosome(c.getLength() - 1);
        int i;
        for (i = 0; i < position; i++) {
            newC.add(c.get(i));
        }
        i++;//!!!! to skip from that codon!!!!
        for (; i < c.getLength(); i++) {
            newC.add(c.get(i));
        }
        c.data = newC.data;
        c.currentSize--;
    }

     /**
     * Calls doMutation(GEIndividual c) and then calls Individual.invalidate()
     * @param operand operand to operate on
     */
    @Override
    public void doOperation(final Individual operand) {
        int i=0;
        for (Chromosome c : operand.getGenotype()) {
            doMutationGE((GEChromosome)c,((NeuronsGEIndividual) operand).getUsedCodons()[i]);
            i++;
        }
        
        doMutationAdd(operand);
        doMutationRemove(operand);
        ((NeuronsGEIndividual)operand).invalidate();
//        if(gennrs%400==0)
//            this.probability*=0.99;
//        gennrs++;
    }
    
    private void doMutationAdd(Individual i){
        if(rng.nextBoolean(probability*2) ){
            int num=rng.nextInt(1)+1;
            for(int k=0;k<num;k++){
                ((NeuronsGEIndividual)i).addNewNeuron(rng);
            }
               
        }
    }
    private void doMutationRemove(Individual i){
        if(rng.nextBoolean(probability*2)){
            int num=rng.nextInt(1)+1;
            for(int k=0;k<num;k++){
                ((NeuronsGEIndividual)i).removeNeuron(rng);
            }
        }
    }    
}
