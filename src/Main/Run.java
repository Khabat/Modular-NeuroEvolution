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
 * Run.java
 *
 * Created on April 17, 2007, 11:02 AM
 *
 */

package Main;

import Algorithm.MyFirstSearchEngine;
import Algorithm.Pipeline;
import Algorithm.SimplePipeline;
import FitnessEvaluation.FitnessFunction;
import Mapper.GEGrammar;
import Operator.*;
import Operator.Operations.*;
import Util.Constants;
import Util.Random.MersenneTwisterFast;
import Util.Statistics.IndividualCatcher;
import Util.Statistics.StatCatcher;

/**
 * Run main class. 
 * Steps to setup the algorithm. 
 * Create the operators you want to use eg: mutation, selection. 
 * Create specific operations eg: Int flip mutation, Tournament Selection.
 * Add the operations to the operators
 * Set the population in each operator.
 * Add opertors to the init pipeline in the desired order of execution.
 * Add operators to the loop pipeline in the desired order of execution.
 * Create a main for the algorithm to run this needs to call init, setup and run(int number_of_iterations) 
 *      (or the step() method can be used in a loop)
 * @author erikhemberg
 */
public class Run extends AbstractRun {

    protected long startTime;
    
    /** Creates a new instance of Run */
    public Run() {
        this.rng = new MersenneTwisterFast();
        //super.propertiesFilePath = Constants.DEFAULT_PROPERTIES;SantaFeAntTrail.properties
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void experiment(String[] args) {
        try{

            //Read the command-line arguments
            if(this.commandLineArgs(args)) {
                //Initialize timing the excecution
                long st = System.currentTimeMillis();
              
                this.setup(args);
                //Initialize the algorithm
                this.init();
                //Hack for number of iterations!!?? Create a proper method
                int its = this.run();
                //Print collected data
                this.printStuff();
                //Time the excecution
                long et = System.currentTimeMillis();
                System.out.println("Done running: Total time(Ms) for " 
                            + its + " generations was:"+(et-st));

            }
        } catch(Exception e) {
            System.err.println("Exception: "+e);
            e.printStackTrace();
        }
    }
    
    /**
     * Setup the algorithm. Read the properties. Create the modules(Operators) 
     * and operations
     * @param args arguments
     */
    public void setup(String[] args) {
        //Read properties
        this.readProperties(args);
        //set rng seed
        this.setSeed();
        /*
         * Operators and Operations
         * Example of setting up an algorithm. 
         * For specific details of operators and operations used see 
         * the respective source or API
         */        
        
        //Grammar
        GEGrammar grammar = getGEGrammar(this.properties);
        //Search engine
        MyFirstSearchEngine alg = new MyFirstSearchEngine();
        //Initialiser
        initialiser = getInitialiser(grammar, this.rng, this.properties);
        //Crossover
        CrossoverOperation singlePointCrossover = new SinglePointCrossover(this.rng, this.properties);
        CrossoverModule crossoverModule = new CrossoverModule(this.rng, singlePointCrossover);
        //Mutation
        MutationOperation mutation = getMutationOperation(this.rng, this.properties);
        MutationOperator mutationModule = new MutationOperator(this.rng, mutation);
        //Selection
        SelectionOperation selectionOperation = getSelectionOperation(this.properties, this.rng);
        SelectionScheme selectionScheme = new SelectionScheme(this.rng, selectionOperation);
        //Replacement
        ReplacementOperation replacementOperation = new ReplacementOperation(this.properties);
        JoinOperator replacementStrategy = this.getJoinOperator(this.properties, this.rng, selectionScheme.getPopulation(), replacementOperation);
        //Elite selection
        //EliteOperationSelection eliteSelectionOperation = new EliteOperationSelection(this.properties);
        //SelectionScheme eliteSelection = new SelectionScheme(this.rng, eliteSelectionOperation);
        //Elite replacement
        //EliteReplacementOperation eliteReplacementOperation = new EliteReplacementOperation(this.properties);
        //EliteReplacementOperator eliteReplacementStrategy = new EliteReplacementOperator(this.rng, eliteSelection.getPopulation(), eliteReplacementOperation);
        //Fitness function
        FitnessFunction fitnessFunction = getFitnessFunction(this.properties);
        FitnessEvaluationOperation fitnessEvaluationOperation = new FitnessEvaluationOperation(fitnessFunction);
        fitnessEvaluationOperation.setProperties(properties);
        FitnessEvaluator fitnessEvaluator = new FitnessEvaluator(this.rng, fitnessEvaluationOperation);
        //Statistics
        StatCatcher stats = new StatCatcher(Integer.parseInt(this.properties.getProperty("generations")));
        IndividualCatcher indCatch = new IndividualCatcher(this.properties);
        stats.addTime(startTime);//Set initialisation time for the statCatcher (Not completly accurate here)
        StatisticsCollectionOperation statsCollection = new StatisticsCollectionOperation(stats, indCatch, this.properties);
        super.collector = new Collector(statsCollection);
        
        /*
         * Init
         */
        //Pipeline
        Pipeline pipelineInit = new SimplePipeline();
        alg.setInitPipeline(pipelineInit);
        //FitnessEvaluator for the init pipeline
        FitnessEvaluator fitnessEvaluatorInit = new FitnessEvaluator(this.rng, fitnessEvaluationOperation);
        //Set population
        fitnessEvaluatorInit.setPopulation(initialiser.getPopulation());
        collector.setPopulation(initialiser.getPopulation());
        //Add modules to pipeline
        pipelineInit.addModule(initialiser);
        pipelineInit.addModule(fitnessEvaluatorInit);
        pipelineInit.addModule(collector);                  
        /*
         * Loop
         */
        //Pipeline
        Pipeline pipelineLoop = new SimplePipeline();
        alg.setLoopPipeline(pipelineLoop);
        //Set population passing
        selectionScheme.setPopulation(initialiser.getPopulation());
        replacementStrategy.setPopulation(initialiser.getPopulation());
        crossoverModule.setPopulation(selectionScheme.getPopulation());
        mutationModule.setPopulation(selectionScheme.getPopulation());
        fitnessEvaluator.setPopulation(selectionScheme.getPopulation());
        //eliteSelection.setPopulation(initialiser.getPopulation());
        //eliteReplacementStrategy.setPopulation(initialiser.getPopulation());
        collector.setPopulation(initialiser.getPopulation());
        //Add modules to pipeline
        //pipelineLoop.addModule(eliteSelection); //Remove elites
        pipelineLoop.addModule(selectionScheme);
        pipelineLoop.addModule(crossoverModule);
        pipelineLoop.addModule(mutationModule);
        pipelineLoop.addModule(fitnessEvaluator);
        pipelineLoop.addModule(replacementStrategy);
        //pipelineLoop.addModule(eliteReplacementStrategy); //Add elites
        pipelineLoop.addModule(collector);
        
        this.algorithm = alg;

    }

    /**
     * Sets the random number generator seed if it is specified
     */
    private void setSeed() {
        long seed;
        if(this.properties.getProperty(Constants.RNG_SEED)!=null) {
            seed = Long.parseLong(this.properties.getProperty(Constants.RNG_SEED));
            this.rng.setSeed(seed);
        }
    }
    
    
    /**
     * Run
     * @param args arguments
     */
    public static void main(String[] args) {
        
        Run mainRun = new Run();
        mainRun.experiment(args);
	System.exit(0);
    }
}
