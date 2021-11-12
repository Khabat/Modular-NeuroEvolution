/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thesisapplication;

import Individuals.NeuronsGEIndividual;
import Util.Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
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
public class Controller_Train_Test {

    public static String args[] = {"-pool_size", "",// 0 , 1
        "-neurons_count", "",// 2, 3
        "-train_set", "",//4, 5
        "-test_set", "",//6, 7
        //"-class_index","",//8, 9
        "-run", "",//8, 9
        "-fold", "",//10, 11
        "-log_file", ""};//12, 13
    //Dataset train,validation,test;
    public static Random rand = new Random();
    DataOutputStream stream = null;
    static DataOutputStream summaryStream = null;
    String propertiesPath = "";
    ArrayList<String> train_datasets = new ArrayList<String>();
    ArrayList<String> test_datasets = new ArrayList<String>();
    ArrayList<double[]> results4Log = new ArrayList<double[]>();//it is of only one experiment
    public static ArrayList<double[]> summaryResults4Log = new ArrayList<double[]>();//it is summary of all experiments
    int runs = 5;// it is equal to the number of datasets
    private int classIndex;
    private Properties properties;
    //Thread threads[]=null;
    public Controller_Train_Test(String summaryFile, String outFile, String propertiesPath) {
        try {
            args[13] = outFile.substring(outFile.lastIndexOf("\\"));
            this.summaryStream  = new DataOutputStream(new FileOutputStream(summaryFile));
            this.stream         = new DataOutputStream(new FileOutputStream(outFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller_Train_Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.propertiesPath = propertiesPath;

        //in following 3 instructions the properties is created and classIndex is extracted from it
        readProperties();
        setProperties(this.properties);
    }

    public void run(String arffPathDirectory, int[] pool, int neurons[], int runs, int folds, int trainPercent) {
        //args[9]=String.valueOf(classIndex);
        //args[14]=this.propertiesPath;

        loadMultipleDataset(arffPathDirectory, classIndex, runs);

        thesisapplication.Logger.logHeader(stream);

        for (int i = 0; i < pool.length; i++) {
            args[1] = String.valueOf(pool[i]);

            run(neurons, runs, folds);
        }

    }

    final static Object monitor=new Integer(0);
    class Runner implements Runnable {

        String[] args;
        String propertiesPath;
        MyRun run;
        Dataset train, validation, test;

        public void run() {
            run = new MyRun(propertiesPath);
            run.experiment(args);
            synchronized(monitor){
                log();
            }
        }

        public Runner(String[] args, String prop, Dataset train, Dataset test) {
            this.args = args;
            this.propertiesPath = prop;
            this.train = train;
            this.test = test;
        }

        public void log() {
            NeuronsGEIndividual bestOnMSE = (NeuronsGEIndividual) run.getBestIndividualOntrain();
            bestOnMSE.map(0);
            NeuronsGEIndividual bestOnMSE_Complexity = (NeuronsGEIndividual) run.getBestIndividualOnMSE_Complexity();
            bestOnMSE_Complexity.map(0);
            log(stream, bestOnMSE, bestOnMSE_Complexity);
        }

        private void log(DataOutputStream stream,
                NeuronsGEIndividual bestOnMSE, NeuronsGEIndividual bestOnMSE_Complexity) {

            double results[] = new double[14];
            //MSE mse= new MSE();
            MSEMultiClass mse = new MSEMultiClass();
            double trainMSE = mse.calculate(bestOnMSE, train, true);
            results[0] = trainMSE;
            double validMSE = mse.calculate(bestOnMSE, train, false);
            results[1] = validMSE;
            double testMSE = mse.calculate(bestOnMSE, test, true);
            results[2] = testMSE;
            double testErr = mse.calculate(bestOnMSE, test, false);
            results[3] = testErr;

            trainMSE = mse.calculate(bestOnMSE_Complexity, train, true);
            results[4] = trainMSE;
            validMSE = mse.calculate(bestOnMSE_Complexity, validation, true);
            results[5] = validMSE;
            testMSE = mse.calculate(bestOnMSE_Complexity, test, true);
            results[6] = testMSE;
            testErr = mse.calculate(bestOnMSE_Complexity, test, false);
            results[7] = testErr;

            results[8] = bestOnMSE.get_neurons_count();
            results[9] = bestOnMSE.get_connections_count();
            results[10] = bestOnMSE.get_features_count();
            results[11] = bestOnMSE_Complexity.get_neurons_count();
            results[12] = bestOnMSE_Complexity.get_connections_count();
            results[13] = bestOnMSE_Complexity.get_features_count();

            System.out.println("pool= " + args[1] + " run= " + args[9] + " fold= " + args[11] + " neurons= " + args[3] + "\n");
            System.out.format("%1$3.6f\t%2$3.6f\t%3$3.6f\t%4$3.6f\n%5$3.6f\t%6$3.6f\t%7$3.6f\t%8$3.6f\n%9$3.6f\t%10$3.6f\t%11$3.6f\t%12$3.6f\t%13$3.6f\t%14$3.6f\n",
                    results[0], results[1], results[2], results[3], results[4], results[5], results[6], results[7], results[8], results[9], results[10], results[11], results[12], results[13]);

            thesisapplication.Logger.log(stream, args, results);
            results4Log.add(results);
        }
    }

    public void runExperiment(int runIndex,String[] args, Dataset train, Dataset test) {
        //threads[runIndex] = new Thread(new Runner(args, propertiesPath, train, test));
        new Runner(args, propertiesPath, train, test).run();
        //threads[runIndex].start();
//        try {
//            threads[runIndex].join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Controller_Train_Test.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    public void run(int neurons[], int runs, int foldCount) {
        
        //threads = new Thread[runs];

        for (int h = 0; h < 30; h++) {

            String[] args2 = new String[args.length];
            System.arraycopy(args, 0, args2, 0, args.length);
            //
            Dataset train = null, test = null;
            args2[9] = String.valueOf(h + 1);
            args2[11] = String.valueOf(h + 1);

            String testFile = test_datasets.get(h);
            String trainFile = train_datasets.get(h);

            args2[5] = trainFile;
            args2[7] = testFile;

            try {
                train = net.sf.javaml.tools.data.FileHandler.loadDataset(new File(train_datasets.get(h)), classIndex);
                test = net.sf.javaml.tools.data.FileHandler.loadDataset(new File(test_datasets.get(h)), classIndex);
            } catch (IOException ex) {
                Logger.getLogger(Controller_Train_Test.class.getName()).log(Level.SEVERE, null, ex);
            }

            args2[3] = String.valueOf(neurons[0]);
            //--
            runExperiment(h, args2, train, test);
        }       

        double[] avg = logAvgs(stream, results4Log);
        summaryResults4Log.add(avg);
    }
    private void joinOnThreads(int startIndex, int endIndex){
//        while(!areAlive(startIndex, endIndex));
//        for (;startIndex<=endIndex;startIndex++) {
//            Thread trd=threads[startIndex];
//            if (trd!=null){
//                try {
//                    trd.join();
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Controller_Train_Test.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//        }
    }
    private boolean areAlive(int startIndex, int endIndex){
       
//       for (;startIndex<=endIndex;startIndex++) {
//            Thread trd=threads[startIndex];
//            if(trd==null || !trd.isAlive()) return false;
//       }
       return true;

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

    public static double[] logAvgs(DataOutputStream stream, ArrayList<double[]> results) {
        double avg[] = new double[results.get(0).length];
        for (Iterator<double[]> it = results.iterator(); it.hasNext();) {
            double[] ds = it.next();
            for (int i = 0; i < ds.length; i++) {
                avg[i] += ds[i];
            }
        }

        for (int i = 0; i < avg.length; i++) {
            avg[i] /= results.size();
        }

        thesisapplication.Logger.log(stream, args, avg);
        return avg;
    }

    public static void logSummary() {

        thesisapplication.Logger.logHeader(summaryStream);
        for (Iterator<double[]> it = summaryResults4Log.iterator(); it.hasNext();) {
            double[] d = it.next();
            thesisapplication.Logger.log(summaryStream, args, d);
        }
        double[] avg = logAvgs(summaryStream, summaryResults4Log);
        try {
            summaryStream.flush();
            summaryStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Controller_Train_Test.class.getName()).log(Level.SEVERE, null, ex);
        }

        summaryResults4Log.add(avg);
    }

    private void loadMultipleDataset(String arffPathDirectory, int classIndex, int datasetCount) {
        train_datasets.clear();
        test_datasets.clear();
        //Dataset data = null;


        for (int i = 0; i < datasetCount; i++) {

            train_datasets.add(new File(arffPathDirectory + "\\train" + (i + 1) + ".arff").toURI().getPath());

            test_datasets.add(new File(arffPathDirectory + "\\test" + (i + 1) + ".arff").toURI().getPath());
        }
    }

    public void setProperties(Properties p) {

        String key = Constants.CLASS_INDEX;
        int value = 0;

        value = Integer.valueOf(p.getProperty(key));


        this.classIndex = value;
    }

    protected void readProperties() {
        ClassLoader loader;
        InputStream in;
        try {
            this.properties = new Properties();
            File f = new File(this.propertiesPath);
            if (!f.exists()) { // try classloading
                loader = ClassLoader.getSystemClassLoader();
                in = loader.getResourceAsStream(this.propertiesPath);
                this.properties.load(in);
                System.out.println("Loading properties from ClassLoader and: " + this.propertiesPath);
            } else {
                FileInputStream is = new FileInputStream(f);
                this.properties.load(is);
                System.out.println("Loading properties from file system: " + this.propertiesPath);
            }


        } catch (IOException e) {
            loader = ClassLoader.getSystemClassLoader();
            in = loader.getResourceAsStream(this.propertiesPath);
            try {
                this.properties.load(in);
            } catch (Exception ex) {
                System.err.println("Properties reading output caught:" + ex);
            }

        } catch (Exception e) {
            System.err.println("Could not commandline argument:" + e + " properties path:" + this.propertiesPath);
        }
    }
}
