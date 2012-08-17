/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensaris;

/**
 *
 * @author pszjmb
 */
public interface DataHandler{
    public void init(String[] params);
    public void execute(String[] data);
    public void setRunning(boolean running);
    
}
