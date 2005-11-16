package Postlet;
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
        for(int i=0; i<filenames.length; i++) {
            try {
                UploadThread u = new UploadThread(destination,filenames[i], fileSize[i], main);
                u.upload();
                System.gc();
            }
            catch (java.lang.OutOfMemoryError memerr){
                System.out.println("Out of memory!");
            }
        }
    }
    
}
