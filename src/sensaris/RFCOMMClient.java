/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;

public class RFCOMMClient implements Runnable{

    Map<String, Float> values = new HashMap<String, Float>();
    // ripped from sensaris decompile
    private static double aO3 = 0.77D;
    private static double bO3 = 1.367D;

    private static double aRhO3 = -0.031D;
    private static double bRhO3 = 0.165D;

    private static double aTO3 = -0.152D;
    private static double bTO3 = 0.3664D;

    private static double RlO3 = 300000.0D;

    private static double alphaTNOx = 707.0D;
    private static double betaTNOx = -2.03D;

    private static double RlNOx = 30000.0D;
    private boolean running = true;

    public static void main(String args[]) {
        RFCOMMClient rcom = new RFCOMMClient();
        Thread t = new Thread(new RFCOMMClient());
        t.start();        
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public void run(){
        //btspp://000780441BF2:1 == Senspod 76
        //btgoep://00078046E0B4:1 == Senspod 78
        try {
            StreamConnection conn = (StreamConnection) 
                    Connector.open("btspp://000780441BF2:1");

            InputStream in = conn.openInputStream();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(in));
            String data;

            while (running) {
                data = inStream.readLine();
                processData(data);
            }
            conn.close();
        } catch (IOException e) {
            System.err.print(e.toString());
        }
    }

    public void processData(String data) {
        String[] bits = data.split(",");

        if (bits.length < 5) {
            return;
        }

        if ("$PSEN".equals(bits[1])) {
            if ("Batt".equals(bits[2])) {
                // min = 3.0, max = 4.5, volts
                //System.out.println(bits[2] + " " + bits[3] + " " + bits[4]);
                float value = Float.parseFloat(bits[4]);
                values.put("batt", value);

                System.out.println("batt " + value);
            }
            if ("Noise".equals(bits[2])) {
                // min = 0, max = 140, db
                //System.out.println(bits[2] + " " + bits[3] + " " + bits[4]);
                float value = Float.parseFloat(bits[4]);
                values.put("noise", value);
                System.out.println("noise " + value);
            }
            if ("NOx".equals(bits[2])) {
                // min = 0, max = 500, v?
                //System.out.println(bits[2] + " " + bits[3] + " " + bits[4]);
                float value = Float.parseFloat(bits[4]);

                if (!values.containsKey("temp")) {
                    return;
                }

                if (!values.containsKey("hum")) {
                    return;
                }

                float temp = values.get("temp");
                float hum = values.get("hum");

                // ripped from sensaris decompile
                double dvalue = value;
                double A2 = aRhO3 * hum + bRhO3;
                double B2 = aTO3 * temp + bTO3;
                double x = Math.log(RlO3 * dvalue) / (3.3D - dvalue);
                value = (float) Math.exp((x - A2 - B2 - bO3) / aO3);
                values.put("nox", value);

                System.out.println("nox " + value * 0.01);

            }
            if ("COx".equals(bits[2])) {
                // min = 0, max = 500, ppm (not v)
                //System.out.println(bits[2] + " " + bits[3] + " " + bits[4]);
                float value = Float.parseFloat(bits[4]);
                values.put("cox", value);

                System.out.println("cox " + value);
            }
            if ("Hum".equals(bits[2])) {
                // min = 0, max = 100, %
                // temp min = -20, max = 120, F
                //System.out.println(bits[2] + " " + bits[3] + " " + bits[4] + " " + bits[5] + " " + bits[6]);
                float hvalue = Float.parseFloat(bits[4]);
                values.put("hum", hvalue);
                float tvalue = Float.parseFloat(bits[6]);
                values.put("temp", tvalue);

                System.out.println("hum " + hvalue);
                System.out.println("temp " + tvalue);

            }
        } else if ("$GPRMC".equals(bits[1])) {
        }
    }
}
