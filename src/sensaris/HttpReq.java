/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

/**
 *
 * @author pszjmb
 */
public class HttpReq implements DataHandler{

    SpBaseClient client;
    String user = "";
    String pwrd="";
    String proc="";
    
    //Params should be: String url, String proxyUrl, String port
    public void init(String[] params) {
        client = new SpXMLRpcClient("", "", Level.INFO);
        if (params.length >= 3 && null != params[1] && null != params[2]) {
            if (null == client.setProxyClient(params[0],//"http://timestreams.wp.horizon.ac.uk/xmlrpc.php", 
                    params[1], params[2])) {//"wwwcache-20.cs.nott.ac.uk", "3128")){
                System.err.println("Could not set proxy client");
                return;
            }
        }else{
            client = (SpBaseClient)client.simpleClient(params[0], true, true);
        }
        
        if(params.length >= 6){
            user = params[3];
            pwrd=params[4];
            proc=params[5];
        }
    }

    @Override
    public void execute(String[] data) {
        //Object[] params = new Object[]{"admin","Time349","wp_1_ts_C02_66"};
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String time = dateFormat.format( new Date() );

        client.execute(proc, new Object[]{user, pwrd, data[0],data[1],time});
    }
}
