/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator;

import java.util.ArrayList;
import java.util.StringTokenizer;
import net.sf.javaml.core.Instance;

/**
 *
 * @author Khabat
 */
enum TransferFunc{ sig, tanh, sin, relu, Gaussian, softsign, square, identity}
public class Neuron {

    int connectionNo;
    int indicesOfInputConnections[];
    double weightsOfInputConnections[];
    double bias;
    double outputWeight;

    double currentNet=Double.NaN;
    TransferFunc tr;


    public Neuron(String str,int inputCount, int indexOfNeuron){
        init(str, inputCount,indexOfNeuron);
    }

    protected  void init(String neuronStruct,int input_count, int indexOfNeuron){
        
       int indices[]=new int[input_count+indexOfNeuron];
       double weights[]=new double [input_count+indexOfNeuron];



       StringTokenizer st=new StringTokenizer(neuronStruct,",");

       outputWeight=Double.valueOf(st.nextToken());
       bias=Double.valueOf(st.nextToken());

       while(st.hasMoreTokens()){
           String temp=st.nextToken();
           if(temp.charAt(0)=='x'){
               int index=Integer.valueOf(temp.substring(1))-1;
               st.nextToken();// to skip * symbol
               weights[index]+=Double.valueOf(st.nextToken());
               indices[index]=1;
           }
           else if (temp.charAt(0)=='n'){
               int index=Integer.valueOf(temp.substring(1))-1;
               st.nextToken();// to skip * symbol
               weights[input_count+index]+=Double.valueOf(st.nextToken());
               indices[input_count + index]=1;
           }

           else if(temp.contains("sig")){
               tr=TransferFunc.sig;
           }
           else if(temp.contains("sin")){
               tr=TransferFunc.sin;
           }
           else if(temp.contains("tanh")){
               tr=TransferFunc.tanh;
           }
           else if(temp.contains("softsign")){
               tr=TransferFunc.softsign;
           }
           else if(temp.contains("square")){
               tr=TransferFunc.square;
           }
           else if(temp.contains("identity")){
               tr=TransferFunc.identity;
           }
           else if(temp.contains("Gaussian")){
               tr=TransferFunc.Gaussian;
           }
           else if(temp.contains("relu")){
               tr=TransferFunc.relu;
           }
       }

       if(tr==null){
           System.out.print(neuronStruct);
       }

       connectionNo=0;
       for(int i=0;i<input_count+indexOfNeuron;i++){

           if(indices[i]!=0) connectionNo++;
       }

        indicesOfInputConnections = new int[connectionNo];
        weightsOfInputConnections = new double[connectionNo];
        int j = 0;
        for (int i = 0; i < input_count+indexOfNeuron; i++) {
            if (indices[i] != 0) {
                indicesOfInputConnections[j] = i;
                if(weights[i]>=+1)weights[i]=1;
                if(weights[i]<=-1)weights[i]=-1;
                weightsOfInputConnections[j++] =weights[i];
            }
        }

   }

    public double calculateNet(ArrayList<Double> input){

        double net=0;

        for(int i=0;i<indicesOfInputConnections.length;i++){
            net+=weightsOfInputConnections[i]*input.get(indicesOfInputConnections[i]);
            //net+=(weightsOfInputConnections[i]==1)?input.get(indicesOfInputConnections[i]):-input.get(indicesOfInputConnections[i]);
          
           //net+=input.get(indicesOfInputConnections[i]);
        }
        net+=bias;

        //transfer it from tr function
        currentNet = activationFunction(net);
        return currentNet;
    }

    public double activationFunction(double net){
        double z=0;
        switch(tr){
            case sig:
                currentNet=1/(1+Math.exp(-1*net));
                break;
            case tanh:
                z= Math.max(-60.0, Math.min(60.0, 2.5 * net));
                currentNet = Math.tanh(z);
                break;
            case relu:
                currentNet = (net>0)? net:0;
                break;
            case sin:
                z= Math.max(-60.0, Math.min(60.0, 5 * net));
                currentNet=Math.sin(z);
                break;
            case Gaussian:
                z = Math.max(-3.4, Math.min(3.4, net));
                currentNet = Math.exp(-5.0 * Math.pow(z, 2));
                break;
            case identity:
                currentNet = net;
                break;
            case square:
                currentNet = Math.pow(z, 2);
                break;
        }
        return currentNet;
    }

    public double getOutput(){
        return currentNet*outputWeight;
        //return outputWeight==1?(currentNet):(-currentNet);
        
    }

    public double getNet(){
       
        return currentNet;
    }

    public double sumOfWeights(){
        double ret=0;
        for(int i=0;i<weightsOfInputConnections.length;i++){
            ret+=weightsOfInputConnections[i]*weightsOfInputConnections[i];
        }
        ret+=bias*bias;
        ret+=outputWeight*outputWeight;
        return ret;
    }
    public static void main (String []args){
        Neuron n=new Neuron("1,3, sig(,x2,*,3,x5,*,5,n1,*,11,)", 5, 1);
        Neuron n2=new Neuron("1,3, sig(,x5,*,1.3,n1,*,-.2,x2,*,1.1,)", 5, 1);
        if(n.isEqual(n2))
            System.out.print("yes");
    }

    public boolean isEqual(Neuron n){
        if(this.indicesOfInputConnections.length!=n.indicesOfInputConnections.length)
            return false;
        for(int i=0;i<this.indicesOfInputConnections.length;i++){
            if(this.indicesOfInputConnections[i]!=n.indicesOfInputConnections[i]||
                    Math.abs(this.weightsOfInputConnections[i]-n.weightsOfInputConnections[i])>0.1)
                return false;
        }
        return true;
    }
}
