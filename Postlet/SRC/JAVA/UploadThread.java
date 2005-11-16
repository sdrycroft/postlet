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
        FileUploader f = null;
        try {
            f = new FileUploader(scriptURL, main);
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
            else {
                System.out.println("HERE INSTEAD!");
            }
        } catch (java.net.MalformedURLException mue){
            System.err.println("MalformedURLException");
            System.err.println(mue.getMessage());
        } catch (java.io.IOException ioee){
            //try {
                System.err.println("IOException");
                System.err.println(ioee.toString());
                //System.err.println(ioee.getStackTrace().toString());
                String response = f.getPostRequestResponse();
                System.out.println("***"+response+"***");
            //} catch (java.io.IOException ioexc){;}
        }
    }
}