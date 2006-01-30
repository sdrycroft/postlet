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

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.awt.event.*;
import java.net.*;
import java.util.Vector;

public class Main extends JApplet implements MouseListener {
    JScrollPane scrollPane;
    JTable table;
    JPanel rightPanel;
    JButton add, remove, upload, help;
    String rowData [][];
    String filenames[];
    TableData tabledata;
    TableColumn sizeColumn;
    //FileUploader fu;
    File [] file;
    JLabel progCompletion;
    JProgressBar progBar;
    int sentBytes;
    int fileSize[];
    int totalBytes;
    Font font;
    String destination;
    URL endpage, helppage;
    Color backgroundColour, columnHeadColourBack, columnHeadColourFore;
    PostletLabels pLabels;
    
    public void init() {
            
        if (getParameter("language")==null){       
            showStatus("Postlet: EN");
            pLabels = new PostletLabels("EN", null);
        }
        else{
            showStatus("Postlet: "+getParameter("language"));
            pLabels = new PostletLabels(getParameter("language"), getCodeBase());
        }
        
        System.out.println("Postlet version: 0.6.2 - 30.01.2006");
        System.out.println("HOST: "+System.getProperties().getProperty("deployment.proxy.http.host"));
        System.out.println("PORT: "+System.getProperties().getProperty("deployment.proxy.http.port"));
        
        try {
            // Set the look of the applet to be the same as the system standard
            // of the computer that the applet is running on.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException exc){;} catch (IllegalAccessException exc){;} catch (ClassNotFoundException exc){;} catch (InstantiationException exc){;}
            
            // Get the destination which is set by a parameter.
            try {
                URL dest = new URL(getParameter("destination"));
                destination = getParameter("destination");
                System.out.println("Destination has been set to:"+destination);
            } catch(java.net.MalformedURLException malurlex){
                // Do something here for badly formed destination, which is ESENTIAL.
                JOptionPane.showMessageDialog(null, pLabels.getLabel(3),pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
                destination = null;
            } catch(java.lang.NullPointerException npe){
                // Do something here for the missing destination, which is ESENTIAL.
                JOptionPane.showMessageDialog(null, pLabels.getLabel(4), pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
                destination = "http://darwin.zoology.gla.ac.uk/~sdrycroft/javaUpload.php";
            }
            
            try {
                // Set the background color, which is set by a parameter.
                Integer redInteger = new Integer(getParameter("red"));
                int red = redInteger.intValue();
                Integer greenInteger = new Integer(getParameter("green"));
                int green = greenInteger.intValue();
                Integer blueInteger = new Integer(getParameter("blue"));
                int blue = blueInteger.intValue();
                backgroundColour = new Color(red, green, blue);
                System.out.println("Background colour has been set to:"+red+"/"+green+"/"+blue);
                // Try and dispose of all variables when finished with (memory is essential).
                redInteger= null;
                blueInteger= null;
                greenInteger = null;
                
                // Set the background colour of the table headers.
                redInteger = new Integer(getParameter("redheaderback"));
                int redheaderback = redInteger.intValue();
                greenInteger = new Integer(getParameter("greenheaderback"));
                int greenheaderback = greenInteger.intValue();
                blueInteger = new Integer(getParameter("blueheaderback"));
                int blueheaderback = blueInteger.intValue();
                columnHeadColourBack = new Color(redheaderback, greenheaderback, blueheaderback);
                redInteger = null;
                blueInteger= null;
                greenInteger = null;
                
                // Set the foreground colour of the table headers.
                redInteger = new Integer(getParameter("redheader"));
                int redheader = redInteger.intValue();
                greenInteger = new Integer(getParameter("greenheader"));
                int greenheader = greenInteger.intValue();
                blueInteger = new Integer(getParameter("blueheader"));
                int blueheader = blueInteger.intValue();
                columnHeadColourFore = new Color(redheader, greenheader, blueheader);
                // Try and dispose of all variables when finished with (memory is essential).
                redInteger = null;
                blueInteger= null;
                greenInteger = null;
            } catch(java.lang.NullPointerException npered){
                // Color isn't set.
                // Just ignore this, and set the background color to the
                // default one.
                backgroundColour = null;
                columnHeadColourFore = null;
                columnHeadColourBack = null;
            } catch(java.lang.NumberFormatException numfe){
                // Color isn't set.
                // Just ignore this, and set the background color to the
                // default one.
                backgroundColour = null;
                columnHeadColourFore = null;
                columnHeadColourBack = null;
            }
            
            // Get the main pane to add content to.
            Container pane = getContentPane();
            
            // Table for the adding of Filenames and sizes to.
            tabledata = new TableData(pLabels.getLabel(0),pLabels.getLabel(1)+" -KB ");
            table = new JTable(tabledata);
            table.setColumnSelectionAllowed(false);
            //table.setDragEnabled(false);            
            sizeColumn = table.getColumn(pLabels.getLabel(1)+" -KB ");
            sizeColumn.setMaxWidth(100);
            table.getColumn(pLabels.getLabel(1)+" -KB ").setMinWidth(100);         
            if (columnHeadColourBack != null && backgroundColour != null){
                table.getTableHeader().setBackground(columnHeadColourBack);
                table.getTableHeader().setForeground(columnHeadColourFore);
                table.setBackground(backgroundColour);
                //table.getTableHeader().setForeground(columnHeadColour);
            }
            scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            // Add the scroll pane/table to the main pane
            pane.add(scrollPane, BorderLayout.CENTER);
            
            rightPanel = new JPanel(new GridLayout(4,1,10,10));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            add = new JButton(pLabels.getLabel(6));
            add.addMouseListener(this);
            rightPanel.add(add);
            
            remove = new JButton(pLabels.getLabel(7));
            remove.addMouseListener(this);
            remove.setEnabled(false);
            rightPanel.add(remove);
            
            upload = new JButton(pLabels.getLabel(8));
            upload.addMouseListener(this);
            upload.setEnabled(false);
            rightPanel.add(upload);
            
            help = new JButton(pLabels.getLabel(9));
            help.addMouseListener(this);
            rightPanel.add(help);
            pane.add(rightPanel,"East");
            
            JPanel progPanel = new JPanel(new GridLayout(2, 1));
            progPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            progCompletion = new JLabel(pLabels.getLabel(10),SwingConstants.CENTER);
            progPanel.add(progCompletion);
            
            progBar = new JProgressBar();
            progPanel.add(progBar);
            progPanel.setBorder(BorderFactory.createEmptyBorder(5,25,5,25));
                                  
            if (backgroundColour != null){
                pane.setBackground(backgroundColour);
                rightPanel.setBackground(backgroundColour);
                scrollPane.setBackground(backgroundColour);
                progPanel.setBackground(backgroundColour);
                // Always set the table background colour as White.
                // May change this if required, only would require alot of Params!
                scrollPane.getViewport().setBackground(Color.white);
            }
            
            pane.add(progPanel,"South");
            
            // If the destination has not been set/isn't a proper URL
            // Then deactivate the buttons.
            if (destination == null) {
                remove.setEnabled(false);
                add.setEnabled(false);
                upload.setEnabled(false);
            }            
            showStatus("Postlet: Started");
            
        } catch (java.lang.OutOfMemoryError oomerr){
            System.out.println("OUT OF MEMORY HERE!");
        }
    }
    
    public void removeClick() {
        if(table.getSelectedRowCount()>0) {
            File [] fileTemp = new File[file.length-table.getSelectedRowCount()];
            int k=0;
            for(int i=0; i<file.length-table.getSelectedRowCount(); i++) {
                for(int j=0; j<table.getSelectedRowCount(); j++) {
                    if(i==table.getSelectedRows()[j]) {
                        k++;
                    }
                    fileTemp[i] = file[i+k];
                }
            }
            file = fileTemp;
            tableUpdate();
        }
        if (file.length==0) {
            upload.setEnabled(false);
            remove.setEnabled(false);
        }
    }
    
    public void uploadClick() {
        if(filenames !=null) {
            try {
                if (getParameter("warnMessage").toLowerCase() == "true"){
                    JOptionPane.showMessageDialog(null, pLabels.getLabel(11), pLabels.getLabel(12), JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (NullPointerException noMess){;// No need to catch this, just assume user doesn't want the warning
            }
            add.setEnabled(false);
            remove.setEnabled(false);
            help.setEnabled(false);
            upload.setEnabled(false);
            sentBytes = 0;
            progBar.setMaximum(totalBytes);
            progBar.setMinimum(0);
            Upload u = new Upload(filenames, fileSize, this, destination);
            u.start();
        }
    }
    
    public void setProgress(int a) {
        sentBytes += a;
        progBar.setValue(sentBytes);
        if (sentBytes == totalBytes){
            progCompletion.setText(pLabels.getLabel(2));
            try {
                endpage = new URL(getParameter("endpage"));
                getAppletContext().showDocument(endpage);
                
            } catch(java.net.MalformedURLException malurlex){
                // Just ignore this error, as it is most likely from the endpage
                // not being set.
                System.err.println("Endpage unset or not valid URL ("+getParameter("endpage")+")");
            }
        }
    }
    
    public void tableUpdate() {
        totalBytes = 0;
        filenames = new String[file.length];
        fileSize = new int[file.length];
        for(int i=0; i<file.length; i++) {
            filenames[i] = file[i].getAbsolutePath();
            fileSize[i] = (int)file[i].length();
            totalBytes += (int)file[i].length();
        }
        int i=0;
        rowData = new String[255][2];
        while(i<file.length) {
            rowData[i][0] = file[i].getName();
            rowData[i][1] = ""+file[i].length();
            i++;
        }
        tabledata.formatTable(rowData,i);
        sizeColumn.setMaxWidth(100);
        sizeColumn.setMinWidth(100);  
        repaint();
    }
    
    public void addClick() {
        JFileChooser chooser = new JFileChooser();
        
        progBar.setValue(0);
        UploaderFileFilter filter = new UploaderFileFilter();
        filter.addExtension("jpg");
        filter.addExtension("jpeg");
        filter.addExtension("gif");
        filter.addExtension("bmp");
        filter.addExtension("png");
        filter.addExtension("raw");
        filter.addExtension("tif");
        filter.setDescription(pLabels.getLabel(13));
        chooser.setFileFilter(filter);
                
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(true);
        chooser.getSelectedFile();
        chooser.setDialogTitle(pLabels.getLabel(14));
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File [] tempFiles = chooser.getSelectedFiles();
            Vector filesForUpload = new Vector();
            for (int i=0; i<tempFiles.length; i++){
                if (tempFiles[i].isDirectory()){
                    File [] subDirFiles = tempFiles[i].listFiles();
                    for (int j = 0; j<subDirFiles.length; j++){
                        if (subDirFiles[j].isFile())
                            filesForUpload.add(subDirFiles[j]);
                    }
                        
                }
                else
                    filesForUpload.add(tempFiles[i]);
            }
            if (file == null){
                file = new File[0];
            }
            tempFiles = new File[filesForUpload.size()+file.length];
            System.out.println("Length of files is: "+filesForUpload.size()+file.length);
            for (int i=0; i<file.length; i++)
                tempFiles[i] = file[i];
            for (int i=0; i<filesForUpload.size(); i++){
                tempFiles[i+file.length] = (File)filesForUpload.elementAt(i);
            }
            file = tempFiles;
            tableUpdate();
        }
        if (file != null && file.length>0) {
            upload.setEnabled(true);
            remove.setEnabled(true);
        }
    }
    
    public void helpClick() {
        // Open a web page in another frame/window
        // Unless specified as a parameter, this will be a help page
        // on the postlet website.
        
        // Get the help page.
        String helpUrl;
        helpUrl = getParameter("helppage");
        if (helpUrl == null){
            helpUrl = "http://www.postlet.com/help/";
        }
        System.out.println("Help page is:"+helpUrl);
        try {
            getAppletContext().showDocument(new URL(helpUrl), "_blank");
        } catch (MalformedURLException helpexception){
            // Show a popup with help instead!
            System.err.println("Error with help dialog");
            JOptionPane.showMessageDialog(null, pLabels.getLabel(14),pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
// Here for testing purposes
    public static void main(String args[]) {
        Frame f = new Frame("Uploader");
        Main main = new Main();
        
        main.init();
        
        f.add("Center", main);
        f.pack();
        f.setSize(600,200);
        f.setVisible(true);
    }
    
    
    public void mouseClicked(MouseEvent e) {
        if(e.getSource()==add && add.isEnabled())           {addClick();}
        if(e.getSource()==upload && upload.isEnabled())     {uploadClick();}
        if(e.getSource()==remove && remove.isEnabled())     {removeClick();}
        if(e.getSource()==help && help.isEnabled())         {helpClick();}
    }
    
    public void mouseEntered(MouseEvent e){;}
    public void mouseExited(MouseEvent e){;}
    public void mousePressed(MouseEvent e){;}
    public void mouseReleased(MouseEvent e){;}
    
}