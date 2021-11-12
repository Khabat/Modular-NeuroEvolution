/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator.DoublePoleBalancing;

import FitnessEvaluation.FitnessFunction;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.SigmoidFunction;
//import ec_grammaticalevolution4nn.TwoChromosomeIndividual_EP;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.ARFFHandler;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 *
 * @author Khabat
 */


public class DoublePolebalancingFitnesssEvaluation implements FitnessFunction {
    static final int MAX_STEPS=100000;

    org.nfunk.jep.JEP mathEvaluator;

    public DoublePolebalancingFitnesssEvaluation(){
        mathEvaluator=new org.nfunk.jep.JEP();
        mathEvaluator.addFunction("sig", new SigmoidFunction());
    }
    protected String RemoveWhiteSpace(String in){
        StringBuffer b = new StringBuffer();
        for (int i=0; i<in.length();i++){
            if(in.charAt(i)!=' ')
                b.append(in.charAt(i));
        }
        return b.toString();
    }
    protected void RemoveWhiteSpaces(ArrayList<String> in){
        for (int i=0;i<in.size();i++){
            in.set(i, RemoveWhiteSpace(in.get(i)));
        }
    }

    public void getFitness(Individual i) {
        ArrayList<String> phenotype=((NeuronsGEIndividual)i).getPhenotypeStrings();

        RemoveWhiteSpaces(phenotype);
        i.getFitness().setDouble(get_fitness(phenotype)+0.001 * ((NeuronsGEIndividual)i).get_neurons_count() );

    }

    public boolean canCache() {
        return false;
    }

    public void setProperties(Properties p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double get_fitness(ArrayList<String> phenotype) {
        double fitness = 0;
        double output;
        Node n = null;

        State initState = new State();
        initState.setTheta1(5.0 / 180 * Math.PI);
        Polebalancing pole = new Polebalancing(initState);

        ArrayList<Node> nodes = createNN_from_Neurons(phenotype, initState.toInstance(true));

        for (int i = 0; i < MAX_STEPS; i++) {
            output = getOutput(nodes, pole.state.toInstance(true));
            pole.performAction(output);
            if (pole.state.violated()) {
                break;
            }
            fitness++;
        }
        return MAX_STEPS-fitness;
    }

    public ArrayList<Node> createNN_from_Neurons(ArrayList<String> strs, Instance ins) {
        ArrayList<Node> ret = new ArrayList<Node>(strs.size());
        for (int i = 0; i < ins.noAttributes(); i++) {
            mathEvaluator.addVariable("x" + String.valueOf(i + 1), ins.value(i));
        }
        int n = 1;
        for (String str : strs) {
            try {
                ret.add(mathEvaluator.parse(str));
                mathEvaluator.addVariable("n" + n++, 0);
            } catch (ParseException ex) {
                Logger.getLogger(DoublePolebalancingFitnesssEvaluation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

     public double getOutput(ArrayList<Node> neurons,Instance input) {
        for (int j = 0; j < input.noAttributes(); j++) {
           mathEvaluator.addVariable("x" + String.valueOf(j + 1), input.value(j));
        }

        double out[]=new double[neurons.size()];

        for(int i=0;i<neurons.size();i++){
            Node node=neurons.get(i);
            try {
                out[i] = Double.valueOf(mathEvaluator.evaluate(node).toString()).doubleValue();
            } catch (ParseException ex) {
                Logger.getLogger(DoublePolebalancingFitnesssEvaluation.class.getName()).log(Level.SEVERE, null, ex);
            }
            mathEvaluator.addVariable("n" + String.valueOf(i+ 1), out[i]);
        }

        double ret=0;
        for(int i=0;i<out.length;i++)
            ret+=out[i];
        //now we trasmit it from sigmoid function
//        Stack s=new Stack();
//        s.push(ret);
//        try {
//            new SigmoidFunction().run(s);
//        } catch (ParseException ex) {
//            Logger.getLogger(NeuralNetworkFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return 1/(1+Math.pow(Math.E,-((Double) ret)));
    }
}
