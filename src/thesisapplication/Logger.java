/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;

/**
 *@author Khabat
 */
public class Logger {

    public static void logHeader(DataOutputStream stream){
        String str="pool_size\trun\tfold\tneur\ttarin mse\tvalid_mse\ttest_mse\ttest_err\ttrain_mse2\tvalid_mse2\ttest_mse2\ttest_error2\tneurons1\tconns1\tfeatures1\tneurons2\tconns2\tfeatures2\n";
        try {
            stream.writeBytes(str);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void log(DataOutputStream stream, String args[], double[] results){
        StringBuilder sb=new StringBuilder();

        DecimalFormat dc=new DecimalFormat("###.####");

//        sb.append(Integer.valueOf(args[1]).doubleValue()+"\t");// poolsize
//        sb.append(Integer.valueOf(args[9]).doubleValue()+"\t");// run number
//        sb.append(Integer.valueOf(args[11]).doubleValue()+"\t");// fold number
//        sb.append(Integer.valueOf(args[3]).doubleValue()+"\t");// neuron count

        //result on best of training
        sb.append(dc.format(Double.valueOf(results[0]).doubleValue())+"\t");// train mse
        sb.append(dc.format(Double.valueOf(results[1]).doubleValue())+"\t");// valdation mse
        sb.append(dc.format(Double.valueOf(results[2]).doubleValue())+"\t");// test mse
        sb.append(dc.format(Double.valueOf(results[3]).doubleValue())+"\t");// test error

        //result on best of validation
        sb.append(dc.format(Double.valueOf(results[4]).doubleValue())+"\t");// train mse
        sb.append(dc.format(Double.valueOf(results[5]).doubleValue())+"\t");// valdation mse
        sb.append(dc.format(Double.valueOf(results[6]).doubleValue())+"\t");// test mse
        sb.append(dc.format(Double.valueOf(results[7]).doubleValue())+"\t");// test error

        sb.append(dc.format(Double.valueOf(results[8]).doubleValue())+"\t");// neuron count
        sb.append(dc.format(Double.valueOf(results[9]).doubleValue())+"\t");// connection count
        sb.append(dc.format(Double.valueOf(results[10]).doubleValue())+"\t");// features count

        sb.append(dc.format(Double.valueOf(results[11]).doubleValue())+"\t");// neuron count
        sb.append(dc.format(Double.valueOf(results[12]).doubleValue())+"\t");// connection count
        sb.append(dc.format(Double.valueOf(results[13]).doubleValue())+"\n");// features count

        try {
            stream.writeBytes(sb.toString());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void log(DataOutputStream stream, String []args,NeuronsGEChromosome chrom_on_train,NeuronsGEChromosome chrom_on_validation){
        StringBuilder sb=new StringBuilder();


        sb.append(Integer.valueOf(args[1]).doubleValue()+"\t");// poolsize
        sb.append(Integer.valueOf(args[9]).doubleValue()+"\t");// run number
        sb.append(Integer.valueOf(args[11]).doubleValue()+"\t");// fold number
        sb.append(Integer.valueOf(args[3]).doubleValue()+"\n");// neuron count

        try {
            stream.writeBytes(sb.toString());
            for(int i=0;i<chrom_on_train.size();i++){
                stream.writeBytes(String.valueOf(chrom_on_train.get(i))+",");
            }
            stream.writeBytes(";");
            for(int i=0;i<chrom_on_validation.size();i++){
                stream.writeBytes(String.valueOf(chrom_on_validation.get(i))+",");
            }
            stream.writeBytes(";\n");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
