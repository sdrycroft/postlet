/*
 * $Id$
 */

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.GridLayout;
import java.io.File;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.UIManager;
import javax.swing.SwingConstants;

import netscape.javascript.JSObject;

import java.net.UnknownHostException;
import java.net.MalformedURLException;
import javax.swing.UnsupportedLookAndFeelException;

public class Main extends JApplet implements MouseListener, DropTargetListener {
    
    JTable table;
    JButton add, remove, upload, help;
    TableData tabledata;
    TableColumn sizeColumn;
    File [] files;
    JLabel progCompletion;
    JProgressBar progBar;
    int sentBytes,totalBytes,buttonClicked;
    Color backgroundColour, columnHeadColourBack, columnHeadColourFore;
    PostletLabels pLabels;
    
    // Boolean set to false when a javascript method is executed
    boolean javascript;
    
    // Parameters
    URL endPageURL, helpPageURL, destinationURL;
    boolean warnMessage, autoUpload;
    String language,endpage,helppage;
    int maxThreads;
    String [] fileExtensions;
    
    // URI list flavor (Hack for linux)
    DataFlavor uriListFlavor;
    
    public void init() {
        // First thing, output the version, for debugging purposes.
        System.out.println("POSTLET VERSION: 0.10");
        String date = "$Date$";
        System.out.println(date.substring(7,date.length()-1));
        
	// URI list flavor:
	try {
		uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
	}
	catch (ClassNotFoundException cnfe){
		errorMessage(System.out, "No class found for DataFlavor");
	}
	
        // Set the javascript to false, and start listening for clicks
        javascript = false;
        JavascriptListener jsListen = new JavascriptListener(this);
        jsListen.start();
        buttonClicked = 0; // Default of add click.
        
        getParameters();
        pLabels = new PostletLabels(language, getCodeBase());
        layoutGui();
        
    }
    
    private void layoutGui(){
        
        // Set the look of the applet
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException exc){;} catch (IllegalAccessException exc){;} catch (ClassNotFoundException exc){;} catch (InstantiationException exc){;}
        
        // Get the main pane to add content to.
        Container pane = getContentPane();
        
        // Attempt to add drop listener to the whole applet
        try {
            DropTarget dt = new DropTarget();
            dt.addDropTargetListener(this);
            pane.setDropTarget(dt);
        } catch (java.util.TooManyListenersException tmle){
            errorMessage(System.out, "Too many listeners to drop!");
        }
        
        // Table for the adding of Filenames and sizes to.
        tabledata = new TableData(pLabels.getLabel(0),pLabels.getLabel(1)+" -KB ");
        table = new JTable(tabledata);
        table.setColumnSelectionAllowed(false);
        //table.setDragEnabled(false); // This method is not available to Java 3!
        sizeColumn = table.getColumn(pLabels.getLabel(1)+" -KB ");
        sizeColumn.setMaxWidth(100);
        table.getColumn(pLabels.getLabel(1)+" -KB ").setMinWidth(100);
        if (columnHeadColourBack != null && backgroundColour != null){
            errorMessage(System.out, "setting the tables colours");
            table.getTableHeader().setBackground(columnHeadColourBack);
            table.getTableHeader().setForeground(columnHeadColourFore);
            table.setBackground(backgroundColour);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        // Add the scroll pane/table to the main pane
        pane.add(scrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new GridLayout(4,1,10,10));
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
        }
        // Always set the table background colour as White.
        // May change this if required, only would require alot of Params!
        scrollPane.getViewport().setBackground(Color.white);
        
        pane.add(progPanel,"South");
        
        // If the destination has not been set/isn't a proper URL
        // Then deactivate the buttons.
        if (destinationURL == null)
            add.setEnabled(false);
    }
    
    public void errorMessage(PrintStream out, String message){
        out.println("***"+message+"***");
    }
    
    // Helper method for getting the parameters from the webpage.
    private void getParameters(){
        
        /*  LANGUAGE */
        try {
            language = getParameter("language");
            if (language == "" || language == null)
                language = "EN";
        } catch (NullPointerException nullLang){
            // Default language being set
            language = "EN";
            errorMessage(System.out,"language is null");
        }
        
        /*  DESTINATION  */
        try {
            destinationURL = new URL(getParameter("destination"));
	    // Following line is for testing, and to hard code the applet to postlet.com
	    //destinationURL = new URL("http://www.postlet.com/example/javaUpload.php");
        } catch(java.net.MalformedURLException malurlex){
            // Do something here for badly formed destination, which is ESENTIAL.
            errorMessage(System.out, "Badly formed destination:###"+getParameter("destination")+"###");
            JOptionPane.showMessageDialog(null, pLabels.getLabel(3),pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
        } catch(java.lang.NullPointerException npe){
            // Do something here for the missing destination, which is ESENTIAL.
            errorMessage(System.out,"destination is null");
            JOptionPane.showMessageDialog(null, pLabels.getLabel(4), pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
        }
        
        /*  BACKGROUND  */
        try {
            Integer bgci = new Integer(getParameter("backgroundcolour"));
            backgroundColour = new Color(bgci.intValue());
        } catch(NumberFormatException numfe){
            errorMessage(System.out, "background colour is not a number:###"+getParameter("backgroundcolour")+"###");
        } catch (NullPointerException nullred){
            errorMessage(System.out, "background colour is null");
        }
        
        /*  TABLEHEADERFOREGROUND  */
        try {
            Integer thfi = new Integer(getParameter("tableheadercolour"));
            columnHeadColourFore = new Color(thfi.intValue());
        } catch(NumberFormatException numfe){
            errorMessage(System.out, "table header colour is not a number:###"+getParameter("tableheadcolour")+"###");
        } catch (NullPointerException nullred){
            errorMessage(System.out, "table header colour is null");
        }
        
        /*  TABLEHEADERBACKGROUND  */
        try {
            Integer thbi = new Integer(getParameter("tableheaderbackgroundcolour"));
            columnHeadColourBack = new Color(thbi.intValue());
        } catch(NumberFormatException numfe){
            errorMessage(System.out, "table header back colour is not a number:###"+getParameter("tableheaderbackgroundcolour")+"###");
        } catch (NullPointerException nullred){
            errorMessage(System.out, "table header back colour is null");
        }
        
        /*  FILEEXTENSIONS  */
        try {
            fileExtensions = getParameter("fileextensions").split(",");
        } catch(NullPointerException nullfileexts){
            errorMessage(System.out, "file extensions is null");
        }
        
        /*  WARNINGMESSAGE  */
        try {
            if (getParameter("warnmessage").toLowerCase() == "true")
                warnMessage = true;
            else
                warnMessage = false;
        } catch(NullPointerException nullwarnmessage){
            errorMessage(System.out, "warnmessage is null");
            warnMessage = false;
        }
        
        /*  AUTOUPLOAD  */
        try {
            if (getParameter("autoupload").toLowerCase() == "true")
                autoUpload = true;
            else
                autoUpload = false;
        } catch(NullPointerException nullwarnmessage){
            errorMessage(System.out, "autoUpload is null");
            autoUpload = false;
        }
        
        /*  MAXTHREADS  */
        try {
            Integer maxts = new Integer(getParameter("maxthreads"));
            maxThreads = maxts.intValue();
        } catch (NullPointerException nullmaxthreads){
            errorMessage(System.out, "maxthreads is null");
        } catch (NumberFormatException nummaxthreads){
            errorMessage(System.out, "maxthread is not a number");}
        
        /*  ENDPAGE  */
        try {
            endPageURL = new URL(getParameter("endpage"));
        } catch(java.net.MalformedURLException malurlex){
            errorMessage(System.out, "endpage is badly formed:###"+getParameter("endpage")+"###");
        } catch(java.lang.NullPointerException npe){
            errorMessage(System.out, "endpage is null");
        }
        
        /*  HELPPAGE  */
        try {
            helpPageURL = new URL(getParameter("helppage"));
        } catch(java.net.MalformedURLException malurlex){
            errorMessage(System.out, "helppage is badly formed:###"+getParameter("helppage")+"###");
        } catch(java.lang.NullPointerException npe){
            errorMessage(System.out, "helppage is null");
        }
    }
    
    public void removeClick() {
        if(table.getSelectedRowCount()>0) {
            File [] fileTemp = new File[files.length-table.getSelectedRowCount()];
            int k=0;
            for(int i=0; i<=files.length-table.getSelectedRowCount(); i++) {
                for(int j=0; j<table.getSelectedRowCount(); j++) {
                    if(i==table.getSelectedRows()[j]) {
                        k++;
                    }
                    fileTemp[i] = files[i+k];
                }
            }
            files = fileTemp;
            tableUpdate();
        }
        if (files.length==0) {
            upload.setEnabled(false);
            remove.setEnabled(false);
        }
    }
    
    public void uploadClick() {
        if(files.length>0) {
            if (warnMessage){
                JOptionPane.showMessageDialog(null, pLabels.getLabel(11), pLabels.getLabel(12), JOptionPane.INFORMATION_MESSAGE);
            }
            add.setEnabled(false);
            remove.setEnabled(false);
            help.setEnabled(false);
            upload.setEnabled(false);
            sentBytes = 0;
            progBar.setMaximum(totalBytes);
            progBar.setMinimum(0);
			UploadManager u;
			try {
				u = new UploadManager(files, this, destinationURL, maxThreads);
			} catch(java.lang.NullPointerException npered){
				u = new UploadManager(files, this, destinationURL);
			}
			u.start();
        }
    }
    
    public synchronized void setProgress(int a) {
        sentBytes += a;
        progBar.setValue(sentBytes);
        if (sentBytes == totalBytes){
            progCompletion.setText(pLabels.getLabel(2));
            if (endPageURL != null){
                getAppletContext().showDocument(endPageURL);
            } else {
                // Just ignore this error, as it is most likely from the endpage
                // not being set.
                // Attempt at calling Javascript after upload is complete.
                JSObject win = (JSObject) JSObject.getWindow(this);
                win.eval("postletFinished();");
            }
            // Reset the applet
            progBar.setValue(0);
            files = new File[0];
            tableUpdate();
            add.setEnabled(true);
            help.setEnabled(true);
        }
    }
    
    public void tableUpdate() {
        totalBytes = 0;
        String [] filenames = new String[files.length];
        int [] fileSize = new int[files.length];
        for(int i=0; i<files.length; i++) {
            filenames[i] = files[i].getAbsolutePath();
            fileSize[i] = (int)files[i].length();
            totalBytes += (int)files[i].length();
        }
        int i=0;
        String [][] rowData = new String[255][2];
        while(i<files.length) {
            rowData[i][0] = files[i].getName();
            rowData[i][1] = ""+files[i].length();
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
        if (fileExtensions != null){
            UploaderFileFilter filter = new UploaderFileFilter();
            for (int i=1; i<fileExtensions.length; i++){                
                filter.addExtension(fileExtensions[i]);
            }
            filter.setDescription(fileExtensions[0]);
            chooser.addChoosableFileFilter(filter);
        }
        else {
            chooser.setFileFilter(chooser.getAcceptAllFileFilter());
        }
        
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
                    
                } else
                    filesForUpload.add(tempFiles[i]);
            }
            if (files == null){
                files = new File[0];
            }
            tempFiles = new File[filesForUpload.size()+files.length];
            for (int i=0; i<files.length; i++)
                tempFiles[i] = files[i];
            for (int i=0; i<filesForUpload.size(); i++){
                tempFiles[i+files.length] = (File)filesForUpload.elementAt(i);
            }
            files = tempFiles;
            tableUpdate();
        }
        if (files != null && files.length>0) {
            upload.setEnabled(true);
            remove.setEnabled(true);
        }
        if (files !=null && autoUpload){
            uploadClick();
        }
    }
    
    public void helpClick() {
        // Open a web page in another frame/window
        // Unless specified as a parameter, this will be a help page
        // on the postlet website.
        
        try {
            getAppletContext().showDocument(helpPageURL, "_blank");
        } catch (NullPointerException nohelppage){
            // Show a popup with help instead!
            try {getAppletContext().showDocument(new URL("http://www.postlet.com/help/"), "_blank");}catch(MalformedURLException mfue){;}
        }
        
    }
    
    public String getCookie(){
        
        // Method reads the cookie in from the Browser using the LiveConnect object.
        // May also add an option to set the cookie using an applet parameter FIXME!
		try {
			JSObject win = (JSObject) JSObject.getWindow(this);
			String cookie = ""+(String)win.eval("document.cookie");
			return cookie;
		}
		catch (Exception e){
			return "";
		}
    }
    
    public void javascriptAddClicked(){
        
        // Set a variable so that the listening thread can call the add click method
        buttonClicked = 0;
        javascript = true;
    }
    public void javascriptUploadClicked(){
        
        // As above
        buttonClicked = 1;
        javascript = true;
    }
    public boolean getJavascriptStatus(){
        
        return javascript;
    }
    public void setJavascriptStatus(){
        
        javascript = false;
    }
    public boolean isUploadEnabled(){
        
        return upload.isEnabled();
    }
    public boolean isAddEnabled(){
        
        return add.isEnabled();
    }
    public boolean isRemoveEnabled(){
        
        return remove.isEnabled();
    }
    public int getButtonClicked(){
        
        return buttonClicked;
    }
    
    public void mouseClicked(MouseEvent e) {
        if(e.getSource()==add && add.isEnabled())		   {addClick();}
        if(e.getSource()==upload && upload.isEnabled())	 {uploadClick();}
        if(e.getSource()==remove && remove.isEnabled())	 {removeClick();}
        if(e.getSource()==help && help.isEnabled())		 {helpClick();}
    }
    
    public void drop(DropTargetDropEvent dtde) {
	    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	    Transferable trans = dtde.getTransferable();
	    try {
		java.awt.datatransfer.DataFlavor dataFlavour [];
		dataFlavour = dtde.getCurrentDataFlavors();
		String mimeType;
		Vector filesFromDrop = new Vector();
		boolean filesFound = false;
		while (!filesFound){
			for (int i=0; i<dataFlavour.length; i++){/*
				mimeType = dataFlavour[i].getMimeType();
				System.out.println(i+": "+dataFlavour[i].toString());
				System.out.println(i+": "+mimeType);
				System.out.println(i+": "+dataFlavour[i].getPrimaryType());
				System.out.println(i+": "+dataFlavour[i].getHumanPresentableName());
				System.out.println(i+": "+dataFlavour[i].getSubType());*/
				if (dataFlavour[i].isFlavorJavaFileListType()){
					// Windows
					System.out.println("Windows");
					List listOfFiles = (List)trans.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator iter = listOfFiles.iterator();
					while (iter.hasNext()) {
						File tempFile = (File) iter.next();
						filesFromDrop.add(tempFile);
					}
					filesFound = true;
				} else if (dataFlavour[i].equals(uriListFlavor)){
					// Linux
					BufferedReader in = new BufferedReader(dataFlavour[i].getReaderForText(trans));
					String line = in.readLine();
					while(line!=null && line !=""){
						try {
							File tempFile = new File(new URI(line));
							filesFromDrop.add(tempFile);
						}
						catch (java.net.URISyntaxException usee){;}
						catch (java.lang.IllegalArgumentException iae){;}
						line = in.readLine();
					}
					filesFound = true;
				}
			}
		}
		File [] tempFiles = new File[filesFromDrop.size()];
		filesFromDrop.copyInto(tempFiles);
		Vector filesForUpload = new Vector();
		for (int j=0; j<tempFiles.length; j++){
			if (tempFiles[j].isDirectory()){
				File [] subDirFiles = tempFiles[j].listFiles();
				for (int k = 0; k<subDirFiles.length; k++){
					if (subDirFiles[k].isFile())
						filesForUpload.add(subDirFiles[k]);
				}
				
			} else
			filesForUpload.add(tempFiles[j]);
		}
		if (files == null){
			files = new File[0];
		}
		tempFiles = new File[filesForUpload.size()+files.length];
		for (int j=0; j<files.length; j++)
			tempFiles[j] = files[j];
		for (int j=0; j<filesForUpload.size(); j++){
			tempFiles[j+files.length] = (File)filesForUpload.elementAt(j);
		}
		files = tempFiles;
		tableUpdate();
		
		if (files != null && files.length>0) {
			upload.setEnabled(true);
			remove.setEnabled(true);
		}
		if (files !=null && autoUpload){
			uploadClick();
		}
		
	    }
	    catch (java.awt.datatransfer.UnsupportedFlavorException usfe){;}
	    catch (java.io.IOException ioe){;}
	    dtde.dropComplete(true);
    }
    public void dropActionChanged(DropTargetDragEvent dtde){;}
    public void dragOver(DropTargetDragEvent dtde){;}
    public void dragExit(DropTargetEvent dte){;}
    public void dragEnter(DropTargetDragEvent dtde){;}
    
    public void mouseEntered(MouseEvent e){;}
    public void mouseExited(MouseEvent e){;}
    public void mousePressed(MouseEvent e){;}
    public void mouseReleased(MouseEvent e){;}
    
}
