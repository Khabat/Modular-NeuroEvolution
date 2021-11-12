/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import FitnessEvaluation.NeuralNetworkEvaluator.FeedForwardNeuralNetwork;
import FitnessEvaluation.NeuralNetworkEvaluator.Neuron;
import Individuals.NeuronsGEIndividual;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;

/**
 *
 * @author Administrator
 */
public class myUtil {
    public static String serializeToString(Dataset d) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out=null ;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(d);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(thesisapplication.myUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<bos.toByteArray().length;i++){
            sb.append((char)bos.toByteArray()[i]);
        }
        return sb.toString();
    }

     public static Dataset deSerializeToString(String objectS){
        byte []arr=new byte[objectS.length()];
        for(int i=0;i<objectS.length();i++)
            arr[i]=(byte)objectS.charAt(i);
        ByteArrayInputStream bis = new ByteArrayInputStream(arr) ;
        ObjectInput in=null ;
        Dataset d=null;
        try {
            in = new ObjectInputStream(bis);
            d=(Dataset)in.readObject();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ThesisView.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(ClassNotFoundException exp){
             Logger.getLogger(ThesisView.class.getName()).log(Level.SEVERE, null, exp);
        }
        return d;
    }

     public static String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }


    public static ArrayList<String> RemoveWhiteSpaces(ArrayList<String> in){
        for (int i=0;i<in.size();i++){
            in.set(i, RemoveWhiteSpace(in.get(i)));
        }
        return in;
    }
//     public static boolean hasEqualNeuronTopology(NeuronsGEIndividual in1, NeuronsGEIndividual in2){
//         FeedForwardNeuralNetwork n1=new FeedForwardNeuralNetwork(RemoveWhiteSpaces(in1.getPhenotypeStrings()), 60);
//         FeedForwardNeuralNetwork n2=new FeedForwardNeuralNetwork(RemoveWhiteSpaces(in2.getPhenotypeStrings()), 60);
//
//         for(int i=0;i<n1.getNeurons().length;i++)
//         {
//             for(int j=i+1;j<n2.getNeurons().length;j++){
//                 if(n1.getNeurons()[i].isEqual(n2.getNeurons()[j]))
//                     return true;
//             }
//         }
//         return false;
//     }

}
