/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
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
    private final Map<String, ConsumeReqQueue> myQueues = new HashMap();
    List<Thread> consumerThreads = new ArrayList();//Thread(new ConsumeReqQueue(myQueue));
    SimpleDateFormat dateFormat;
    String time;
    boolean running = true;
    private final ReentrantLock reqLock = new ReentrantLock();

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void addConsumerQueue(String name) {
        ConsumeReqQueue c = new ConsumeReqQueue(new LinkedBlockingQueue());
        myQueues.put(name, c);
        Thread t = new Thread(c);
        consumerThreads.add(t);
        t.start();
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
    }

    @Override
    public void execute(String[] data) {
        time = dateFormat.format(new Date());
        //Object[] params = new Object[]{"admin","Time349","wp_1_ts_C02_66"};
        if (!myQueues.containsKey(data[0])) {
            addConsumerQueue(data[0]);
        }
        myQueues.get(data[0]).put(new ReqObj(proc, new Object[]{user, pwrd, data[0], data[1], time}));
        System.out.println(time + " " + data[0] + " " + data[1]);
    }

    private class ConsumeReqQueue implements Runnable {

        private final BlockingQueue<ReqObj> queue;
        private final ReentrantLock takeLock = new ReentrantLock();
        private int insertionCounter = 0;

        public ConsumeReqQueue(BlockingQueue<ReqObj> queue) {
            this.queue = queue;
        }

        public void put(ReqObj ro) {
            try {
                queue.put(ro);
            } catch (InterruptedException ex) {
                Logger.getLogger(HttpReq.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            while (running) {
                final ReentrantLock takeLock = this.takeLock;
                Collection<ReqObj> tempCol = new Vector<ReqObj>();
                try {
                    takeLock.lockInterruptibly();
                    tempCol.add(queue.take());
                    queue.drainTo(tempCol);
                    queue.clear();
                } catch (InterruptedException ex) {
                } finally {
                    takeLock.unlock();
                }
                consume(tempCol);
            }
        }

        void consume(Collection<ReqObj> tempCol) {
            ReqObj req = null;
            Iterator<ReqObj> it = tempCol.iterator();
            boolean first = true;
            Vector params = new Vector();
            Object[] reqParams = null;
            while (it.hasNext()) {
                req = it.next();
                reqParams = req.getParams();
                if (first) {
                    first = false;
                    params.add(reqParams[0]);
                    params.add(reqParams[1]);
                    params.add(reqParams[2]);
                }
                params.add(reqParams[3]);
                params.add(reqParams[4]);
            }
            synchronized (client) {
                if (null != reqParams) {
                    System.out.println(reqParams[2] + " " + ++insertionCounter + " " + params.toString());
                    System.out.println(reqParams[2] + " " + insertionCounter + " " + client.execute(req.getProc(), params.toArray()));
                }
            }
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
