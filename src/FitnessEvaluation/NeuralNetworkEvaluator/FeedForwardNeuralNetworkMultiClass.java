/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator;

import java.util.ArrayList;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 *
 * @author Khabat
 */
public class FeedForwardNeuralNetworkMultiClass {

    int inputCount;
    int classes;


    ArrayList<Neuron> neurons=new ArrayList<Neuron>();

    public FeedForwardNeuralNetworkMultiClass(ArrayList<String> neurons, int inputCount, int classCount){
        this.inputCount=inputCount;
        int indexOfNeuron=0;
        classes=classCount;
        
        for (String string : neurons) {
            Neuron n=new Neuron(string, inputCount,indexOfNeuron++);
            this.neurons.add(n);
        }
    }

    public double[] feedForwad(ArrayList<Double> ins){

        double output[]=new double[classes];

        for (Neuron n : neurons) {
           n.calculateNet(ins);
           ins.add(n.getNet());
        }
        //--
        //last neurons are out put neurons so we return the output of classes neurons in the last
        double sumOfOutputs=0;
        for(int i=neurons.size()-classes, j=0; i<neurons.size();i++, j++){
            output[j]=neurons.get(i).currentNet;
            sumOfOutputs+=output[j];
        }

        for(int i=neurons.size()-classes, j=0; i<neurons.size();i++, j++){
            output[j]=output[j]/sumOfOutputs;
        }
        //--
        return output;

    }

    public double sumOfWeights(){

        double ret=0;
        for (Neuron n : neurons) {

           ret+=n.sumOfWeights();
        }
        return ret;
    }

    public Neuron[] getNeurons(){
        return  neurons.toArray(new Neuron[neurons.size()]);
    }

    public int connectionCount(){
        int ret=0;
        for (Neuron n : neurons) {

           ret+=n.connectionNo;
        }

        return ret;
    }

}
