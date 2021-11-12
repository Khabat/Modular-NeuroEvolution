/*
Grammatical Evolution in Java
Release: GEVA-v1.2.zip
Copyright (C) 2008 Michael O'Neill, Erik Hemberg, Anthony Brabazon, Conor Gilligan 
Contributors Patrick Middleburgh, Eliott Bartley, Jonathan Hugosson, Jeff Wrigh

Separate licences for asm, bsf, antlr, groovy, jscheme, commons-logging, jsci is included in the lib folder. 
Separate licence for rieps is included in src/com folder.

This licence refers to GEVA-v1.2.

This software is distributed under the terms of the GNU General Public License.


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
/>.
*/

package Operator.Operations;

import Individuals.Chromosome;
import Individuals.Genotype;
import Individuals.Individual;
import Individuals.NeuronsGEIndividual;
import Util.Statistics.IndividualCatcher;
import Util.Statistics.StatCatcher;
import Util.Constants;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import thesisapplication.MyStateCatcher;

/**
 * Operation that collects statistics from the algorithm.
 * Catches statistics from the run.
 * Cathes statistics about the individuals.
 * @author erikhemberg
 */
public class StatisticsCollectionOperation implements Operation, OutputI{

    //HACK FIXME??!!
    int uglyCnt = 0;
    int cntIntervall;
    protected StatCatcher stats;
    protected IndividualCatcher indCatch;
    protected String fileName;
    private static final String OUTPUT_COLUMNS = "#bestFitness averageFitness worstFitness bestVFitness averageUsedGeneLength time invalids varFitness aveLength aveDerivationTreeDepth avgNeurons avgConns avg_Features sum_weights";
    private static final String OUTPUT_COLUMNS_STDOUT = "Gen\tFitEvals\tTime(ms)\tInvalids\tBestFit\tAveFit\tVarFit\tAveUsedGenes\tAveLength\tAveDTDepth";

    /** Creates a new instance of StatisticsCollectionOperation
     * @param stats statistics extraction
     * @param indCatch indivudal information extraction
     */
    public StatisticsCollectionOperation(StatCatcher stats, IndividualCatcher indCatch) {
        this.stats = stats;
        this.indCatch = indCatch;
    }

    /** Creates a new instance of StatisticsCollectionOperation
     * @param stats statistics extraction
     * @param indCatch indivudal information extraction
     * @param p properties
     */
    public StatisticsCollectionOperation(StatCatcher stats, IndividualCatcher indCatch, Properties p) {
        this.stats = stats;
        this.indCatch = indCatch;
        this.setProperties(p);
    }

    /**
     * Prints the header for statistics output
     */
    public void printHeader() {
        System.out.println("#---Data---");
        System.out.println(StatisticsCollectionOperation.OUTPUT_COLUMNS_STDOUT);
    }

    /**
     * Print stats for the current generation
     * @param time time it took to run
     */
    public void printStatistics(long time) {

        System.out.format
        (   "%1$4d\t%9$5d\t%2$6d\t%3$4d\t%4$6.3f\t%5$6.3f\t%11$6.3f\t%12$6.3f\t%6$8.3f\t%7$6.3f\t%8$6.3f\t%10$6.3f\t%13$3.6f\t%14$3.6f\t%15$3.4f\t%16$8.4f%n",
            stats.getInvalids().size()-1,
            time,
            stats.getInvalids().get(stats.getInvalids().size()-1),
            stats.getCurrentBestFitness(),
            stats.getCurrentMean(),
            stats.getVarFitness().get(stats.getVarFitness().size()-1),
            stats.getCurrentMeanUsedGenes(),
            stats.getAveLength().get(stats.getAveLength().size()-1),
            0,
            stats.getMeanDerivationTreeDepth().get(stats.getMeanDerivationTreeDepth().size() - 1),
            ((MyStateCatcher)stats).getBestValidationFitness().get(((MyStateCatcher)stats).getBestValidationFitness().size()-1),
            ((MyStateCatcher)stats).getLastMeanValidation().get(((MyStateCatcher)stats).getLastMeanValidation().size()-1),
            ((MyStateCatcher)stats).getNeuronsCount().get(((MyStateCatcher)stats).getNeuronsCount().size()-1),
            ((MyStateCatcher)stats).getConnectionsCount().get(((MyStateCatcher)stats).getConnectionsCount().size()-1),
            ((MyStateCatcher)stats).getPenaltyCoefs().get(((MyStateCatcher)stats).getPenaltyCoefs().size()-1),
            ((MyStateCatcher)stats).getWeightSum().get(((MyStateCatcher)stats).getWeightSum().size()-1)
            );
        //System.err.println(stats.total());
    }

    /**
     * Set properties
     *
     * @param p object containing properties
     */
    public void setProperties(Properties p) {
        String value = System.getProperty("user.dir");
        File f;
        String key;
        try {
            key = Constants.OUTPUT;
            String tmp = p.getProperty(key);
            if(tmp!=null) {
                if(tmp.equals(Constants.FALSE)) {
                    value = "";
                } else {
                    if(!tmp.startsWith(System.getProperty("file.separator"))) {
                        value += System.getProperty("file.separator");
                    }
                    value += tmp;
                    f = new File(value);
                    if(value.endsWith(System.getProperty("file.separator")) && !f.isDirectory()) {
                        throw new FileNotFoundException(value);
                    }
                    System.out.println("Output directory is "+value);
                }
            } else {
                value = "";
            }
        } catch(Exception e) {
            value = System.getProperty("user.dir")+System.getProperty("file.separator");
            System.out.println("Using default output directory: "+value);
        }
        this.fileName = value;
        //change by Khabat
        f=new File(fileName);
        this.fileName=f.getParent()+p.getProperty("log_file")+this.fileName.substring(1+this.fileName.lastIndexOf("/"));
        //by here
	//HACK FIXME??!!
	this.cntIntervall = Integer.parseInt(p.getProperty("intervall_cnt","10000000"));

    }

    public void doOperation(Individual operand) {}

    /**
     * Store the time of the operation.
     * Add statistics.
     * @param operands Individuals used for data derivation
     **/
    private void tempOpSaveIndividuals(List<Individual> operands){
        FileWriter fw = null;
        try {
                                    
            ArrayList<Individual> categorized[]=(ArrayList<Individual>[]) categorize(operands);

            for(int k=0;k<categorized.length;k++){
                //---
                StringBuffer buffer=new StringBuffer();
                //---
                int count=0;
                for (Individual individual : categorized[k]) {
                    count++;
                    if(count>=10000)break;
                    NeuronsGEIndividual ind=(NeuronsGEIndividual)individual;
                    //buffer.append(genToString(individual)+"|"+makeFirstOrder(individual)+"|"+ind.getNeuronsCount()+"|"+ind.getUsedCodons()[0]+"\n");
                    buffer.append(genToString(individual)+"|"+makeFirstOrder(individual)+"\n");
                }

                if(categorized[k].size()!=0){
                    //String logFileName = "GE-gen-phen-mge"+ operands.size()+"-"+String.valueOf(operands.get(0).getGenotype().size())+"neurons--" + 2 +"HMD-"+new Date().getTime()+".txt";
                    String logFileName = "GE-gen-phen-"+ categorized[k].size()+"-"+k+"neurons--" + 5 +"HMD-"+new Date().getTime()+".txt";
                    fw = new FileWriter(logFileName, false);
                    //BufferedWriter bw = new BufferedWriter(fw);
                    fw.write(buffer.toString());
                    fw.close();
                }


            }

            
           
        }
        catch (IOException ex) {
            Logger.getLogger(StatisticsCollectionOperation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(StatisticsCollectionOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    private List<Individual>[] categorize(List<Individual> operands){
        //based on their neurons count categorize them
        
        ArrayList<Individual>[] ret=new ArrayList[30];// 20- the most probable number of neurons
        for(int i=0;i<30;i++){
            ret[i] = new ArrayList<Individual>();
        }
        //--
        //--
        Iterator<Individual> itr= operands.iterator();
        //--
        while(itr.hasNext()){
           NeuronsGEIndividual d=(NeuronsGEIndividual)itr.next();
           NeuronsGEIndividual d2=(NeuronsGEIndividual)itr.next();
           ret[d.getNeuronsCount()].add(d);// d.getNeuronsCount() determines the category. Both d and d2 must be saved in that category
           ret[d.getNeuronsCount()].add(d2);
        }
        
        return ret;
    }

    private String genToString(Individual ind){
        StringBuffer st=new StringBuffer();

        for (int i=0;i<ind.getGenotype().size();i++) {
            Chromosome ch=ind.getGenotype().get(i);
            st.append(ch.toString());
        }

        return st.toString();
    }

    private String makeFirstOrder(Individual ind){
        String ret=new String();
        ArrayList<String> phenotype=((NeuronsGEIndividual)ind).getPhenotypeStrings();
        if(phenotype.size()>1){
            String st1=phenotype.remove(0);
            String st2=phenotype.remove(0);
            ret="{+" + st1 + st2 +"}";
        }

        for (String string : phenotype) {
            ret="{+" + ret + string +"}";            
        }

        return ret;
    }
    public void doOperation(List<Individual> operands) {

        //tempOpSaveIndividuals(operands);

        Long start = System.currentTimeMillis();
        this.stats.addTime(start);
        this.stats.addStatsPop((ArrayList<Individual>)operands);
        if(this.indCatch.getCatchInterval() < Integer.MAX_VALUE) {
            catchIndividuals(operands);
        }
	//HACK FIXME??!!
	if(uglyCnt%cntIntervall==0 && uglyCnt > 0) {
	    System.out.println("Best individual: "+this.stats.getBestIndividualOfGeneration());
	}
	uglyCnt++;
    }

    private void catchIndividuals(List<Individual> operands) {
        ArrayList<Individual> alI = new ArrayList<Individual>(operands.size()/this.indCatch.getCatchInterval());
        Iterator<Individual> ind_it = operands.iterator();
        int cnt = 1;
        Individual ind;
        while(ind_it.hasNext()) {
            ind = ind_it.next();
            if(cnt%this.indCatch.getCatchInterval() == 0) {
                alI.add(ind);
            }
            cnt++;
        }
        this.indCatch.addPop(alI);
        this.indCatch.addString(System.getProperty("line.separator"));
        if(this.indCatch.getCapacity() > 10000000) {
            System.out.println(this.indCatch.getCapacity());
            printIndividuals("tmpInd_ind_.dat", true);
            this.indCatch.clear();
        }
        //        System.out.println(this.indCatch.getCatchInterval()+" "+alI.size()+" "+operands.size()+"\n cI:"+this.indCatch);
    }

    /**
     * Print individuals
     * @param fileName name of file
     * @param append append data to file
     */
    @SuppressWarnings({"SameParameterValue", "IOResourceOpenedButNotSafelyClosed"})
    private void printIndividuals(String fileName, boolean append) {
        try {
            FileWriter fw = new FileWriter(fileName, append);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.indCatch.toString());
            bw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return StatCatcher for information retival.
     * @return StatCatcher containing data about the run
     **/
    public StatCatcher getStats() {
        return stats;
    }

    /**
     * Return IndividualCatcher for information retival.
     * @return IndividualCatcher containing data about the run
     **/
    public IndividualCatcher getIndividualCatcher() {
        return indCatch;
    }

    /**
     * Print the statistics to file.
     * Add the operand to the IndividualStatistics and print to System.out
     * @param operand Individuals that will be printed
     * @param toFile Boolean for printing to file
     **/
    public void print(List<Individual> operand, boolean toFile) {
        if(toFile) {
            this.printStats();
        }
        this.indCatch.addPop(operand);
        System.out.println(this.indCatch);
    }

    /**
     * Return the best individual in the group
     * @param operand Individuals that will be printed
     **/
    public IndividualCatcher getBest(List<Individual> operand){
        this.indCatch.addPop(operand);
        return this.indCatch;
    }

    /**
     * Print individual informatino to file
     */
    @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed", "UnusedDeclaration"})
    private void printIndividuals() {
        try {
            FileWriter fw = new FileWriter(this.fileName+"_ind_.dat");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.indCatch.toString());
            bw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print the StatCatcher to file
     **/
    @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
    public void printStats() {
        try {
	    this.fileName = fileName+System.currentTimeMillis();
            FileWriter fw = new FileWriter(fileName+".dat");
            BufferedWriter bw = new BufferedWriter(fw);

            ArrayList<Double> m,b,w,bOnVali, aUG, aV, aL, aD, neurs, conns, features,penalty,beforPs,afterPs,afterXr,afterMt,afterRp, weights;
            ArrayList<Long> t;
            ArrayList<Integer> inv, cc;
            weights = ((MyStateCatcher)stats).getWeightSum();
            b = stats.getBestFitness();
            m = stats.getMeanFitness();
            w=stats.getWorstFitness();
            bOnVali=((MyStateCatcher)stats).getBestValidationFitness();
            aUG = stats.getMeanUsedGenes();
            t = stats.getTime();
            inv = stats.getInvalids();
            aV = stats.getVarFitness();
            aL = stats.getAveLength();
            aD = stats.getMeanDerivationTreeDepth();
            penalty=((MyStateCatcher)stats).getPenaltyCoefs();
            neurs=((MyStateCatcher)stats).getNeuronsCount();
            conns=((MyStateCatcher)stats).getConnectionsCount();
            features=((MyStateCatcher)stats).getFeaturesCount();
            beforPs=((MyStateCatcher)stats).beforeParentSelecion;
            afterPs=((MyStateCatcher)stats).afterParentSelecion;
            afterXr=((MyStateCatcher)stats).afterXover;
            afterMt=((MyStateCatcher)stats).afterMutation;
            afterRp=((MyStateCatcher)stats).afterReplacement;
            cc=((MyStateCatcher)stats).competingConvention;



            Iterator<Double> ib = b.iterator();
            Iterator<Double> im = m.iterator();
            Iterator<Double> iw = w.iterator();
            Iterator<Double> ibonv = bOnVali.iterator();
            Iterator<Double> iAV = aV.iterator();
            Iterator<Double> iAUG = aUG.iterator();
            Iterator<Double> iAD = aD.iterator();
            Iterator<Long> iT = t.iterator();
            Iterator<Integer> iInv = inv.iterator();
            Iterator<Double> iAL = aL.iterator();
            Iterator<Double> iNeurs=neurs.iterator();
            Iterator<Double> iConns=conns.iterator();
            Iterator<Double> iFeaturs=features.iterator();
            Iterator<Double> ipenalty=penalty.iterator();
            Iterator<Double> ibeforPs=beforPs.iterator();
            Iterator<Double> iafterPs=afterPs.iterator();
            Iterator<Double> iafterXr=afterXr.iterator();
            Iterator<Double> iafterMt=afterMt.iterator();
            Iterator<Double> iafterRp=afterRp.iterator();
            Iterator<Integer> icc=cc.iterator();
            Iterator<Double> iweights=weights.iterator();


            Long start = iT.next();
            Long diff, stop;
            bw.write(StatisticsCollectionOperation.OUTPUT_COLUMNS);
            bw.newLine();
            while(ib.hasNext()&&im.hasNext() && iAUG.hasNext() && iT.hasNext() && iInv.hasNext() && iAV.hasNext() && iAL.hasNext() && iAD.hasNext() && iweights.hasNext()/*&&iafterPs.hasNext()*/) {
                stop = iT.next();
                diff = stop - start;
                start = stop;
                bw.write(ib.next()+" "+im.next()+" "+iw.next()+" "+ ibonv.next()+" "+iAUG.next()+" "+diff+" "+iInv.next()+" "+iAV.next()+" "+iAL.next()+" "+iAD.next()+" "+iNeurs.next()+" "+iConns.next()+" " + iFeaturs.next()+" "+ ipenalty.next()+" "+iweights.next()
                        
                        /*+" "+ibeforPs.next()+" "+iafterPs.next()+" "+iafterXr.next()+" "+ iafterMt.next()+" "+iafterRp.next() +" " + icc.next()*/);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}

