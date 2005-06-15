/*
 * Upload.java
 *
 * Created on 11 June 2003, 11:49
 */

/**
 *
 * @author  simon
 */
import java.net.*;

public class Upload extends Thread {
    String [] filenames;
    Main main;
    int [] fileSize;
    String destination;
    
    /** Creates a new instance of Upload */
    public Upload(String [] f, int[] fs, Main m, String d) {
        filenames = f;
        main = m;
        fileSize = fs;
        destination = d;
    }
    
    public void run() {
        /*
        for(int i=0; i<filenames.length; i+=10) {
            int j=0;
            while(j<10 && (i+j)<filenames.length) {
                UploadThread u[] = new UploadThread[filenames.length];
                try {
                    InetAddress myAdr = InetAddress.getLocalHost();
                    String thisComp = myAdr.getHostAddress();
                    u[i+j] = new UploadThread(destination,filenames[i+j], fileSize[i+j], main);
                    u[i+j].start();
                    try{
                        u[i+j].sleep(1000);} catch(InterruptedException ie){System.out.println("### exception");}
                    j++;
                } catch (UnknownHostException e) {System.err.println(e.getMessage());}
         
         
            }
        }
         */
        for(int i=0; i<filenames.length; i++) {
            UploadThread u = new UploadThread(destination,filenames[i], fileSize[i], main);
            u.start();
            // Wait for thread to finish
            while(u.isAlive()){;}
            System.gc();
        }
    }
}
