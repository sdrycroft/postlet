import java.io.*;

public class UploadThread extends Thread
{
   private String filename;
   private String scriptURL;
   private Main main;
   private int fileSize;
   private int i;
   
   public UploadThread(String s, String f, int fs, Main m)
   {
      filename = f;
      scriptURL = s;
      main = m;
      fileSize = fs;
      i = 0;
   }
   
   public void run() 
   {      
      FileUploader f = new FileUploader(scriptURL);
      f.uploadFile(filename);
      main.setProgress(fileSize);
      System.out.println("***"+f.getPOSTRequestResponse()+"***");
      if (f.getPOSTRequestResponse().indexOf("File is valid inserting data")>0)
      {
          try{
              sleep(1000);}
          catch(InterruptedException ie){System.out.println("### exception");}
          if (i<5)
          {
              i++;
              run();
          }
      }              
   }
}