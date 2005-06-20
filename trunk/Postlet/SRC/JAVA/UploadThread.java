import java.io.*;

public class UploadThread {
//public class UploadThread extends Thread {
    private String filename;
    private String scriptURL;
    private Main main;
    private int fileSize;
    private int i;
    
    public UploadThread(String s, String f, int fs, Main m) {
        filename = f;
        scriptURL = s;
        main = m;
        fileSize = fs;
        i = 0;
    }
    
    public void upload() {
        try {
            FileUploader f = new FileUploader(scriptURL, main);
            f.uploadFile(filename);
            String response = f.getPostRequestResponse();
            System.out.println("***"+response+"***");
            if (response.indexOf("NO")>=0) {
                if (response.indexOf("[error] => 1")<0){
                    if (i<3) {
                        main.setProgress(-fileSize);
                        System.out.println("Error, retrying file \""+filename+"\"");
                        i++;
                        this.upload();
                    }
                } else {
                    System.out.println("The file is too large");
                    i = 5;
                }
            }
        } catch (java.io.FileNotFoundException fnfe){;} catch (java.net.MalformedURLException mue){;} catch (java.net.UnknownHostException uhe){;} catch (java.io.IOException ioee){;}
    }
}