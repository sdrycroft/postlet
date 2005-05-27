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

public class Upload extends Thread
{
    String [] filenames;
    Main main;
    int [] fileSize;
    URL destination;
    
    /** Creates a new instance of Upload */
    public Upload(String [] f, int[] fs, Main m, URL u)
    {
        filenames = f;
        main = m;
        fileSize = fs;
        destination = u;
    }
    
    public void run()
    {
        for(int i=0; i<filenames.length; i+=10)
        {    
            int j=0;
            while(j<10 && (i+j)<filenames.length)
            {
                UploadThread u[] = new UploadThread[filenames.length];                
                try 
                {
                    InetAddress myAdr = InetAddress.getLocalHost();
                    String thisComp = myAdr.getHostAddress();
                    String uploadDest = "http://130.209.46.59/image_database/upload/upload_php_files/"+thisComp+".php";
                    u[i+j] = new UploadThread(uploadDest,filenames[i+j], fileSize[i+j], main);
                    u[i+j].start();
                    try{
                        u[i+j].sleep(1000);}
                    catch(InterruptedException ie){System.out.println("### exception");}
                    j++;
                }
                catch (UnknownHostException e) {System.err.println(e.getMessage());}
                
                
            }
        }
    }
}
