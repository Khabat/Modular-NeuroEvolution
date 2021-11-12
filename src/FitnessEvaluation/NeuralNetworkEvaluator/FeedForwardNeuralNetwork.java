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
public class FeedForwardNeuralNetwork {

    int inputCount;


    ArrayList<Neuron> neurons=new ArrayList<Neuron>();

    public FeedForwardNeuralNetwork(ArrayList<String> neurons, int inputCount){
        this.inputCount=inputCount;
        int indexOfNeuron=0;
        for (String string : neurons) {
            Neuron n=new Neuron(string, inputCount,indexOfNeuron++);
            this.neurons.add(n);
        }
    }

    public double feedForwad(ArrayList<Double> ins){

        double output=0;

        for (Neuron n : neurons) {
           n.calculateNet(ins);
           ins.add(n.getNet());
        }
        //--
        for(Neuron n: neurons){
            output+=n.getOutput();
        }
        //--
        return 1/(1+Math.exp(-1*output));

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
