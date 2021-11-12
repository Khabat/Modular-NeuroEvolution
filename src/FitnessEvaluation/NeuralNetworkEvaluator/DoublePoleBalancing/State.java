/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator.DoublePoleBalancing;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
/**
 *
 * @author Khabat
 */
public class State {
    private double x; //-2.4 to =2.4
    private double x_velocity;
    private double theta1; //-36 to =36
    private double theta1_velocity;
    private double theta2;
    private double theta2_velocity;


    public State(State s){
        x=s.x;
        x_velocity=s.x_velocity;
        theta1=s.theta1;
        theta1_velocity=s.theta1_velocity;
        theta2=s.theta2;
        theta2_velocity=s.theta2_velocity;
    }
    public State(){
        
    }


    public Object clone(){
        return new State(this);
    }

    public void setX(double x){
        //if(x<2.4 && x>-2.4)
            this.x=x;
        //else throw new ExceptionInInitializerError("cart's bound violation");
    }

    public double getX(){
        return x;
    }

    public void setX_Velocity(double x_velocity){
        this.x_velocity=x_velocity;
    }
    public double getX_Velocity(){
        return x_velocity;
    }

    public void setTheta1(double theta){
//        if(theta<-Polebalancing.thirty_six_degrees ||
//                theta>Polebalancing.thirty_six_degrees)
//            throw new ExceptionInInitializerError("Theta1 "+ theta/Math.PI*180+" is not in range");

        this.theta1=theta;
    }
    public double getTheta1(){
        return theta1;
    }

    public void setTheta2(double theta){
//        if(theta<-Polebalancing.thirty_six_degrees ||
//                theta>Polebalancing.thirty_six_degrees)
//            throw new ExceptionInInitializerError("Theta2 "+ theta/Math.PI*180+" is not in range");

        this.theta2=theta;
    }

    public double getTheta2(){
        return theta2;
    }

    public void setTheta1V(double v){
        this.theta1_velocity=v;
    }

    public double getTheta1V(){
        return this.theta1_velocity;
    }

    public void setTheta2V(double v){
        this.theta2_velocity=v;
    }

    public double getTheta2V(){
        return this.theta2_velocity;
    }
    private static double one_over_256=0.0390625;
    public void nextState(double force, State derivs){

int numPoles=2;
        double costheta_1, costheta_2=0;
        double sintheta_1, sintheta_2=0;
        double gsintheta_1, gsintheta_2=0;
        double temp_1, temp_2=0;
        double ml_1, ml_2;
        double fi_1, fi_2 = 0.0;
        double mi_1, mi_2 = 0.0;

        // action += (drand48() - 0.5) * trajNoise;
        //using tanh net output!!

        force = (force - 0.5) * Polebalancing.FORCE_MAG * 2;




        if ((force >= 0) && (force < one_over_256)) {
            force = one_over_256;
        }
        if ((force < 0) && (force > -one_over_256)) {
            force = -one_over_256;
        }


        costheta_1 = Math.cos(getValue(2));
        sintheta_1 = Math.sin(getValue(2));
        gsintheta_1 = Polebalancing.GRAVITY * sintheta_1;
        ml_1 = Polebalancing.LENGTH_1 * Polebalancing.MASSPOLE_1;
        temp_1 = Polebalancing.MUP * getValue(3) / ml_1;
        fi_1 = (ml_1 * getValue(3) * getValue(3) * sintheta_1)
                + (0.75 * Polebalancing.MASSPOLE_1 * costheta_1 * (temp_1 + gsintheta_1));
        mi_1 = Polebalancing.MASSPOLE_1 * (1 - (0.75 * costheta_1 * costheta_1));

        if (numPoles > 1) {
            costheta_2 = Math.cos(getValue(4));
            sintheta_2 = Math.sin(getValue(4));
            gsintheta_2 = Polebalancing.GRAVITY * sintheta_2;
            ml_2 = Polebalancing.LENGTH_2 * Polebalancing.MASSPOLE_2;
            temp_2 = Polebalancing.MUP * getValue(5) / ml_2;
            fi_2 = (ml_2 * getValue(5) * getValue(5) * sintheta_2)
                    + (0.75 * Polebalancing.MASSPOLE_2 * costheta_2 * (temp_2 + gsintheta_2));
            mi_2 = Polebalancing.MASSPOLE_2 * (1 - (0.75 * costheta_2 * costheta_2));
        }

        derivs.set(1,  (force - Polebalancing.MUC * Math.signum(getValue(1)) + fi_1 + fi_2)
                / (mi_1 + mi_2 + Polebalancing.MASSCART));

        derivs.set(3, -0.75 * (derivs.getValue(1) * costheta_1 + gsintheta_1 + temp_1)
                / Polebalancing.LENGTH_1);
        if (numPoles > 1) {
            derivs.set(5, -0.75 * (derivs.getValue(1) * costheta_2 + gsintheta_2 + temp_2)
                    / Polebalancing.LENGTH_2);
        }
    }
    public void set(int index, double value){
        switch(index){
            case 0:
                setX(value);
                break;
            case 1:
                setX_Velocity(value);
                break;
            case 2:
                setTheta1(value);
                break;
            case 3:
                setTheta1V(value);
                break;
            case 4:
                setTheta2(value);
                break;
            case 5:
                setTheta2V(value);
                break;
        }
    }
    public double getValue(int index){
        double ret=0;

        switch(index){
            case 0 :
                ret=getX();break;
            case 1:
                ret=getX_Velocity();break;
            case 2:
                ret=getTheta1(); break;
            case 3:
                ret=getTheta1V();break;
            case 4:
                ret=getTheta2(); break;
            case 5:
                ret=getTheta2V(); break;
        }
        return ret;
    }

    public Instance toInstance(boolean withVelocity){
        
        
        DenseInstance ins;
        if(withVelocity) { 
            double []values=new double[6];
            values[0]=x;
            values[1]=x_velocity;
            values[2]=theta1;
            values[3]=theta1_velocity;
            values[4]=theta2;
            values[5]=theta2_velocity;
            ins=new DenseInstance(values);
        }
        else{
            double []values=new double[3];
            values[0]=x;

            values[1]=theta1;

            values[2]=theta2;

            ins=new DenseInstance(values);
        }
        return ins;
    }

    public boolean violated(){
        return x<-2.4 || x>2.4 || theta1<-Polebalancing.thirty_six_degrees
                || theta1>Polebalancing.thirty_six_degrees
                || theta2<-Polebalancing.thirty_six_degrees
                || theta2>Polebalancing.thirty_six_degrees;
    }

}
