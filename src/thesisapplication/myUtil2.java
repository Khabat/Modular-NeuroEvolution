/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thesisapplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;

/**
 *
 * @author Administrator
 */
public class myUtil2 {
    public static String serializeToString(Dataset d) throws UnsupportedEncodingException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out=null ;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(d);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ThesisView.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<bos.toByteArray().length;i++){
            sb.append((char)bos.toByteArray()[i]);
        }
        return sb.toString();
    }

     public static Dataset deSerializeToString(String objectS){
        byte []arr=new byte[objectS.length()];
        for(int i=0;i<objectS.length();i++)
            arr[i]=(byte)objectS.charAt(i);
        ByteArrayInputStream bis = new ByteArrayInputStream(arr) ;
        ObjectInput in=null ;
        Dataset d=null;
        try {
            in = new ObjectInputStream(bis);
            d=(Dataset)in.readObject();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ThesisView.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(ClassNotFoundException exp){
             Logger.getLogger(ThesisView.class.getName()).log(Level.SEVERE, null, exp);
        }
        return d;
    }

}
