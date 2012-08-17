/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pszjmb
 */
public class HttpReq implements DataHandler {

    SpBaseClient client;
    String user = "";
    String pwrd = "";
    String proc = "";
    private final BlockingQueue<ReqObj> myQueue = new LinkedBlockingQueue();
    Thread consumer = new Thread(new ConsumeReqQueue(myQueue));
    SimpleDateFormat dateFormat;
    String time;
    boolean running = true;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    

    //Params should be: String url, String proxyUrl, String port
    @Override
    public void init(String[] params) {
        client = new SpXMLRpcClient("", "", Level.INFO);
        if (params.length >= 3 && !("null".equals(params[1]) || "null".equals(params[2]))) {
            if (null == client.setProxyClient(params[0],//"http://timestreams.wp.horizon.ac.uk/xmlrpc.php", 
                    params[1], params[2])) {//"wwwcache-20.cs.nott.ac.uk", "3128")){
                System.err.println("Could not set proxy client");
                return;
            }
        } else if (params.length >= 1) {
            client.simpleClient(params[0], true, true);
        }

        if (params.length >= 6) {
            user = params[3];
            pwrd = params[4];
            proc = params[5];
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        consumer.start();
    }

    @Override
    public void execute(String[] data) {
        try {
            time = dateFormat.format(new Date());
            //Object[] params = new Object[]{"admin","Time349","wp_1_ts_C02_66"};
            myQueue.put(new ReqObj(proc, new Object[]{user, pwrd, data[0], data[1], time}));
        } catch (InterruptedException ex) {
            Logger.getLogger(HttpReq.class.getName()).log(Level.INFO, null, ex);
        }
    }

    private class ConsumeReqQueue implements Runnable {

        private final BlockingQueue<ReqObj> queue;

        public ConsumeReqQueue(BlockingQueue<ReqObj> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    consume(queue.take());
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(HttpReq.class.getName()).log(Level.INFO, null, ex);
            }
        }

        void consume(ReqObj req) {
            client.execute(req.getProc(), req.getParams());
        }
    }

    private class ReqObj {

        private String proc;
        private Object[] params;

        public ReqObj(String proc, Object[] params) {
            this.proc = proc;
            this.params = params;
        }

        public Object[] getParams() {
            return params;
        }

        public void setParams(Object[] params) {
            this.params = params;
        }

        public String getProc() {
            return proc;
        }

        public void setProc(String proc) {
            this.proc = proc;
        }
    }
}
