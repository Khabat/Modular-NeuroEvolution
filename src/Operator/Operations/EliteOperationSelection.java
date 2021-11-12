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

package Operator.Operations;

import Individuals.FitnessPackage.Fitness;
import Individuals.GEIndividual;
import Individuals.Individual;
import Individuals.Populations.SimplePopulation;
import Util.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Class for selection of elites.
 **/
public class EliteOperationSelection extends SelectionOperation  {
    
    private boolean evaluate_elites;

    /**
     * New instance
     * @param size size of elites
     */
    public EliteOperationSelection(int size){
        super(size);
    }

    /**
     * New instance
     * @param p properties
     */
    public EliteOperationSelection(Properties p){
        super();
        setProperties(p);
    }
    
    @Override
    public void setProperties(Properties p) {
        int value =0;
        String key = Constants.ELITE_SIZE;
	value = Integer.parseInt(p.getProperty(key,"0"));
        if (value == -1) {//-1 indicates elites is turned off
            value = 0;
        }
        this.size = value;
        String valueS;
        key = Constants.EVALUATE_ELITES;
        try {
            valueS = p.getProperty(key);
            if(valueS == null ) {
		valueS = Constants.FALSE;
            }
        } catch(Exception e) {
            valueS = Constants.FALSE;
            System.out.println(this.getClass().getName()+".setProperties "+e+" using default: "+valueS);
        }
        this.evaluate_elites = valueS.equals(Constants.TRUE);
        super.selectedPopulation = new SimplePopulation(this.size);
    }

    public boolean isEvaluateElites() {
        return this.evaluate_elites;
    }

    public void doOperation(Individual operand) {
    }
    
    /**
     * Ranks the population. Takes out size number of individuals and adds
     * to the selectedPopulation.
     * @param operands Individuals to select from
     **/
    public void doOperation(List<Individual> operands) {
        Fitness[] fA=rankPopulation(operands);
        int cnt = 0;
        while(cnt < this.size && cnt < operands.size()){
            //Avoid duplicates
            final boolean valid = fA[cnt].getIndividual().isValid();
            final boolean duplicate = this.selectedPopulation.contains(fA[cnt].getIndividual());
            if(!duplicate && valid) {
                Individual ind = fA[cnt].getIndividual().clone();
                //		System.out.println("org:\t"+fA[cnt].getIndividual().getGenotype().hashCode());
                //		System.out.println("new:\t"+ind.getGenotype().hashCode());
                //Set individual as valid
                if(!this.evaluate_elites) {
                    ind.setEvaluated(fA[cnt].getIndividual().isEvaluated());
                    ind.setValid(fA[cnt].getIndividual().isValid());
                    ind.setAge(fA[cnt].getIndividual().getAge());
                    ((GEIndividual)ind).setMapped(((GEIndividual)(fA[cnt].getIndividual())).isMapped());
                    ((GEIndividual)ind).setUsedCodons(((GEIndividual)(fA[cnt].getIndividual())).getUsedCodons());
                }
                this.selectedPopulation.add(ind);
            }
            cnt++;
        }
        //System.out.println("E:"+this.selectedPopulation);

    }

    public void setEvaluate_elites(boolean evaluate_elites) {
        this.evaluate_elites = evaluate_elites;
    }

    /**
     * Helper function to rank the poulation in ascending order.
     * @param operands List of Individuals to rank
     * @return An ordered Fitness array
     **/
    Fitness[] rankPopulation(List<Individual> operands){
        Fitness[] fAt = new Fitness[operands.size()];
        
        //System.out.print("EliteRank org:");
        for(int i=0;i<fAt.length;i++) {
            fAt[i] = operands.get(i).getFitness();
            //System.out.print(fAt[i].getDouble()+",");
        }
        //System.out.println();
        //Sort ascending
        Arrays.sort(fAt);
        return fAt;
    }
    
}
