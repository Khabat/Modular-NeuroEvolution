package thesisapplication;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Operator.Operations.ReplacementOperation;
import Util.Constants;
import Util.Random.RandomNumberGenerator;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author sky
 */
public class RoulleteWheelReplaceOperation extends ReplacementOperation {

    //RankbasedRouletteWheel roulletewheel;
    MyPropotionatRouletteWheel roulletewheel;
    int elit_size;

    MSE_Complexity_Ranking sorter;
    public RoulleteWheelReplaceOperation(int size,int eliteSize,double selectPreasure, RandomNumberGenerator rng) {
        super(size);
        //roulletewheel = new RankbasedRouletteWheel(size-eliteSize, rng,selectPreasure);
        roulletewheel = new MyPropotionatRouletteWheel(size-eliteSize, rng);
        this.elit_size=eliteSize;
    }

    /** Creates a new instance of ReplacementOperation
     * @param p properties
     */
    public RoulleteWheelReplaceOperation(RandomNumberGenerator rng, Properties p) {
        super(p);
        getProperties(p);
        //roulletewheel = new RankbasedRouletteWheel();
        roulletewheel=new MyPropotionatRouletteWheel();
        roulletewheel.setRNG(rng);
        roulletewheel.setProperties(p);
        roulletewheel.setSize(replacementSize-elit_size);
    }

    public void setSorter(MSE_Complexity_Ranking r){
        this.sorter=r;
    }

    @Override
    public void doOperation(List<Individual> operand, int size) {
        //pass the elits to the next generation and remove them from selection pool
        Collections.sort(operand);


        ArrayList<Individual> elit=new ArrayList<Individual>();

        sorter.rankBasedOnComplexity(operand);//after ranking based on MSE we rank it based on net's complexity

        for(int i=0;i<elit_size;i++){
            elit.add(operand.get(i).clone());
        }

        //end here
        roulletewheel.doOperation(operand);
        operand.clear();
        operand.addAll(elit);

        operand.addAll(roulletewheel.getSelectedPopulation().getAll());
        Collections.sort(operand);



        
        
        

        

        
    }
    @Override
    public void doOperation(List<Individual> operand){
        doOperation(operand, this.replacementSize);
    }


    private void getProperties(Properties p) {
        String key=Constants.ELITE_SIZE;

        int value;
        try{
        value=Integer.valueOf(p.getProperty(key));
        }catch(Exception e){
            value=0;
        }
        
        this.elit_size=value;
    }


    

}
