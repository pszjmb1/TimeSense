/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pszjmb
 */
public class ECOsense extends RFCOMMClient{
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
    Float temp = null;
    Float hum = null;
    float value = -1;

    /**
     * 
     * @param btspp 
     */
    public ECOsense(String btspp,DataHandler dhIn) {
        super(btspp, dhIn);
    }
    
    @Override
    public void processData(String data) {
        String[] columns = data.split(",");

        if (columns.length < 5) {
            return;
        }

        if ("$PSEN".equals(columns[1])) {
            if ("Batt".equals(columns[2])) {
                // min = 3.0, max = 4.5, volts
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                value = Float.parseFloat(columns[4]);
                //values.put("batt", value);

                System.out.println("batt " + value);
                myHandler.execute(new String[]{"wp_1_ts_Battery_72",""+value});
            }
            if ("Noise".equals(columns[2])) {
                // min = 0, max = 140, db
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                value = Float.parseFloat(columns[4]);
                System.out.println("noise " + value);
                myHandler.execute(new String[]{"wp_1_ts_noise_73",""+value});
            }
            if ("NOx".equals(columns[2])) {
                // min = 0, max = 500, v?
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                double dvalue = Double.parseDouble(columns[4]);

                if (null == temp || null == hum) {
                    return;
                }

                // ripped from sensaris decompile
                double A2 = aRhO3 * hum + bRhO3;
                double B2 = aTO3 * temp + bTO3;
                double x = Math.log(RlO3 * dvalue) / (3.3D - dvalue);
                value = (float) Math.exp((x - A2 - B2 - bO3) / aO3);

                System.out.println("nox " + value);
                myHandler.execute(new String[]{"wp_1_ts_Nitrogen_Oxide_77",""+value});
                

            }
            if ("COx".equals(columns[2])) {
                // min = 0, max = 500, ppm (not v)
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                value = Float.parseFloat(columns[4]);

                System.out.println("cox " + value);
                myHandler.execute(new String[]{"wp_1_ts_cox_74",""+value});
            }
            if ("Hum".equals(columns[2])) {
                // min = 0, max = 100, %
                // temp min = -20, max = 120, F
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4] + " " + columns[5] + " " + columns[6]);
                hum = Float.parseFloat(columns[4]);
                temp = Float.parseFloat(columns[6]);

                System.out.println("hum " + hum);
                myHandler.execute(new String[]{"wp_1_ts_Humidity_75",""+hum});
                System.out.println("temp " + temp);
                myHandler.execute(new String[]{"wp_1_ts_Temperature_76",""+temp});

            }
        } else if ("$GPRMC".equals(columns[1])) {
            // This is the GPS. See http://sensing2010.blogspot.co.uk/p/device.html for format
        }
    }    

    public static void main(String args[]) {
        //btspp://000780441BF2:1 == Senspod 76
        DataHandler dh = new HttpReq();
        dh.init(args);
        RFCOMMClient rcom = new ECOsense("btspp://000780441BF2:1", dh);
        Thread t = new Thread(rcom);
        t.start();        
    }
}
