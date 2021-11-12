/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.FitnessPackage.BasicFitness;
import Individuals.FitnessPackage.Fitness;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Individuals.Populations.Population;
import Individuals.Populations.SimplePopulation;
import Mapper.GEGrammar;
import Util.Constants;
import Util.Statistics.StatCatcher;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author khs
 */
public class MyStateCatcher extends StatCatcher{
    protected ArrayList<Double> bestValidationFitness;
    protected ArrayList<Double> meanValidationFitness;

    protected ArrayList<Double> neurons_count;
    protected ArrayList<Double> connections_count;
    protected ArrayList<Double> inputFeatures_count;
    public ArrayList<Double> beforeParentSelecion;
    public ArrayList<Double> beforeParentSelecionSTD;
    public ArrayList<Double> afterParentSelecion;
    public ArrayList<Double> afterParentSelecionSTD;
    public ArrayList<Double> afterXover;
    public ArrayList<Double> afterXoverSTD;
    public ArrayList<Double> afterMutation;
    public ArrayList<Double> afterMutationSTD;
    public ArrayList<Double> afterReplacement;
    public ArrayList<Double> afterReplacementSTD;

    public ArrayList<Integer> competingConvention;

    public ArrayList<Double> weightsSum;

    protected ArrayList<Double>     penalty_coefs;
    MyFitnessEvaluationOperation fitnessEvaluation;
    
    //int max_validation_exceed;
    int no_of_validation_exceeded;
    double minValue_of_validation;

    Individual bestInd_OnValidation;
    public MyStateCatcher(Properties p){
        super();
        getProperties(p);
        minValue_of_validation=Double.MAX_VALUE;
        no_of_validation_exceeded=0;
        bestValidationFitness=new ArrayList<Double>();
        meanValidationFitness=new ArrayList<Double>();

        neurons_count=new ArrayList<Double>();
        connections_count=new ArrayList<Double>();
        inputFeatures_count=new ArrayList<Double>();
        penalty_coefs=new ArrayList<Double>();
        beforeParentSelecion=new ArrayList<Double>();
        beforeParentSelecionSTD=new ArrayList<Double>();
        afterParentSelecion=new ArrayList<Double>();
        afterParentSelecionSTD=new ArrayList<Double>();
        afterXover=new ArrayList<Double>();
        afterXoverSTD=new ArrayList<Double>();
        afterMutation=new ArrayList<Double>();
        afterMutationSTD=new ArrayList<Double>();
        afterReplacement=new ArrayList<Double>();
        afterReplacementSTD=new ArrayList<Double>();
        competingConvention=new ArrayList<Integer>();
        weightsSum=new ArrayList<Double>();
    }
    public MyStateCatcher(Properties p,int gen, MyFitnessEvaluationOperation fitnessEvaluation){
        super(gen);
        getProperties(p);
        minValue_of_validation=Double.MAX_VALUE;
        no_of_validation_exceeded=0;
        bestValidationFitness=new ArrayList<Double>();
        meanValidationFitness=new ArrayList<Double>();

        neurons_count=new ArrayList<Double>();
        connections_count=new ArrayList<Double>();
        inputFeatures_count=new ArrayList<Double>();
        penalty_coefs=new ArrayList<Double>();
        beforeParentSelecion=new ArrayList<Double>();
        beforeParentSelecionSTD=new ArrayList<Double>();
        afterParentSelecion=new ArrayList<Double>();
        afterParentSelecionSTD=new ArrayList<Double>();
        afterXover=new ArrayList<Double>();
        afterXoverSTD=new ArrayList<Double>();
        afterMutation=new ArrayList<Double>();
        afterMutationSTD=new ArrayList<Double>();
        afterReplacement=new ArrayList<Double>();
        afterReplacementSTD=new ArrayList<Double>();
        competingConvention=new ArrayList<Integer>();
        weightsSum=new ArrayList<Double>();
        this.fitnessEvaluation=fitnessEvaluation;
    }

    protected void getProperties(Properties p){
//        String key=Constants.MAX_VALIDATION_EXCEED;
//        String value="";

        //value=p.getProperty(key);
        //max_validation_exceed=Integer.valueOf(value);
    }
    private double calculateSumWeights(Population pop){
        double sum=0;

        int i=0;
        for (Individual ind : pop.getAll()) {
            ind.map(0);
            sum+=((NeuronsGEIndividual)ind).get_Weights_Avg();
        }
        return sum/pop.size();
    }
    private double calculateAvg(Fitness[] popFitness){
        double sum = 0, temp, avg;
        int n=0;
        for (Fitness fitness : popFitness) {
            temp = fitness.getDouble();
            if (!Double.isNaN(temp) && !Double.isInfinite(temp) && temp != BasicFitness.DEFAULT_FITNESS) {
                n++;
                sum += temp;
            }
        }
        avg=sum/n;
        return avg;
    }
    private double calculateSTD(Fitness[] popFitness, double avg){
        double temp=0,std=0;
        int n=0;
        for (Fitness fitness : popFitness) {
            temp = fitness.getDouble();
            if (!Double.isNaN(temp) && !Double.isInfinite(temp) && temp != BasicFitness.DEFAULT_FITNESS) {
                n++;
                std+=(temp-avg)*(temp-avg);

            }
        }
        std=std/n;
        return std;
    }
    private Fitness[] extractFitness(Population pop){
        Fitness fit[]=new Fitness[pop.size()];
        int i=0;
        for (Individual ind : pop.getAll()) {
            ind.map(0);
            fit[i++]=ind.getFitness();
        }
        return fit;
    }

    
    public void set_beforeParentSelection(Population pop){
//        Fitness[] popFitness=extractFitness(pop);
//        double avg=calculateAvg(popFitness);
//        double std=calculateSTD(popFitness, avg);
//        beforeParentSelecion.add(avg);
//        beforeParentSelecionSTD.add(std);
    }
    public void set_afterParentSelection(Population pop){
//        Fitness[] popFitness=extractFitness(pop);
//        double avg=calculateAvg(popFitness);
//        double std=calculateSTD(popFitness, avg);
//        afterParentSelecion.add(avg);
//        afterParentSelecionSTD.add(std);
    }
    public void set_afterXover(Population pop){
//        Fitness[] popFitness=extractFitness(pop);
//        double avg=calculateAvg(popFitness);
//        double std=calculateSTD(popFitness, avg);
//        afterXover.add(avg);
//        afterXoverSTD.add(std);
    }
    public void set_afterMutation(Population pop){
//        Fitness[] popFitness=extractFitness(pop);
//        double avg=calculateAvg(popFitness);
//        double std=calculateSTD(popFitness, avg);
//        afterMutation.add(avg);
//        afterMutationSTD.add(std);
    }
    public void set_afterReplacement(Population pop){
//        Fitness[] popFitness=extractFitness(pop);
//        double avg=calculateAvg(popFitness);
//        double std=calculateSTD(popFitness, avg);
//        afterReplacement.add(avg);
//        afterReplacementSTD.add(std);
    }

    public void set_competingConvention(int competingConvention){
        this.competingConvention.add(competingConvention);
    }
    
    @Override
    public void addStats(Fitness[] popFitness){
        super.addStats(popFitness);
        NeuronsGEIndividual bestOfGnr= (NeuronsGEIndividual) super.getBestIndividualOfGeneration();

        double neurons_count2=0;
        double connection_count=0;
        double inFeatures_count=0;


        double best=bestOfGnr.validation;
        bestValidationFitness.add(new Double(best));


        double sum = 0;
        double temp;
        double sumOfWeights=0;
        for (Fitness fitness : popFitness) {
            temp = fitness.getDouble();
            if (!Double.isNaN(temp) && !Double.isInfinite(temp) && temp != BasicFitness.DEFAULT_FITNESS) {

                NeuronsGEIndividual i = (NeuronsGEIndividual) fitness.getIndividual();
                sum += i.validation;
                sumOfWeights+=i.get_Weights_Sum();
                neurons_count2+=i.get_neurons_count();
                connection_count+=i.get_connections_count();
                inFeatures_count+=i.get_features_count();
                if (i.validation < this.minValue_of_validation) {
                    this.minValue_of_validation = i.validation;
                    this.bestInd_OnValidation = i.clone();
                }
            }
        }

        neurons_count2=neurons_count2/popFitness.length;
        connection_count=connection_count/popFitness.length;
        inFeatures_count=inFeatures_count/popFitness.length;
        sumOfWeights =sumOfWeights / popFitness.length;
        this.connections_count.add(connection_count);
        this.neurons_count.add(neurons_count2);
        this.inputFeatures_count.add(inFeatures_count);

        penalty_coefs.add(fitnessEvaluation.penalty_coef);
        
        meanValidationFitness.add(new Double(sum/popFitness.length));
        weightsSum.add(sumOfWeights);
    }
    /*
     * this returns true if validation has beed exceeded max_validation_exceed times
     */

     public ArrayList<Double> getPenaltyCoefs(){
        return this.penalty_coefs;
    }
    public boolean hasValidationExceeded(){
       //if(no_of_validation_exceeded>=max_validation_exceed)return true;
        return false;
    }
    public ArrayList<Double> getBestValidationFitness(){
        return bestValidationFitness;
    }

    public ArrayList<Double> getLastMeanValidation(){
        return meanValidationFitness;
    }

    public Individual getBestIndividualOnValidation(){
        return this.bestInd_OnValidation;
    }

    public void addMeanUsedGenes(Fitness[] mG) {
        int total = 0;
        int temp;
        double n = 0;
        //Calc best and mean
//        for (Fitness aMG : mG) {
//            if (aMG.getIndividual().isValid()) {
//                temp = ((NeuronsGEIndividual) (aMG.getIndividual())).getUsedCodons();
//                total += temp;
//                n++;
//            }
//        }
        double mean = total/n;
        this.meanUsedGenes.add(mean);
    }

    public void addMeanDerivationTreeDepth(Fitness[] mG) {
        int total = 0;
        int temp;
        double n = 0;
	//	ArrayList<Integer> ali = new ArrayList<Integer>(mG.length);
        //Calc best and mean
        for (Fitness aMG : mG) {
            if (aMG.getIndividual().isValid()) {
                NeuronsGEIndividual ind = (NeuronsGEIndividual)aMG.getIndividual();
		if (ind.getMapper() == null) {
		    // FIXME this hack allows NGram to run even though inds have no mapper
		    temp = 0;
		} else {
		    temp = ((GEGrammar)ind.getMapper()).getMaxCurrentTreeDepth();
		    //		    ali.add(temp);
		}
                total += temp;
                n++;
            }
        }
        double mean = total/n;
        this.meanDerivationTreeDepth.add(mean);
    }

    public ArrayList<Double> getNeuronsCount(){
        return this.neurons_count;
    }

    public ArrayList<Double> getConnectionsCount(){
        return this.connections_count;
    }
    public ArrayList<Double> getFeaturesCount(){
        return this.inputFeatures_count;
    }

 public ArrayList<Double> getWeightSum() {
        return weightsSum;
    }
}


