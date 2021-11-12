/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Individuals.NeuronsGEIndividual;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.tools.data.ARFFHandler;

/**
 *
 * @author Khabat
 */
public class Controller {

    public static String args[]={"-pool_size","",// 0 , 1
                                "-neurons_count","",// 2, 3
                                "-train_set","",//4, 5
                                "-test_set","",//6, 7
                                "-class_index","",//8, 9
                                "-run","",//10, 11
                                "-fold","",//12, 13
                                "-property_file",""};//14, 15
    Dataset train,validation,test;
    public static Random rand=new Random();

    DataOutputStream stream=null;
    String propertiesPath="";

    public Controller(String outFile,String propertiesPath){
        try {
            this.stream = new DataOutputStream(new FileOutputStream(outFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.propertiesPath=propertiesPath;
    }

    public void run(String arffPath,int classIndex,int []pool,int neurons[],int runs,int folds,int trainPercent){
        args[9]=String.valueOf(classIndex);
        args[14]=this.propertiesPath;

        Dataset total=loadDataset(arffPath, classIndex);

        thesisapplication.Logger.logHeader(stream);
        
        for(int i=0;i<pool.length;i++){
            args[1]=String.valueOf(pool[i]);
            runParallel(neurons, runs, folds, trainPercent, total);
        }

    }
   
    
    public void runExperiment() {
        MyRun run = new MyRun(propertiesPath);
        run.experiment(args);

        NeuronsGEIndividual bestOnTrain = (NeuronsGEIndividual) run.getBestIndividualOntrain();
        NeuronsGEIndividual bestOnValid = (NeuronsGEIndividual) run.getBestIndividualOnValidation();
        log(stream, bestOnTrain, bestOnValid);
    }

    public void runParallel(int neurons[], int runs, int foldCount, int trainPercent, Dataset total) {

        for (int i = 0; i < 1; i++) {
        //for (int i = 0; i < runs; i++) {
            args[11] = String.valueOf(i + 1);//#run
            Dataset[] folds = total.folds(foldCount, rand);
            
            //for (int j = 0; j < foldCount; j++) {
            for (int j = 0; j < 1; j++) {
                args[13]=String.valueOf(j+1);

                Dataset temp = getTrainData(folds, j);
                seperateTrainAndValidation(temp, trainPercent);
                this.test = folds[j];

                //!!!
                this.train=total;// HACK X! it must be corrected


                //args[5] = myUtil.serializeToString(this.train);
                args[5] = myUtil.serializeToString(total);
                
                args[7] = myUtil.serializeToString( new DefaultDataset(this.test));

                for (int k = 0; k < neurons.length; k++) {
                    args[3] = String.valueOf(neurons[k]);
                    runExperiment();
                }
            }
        }
    }

    public static DefaultDataset getTrainData(Dataset[] totalData, int testFold) {
        DefaultDataset ret = new DefaultDataset();

        for (int i = 0; i < totalData.length; i++) {
            if (i != testFold) {
                ret.addAll(totalData[i]);
            }
        }
        return ret;
    }

    private void seperateTrainAndValidation(Dataset train, int trainPercent) {

        if (trainPercent % 10 != 0) {
            return;
        }

        this.train = new DefaultDataset();
        this.validation = new DefaultDataset();

        Dataset[] folds_data = train.folds(10, rand);

        for (int i = 0; i < 10; i++) {
            if (i < trainPercent/10) {
                this.train.addAll(folds_data[i]);
            } else {
                this.validation.addAll(folds_data[i]);
            }
        }
        return;
    }

    private void log(DataOutputStream stream,
         NeuronsGEIndividual bestOnTrainI, NeuronsGEIndividual bestOnValidationI) {
        ArrayList<String> bestOnTrain=null;
        ArrayList<String> bestOnValidation=null;

        if(bestOnTrainI!=null)
            bestOnTrain = bestOnTrainI.getPhenotypeStrings();

        if(bestOnValidationI!=null)
            bestOnValidation = bestOnValidationI.getPhenotypeStrings();

        double results[] = new double[10];
        MSE mse=new MSE();
        double trainMSE = mse.calculate(bestOnTrainI, train, true);
        results[0] = trainMSE;
        double validMSE = mse.calculate(bestOnTrainI, train, false);
        results[1] = validMSE;
        double testMSE = mse.calculate(bestOnTrainI,test, true);
        results[2] = testMSE;
        double testErr = mse.calculate(bestOnTrainI,test,  false);
        results[3] = testErr;

        trainMSE = mse.calculate(bestOnValidationI,train,  true);
        results[4] = trainMSE;
        validMSE = mse.calculate(bestOnValidationI,validation, true);
        results[5] = validMSE;
        testMSE = mse.calculate(bestOnValidationI,test, true);
        results[6] = testMSE;
        testErr = mse.calculate(bestOnValidationI,test,false);
        results[7] = testErr;

        results[8]=bestOnTrainI.get_neurons_count();
        results[9]=bestOnTrainI.get_connections_count();

        System.out.println("pool= " + args[1] + " run= " + args[11] + " fold= " + args[13] + " neurons= " + args[3] + "\n");
        System.out.format("%1$3.6f\t%2$3.6f\t%3$3.6f\t%4$3.6f\n%5$3.6f\t%6$3.6f\t%7$3.6f\t%8$3.6f\t%9$3.6f\t%9$3.6f\t",
                results[0] ,results[1],results[2],results[3],results[4],results[5],results[6],results[7],results[8],results[9]);

        thesisapplication.Logger.log(stream, args, results);
    }

    private Dataset loadDataset(String arffPath, int classIndex) {
        Dataset totalData = null;
        try {
            totalData = ARFFHandler.loadARFF(new File(arffPath), classIndex);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return totalData;
    }
}
