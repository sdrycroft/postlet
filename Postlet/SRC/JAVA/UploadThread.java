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
      String response = f.getPOSTRequestResponse();
      System.out.println("***"+response+"***");
      if (response.indexOf("NO")>=0)
      {
          if (response.indexOf("[error] => 1")<0){
              try{
                  sleep(1000);}
              catch(InterruptedException ie){System.out.println("### exception");}
              if (i<5)
              {
                  System.out.println("Error, retrying file \""+filename+"\"");
                  i++;
                  run();
              }
          }
          else {
              System.out.println("The file is too large");
              i = 5;
          }
      }          
      main.setProgress(fileSize);    
   }
}