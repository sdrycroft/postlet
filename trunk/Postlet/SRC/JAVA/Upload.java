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

import java.net.*;

public class Upload extends Thread {
    String [] filenames;
    Main main;
    int [] fileSize;
    String destination;
    
    /** Creates a new instance of Upload */
    public Upload(String [] f, int[] fs, Main m, String d) {
        filenames = f;
        main = m;
        fileSize = fs;
        destination = d;
    }
    
    public void run() {
        for(int i=0; i<filenames.length; i++) {
            try {
                UploadThread u = new UploadThread(destination,filenames[i], fileSize[i], main);
                u.upload();
                System.gc();
            }
            catch (java.lang.OutOfMemoryError memerr){
                System.out.println("Out of memory!");
            }
        }
    }
    
}
