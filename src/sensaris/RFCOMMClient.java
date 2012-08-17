/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

import java.io.*;
import javax.microedition.io.*;

public abstract class RFCOMMClient implements Runnable{

    private String btspp;       // bluetooth address in the form: btspp://000780441BF2:1
    private boolean running = true;
    protected DataHandler myHandler;

    public RFCOMMClient(String btspp, DataHandler dhIn) {
        this.btspp = btspp;
        myHandler = dhIn;
    }

    public String getBluetoothAddressspp() {
        return btspp;
    }

    /**
     * In the form btspp://000780441BF2:1
     * @param btspp 
     */
    public void setBluetoothAddress(String btspp) {
        this.btspp = btspp;
    }
        
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
    @Override
    public void run(){
        try {
            StreamConnection conn = (StreamConnection) 
                    Connector.open(btspp);

            InputStream in = conn.openInputStream();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(in));
            String data;

            while (running) {
                data = inStream.readLine();
                //System.out.println(data);
                processData(data);
            }
            myHandler.setRunning(false);
            conn.close();
        } catch (IOException e) {
            System.err.print(e.toString());
        }
    }

    public abstract void processData(String data);
}
