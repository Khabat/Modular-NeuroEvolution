/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import Mapper.GEGrammar;
import Mapper.Production;
import Mapper.Rule;
import Mapper.Symbol;
import java.util.LinkedList;
import java.util.Properties;

/**
 *
 * @author sky
 */
public class NeuronsGEGrammar extends GEGrammar{
    public NeuronsGEGrammar() {
        super();

    }

    /**
     * New instance
     * @param file file to read grammar from
     */
    public NeuronsGEGrammar(String file) {
        super(file);

    }

    /**
     * New instance
     * @param p properties
     */
    public NeuronsGEGrammar(Properties p) {
        super(p);
    }

    /**
     * Copy constructor. Does not copy the genotype and phenotype
     * @param copy grammar to copy
     */
    public NeuronsGEGrammar(NeuronsGEGrammar copy) {
        super(copy);
    }

    
    public int genotype2Phenotype(boolean tt,int tti) {
        boolean validMap=false;
        int ret=0;
        if(tt){
                LinkedList<Symbol> phenoType=new LinkedList<Symbol>();
                int currentNTIndex=0;

                phenoType.add(this.getStartRule().getLHS());
                int genUsed=0;
                for(;genUsed< genotype.getLength() + genotype.getLength()*(this.maxWraps-1);genUsed++){
                    int b=(int) genotype.get(genUsed%genotype.getLength());
                    Symbol s=phenoType.get(currentNTIndex);
                    //if(s.getSymbolString().toLowerCase().equals("<xxlist>")) ret++;//FIXME Khabat
                    if(s.getSymbolString().toLowerCase().equals("<node>")) ret++;
                    Rule r=super.getRule(s);
                    
                    Production p=r.get(b%r.size());

                    if(r.size()==1)
                        genUsed--;//when it has only one option we should not use a codon. it is important for locality measurement

                    phenoType.remove(currentNTIndex);
                    for(int j=0;j<p.size();j++){
                        phenoType.add(j+currentNTIndex, p.get(j));
                    }
                    currentNTIndex=getNextNT_Index(phenoType, currentNTIndex);
                    if(currentNTIndex==-1)break;
                }
                ((NeuronsGEChromosome)genotype).phenotype.clear();
                ((NeuronsGEChromosome)genotype).phenotype.addAll(phenoType);
                this.dT=null;
                if(getNextNT_Index(phenoType, 0)==-1){
                    ((NeuronsGEChromosome)genotype).isValid=true;
                    validMap=true;
                }else{
                    ((NeuronsGEChromosome)genotype).isValid=false;
                    validMap=false;
                }

                setDerivationTree(null);
                this.usedCodons=genUsed;

                this.usedWraps=(genUsed-1)/genotype.getLength();
                this.maxCurrentTreeDepth=0;
                
        }else{
            validMap=false;
        }
        if(validMap) return ret;
        return 0;
    }
}
