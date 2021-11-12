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
 * Collector.java
 *
 * Created on April 26, 2007, 1:46 PM
 *
 */

package Operator;

import Individuals.Individual;
import Individuals.Populations.Population;
import Operator.Operations.Operation;
import Operator.Operations.OutputI;
import Operator.Operations.StatisticsCollectionOperation;
import Util.Statistics.IndividualCatcher;
import java.util.List;
import thesisapplication.SUSReplaceOperation;

/**
 * Collector is used as a base class for collecting data from a 
 * population, e.g for collecting statistics. This class should not
 * be used to derive a class that alters a population. 
 * @author erikhemberg
 */
public class Collector implements Operator{
    
    private Operation operation;
    private Population population;
    
    /** Creates a new instance of Collector
     * @param op operation
     */
    public Collector(Operation op) {
        this.operation = op;
    }
    
    public Operation getOperation() {
        return this.operation;
    }
    public Population getPopulation(){
        return population;
    }
    public void setOperation(Operation op) {
        this.operation = op;
    }
    
    public void setPopulation(Population p) {
        this.population = p;
    }
    
    public void perform() {
        this.operation.doOperation(this.population.getAll());
    }

    /**
     * Call the print(list<Individual> operands, boolean b)
     * in the operation.
     * @param toFile if output is written to file
     **/
    public void print(boolean toFile) {
        ((OutputI)this.operation).print(this.population.getAll(), toFile);
    }

    /**
     * Call the getBest(list<Individual> operands)
     * in the operation.
     **/
    public IndividualCatcher getBest(){
        return ((OutputI)this.operation).getBest(this.population.getAll());
    }

    public Individual getBestIndividual(){
        
        this.population.sort();
        return population.get(0);
    }
    //khabat
//    public Individual getBestIndividualOn_MSE_Complexity(){
//
//        getPopulation().sort();
//        List<Individual> operands=getPopulation().getAll();
//        SUSReplaceOperation.rankBasedOnComplexity(operands);
//        return operands.get(0);
//    }

}
