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
import java.io.IOException;

// Note, the upload manager extends Thread so that the GUI is
// still responsive, and updates.
public class UploadManager extends Thread {
    
    File [] files;
    Main main;
    URL destination;
    int maxThreads = 5;
    
    /** Creates a new instance of Upload */
    public UploadManager(File [] f, Main m, URL d){
        files = f;
        main = m;
        destination = d;
    }
    
    public UploadManager(File [] f, Main m, URL d, int max){
        try {
            if (max>5 || max < 1)
                max = 5;
            maxThreads = max;
        } catch (NullPointerException npe){
            maxThreads = 5;// Leave the maxThreads as default
        }
        files = f;
        main = m;
        destination = d;
    }
    
    public void run() {
        UploadThread u[] = new UploadThread[files.length];
        try {
            SocketManager sockets = new SocketManager(destination, maxThreads);
            
            int filesStarted = 0;
            while (filesStarted < files.length){
                while (sockets.isSocketAvailable()){
                    
                    // Following check is added as it isn't worked as I expected!
                    if (filesStarted >= files.length)
                        break;
                    else {
                        System.out.println("FilesStarted: "+filesStarted);
                        u[filesStarted] = new UploadThread(sockets, destination, files[filesStarted], main);
                        u[filesStarted].start();
                        filesStarted ++;
                    }
                }
            }
        } 
        catch (IOException ioe){
            main.errorMessage(main.errorLog,"IOException:UploadManager");
        }   
        catch (SocketManagerException sme){
            main.errorMessage(main.errorLog,"SocketManagerException:UploadManager");
            sme.printStackTrace();
        }
        catch (NullPointerException npe){
            main.errorMessage(main.errorLog,"NullPointerException:UploadManager");
            npe.printStackTrace();
        }
    }
    
    private void urlFailure(){
        // Output a message explaining that the URL has failed.
        // This should stop all the threads!
    }
}
