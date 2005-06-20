import java.io.*;
import java.net.*;
import javax.swing.*;

public class FileUploader {
    
    private String serverUrl, serverHost, serverPath, filename;
    private Main main;
    private Socket serverSock;
    private int serverPort;
    private static final String hyphens="---------------------------";
    private static final String lineEnd="\r\n";
    private String boundary = "arandomarrayoflettersthatmeannothingverymuch";
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private BufferedReader buffIn;
    private FileInputStream fileInStream;
    private static final int maxBufferSize = 1024*1024;
    private byte [] buffer;
    
    
    public FileUploader(String u, Main m) throws MalformedURLException{
        
        main = m;
        serverUrl = u;
        URL url = new URL(serverUrl);
        serverHost = url.getHost();
        serverPath = url.getPath();
        serverPort = url.getPort();
    }
    
    public void uploadFile(String f) throws FileNotFoundException, UnknownHostException, IOException {
        
        filename = f;
        serverSock = new Socket(serverHost, serverPort);
        outStream = new DataOutputStream(new BufferedOutputStream(serverSock.getOutputStream()));
        inStream = new DataInputStream(new BufferedInputStream(serverSock.getInputStream()));
        
        // Add first line (e.g. POST /~sdrycroft/javaUpload.php HTTP/1.1)
        StringBuffer output = new StringBuffer();
        output.append("POST ");
        output.append(serverPath);
        output.append(" HTTP/1.1");
        output.append(lineEnd);
        
        // Add second line (e.g. Host: darwin.zoology.gla.ac.uk)
        output.append("Host: ");
        output.append(serverHost);
        output.append(lineEnd);
        
        // Add a number of lines which are not dynamic.
        output.append("Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\nAccept-Language: en-gb,en;q=0.5\r\nAccept-Encoding: gzip,deflate\r\nAccept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\nKeep-Alive: 300\r\nConnection: keep-alive\r\n");
        
        // Boundary between files.
        output.append("Content-Type: multipart/form-data; boundary=");
        output.append(hyphens);
        output.append(boundary);
        output.append(lineEnd);
        
        outStream.writeBytes(output.toString());
        outStream.writeBytes("Content-Disposition: form-data; name=\"userfile\";" + " filename=\""+ filename + "\"" + lineEnd+lineEnd);
        outputFile();
        output.append(lineEnd);
        output.append(hyphens);
        output.append(boundary);
    }
    
    private void createAndSetBoundary(int l){
        
        // Create a random string of length l, and set it to boundary.
        boundary = "somerandomstringhere";
    }
    
    private void outputFile() throws FileNotFoundException, IOException{
        
        fileInStream = new FileInputStream(new File(filename));
        
        int bytesAvailable = fileInStream.available();
        int bufferSize = Math.min(bytesAvailable,maxBufferSize);
        
        buffer = new byte[bufferSize];
        
        int bytesRead = fileInStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            outStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInStream.available();
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            
            bytesRead = fileInStream.read(buffer, 0, bufferSize);
        }
        fileInStream.close();
    }
    
    public String getPostRequestResponse(){
        return "YES";
    }
}
    /*
    HttpURLConnection httpURLConn;
    private static String boundary="leedsunitedarethebestteaminthepremierleagueHoHum1966andallthat";
    private static String twoHyphens="--";
    private static String lineEnd="\r\n";
     
    public FileUploader(String phpScript) {
        httpURLConn = null;
        try {
            URL theURL = new URL(phpScript);
            httpURLConn  = (HttpURLConnection)theURL.openConnection();
            httpURLConn.setRequestMethod("POST");
            httpURLConn.setRequestProperty("Connection", "Keep-Alive");
            httpURLConn.setDoOutput(true);
            httpURLConn.setUseCaches(false);
            httpURLConn.setRequestProperty("Accept-Charset", "iso-8859-1,*,utf-8");
            httpURLConn.setRequestProperty("Accept-Language", "en");
            httpURLConn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
        } catch (MalformedURLException ex){System.out.println("### exception");} catch (IOException ioex){System.out.println("### exception");}
    }
     
    public void uploadFile(String filename) {
        DataOutputStream outStream;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024*1024;
     
        try {
            outStream = new DataOutputStream(httpURLConn.getOutputStream());
            outStream.writeBytes(twoHyphens + boundary + lineEnd);
            FileInputStream fileInputStream = new FileInputStream(new File(filename));
            outStream.writeBytes("Content-Disposition: form-data; name=\"userfile\";" + " filename=\""+ filename + "\"" + lineEnd);
            outStream.writeBytes(lineEnd);
     
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
     
            buffer = new byte[bufferSize];
     
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
     
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            fileInputStream.close();
            outStream.writeBytes(lineEnd);
     
            outStream.writeBytes(lineEnd);
     
            outStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
     
            outStream.flush();
            outStream.close();
        } catch (IOException ioex){;}
    }
     
    public String getPOSTRequestResponse() {
        BufferedReader inStream;
        StringBuffer results = new StringBuffer();
        String newURL = "";
        try {
            inStream = new BufferedReader(new InputStreamReader(httpURLConn.getInputStream()));
            String str;
            int i = 0;
            while ((str = inStream.readLine()) != null)
                results.append(str+"\n");
     
            inStream.close();
        } catch (IOException ioex){;}
        return results.toString();
    }
}
     */