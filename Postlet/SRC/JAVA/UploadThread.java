/*  Copyright (C) 2005 Simon David Rycroft

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.FileNotFoundException;

public class UploadThread extends Thread{

    private File file;
    private String scriptURL;
    private Main main;
    private int i;


    private static final String lotsHyphens="---------------------------";
    private static final String lineEnd="\n";
    private String header, footer, request, reply, afterContent;
    private URL url;
    private String boundary;

    public UploadThread(String s, File f, Main m) {

        file = f;
        scriptURL = s;
        main = m;
        i = 0;
    }

    public void run(){
        try {
            upload();
        }
        catch (java.net.UnknownHostException uhe){;}
        catch (java.net.MalformedURLException mue){;}
        catch (java.io.FileNotFoundException fnfe){
            // A file has been moved or deleted. This file will NOT
            // be uploaded.

        }
    }

    private void upload() throws java.net.MalformedURLException, java.net.UnknownHostException, java.io.FileNotFoundException{

        url = new URL(scriptURL);
        this.uploadFile();
        System.out.println("***"+reply+"***");
        if (reply != null && reply.indexOf("FILEFAILED")>=0) {
            if (reply.indexOf("FILETOOBIG")<0){
                if (i<3) {
                    main.setProgress(-(int)file.length());
                    System.out.println("Error, retrying file \""+file.getName()+"\"");
                    i++;
                    this.upload();
                }
            } else {
                System.out.println("The file is too large");
                i = 5;
            }
        }
    }

    // Throw the host exception as will be handled in the same way as the
    // malformed URL exception (this will stop all threads, as will affect
    // all files).
    private void uploadFile() throws UnknownHostException{
        
        try {
            this.setBoundary(40);
            this.setHeaderAndFooter();
            Socket sock = getSocket();

            // Output stream, for writing to the socket.
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            // Reader for accepting the reply from the server.
            BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            // Write the request, and the header.
            output.writeBytes(request);
            output.writeBytes(header);
            System.out.println(request+header);


            output.flush();
            // Read the reply
            // OK OK, don't bother reading the reply yet, I'll get round to changing this.
            // just send the post request.
            try {
                sleep(1000);
            }
            catch (java.lang.InterruptedException ie){;}
            // Read the reply
            String line="";
            while ((line = input.readLine())!=null){
                if (line.equalsIgnoreCase(lineEnd))
                    break;
                System.out.println(line);
            }

            System.out.println("Finished reading input.  Now outputing file");
            
            //output = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            output.writeBytes(afterContent);

            try {
                // Following reads the file, and streams it.
                /////////////////////////////////////////////////////////////             
                FileInputStream fileStream = new FileInputStream(file);
                int numBytes = 0;
    
                if (file.length()>Integer.MAX_VALUE){
                    throw new IOException("File is too large for upload");
                }

                // Size of buffer - May need reducing if users encounter
                // memory issues.
                int maxBufferSize = 1024*256;
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
    
                output.flush();
    
                // Read the reply
                reply ="";
                while ((line = input.readLine())!=null){
                    reply += line;
                }
    
                // Close the socket and streams.
                input.close();
                output.close();
                sock.close();
    
                // Set the progress, that this file has uploaded.
                main.setProgress((int)file.length());
            }
            catch (java.io.FileNotFoundException fnfe){
                // Output a warning.
                // Make adjustments to the total length of all the files.
                // removing this file from the list of files.
            }

        } catch(IOException ioe){
            // Some kind of error caught
            System.out.println(ioe.getMessage());
            ioe.printStackTrace(System.out);
        }
    }

    private Socket getSocket() throws UnknownHostException, IOException{
        Socket sock;
        String proxyHost = System.getProperties().getProperty("deployment.proxy.http.host");
        String proxyPort = System.getProperties().getProperty("deployment.proxy.http.port");
        String proxyType = System.getProperties().getProperty("deployment.proxy.type");
        if ( (proxyHost == null || proxyType == null) || 
                (proxyHost.equalsIgnoreCase("") || proxyType.equalsIgnoreCase("0") || proxyType.equalsIgnoreCase("2") || proxyType.equalsIgnoreCase("-1") )) {
            if (url.getPort()>0)
                sock = new Socket(url.getHost(),url.getPort());
            else
                sock = new Socket(url.getHost(),80);
        }
        else{
            System.out.println("proxy host: "+proxyHost);
            System.out.println("proxy port: "+proxyPort);
            System.out.println("proxy type: "+proxyType);
            try {
                sock = new Socket(proxyHost,Integer.parseInt(proxyPort));}
            catch (NumberFormatException badPort){
                // Probably not a bad idea to try a list of standard Proxy ports
                // here (8080, 3128 ..), then default to trying the final one.
                // This could possibly be causing problems, display of an
                // error message is probably also a good idea.
                if (url.getPort()>0)
                    sock = new Socket(url.getHost(),url.getPort());
                else
                    sock = new Socket(url.getHost(),80);
            }
        }
        return sock;
    }

    private void setBoundary(int length){

        char [] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        Random r = new Random();
        String boundaryString ="";
        for (int i=0; i< length; i++)
            boundaryString += alphabet[r.nextInt(alphabet.length)];
        boundary = boundaryString;
    }

    private void setHeaderAndFooter(){

        header = new String();
        footer = new String();
        request = new String();

        // AfterContent is what is sent after the Content-Length header field,
        // but before the file itself.  The length of this, is what is required
        // by the content-length header (along with the length of the file).
        afterContent = lotsHyphens +"--"+ boundary + lineEnd +
                                    "Content-Disposition: form-data; name=\"userfile\"; filename=\""+file.getName()+"\""+lineEnd+
                                    "Content-Type: application/octet-stream"+lineEnd+lineEnd;

        footer = lineEnd + lineEnd + "--"+ lotsHyphens+boundary+"--"+lineEnd;

        // The request includes the absolute URI to the script which will
        // accept the file upload.  This is perfectly valid, although it is
        // normally only used by a client when connecting to a proxy server.
        // COULD CREATE PROBLEMS WITH SOME WEB SERVERS.
        request="POST ";
        request +=url.toExternalForm();
        request +=" HTTP/1.1";
        request +=lineEnd;

        // Host that we are sending to (not necesarily connecting to, if behind
        // a proxy)
        header +="Host: ";
        header +=url.getHost();
        header +=lineEnd;

        // Give a  user agent just for completeness.  This could be changed so that
        // access by the Postlet applet can be logged.
        header +="User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.10)";
        header +=lineEnd;

        // Expect a 100-Continue message
        header +="Expect: 100-continue";
        header +=lineEnd;

        // Standard accept
        header +="Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
        header += lineEnd;        
        header +="Accept-Language: en-us,en;q=0.5";
        header += lineEnd;                
        header +="Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7";
        header += lineEnd;

        // What we are sending.
        header +="Content-Type: multipart/form-data; boundary=";
        header +=lotsHyphens;
        header +=boundary;
        header +=lineEnd;

        // Length of what we are sending.
        header +="Content-Length: ";
        header += ""+(file.length()+afterContent.length());
        header +=lineEnd;
        header +=lineEnd;

        //header +=afterContent;

        // Debugging.  Left in as prints to Java console, and could prove useful
        // for people using the applet.
        /*
        System.out.println("****************************************************************");
        System.out.println("HeaderLength: "+header.length());
        System.out.println("FooterLength: "+footer.length());
        System.out.println("RequestLength: "+request.length());
        System.out.println("FileLength: "+file.length());
        System.out.println("Hypens Length: "+lotsHyphens.length());
        System.out.println("Boundary Length: "+boundary.length());
        System.out.println("AfterContent length: "+afterContent.length());
        */
    }
}