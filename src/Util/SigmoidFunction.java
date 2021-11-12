/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Util;

import java.util.Stack;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 *
 * @author Administrator
 */
public class SigmoidFunction extends PostfixMathCommand {
    public SigmoidFunction(){
        numberOfParameters=1;
    }
    @Override
    public void run(Stack inStack) throws ParseException {
        checkStack(inStack);

        // get the parameter from the stack
        Object param = inStack.pop();

        // check whether the argument is of the right type
        if (param instanceof Double) {
            // calculate the result
            double r = 1/(1+Math.pow(Math.E,-((Double) param).doubleValue())) ;
            // push the result on the inStack
            inStack.push(new Double(r));
        } else {
            throw new ParseException("Invalid parameter type");
        }

    }
}
