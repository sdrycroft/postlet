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
    private Main main;
    private int attempts;
    private static final String lotsHyphens="---------------------------";
    private static final String lineEnd="\n";
    private String header, footer, request, reply, afterContent;
    //private String scriptUrl;
    private URL url;
    private String boundary;
    private SocketManager sockMan;
    private Socket sock;
    
    public UploadThread(SocketManager s, URL u, File f, Main m) throws IOException, UnknownHostException, SocketManagerException{
    
        url = u;
        file = f;
        main = m;
        attempts = 0;
        sockMan = s;
        sock = sockMan.getSocket();
    }
    
    public void run(){
        try {
            upload();
        } catch (FileNotFoundException fnfe) {
            // A file has been moved or deleted. This file will NOT
            // be uploaded.
            // Set the progress to include this file.
            main.setProgress((int)file.length());
        } catch (IOException ioe){
            // This could end up looping.  Probably need to find out what could cause this.
            main.errorMessage(main.errorLog,"IOException: UploadThread");
            ioe.printStackTrace();
        } catch (SocketManagerException sme){
            // Hmm, the socket is probably in use
            main.errorMessage(main.errorLog,"SocketManagerException: UploadThread ");
            sme.printStackTrace();
        }
    }
    
    private void upload() throws FileNotFoundException, IOException, SocketManagerException{
        
        this.uploadFile();
        if (reply != null && reply.indexOf("FILEFAILED")>=0) {
            if (reply.indexOf("FILETOOBIG")<0){
                if (attempts<3) {
                    main.setProgress(-(int)file.length());
                    attempts++;
                    this.upload();
                }
            } else {
                attempts = 5;
            }
        }
    }
    
    private synchronized void uploadFile() throws FileNotFoundException, IOException, SocketManagerException{
        
        // Get the socket for output
        this.setBoundary(40);
        this.setHeaderAndFooter();
        System.out.println("SET THE BOUNDARY"+file.getAbsolutePath());
        // Output stream, for writing to the socket.
        DataOutputStream output = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
        // Reader for accepting the reply from the server.
        BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        
        System.out.println("GOT STREAMS"+file.getAbsolutePath());
        // Write the request, and the header.
        output.writeBytes(request);
        output.writeBytes(header);
        //output.flush();
        
        System.out.println("WRITTEN REQUEST"+file.getAbsolutePath());
        
        // Create a ReadLine thread to read the possible output.
        // Possible catching of errors here if the return isn't 100 continue
        ReadLine rl = new ReadLine(input, this);
        rl.start();
        try {
            // Should be dynamically setting this.
            wait(1000);
        } catch (InterruptedException ie){
            // Thread was interuppted, which means there was probably
            // some output!
        }
        output.writeBytes(afterContent);
        System.out.println("STARTED READER"+file.getAbsolutePath());
        // Debug: Show that the above has passed!
        
        // Following reads the file, and streams it.
        /////////////////////////////////////////////////////////////
        FileInputStream fileStream = new FileInputStream(file);
        int numBytes = 0;
        
        if (file.length()>Integer.MAX_VALUE){
            main.errorMessage(main.errorLog, "FILE TOO BIG");
            throw new IOException("*** FILE TOO BIG ***");
        }
        
        // Size of buffer - May need reducing if users encounter
        // memory issues.
        int maxBufferSize = 1024;
        int bytesAvailable = fileStream.available();
        int bufferSize = Math.min(bytesAvailable,maxBufferSize);
        int finalByteSize = 0;
        
        byte buffer [] = new byte[bufferSize];
        
        int bytesRead = fileStream.read(buffer, 0, bufferSize);
        while (bytesAvailable > 0) {
            output.write(buffer, 0, bufferSize);
            if (bufferSize == maxBufferSize)
                main.setProgress(bufferSize);
            else
                finalByteSize = bufferSize;
            bytesAvailable = fileStream.available();
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            bytesRead = fileStream.read(buffer, 0,  bufferSize);
        }
        ///////////////////////////////////////////////////////////
        System.out.println("WRITTEN FILE"+file.getAbsolutePath());
        
        output.writeBytes(footer);
        
        output.flush();
        
        try {
            // Should be dynamically setting this.
            wait(1000);
        } catch (InterruptedException ie){
            // Thread was interuppted, which means there was probably
            // some output!
            main.errorMessage(main.errorLog, "UploadThread:161:Interrupted");
        }
        reply = rl.getRead();
        // Close the socket and streams.
        //input.close();
        //output.close();
        System.out.println("GOT TO THIS POINT!");
        sockMan.returnSocket(sock);
        System.out.println("GOT TO THIS POINT TOO!");
        
        // Should I kill the reader here? - Probably
        
        // Set the progress, that this file has uploaded.
        //main.setProgress((int)file.length());
        main.setProgress(finalByteSize);
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
        
        footer = lineEnd + lineEnd + "--"+ lotsHyphens+boundary+"--"+lineEnd+lineEnd;
        
        // The request includes the absolute URI to the script which will
        // accept the file upload.  This is perfectly valid, although it is
        // normally only used by a client when connecting to a proxy server.
        // COULD CREATE PROBLEMS WITH SOME WEB SERVERS.
        request="POST " + url.toExternalForm() + " HTTP/1.1" + lineEnd;
        
        // Host that we are sending to (not necesarily connecting to, if behind
        // a proxy)
        header +="Host: " + url.getHost() + lineEnd;
        
        // Give a  user agent just for completeness.  This could be changed so that
        // access by the Postlet applet can be logged.
        header +="User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.10)" + lineEnd;
        
        // Expect a 100-Continue message
        header +="Expect: 100-continue" + lineEnd;
        
        // Standard accept
        header +="Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"+ lineEnd;
        header +="Accept-Language: en-us,en;q=0.5" + lineEnd;
        header +="Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7" + lineEnd;
        
        // Add the cookie if it is set in the browser
        String cookie = main.getCookie();
        if (cookie.length()>0){
            header +="Cookie: "+cookie+lineEnd;
        }
        
        header +="Connection: keep-alive" + lineEnd;
        
        // What we are sending.
        header +="Content-Type: multipart/form-data; boundary=" + lotsHyphens + boundary + lineEnd;
        
        // Length of what we are sending.
        header +="Content-Length: ";
        header += ""+(file.length()+afterContent.length()+footer.length())+lineEnd+lineEnd;
    }
}