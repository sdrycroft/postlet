/*  Copyright (C) 2006 Simon David Rycroft

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

import java.io.BufferedReader;
import java.io.IOException;

public class ReadLine extends Thread {

    BufferedReader input;
    String read;
    private static final String newLine = "\n";

    public ReadLine (BufferedReader i){

        input = i;
        read = "";
    }

    public synchronized void run(){

        try {
            String line="";
            while ((line = input.readLine())!=null){
                read += ""+line + newLine;
                System.out.println("*** READ A LINE ***");
                System.out.println("###"+line+"###");
                if (line ==""){
                    System.out.println("*** NOTIFYING ALL ***");
                    notifyAll();
                }
            }
        }
        catch (IOException ioe){
            System.out.println("*** IOException ReadLine.java:47 ***");
        }
    }

    public String getRead(){

        return read;
    }
}