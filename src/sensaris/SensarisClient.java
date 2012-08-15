/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.ResponseCodes;

/**
 *
 * @author pszjmb
 */
public class SensarisClient {
     public static void main(String[] args) throws IOException, InterruptedException {

        String serverURL =
           "btgoep://000780441BF2:2;authenticate=false;encrypt=false;master=false";
        System.out.println("Connecting to " + serverURL);
        ClientSession clientSession = (ClientSession) Connector.open(serverURL);        
        HeaderSet hsConnectReply = clientSession.connect(null);
        if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
            System.out.println("Failed to connect");
            return;
        }
        
        clientSession.disconnect(null);
        clientSession.close();
     }
}
