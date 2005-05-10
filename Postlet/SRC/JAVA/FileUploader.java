import java.io.*;
import java.net.*;
import javax.swing.*;

public class FileUploader 
{
    HttpURLConnection httpURLConn;
    private static String boundary="leedsunitedarethebestteaminthepremierleagueHoHum1966andallthat";
    private static String twoHyphens="--";
    private static String lineEnd="\r\n";
       
    public FileUploader(String phpScript)
    {
       httpURLConn = null;
       try
       {
          URL theURL = new URL(phpScript);
          httpURLConn  = (HttpURLConnection)theURL.openConnection();          
          httpURLConn.setRequestMethod("POST");
          httpURLConn.setRequestProperty("Connection", "Keep-Alive");
          httpURLConn.setDoOutput(true);
          httpURLConn.setUseCaches(false);
          httpURLConn.setRequestProperty("Accept-Charset", "iso-8859-1,*,utf-8");
          httpURLConn.setRequestProperty("Accept-Language", "en");
          httpURLConn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
       }
       catch (MalformedURLException ex){System.out.println("### exception");}
       catch (IOException ioex){System.out.println("### exception");}
    }
    
    public void uploadFile(String filename)
    {
        DataOutputStream outStream;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024*1024;
        
        try 
        {
            outStream = new DataOutputStream (httpURLConn.getOutputStream ());
            outStream.writeBytes(twoHyphens + boundary + lineEnd);
            FileInputStream fileInputStream = new FileInputStream(new File(filename));
            outStream.writeBytes("Content-Disposition: form-data; name=\"userfile\";" + " filename=\""+ filename + "\"" + lineEnd);
            outStream.writeBytes(lineEnd);
            
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            
            buffer = new byte[bufferSize];
            
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) 
            {
               outStream.write(buffer, 0, bufferSize);
               bytesAvailable = fileInputStream.available();
               bufferSize = Math.min(bytesAvailable,maxBufferSize);
               
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            fileInputStream.close();
            outStream.writeBytes(lineEnd);
                        
            outStream.writeBytes(lineEnd);
            
            outStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            
            outStream.flush ();
            outStream.close ();
        }
        catch (IOException ioex){;}
    }
    
    public String getPOSTRequestResponse()
    {
       BufferedReader inStream;
       StringBuffer results = new StringBuffer();
       String newURL = "";
       try
       {
          inStream = new BufferedReader(new InputStreamReader(httpURLConn.getInputStream()));
          String str;
          int i = 0;
          while ((str = inStream.readLine()) != null)
             results.append(str+"\n");
          
          inStream.close ();
       }
       catch (IOException ioex){;}
       return results.toString();
    }
}