/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Main.Run;
import Algorithm.MyFirstSearchEngine;
import Algorithm.Pipeline;
import Algorithm.SimplePipeline;
import Exceptions.BadParameterException;
import FitnessEvaluation.FitnessFunction;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Individuals.Populations.Population;
import Mapper.GEGrammar;
import Operator.*;
import Operator.Operations.*;
import Util.Constants;
import Util.Random.RandomNumberGenerator;
import Util.Statistics.IndividualCatcher;
import Util.Statistics.StatCatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 *
 * @author Administrator
 */
public class MyRun extends Run {

    StatCatcher st;
    SelectionScheme ssc_temp;
    MSE_Complexity_Ranking sorter;

    
    private int threads;

    public MyRun(String propertiesFilePath){
        super();
        super.propertiesFilePath = propertiesFilePath;
        

    }
    public Individual getBestIndividualOntrain() {
        return this.collector.getBestIndividual();
    }

    public Individual getBestIndividualOnMSE_Complexity() {

        collector.getPopulation().sort();
        List<Individual> operands=collector.getPopulation().getAll();
        sorter.rankBasedOnComplexity(operands);
        return operands.get(0);
    }
    

    public Individual getBestIndividualOnValidation() {
        return ((MyStateCatcher) this.st).getBestIndividualOnValidation();
    }
    public ArrayList<String> getBestPhenotype(){
        ArrayList<String> t=((NeuronsGEIndividual)this.collector.getBestIndividual()).getPhenotypeStrings();
        return t;
    }
    public Population getPopulation(){
        return this.collector.getPopulation();
    }
    
    @Override
    public void setup(String[] args) {
        //Read properties
        this.readProperties(args);
        getProperties(properties);

        //set rng seed
        setSeed();
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

        FitnessFunction fitnessFunction[]=new FitnessFunction[threads];
        for(int kk=0;kk<threads;kk++){
            fitnessFunction[kk] = getFitnessFunction(this.properties);
        }

        FitnessEvaluationOperation fitnessEvaluationOperation = new MyFitnessEvaluationOperation(fitnessFunction,null);
        fitnessEvaluationOperation.setProperties(properties);
        FitnessEvaluator fitnessEvaluator = new FitnessEvaluator(this.rng, fitnessEvaluationOperation);

        StatCatcher stats = new MyStateCatcher(properties,Integer.parseInt(this.properties.getProperty("generations")), (MyFitnessEvaluationOperation) fitnessEvaluationOperation);

        this.st=stats;

        CrossoverOperation singlePointCrossover = getCrossoverOperation(rng, properties);
        CrossoverModule crossoverModule = new MyCrossoverModule(this.rng, singlePointCrossover,this.st);
        //Mutation
        MutationOperation mutation = getMutationOperation(this.rng, this.properties);
        MutationOperator mutationModule = new MyMutationOperator(this.rng, mutation,this.st);
        //Selection
        SelectionOperation selectionOperation = getSelectionOperation(this.properties, this.rng);
         //Statistics
                
        ReplacementOperation replacementOperation = new RoulleteWheelReplaceOperation(this.rng,this.properties);

        

        SelectionScheme selectionScheme = new MultiObjectiveSelectionScheme(this.rng, selectionOperation,this.st);

        JoinOperator replacementStrategy = this.getJoinOperator(this.properties, this.rng, selectionScheme.getPopulation(), replacementOperation);
        ((ReplacementStrategy) replacementStrategy).stats=(MyStateCatcher) this.st;
   

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
        this.ssc_temp=selectionScheme;
        //the complexity vased soretr for ranking
        sorter=new MSE_Complexity_Ranking(properties);
        ((MultiObjectiveSelectionScheme)selectionScheme).setSorter(sorter);
        ((RoulleteWheelReplaceOperation)replacementOperation).setSorter(sorter);

        //
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
        //pipelineLoop.addModule(fitnessEvaluator);///!!!!FIX ME Khabat
        pipelineLoop.addModule(mutationModule);
        pipelineLoop.addModule(fitnessEvaluator);
        pipelineLoop.addModule(replacementStrategy);

        //pipelineLoop.addModule(eliteReplacementStrategy); //Add elites
        pipelineLoop.addModule(collector);

        this.algorithm = alg;
    }
    @Override
     protected Initialiser getInitialiser(GEGrammar g, RandomNumberGenerator rng, Properties p) {
        String className;
        String key = Constants.INITIALISER;
        try {
            className = p.getProperty(key);
            if (className == null) {
                throw new BadParameterException(key);
            }
            Class clazz = Class.forName(className);
            initialiser = (Initialiser) clazz.newInstance();
            // For RampedFullGrowInitialiser
            if (clazz.getName().equals(RampedFullGrowInitialiser.class.getName())) {
                CreationOperation fullInitialiser = new FullInitialiser(rng, g, p);
                CreationOperation growInitialiser = new GrowInitialiser(rng, g, p);
                ArrayList<CreationOperation> opL = new ArrayList<CreationOperation>();
                opL.add(fullInitialiser);
                opL.add(growInitialiser);
                ((RampedFullGrowInitialiser) initialiser).setOperations(opL);
            } else {
                // The default initialiser
                CreationOperation randomInitialiser;
                randomInitialiser = new MyRandomInitialiser(rng, g, p);//JUST this Line
                initialiser.setOperation(randomInitialiser);
            }
            initialiser.setProperties(p);
            initialiser.setRNG(rng);
            initialiser.init();
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ".getInitialiser(.) Exception: " + e);
            e.printStackTrace();
        }
        return initialiser;
    }
    private void setSeed() {
        long seed;
        if(this.properties.getProperty(Constants.RNG_SEED)!=null) {
            seed = Long.parseLong(this.properties.getProperty(Constants.RNG_SEED));
            this.rng.setSeed(seed);
        }
    }

    public void getProperties (Properties p) {

        String value;
        threads=0;
        String key;
        try {
            key = Constants.Num_Threads;
            value = p.getProperty(key);
            if (value != null) {
                threads=Integer.valueOf(value);
            }
        } catch (Exception e) {
            //System.out.println(e + " using default: " );
        }

    }
  

}
