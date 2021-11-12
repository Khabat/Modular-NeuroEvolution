///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package thesisapplication;
//
//import Operator.Operations.ReplacementOperation;
//import Util.Random.RandomNumberGenerator;
//import java.util.Properties;
//
///**
// *
// * @author KHS
// */
//public class TournamentReplacement extends ReplacementOperation{
////    MyTournamentSelection sus;
////    int elit_size;
////    int initialClusterCount, endClusterCount;
////    MSE_Complexity_Ranking sorter;
////    public TournamentReplacement(int size,int eliteSize,int tourSize, RandomNumberGenerator rng) {
////        super(size);
////        sus = new MyTournamentSelection(size-eliteSize,tourSize ,rng);
////        this.elit_size=eliteSize;
////    }
////
////    /** Creates a new instance of ReplacementOperation
////     * @param p properties
////     */
////    public TournamentReplacement(RandomNumberGenerator rng, Properties p) {
////        super(p);
////        getProperties(p);
////        sus = new MyTournamentSelection();
////        sus.setRNG(rng);
////        sus.setProperties(p);
////
////        sus.setSize(replacementSize-elit_size);
////    }
////    public void setSorter(MSE_Complexity_Ranking r){
////        this.sorter=r;
////    }
////
////    @Override
////    public void doOperation(List<Individual> operand, int size) {
////        //pass the elits to the next generation and remove them from selection pool
////
////        Collections.sort(operand);
////
////        ArrayList<Individual> elit=new ArrayList<Individual>();
////
////        //sorter.rankBasedOnComplexity(operand);//after ranking based on MSE we rank it based on net's complexity
////
////        for(int i=0;i<elit_size;i++){
////            elit.add(operand.get(i).clone());
////        }
////
////        //end here
////        sus.doOperation(operand);
////        operand.clear();
////        operand.addAll(elit);
////
////        operand.addAll(sus.getSelectedPopulation().getAll());
////    }
////    @Override
////    public void doOperation(List<Individual> operand){
////        doOperation(operand, this.replacementSize);
////    }
////
////
////    private void getProperties(Properties p) {
////        String elitSize=Constants.ELITE_SIZE;
////        int value;
////        try{
////            value = Integer.valueOf(p.getProperty(elitSize));
////        }catch(Exception e){
////            value=0;
////        }
////        elit_size=value;
////    }
//
//}
