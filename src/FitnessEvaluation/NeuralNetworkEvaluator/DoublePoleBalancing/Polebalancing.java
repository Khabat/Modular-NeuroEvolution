/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.NeuralNetworkEvaluator.DoublePoleBalancing;

/**
 *
 * @author Khabat
 */
public class Polebalancing {
      static final double GRAVITY=-9.8;
      static final double MASSCART=1.0;
      static final double MASSPOLE_1=0.1;
      static final double MASSPOLE_2=0.01;
      static final double TOTAL_MASS=MASSPOLE_1 + MASSCART + MASSPOLE_2;
      static final double LENGTH_1=0.5;	  /* actually half the pole's length */
      static final double LENGTH_2=0.05;	  /* actually half the pole's length */

      final static double MUP = 0.000002;
      final static double MUC = 0.0005;
      static final double FORCE_MAG=10.0;
      static final double TAU=0.02;	  /* seconds between state updates */
      static final double FOURTHIRDS=1.3333333333333;

      static final double one_degree= 0.0174532;	/* 2pi/360 */
      //static final double six_degrees= 0.1047192;
      //static final double twelve_degrees= 0.2094384;
      //static final double fifteen_degrees= 0.2617993;
      static final double thirty_six_degrees= 0.628329;
      //static final double fifty_degrees= 0.87266;
      public State state;
      public Polebalancing(State s){
          state=new State(s);
      }
      public void performAction(double force){
          State dydx=new State();
          for (int i = 0; i < 2; ++i) {
              dydx.setX( state.getX_Velocity() );
              dydx.setTheta1(state.getTheta1V());
              dydx.setTheta2(state.getTheta2V());

              state.nextState(force,dydx);
              
              rk4(force, state, dydx, state);
          }
      }


      private static void rk4(double force, State y, State dydx, State yout) {

        int i;

        double hh, h6;
        //double dym[]=new double[6];
        State dym = new State();
        //double dyt[]=new double[6];
        State dyt = new State();
        //double yt []=new double[6];
        State yt = new State();



        int vars = 3;
        int numPoles=2;
        if (numPoles > 1) {
            vars = 5;
        }

        hh = TAU * 0.5;
        h6 = TAU / 6.0;
        for (i = 0; i <= vars; i++) {
            yt.set(i, y.getValue(i) + hh * dydx.getValue(i));
        }
        yt.nextState(force, dyt);
        dyt.set(0, yt.getValue(1));
        dyt.set(2, yt.getValue(3));
        dyt.set(4, yt.getValue(5));

        for (i = 0; i <= vars; i++) {

            yt.set(i, y.getValue(i)+hh*dyt.getValue(i));
        }
        yt.nextState(force, dym);
        dym.set(0, yt.getValue(1));
        dym.set(2, yt.getValue(3));
        dym.set(4, yt.getValue(5));
        
        for (i = 0; i <= vars; i++) {
            yt.set(i, y.getValue(i)+Polebalancing.TAU*dym.getValue(i));
            dym.set(i, dym.getValue(i) + dyt.getValue(i));
        }
        
        yt.nextState(force, dyt);

        dyt.set(0, yt.getValue(1));
        dyt.set(2, yt.getValue(3));
        dyt.set(4, yt.getValue(5));
        
        for (i = 0; i <= vars; i++) {
            yout.set(i, y.getValue(i) + h6 * (dydx.getValue(i)+dyt.getValue(i)+
                    2.0 * dym.getValue(i)));
        }

    }

      public static void main (String [] args){
          State s=new State();
          s.setTheta1(20.0/180*Math.PI);
          Polebalancing b=new Polebalancing(new State());
          b.state=s;
          b.performAction(0.5);
          System.out.print(s.getX());
      }


}
