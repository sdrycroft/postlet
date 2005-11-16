import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.URL;
import java.lang.Integer;
import javax.swing.*;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.FileNotFoundException;

public class FileUploader {
    private static String lotsHyphens="---------------------------";
    private static String lineEnd="\n";
    private String header, footer, request;
    private File file;
    private URL url;
    private String boundary ="kjaslfkajsdhfnassfioausycntiasuhcn";
    private Main main;
    
    public FileUploader(String phpScript, Main m) throws MalformedURLException{
        url = new URL(phpScript);
        main = m;
    }
    
    // Throw the host exception as will be handled in the same way as the
    // malformed URL exception (this will stop all threads, as will affect
    // all files).
    public void uploadFile(String filename) {
        
        try {
            file = new File(filename);
            this.setBoundary();
            this.setHeaderAndFooter();
            
            Socket sock = new Socket(url.getHost(),80);
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            
            output.writeBytes(request);
            output.writeBytes(header);
            
            byte [] byteBuff = new byte[1024];
            FileInputStream fileStream = new FileInputStream(file);
            int numBytes = 0;
            
            if (file.length()>Integer.MAX_VALUE){
                throw new IOException("File is too large for upload");
            }
            
            // Following reads the file, and streams it.
            /////////////////////////////////////////////////////////////     
            int maxBufferSize = 1024;
            int bytesAvailable = fileStream.available();
            System.out.println("File available: "+bytesAvailable);
            int bufferSize = Math.min(bytesAvailable,maxBufferSize);
            
            byte buffer [] = new byte[bufferSize];
            
            int bytesRead = fileStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) 
            {
               output.write(buffer, 0, bufferSize);
               bytesAvailable = fileStream.available();
               bufferSize = Math.min(bytesAvailable,maxBufferSize);
               
               bytesRead = fileStream.read(buffer, 0,  bufferSize);
            }
            ///////////////////////////////////////////////////////////
            
            output.writeBytes(footer);
            
            // Seems to be a bug in the way that java (or Apache), or more likely
            // I handle the file sizes and thus the content-length (which is always
            // too high).  Thus, send blanks till the apache server finishes
            // listening for input.            
            // Shouldn't need more than 1KB
            /*
             for (int i = 0; i<1024; i++){
                output.write(0);
            }
             */
            
            output.flush();
            //output.close();
            
            StringBuffer replyString = new StringBuffer();
            
            String line = "";
            while ((line = input.readLine())!=null){
                replyString.append(line+lineEnd);
            }
            //output.close();
            input.close();
            output.close();
            sock.close();
            
            main.setProgress((int)file.length());
            
            output.close();
            
            header = null;
            footer = null;
            boundary = null;
            file = null;
            url = null;
            
        } catch(IOException ioe){
            // Some kind of error caught
            System.out.println(ioe.getMessage());
            ioe.printStackTrace(System.out);
        }
    }
    
    public String getPostRequestResponse(){
        
        return "";
    }
    
    private void setBoundary(){
        
        
    }
    
    private void setHeaderAndFooter(){
        
        header = new String();
        footer = new String();
        request = new String();
        
        String afterContent = lotsHyphens +"--"+ boundary + lineEnd +
                                    "Content-Disposition: form-data; name=\"userfile\"; filename=\""+file.getName()+"\""+lineEnd+
                                    "Content-Type: application/octet-stream"+lineEnd+lineEnd;
        
        footer = lineEnd + lineEnd + "--"+ lotsHyphens+boundary+"--"+lineEnd;
        
        request="POST ";
        request +=url.getFile();
        request +=" HTTP/1.1";
        request +=lineEnd;
               
        header +="Host: ";
        header +=url.getHost();
        header +=lineEnd;
        
        header +="User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.10)";
        header +=lineEnd;
        
        header +="Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
        header += lineEnd;
        
        header +="Accept-Language: en-us,en;q=0.5";
        header += lineEnd;
                
        header +="Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7";
        header += lineEnd;
                
        header +="Content-Type: multipart/form-data; boundary=";
        header +=lotsHyphens;
        header +=boundary;
        header +=lineEnd;
                       
        header +="Content-Length: ";
        header += ""+(file.length()+afterContent.length());
        //header += ""+file.length();
        header +=lineEnd;
        header +=lineEnd;
                
        header +=afterContent;
        
        System.out.println("****************************************************************");
        System.out.println("HeaderLength: "+header.length());
        System.out.println("FooterLength: "+footer.length());
        System.out.println("RequestLength: "+request.length());
        System.out.println("FileLength: "+file.length());
        System.out.println("Hypens Length: "+lotsHyphens.length());
        System.out.println("Boundary Length: "+boundary.length());
        System.out.println("AfterContent length: "+afterContent.length());
    }
}