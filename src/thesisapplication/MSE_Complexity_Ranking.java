/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Khabat
 */
public class MSE_Complexity_Ranking  {

    float numOfClusters=0;
    private int initialClusterCount;
    private int endClusterCount;

    public MSE_Complexity_Ranking(Properties p){
        getProperties(p);
        numOfClusters=initialClusterCount;
    }

    public void getProperties(Properties p){

        String initClustrts=Constants.INIT_RANKING_CLUSTERS;
        String endClusters=Constants.END_RANKING_CLUSTERS;

        int value;
        try{
            value = Integer.valueOf(p.getProperty(initClustrts));
            this.initialClusterCount = value;

            value = Integer.valueOf(p.getProperty(endClusters));
            this.endClusterCount = value;
        }catch(Exception e){
            this.initialClusterCount=0;
            this.endClusterCount=0;
        }
    }
    private static double averageFitness( List<Individual> operand){
        double ret=0;
        int i;
        for(i=0;i<operand.size()&&operand.get(i).isValid();i++){
            ret+=operand.get(i).getFitness().getDouble();
        }
        return ret/i;
    }
    public void rankBasedOnComplexity2( List<Individual> operand){
        //preconditin: operand must be sorted based on MSE (best to worst)
        if(endClusterCount==0)return;
        numOfClusters+=((endClusterCount-initialClusterCount+1.0f)/1000);

        double best=operand.get(0).getFitness().getDouble();
        //double fitnessAverage=averageFitness(operand);
        //obtain worst Individual
        int worst_valid_index = operand.size() - 1;//temporary is used as an index
        double worst;

        while (operand.get(worst_valid_index).isValid() == false
      //  /*from begining to avg*/ || operand.get(worst_valid_index).getFitness().getDouble()>fitnessAverage
                ) {
            worst_valid_index--;
        }
        worst = operand.get(worst_valid_index).getFitness().getDouble();

        double diff = worst - best;

        ArrayList<ArrayList<Individual>> lists = new ArrayList<ArrayList<Individual>>();

        int j = 0;
        for (int i = 0; i <((int) numOfClusters); i++) {
            double end = best + (diff / ((int)numOfClusters));
            lists.add(new ArrayList<Individual>());
            while (j < operand.size() && operand.get(j).getFitness().getDouble() <= end) {
                lists.get(i).add(operand.get(j++));
            }
            best += (diff / ( (int) numOfClusters));
        }

        ArrayList<Individual> temp_invalids = new ArrayList<Individual>();
        for ( ; j < operand.size(); j++) {
            temp_invalids.add(operand.get(j));
        }

        operand.clear();

        NeuronsGEIndividual.comparisonType=NeuronsGEIndividual.COMPARE_COMPLEXITY;//GIVE ATTENTION THAT RECOVER IT TO mse
        for (int i = 0; i <  ((int) numOfClusters); i++) {
            Collections.sort(lists.get(i));
            operand.addAll(lists.get(i));
        }
        NeuronsGEIndividual.comparisonType=NeuronsGEIndividual.COMPARE_MSE;
        operand.addAll(temp_invalids);
    }
    
    public void rankBasedOnComplexity( List<Individual> operand){
        //preconditin: operand must be sorted based on MSE (best to worst)
        if(endClusterCount==0)return;
        numOfClusters+=((endClusterCount-initialClusterCount+1.0f)/1000);

        double best=operand.get(0).getFitness().getDouble();
        //double fitnessAverage=averageFitness(operand);
        //obtain worst Individual
        int worst_valid_index = operand.size() - 1;//temporary is used as an index
        int best_index=0;
        double worst;

        while (operand.get(worst_valid_index).isValid() == false
      //  /*from begining to avg*/ || operand.get(worst_valid_index).getFitness().getDouble()>fitnessAverage
                ) {
            worst_valid_index--;
        }
        //worst = operand.get(worst_valid_index).getFitness().getDouble();

        //double diff = worst - best;

        ArrayList<ArrayList<Individual>> lists = new ArrayList<ArrayList<Individual>>();

        int cluster_length= ((worst_valid_index - best_index) / (int) numOfClusters);
        int j = 0;
        for (int i = 0; i <((int) numOfClusters); i++) {
            
            lists.add(new ArrayList<Individual>());
            
            while (j < operand.size() && j<=(i+1)*cluster_length) {
                lists.get(i).add(operand.get(j++));
            }
            
        }

        ArrayList<Individual> temp_invalids = new ArrayList<Individual>();
        for ( ; j < operand.size(); j++) {
            temp_invalids.add(operand.get(j));
        }

        operand.clear();

        NeuronsGEIndividual.comparisonType=NeuronsGEIndividual.COMPARE_COMPLEXITY;//GIVE ATTENTION THAT RECOVER IT TO mse
        for (int i = 0; i <  ((int) numOfClusters); i++) {
            Collections.sort(lists.get(i));
            operand.addAll(lists.get(i));
        }
        NeuronsGEIndividual.comparisonType=NeuronsGEIndividual.COMPARE_MSE;
        operand.addAll(temp_invalids);
    }

}
