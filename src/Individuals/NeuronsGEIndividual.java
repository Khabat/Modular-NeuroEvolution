/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Individuals;

import Individuals.FitnessPackage.BasicFitness;
import Individuals.FitnessPackage.Fitness;
import Mapper.DerivationTree;
import Mapper.GEGrammar;
import Mapper.Mapper;
import Mapper.Production;
import Mapper.Rule;
import Mapper.Symbol;
import Util.Enums.SymbolType;
import Util.Random.RandomNumberGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import thesisapplication.NeuronsGEChromosome;

import thesisapplication.NeuronsGEGrammar;

/**
 *
 * @author khs
 */
public class NeuronsGEIndividual extends AbstractIndividual{
    protected   Genotype            genotype;
    protected   int[]               moduleHead; // 0/1 bits with size of genotype, each 1 shows a
                                                // module start that is the end of previous module
    protected Phenotype           phenotype;
    protected GEGrammar           grammar;
    private boolean               mapped;
    private boolean []            valid;
    private int []                usedCodons;
    private int []                usedWraps;
    private int []                previouslyUsedCodons;
    private boolean []            previouslyValid;
    private DerivationTree        dT;
    public double validation;
    int[] mutationPoints;
    int[] crossoverPoints;

    int neuron_connection;
    int neurons;
    //ArrayList<Fitness> parentsFitness;
    public static final int COMPARE_MSE=0;
    public static final int COMPARE_COMPLEXITY=1;
    private double sumOfWeights=0;
    public static int comparisonType=COMPARE_MSE;//EITHER based on MSE or Net's Complexity
    public NeuronsGEIndividual(){
        super();
        this.mapped = false;
        this.valid = null;
	this.previouslyValid = null;
        this.usedCodons = null;
        this.usedWraps = null;
        neuron_connection=0;
    }

    public NeuronsGEIndividual(GEGrammar g, Phenotype p, Genotype gen, Fitness f){
        this.grammar = new NeuronsGEGrammar((NeuronsGEGrammar)g);
        this.phenotype = p;
        this.genotype = gen;
        this.fitness = f;
        this.evaluated = false;
        this.mapped = false;
        this.valid = new boolean[gen.size()];
	this.previouslyValid = new boolean[gen.size()];
        this.usedCodons = new int[gen.size()];
        this.previouslyUsedCodons= new int [gen.size()];
        this.usedWraps = new int[gen.size()];

        for(int i=0;i<gen.size();i++){
            this.valid[i]=false;
            this.previouslyValid[i]=false;
            this.previouslyUsedCodons[i]=-1;
            this.usedCodons[i]=0;
            this.usedWraps[i]=-1;
        }

        //Setting genotype and phenotype

        this.grammar.setPhenotype(this.phenotype);
       // this.grammar.setGenotype(this.genotype.get(0));
        this.fitness.setIndividual(this);
        this.age = 0;

        this.mutationPoints = null;
        this.crossoverPoints = null;
//        this.parentsFitness = null;
    }

    protected  NeuronsGEIndividual(NeuronsGEIndividual i) {
        super(i);
        //Check the grammar type
        this.grammar = new NeuronsGEGrammar((NeuronsGEGrammar)i.grammar);
        this.phenotype = new Phenotype(i.phenotype);
        if (i.getGenotype() != null) {
            // FIXME this hack allows NGram inds to work with no genotype
            this.genotype = new Genotype(i.genotype.size());
            this.moduleHead = new int[genotype.size()];
            System.arraycopy(i.moduleHead, 0, moduleHead, 0,moduleHead.length);
            //
            for (Chromosome c : i.genotype) {
                this.genotype.add(new NeuronsGEChromosome((NeuronsGEChromosome)c));
            }
        }

        this.crossoverPoints = null;
        this.mutationPoints = null;

        this.valid = new boolean[this.genotype.size()];
	this.previouslyValid = new boolean[this.genotype.size()];
        this.usedCodons = new int[this.genotype.size()];
        this.usedWraps = new int[this.genotype.size()];
        this.previouslyUsedCodons=new int[this.genotype.size()];

        for(int j=0;j<this.genotype.size();j++){
            this.valid[j]=i.valid[j];
            this.previouslyValid[j]=i.previouslyValid[j];
            this.previouslyUsedCodons[j]=i.previouslyUsedCodons[j];
            this.usedCodons[j]=i.usedCodons[j];
            this.usedWraps[j]=i.usedWraps[j];
        }
//        if (i.parentsFitness != null) {
//            this.parentsFitness = new ArrayList<Fitness>(i.parentsFitness.size());
//            for (Fitness f : i.parentsFitness) {
//                this.parentsFitness.add(new BasicFitness(i));
//            }
//        }
        this.fitness = new BasicFitness(i.fitness.getDouble(), this);

        this.grammar.setPhenotype(this.phenotype.clone());

        this.validation=i.validation;

        this.neuron_connection=i.neuron_connection;

        this.neurons = i.neurons;

        this.sumOfWeights = i.sumOfWeights;
    }

    public void revalidate(NeuronsGEIndividual ind) {
        this.setMapped(ind.isMapped());
        this.setEvaluated(ind.isEvaluated());
        this.setUsedCodons(ind.getUsedCodons());
        this.setUsedWraps(ind.getUsedWraps());
        this.setValid(ind.isValid());
        this.setAge(ind.getAge());
    }

    /**
     * Invalidate the individual.
     */
    public void invalidate() {
        this.usedCodons = new int[this.genotype.size()];
        this.usedWraps = new int[this.genotype.size()];
        this.valid=new boolean[this.genotype.size()];
        for(int i=0;i<this.genotype.size();i++){
            this.usedCodons[i]=-1;
            this.usedWraps[i]=-1;
        }
        this.mapped = false;
        this.evaluated = false;// All new individuals must be evaluated seperaately
        this.age = 1;
        //this.fitness.setDefault();
    }

    public boolean isMapped() {
        return mapped;
    }

    /**
     * Set the mapped status of the individual
     * @param mapped status of the individuals mapping
     */
    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    public boolean isValid() {
        for(int i=0;i<valid.length;i++)
            if(valid[i]) return true;
        return false;
    }

    public void setValid(boolean[] b) {
        this.valid = b;
    }

    /**
     * Find out whether individual was *previously* valid. Used in NGramUpdateOperator,
     * because it needs to know, after selection has invalidated everything, whether individuals
     * are "really" valid.
     * @return whether it was valid before being invalidated by clone().
     */
    public boolean wasPreviouslyValid() {
        for(int i=0;i<this.previouslyValid.length;i++)
            if(this.previouslyValid[i]==true)return true;
	return false;
    }

    /**
     * Set how many codons were used
     * @param usedCodons number of codons used
     */
    public void setUsedCodons(int[] usedCodons) {
        this.usedCodons = usedCodons;
    }

    /**
     * Set how many wraps were used
     * @param usedWraps number of wraps used
     */
    public void setUsedWraps(int[] usedWraps) {
        this.usedWraps = usedWraps;
    }

    public String getPhenotypeString(int map) {
        return this.phenotype.getString();
    }

    public String getPhenotypeString(){
        return "sig("+this.phenotype.getString()+")";
    }
    public Genotype getGenotype() {
        return this.genotype;
    }

    public Mapper getMapper() {
        return this.grammar;
    }


    public void setMapper(Mapper m) {
        this.grammar = (GEGrammar) m;
    }

    /**
     * Invalidates the individual because a change has been made to the genotype.
     * Sets the new genotype in the indivudual as well as in the mapper
     * @param g genotype
     */
    public void setGenotype(Genotype g) {
        this.invalidate();
        this.genotype = g;

    }

    public void setPhenotype(Phenotype p) {
        this.phenotype = p;
    }

    public Phenotype getPhenotype() {
        return this.phenotype;
    }

    public ArrayList<String> getPhenotypeStrings(){
        //this.map(0);
        ArrayList<String> ret=new ArrayList<String>();
        for(int i=0;i<genotype.size();i++){
            NeuronsGEChromosome chr=(NeuronsGEChromosome)genotype.get(i);
            if(this.valid[i])
                ret.add(chr.getPhenotype().getString());
        }

        return ret;
    }

    /**
     * Clone this individual, invialidate and return the clone
     * @return Individual cloned and invalidated individual
     */
    public Individual clone() {
        //System.out.println("Before clone dT:"+this.grammar.getDerivationTree());
        NeuronsGEIndividual ind = new NeuronsGEIndividual(this);

        ind.setPreviouslyUsedCodons(this.usedCodons);
        //ind.invalidate();

        //System.out.println("After invalidate dT:"+this.grammar.getDerivationTree());
        return ind;
    }

    @Override
    public String toString() {
        String s = "";
//        if (this.phenotype != null) {
//            s = this.phenotype.getString();
//        }
        return getPhenotypeStrings().toString();
    }

    /**
     * Get number of codons used for mapping
     * @return codons used for mapping
     */


    public int[] getUsedCodons() {
        return usedCodons;
    }

    public void setPreviouslyUsedCodons(int[] previouslyUsedCodons) {
        this.previouslyUsedCodons = previouslyUsedCodons;
    }

    /**
     * Get number of codons used for mapping previoulsy. Used when the
     * individual gets invalidated but previous information is needed
     * @return codons used for mapping previously
     */
    public int[] getPreviouslyUsedCodons() {
        return this.previouslyUsedCodons;
    }

    /**
     * Get number of wraps used for mapping
     * @return wraps used
     */
    public int[] getUsedWraps() {
        return usedWraps;
    }

    public int[] getCrossoverPoints() {
        return crossoverPoints;
    }

    public int[] getMutationPoints() {
        return mutationPoints;
    }

//    public ArrayList<Fitness> getParents() {
//        return parentsFitness;
//    }

    public void setCrossoverPoints(int[] crossoverPoints) {
        this.crossoverPoints = crossoverPoints;
    }

    public void setMutationPoints(int[] mutationPoints) {
        this.mutationPoints = mutationPoints;
    }

//    public void setParents(ArrayList<Fitness> parents) {
//        this.parentsFitness = parents;
//    }

    private void generatePredefinedModules(){
        //assign 3 neurons to each module
        int neuronsCount=genotype.size();
        moduleHead = new int[neuronsCount];

        int moduleSize=1;
        //---
        //---
        for(int i=0;i<moduleHead.length-10;i++){
            moduleHead[i]=0;

            if((i+1)%moduleSize==0) moduleHead[i]=1;
        }

        for(int i=0;i<10;i++){
            moduleHead[neuronsCount-i-1] = 1;
        }
    }
    public void map(int map) {
        if (!this.mapped) {
            //Clear the phenotype
            this.phenotype.clear();
            int no_of_validNeurons=1;

            generatePredefinedModules();
            removeAddedInputNeurons();

            this.neuron_connection=0;
            int numOfConnections=0;

            int moduleInternalNeurons=0;

            //this.neurons=0;

            for (int i = 0; i < genotype.size(); i++) {
                this.grammar.setGenotype(this.genotype.get(i));
                //numOfConnections=((NeuronsGEGrammar)this.grammar).genotype2Phenotype(true,0);//FIXME KHABAT
                this.neurons=((NeuronsGEGrammar)this.grammar).genotype2Phenotype(true,0);

                //if(numOfConnections==0){
                //if(this.neuron_connection==0){
                if(this.neurons==0){
                    this.valid[i] = false;
                }
                else
                    this.valid[i]=true;

                this.previouslyValid[i] = this.valid[i];

                this.usedCodons[i] = this.grammar.getUsedCodons();
                this.usedWraps[i] = this.grammar.getUsedWraps();

                if (valid[i]) {

                    if(moduleHead[i]==0){
                     addInputNeuronSymbol(no_of_validNeurons++);
                     moduleInternalNeurons++;
                    }
                    else{
                       removeLastAddedInputNeurons(moduleInternalNeurons);
                       moduleInternalNeurons=0;
                       addInputNeuronSymbol(no_of_validNeurons++);
                    }
                     //this.neuron_connection+=numOfConnections+1;
                     //this.neurons=neurons;
                }
            }
            this.mapped = true;
        }
    }
//    public void addInputNeuronSymbol(int no){
//        //adding createdNeurons as input to next neurons that are created
//        //it is done by adding x<n> to the input symbols, where n is the counter of input vars
//        //Rule r=this.grammar.getRule(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
//        Rule r=this.grammar.getRule(new Symbol( "<xxList>",SymbolType.NTSymbol));
//        /*if (r==null){
//            r=new Rule();
//            r.setLHS(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
//            this.grammar.getRules().add(r);
//            //--
//            Rule r2=this.grammar.getRule(new Symbol( "<Input>",SymbolType.NTSymbol));
//            Production p=new Production();
//            p.add(new Symbol("<HiddenNeuron>", SymbolType.NTSymbol));
//            r2.add(p);
//        }
//         *
//         */
//
//        //--
//        Production p=new Production();
//        p.add(new Symbol("n"+no, SymbolType.TSymbol));
//
//        r.add(p);
//
//    }

    public void addInputNeuronSymbol(int no){
        //adding createdNeurons as input to next neurons that are created
        //it is done by adding x<n> to the input symbols, where n is the counter of input vars
        //Rule r=this.grammar.getRule(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
        if (no == 1) {
            Rule r1=this.grammar.getRule(new Symbol( "<XList>",SymbolType.NTSymbol));
            Production p=new Production();
            p.add(new Symbol("<xnList>", SymbolType.NTSymbol));
            r1.add(p);
            return;
        }

        Rule r=this.grammar.getRule(new Symbol( "<xnList>",SymbolType.NTSymbol));
        /*if (r==null){
            r=new Rule();
            r.setLHS(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
            this.grammar.getRules().add(r);
            //--
            Rule r2=this.grammar.getRule(new Symbol( "<Input>",SymbolType.NTSymbol));
            Production p=new Production();
            p.add(new Symbol("<HiddenNeuron>", SymbolType.NTSymbol));
            r2.add(p);
        }
         *
         */

        //--
        Production p=new Production();
        p.add(new Symbol("n"+no, SymbolType.TSymbol));

        r.add(p);

    }
//    public void removeAddedInputNeurons(){
//        //Rule r=this.grammar.getRule(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
//        //if (r==null) return;
//        Rule r=this.grammar.getRule(new Symbol( "<xxList>",SymbolType.NTSymbol));
//
//        int size=r.size();
//        Rule newR=new Rule();
//        for (Production p : r) {
//            if(p.get(0).getSymbolString().contains("n")==false)
//               newR.add(p);
//        }
//        r.clear();
//        r.addAll(newR);
//
//        /*if(r.size()==0){
//            this.grammar.getRules().remove(r);
//
//            Rule r2=this.grammar.getRule(new Symbol( "<Input>",SymbolType.NTSymbol));
//
//            r2.remove(1);// the rule has only two productions
//        }
//         *
//         */
//    }

    public void removeAddedInputNeurons(){
        //Rule r=this.grammar.getRule(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
        //if (r==null) return;

        Rule r1=this.grammar.getRule(new Symbol( "<XList>",SymbolType.NTSymbol));
        if(r1.size()==2)
            r1.remove(1);



        Rule r=this.grammar.getRule(new Symbol( "<xnList>",SymbolType.NTSymbol));
       
        r.clear();
        //---
        Production p=new Production();
        p.add(new Symbol("n"+1, SymbolType.TSymbol));

        r.add(p);
        //---
    }
    public void setValid(boolean b) {
        throw new UnsupportedOperationException("Not supported yet. setValid in the NeuronsGEIndividual");
    }
    /*
     * randomly adds a new neuron
     */
    public void addNewNeuron(RandomNumberGenerator rng) {
        int len = genotype.get(0).getLength();
        GEChromosome neuron = new NeuronsGEChromosome(len);

        for (int i = 0; i < len; i++) {
            neuron.add(rng.nextInt(256));
        }
        genotype.add(0,neuron);
        int length = this.genotype.size();


        this.valid = Arrays.copyOf(this.valid, length );
        this.previouslyValid = Arrays.copyOf(this.previouslyValid, length );

        this.usedCodons = Arrays.copyOf(usedCodons, length);
        this.usedWraps = Arrays.copyOf(usedWraps, length );

        this.previouslyUsedCodons = Arrays.copyOf(this.previouslyUsedCodons, length);
    }

    public void removeNeuron(RandomNumberGenerator rng){
        if (genotype.size() < 2) {
            return;
        }

        int indexOfremoved = rng.nextInt(genotype.size());

        genotype.remove(indexOfremoved);

        boolean[] validT = new boolean[genotype.size()];
        boolean[] previouslyValidT = new boolean[genotype.size()];

        int[] usedCodonsT = new int[genotype.size()];
        int[] usedWrapsT = new int[genotype.size()];

        int[] previouslyUsedCodonsT = new int[genotype.size()];

        int j = 0;

        for (int i = 0; i < this.valid.length; i++) {
            if (i != indexOfremoved) {
                validT[j] = this.valid[i];
                previouslyValidT[j] = this.previouslyValid[i];

                usedCodonsT[j] = this.usedCodons[i];
                usedWrapsT[j] = this.usedWraps[i];
                previouslyUsedCodonsT[j] = this.previouslyUsedCodons[i];
                j++;
            }
        }

        this.valid = validT;
        this.previouslyValid = previouslyValidT;

        this.usedCodons = usedCodonsT;
        this.usedWraps = usedWrapsT;

        this.previouslyUsedCodons=previouslyUsedCodonsT;
    }

    public int compareTo(Individual i){
        if(comparisonType==COMPARE_MSE){
            int result=super.compareTo(i);
            if(result==0){
                return compareBasedComplexity(i);
            }
            return result;
        }
        //ELSE, EITHER comparison is not based on MSE do comparison based on complexity:
        return compareBasedComplexity(i);
    }

    private int compareBasedComplexity(Individual ind){
        NeuronsGEIndividual ni=(NeuronsGEIndividual)ind;
        if (this.get_neurons_count() == ni.get_neurons_count()) {

            if (this.get_connections_count() < ni.get_connections_count()) {
                return -1;
            }
            if (this.get_connections_count() > ni.get_connections_count()) {
                return 1;
            }
            return 0;
        }
        if (this.get_neurons_count() > ni.get_neurons_count()) {
                return 1;
        }
        return -1;

    }

    public int get_neurons_count(){
        int n=0;
        for(int i=0;i<this.valid.length;i++){
            if(valid[i]==true)
                n++;
        }

        return n;
    }

    public int get_connections_count(){
        return this.neuron_connection;
    }
    public void set_Weights_Sum(double sum){
        this.sumOfWeights=sum;
    }
    public double  get_Weights_Sum(){
        return sumOfWeights;
    }
    public double get_Weights_Avg(){
        return sumOfWeights/get_connections_count();
    }
    public int get_features_count(){
        ArrayList<String> features=new ArrayList<String>();
        ArrayList<String> localPhen=new ArrayList<String>();
        int n=0;

        localPhen=this.getPhenotypeStrings();
        for(int i=0;i<localPhen.size();i++){
            String s=localPhen.get(i);
            StringTokenizer st=new StringTokenizer(s, ",");

            while(st.hasMoreTokens()){
                String tmp=st.nextToken();
                if(tmp.contains("x") && (features.size()==0 || !features.contains(tmp.trim()))){
                    features.add(tmp.trim());
                }
            }
        }
        return features.size();
    }

    public int getNeuronsCount(){
        return neurons;
    }

    private void removeLastAddedInputNeurons(int moduleInternalNeurons) {
        //Rule r=this.grammar.getRule(new Symbol( "<HiddenNeuron>",SymbolType.NTSymbol));
        //if (r==null) return;


        Rule r=this.grammar.getRule(new Symbol( "<xnList>",SymbolType.NTSymbol));

        int size=r.size();


        if(moduleInternalNeurons>size)
            throw new IndexOutOfBoundsException("size of neurons that must be removed in a module should be lower than entire neurons");

        for(int i=0;i<moduleInternalNeurons; i++){
            r.remove(r.size()-1);
        }
    }


}
