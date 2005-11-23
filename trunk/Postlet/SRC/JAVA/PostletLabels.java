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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.io.InputStreamReader;

public class PostletLabels {
    
    // languages listed by their English/British names.
    public static final String languages [] = { "EN" , "DE" , "NL"};    
    private String [] language;     
    private static final int numLabels = 16;
    
    private static final String [] EN = {
                "Filename",             //0
                "File Size",            //1
                "Finished",             //2
                "The destination URL provided is not a valid one.", //3
                "You have not provided a destination URL.",         //4
                "Postlet error",        //5
                "Add",                  //6
                "Remove",               //7
                "Upload",               //8
                "Help",                 //9
                "Upload progress:",     //10
                "Do not close your web browser, or leave this page until upload completes.", //11
                "Postlet warning",      //12
                "Image files",          //13
                "Select file for upload", //14
                "The help URL provided is not a valid one."}; //15
                
    private static final String [] DE = {
                "Dateiname",
                "Dateigröße",
                "Fertig",
                "Die angegebene Ziel-URL ist ungültig.",
                "Es ist keine Ziel-URL angegeben.",
                "Postlet Fehler",
                "Hinzufügen",
                "Entfernen",
                "Upload",
                "Hilfe",       
                "Upload prozess:",
                "Der Browser darf nicht geschlossen werden solange der Upload läuft.",
                "Postlet Warnung",
                "Bild-Dateien",
                "Datei zum hochladen auswählen",
                "Die angegebene Hilfe-URL ist nicht gültig."};
                
    private static final String [] NL = {
                "Bestands naam",
                "Bestands grootte",
                "Klaar",
                "De opgegeven doel URL is niet correct.",
                "U heeft geen doel URL opgegeven.",
                "Postlet fout",
                "Toevoegen",
                "Verwijder",
                "Upload",
                "Help",
                "Upload voortgang",
                "Uw web browser niet sluiten, of deze pagina verlaten tot dat de upload compleet is.",
                "Postlet waarschuwing",
                "Plaatjes bestanden",
                "Selecteer bestand voor upload",
                "De help URL is niet correct"};
                
    /** Creates a new instance of PostletLabels */
    public PostletLabels(String l, URL codeBase) {
        System.out.println(l);
        boolean languageIncluded = false;
        for (int i=0; i<languages.length; i++){
            if (languages[i].equalsIgnoreCase(l)){
                languageIncluded = true;
                break;
            }
        }
        if (languageIncluded){
            if (l.equalsIgnoreCase("EN"))
                language = EN;
            else if (l.equalsIgnoreCase("DE"))
                language = DE;
            else if (l.equalsIgnoreCase("NL"))
                language = NL;
        }
        else
            readUserDefinedLanguageFile(codeBase, l);
    }
    
    public String getLabel(int i){
        if (i >= numLabels)
            return "ERROR!";
        return language[i];
    }
    // Method reads a standard named file, from the server (same directory as the
    // jar file), and sets the labels from this.
    private void readUserDefinedLanguageFile(URL codeBase, String lang){
        try {
            URL languageURL = new URL(codeBase.getProtocol()+"://"+codeBase.getHost()+codeBase.getPath()+"language_"+lang.toUpperCase().trim());            
            BufferedReader in = new BufferedReader(new InputStreamReader(languageURL.openStream()));
            language = new String [numLabels];
            for (int i=0; i<numLabels; i++)
                language[i]=in.readLine();
        }
        catch (FileNotFoundException fnf){
            // File not found, default used instead.
            language = EN;
            System.out.println("Language file not found, using English as default.");       
        }
        catch (IOException ioe){
            // File probably too short.
            language = EN;
            System.out.println("Language file possibly too short, please ensure it has 16 lines, terminated by a final carriage return");
        }
    }
}
