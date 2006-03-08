/*
 * JavascriptListener.java
 *
 * Created on 08 March 2006, 10:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author simon
 */
public class JavascriptListener extends Thread{
    
    Main main;
    
    /** Creates a new instance of JavascriptListener */
    public JavascriptListener(Main m) {
        main = m;
    }    
    
    public void run() {
        
         while (true){
             if (main.getJavascriptStatus()){
                 main.setJavascriptStatus();
                 if (main.getButtonClicked() == 0){
                     main.addClick();
                 }
                 else if (main.getButtonClicked() == 1){
                     main.uploadClick();
                 }
             }
             try{
                 sleep(500);
             }
             catch (Throwable t){
                 t.printStackTrace();
             }
         }
    }
}
