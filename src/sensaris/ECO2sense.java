/*
 * Bluetooth communication for ECO2sense device
 */
package sensaris;

/**
 *
 * @author pszjmb
 */
public class ECO2sense extends RFCOMMClient{

    /**
     * 
     * @param btspp 
     */
    public ECO2sense(String btspp,DataHandler dhIn) {
        super(btspp, dhIn);
    }
    
    @Override
    public void processData(String data) {
        String[] columns = data.split(",");
        float value=-1;

        if (columns.length < 5) {
            return;
        }

        if ("$PSEN".equals(columns[1])) {
            if ("CO2".equals(columns[2])) {
                // ppm (not v)
                value = Float.parseFloat(columns[4]);
                System.out.println("CO2 " + value);
            }
        } else if ("$GPRMC".equals(columns[1])) {
            // This is the GPS. See http://sensing2010.blogspot.co.uk/p/device.html for format
        }
    }    

    public static void main(String args[]) {       
        //btgoep://00078046E0B4:1 == Senspod 78
        DataHandler dh = new HttpReq();
        dh.init(args);
        RFCOMMClient rcom = new ECO2sense("btspp://00078046E0B4:1", dh);
        Thread t = new Thread(rcom);
        t.start();        
    }
}
