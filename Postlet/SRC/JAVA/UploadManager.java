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
import java.net.*;

// Note, the upload manager extends Thread so that the GUI is
// still responsive, and updates.
public class UploadManager extends Thread {
    
    File [] files;
    Main main;
    String destination;
    private static final int maxThreads = 5;
    
    /** Creates a new instance of Upload */
    public UploadManager(File [] f, Main m, String d) {
        files = f;
        main = m;
        destination = d;
    }
    
    public void run() {
        for(int i=0; i<files.length; i+=maxThreads) {
            //UploadThread u = new UploadThread(destination,files[i], main);
            //u.upload();
            int j=0;
            while(j<maxThreads && (i+j)<files.length)
            {
                UploadThread u[] = new UploadThread[files.length];
                u[i+j] = new UploadThread(destination,files[i+j],  main);
                u[i+j].start();
                try{
                    u[i+j].sleep(1000);}
                catch(InterruptedException ie){System.out.println("### exception");}
                j++;
                
                
            }
        }
    }
    
}
