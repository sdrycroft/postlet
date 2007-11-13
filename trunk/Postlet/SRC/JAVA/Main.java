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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

	private JTable table;
	private JScrollPane scrollPane;
	private JPanel rightPanel;
	private JButton add,remove,upload,help;
	private ImageIcon dropIcon,dropIconUpload,dropIconAdded;	
	private TableData tabledata;
	private TableColumn sizeColumn;
	private File [] files;
	private JLabel progCompletion,iconLabel;
	private JProgressBar progBar;
	private int sentBytes,totalBytes,buttonClicked, maxPixels;
	private Color backgroundColour,columnHeadColourBack,columnHeadColourFore;
	private PostletLabels pLabels;
	private Vector failedFiles,uploadedFiles;

	// Default error PrintStream!
	private PrintStream out = System.out;

	// Boolean set to false when a javascript method is executed
	private boolean javascript;

	// Parameters
	private URL endPageURL, helpPageURL, destinationURL,dropImageURL,dropImageUploadURL,dropImageAddedURL;
	private boolean warnMessage,autoUpload,helpButton,failedFileMessage,addButton,removeButton,uploadButton;
	private String language, dropImage, dropImageAdded, dropImageUpload;
	private int maxThreads;
	private String [] fileExtensions;

	// URI list flavor (Hack for linux/KDE)
	private DataFlavor uriListFlavor;

	// Postlet Version (Mainly for diagnostics and tracking)
	public static final String postletVersion = "0.13";

	public void init() {
		// First thing, output the version, for debugging purposes.
		System.out.println("POSTLET VERSION: "+postletVersion);
		String date = "$Date$";
		System.out.println(date.substring(7,date.length()-1));

	// URI list flavor:
	try {
		uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
	}
	catch (ClassNotFoundException cnfe){
		errorMessage("No class found for DataFlavor");
	}

		// Set the javascript to false, and start listening for clicks
		javascript = false;
		JavascriptListener jsListen = new JavascriptListener(this);
		jsListen.start();
		buttonClicked = 0; // Default of add click.

		getParameters();// Also sets pLabels
		layoutGui();
		// Vector of failedFiles
		failedFiles = new Vector();
		uploadedFiles = new Vector();

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
			errorMessage( "Too many listeners to drop!");
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
			errorMessage( "setting the tables colours");
			table.getTableHeader().setBackground(columnHeadColourBack);
			table.getTableHeader().setForeground(columnHeadColourFore);
			table.setBackground(backgroundColour);
		}
		scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		if (backgroundColour != null){
			scrollPane.setBackground(backgroundColour);
		}
		// Always set the table background colour as White.
		// May change this if required, only would require alot of Params!
		scrollPane.getViewport().setBackground(Color.white);
		
		if (dropImageURL!=null){
			// Instead of the table, we'll add a lovely image to the center
			// of the applet to drop images on.
			dropIcon = new ImageIcon(dropImageURL);
			iconLabel = new JLabel(dropIcon);
			pane.add(iconLabel, BorderLayout.CENTER);			
		}
		else {
			// Add the scroll pane/table to the main pane
			pane.add(scrollPane, BorderLayout.CENTER);
		}
		if (dropImageUploadURL!=null)
			dropIconUpload = new ImageIcon(dropImageUploadURL);
		if (dropImageAddedURL!=null)
			dropIconAdded = new ImageIcon(dropImageAddedURL);

		errorMessage("Adding button");
		if (helpButton)
			rightPanel = new JPanel(new GridLayout(4,1,10,10));
		else
			rightPanel = new JPanel(new GridLayout(3,1,10,10));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		add = new JButton(pLabels.getLabel(6));
		if(addButton){
			add.addMouseListener(this);
			rightPanel.add(add);
		}

		remove = new JButton(pLabels.getLabel(7));
		if(removeButton){
			remove.addMouseListener(this);
			remove.setEnabled(false);
			rightPanel.add(remove);
		}

		upload = new JButton(pLabels.getLabel(8));
		if(uploadButton){
			upload.addMouseListener(this);
			upload.setEnabled(false);
			rightPanel.add(upload);
		}

		help = new JButton(pLabels.getLabel(9));
		if (helpButton){
			help.addMouseListener(this);
			rightPanel.add(help);
		}
		if (backgroundColour != null)
			rightPanel.setBackground(backgroundColour);
		if(addButton || removeButton || helpButton || uploadButton)
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
			progPanel.setBackground(backgroundColour);
		}
		pane.add(progPanel,"South");

		// If the destination has not been set/isn't a proper URL
		// Then deactivate the buttons. 
		if (destinationURL == null)
			add.setEnabled(false);
	}

	public void errorMessage(String message){
		out.println("*** "+message+" ***");
	}
	// Helper method for getting the parameters from the webpage.
	private void getParameters(){

		/* LANGUAGE */
		try {
			language = getParameter("language");
			if (language == "" || language == null)
				language = "EN";
		} catch (NullPointerException nullLang){
			// Default language being set
			language = "EN";
			errorMessage("language is null");
		}
	// This method (getParameters) relies on labels from PostletLabels if
	// there is an error.
		pLabels = new PostletLabels(language, getCodeBase());

		/* DESTINATION */
		try {
			destinationURL = new URL(getParameter("destination"));
		// Following line is for testing, and to hard code the applet to postlet.com
		//destinationURL = new URL("http://www.postlet.com/example/javaUpload.php");
		} catch(java.net.MalformedURLException malurlex){
			// Do something here for badly formed destination, which is ESENTIAL.
			errorMessage( "Badly formed destination:###"+getParameter("destination")+"###");
			JOptionPane.showMessageDialog(null, ""+pLabels.getLabel(3), ""+pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
		} catch(java.lang.NullPointerException npe){
			// Do something here for the missing destination, which is ESENTIAL.
			errorMessage("destination is null");
			JOptionPane.showMessageDialog(null, pLabels.getLabel(4), pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
		}

		/* BACKGROUND */
		try {
			Integer bgci = new Integer(getParameter("backgroundcolour"));
			backgroundColour = new Color(bgci.intValue());
		} catch(NumberFormatException numfe){
			errorMessage( "background colour is not a number:###"+getParameter("backgroundcolour")+"###");
		} catch (NullPointerException nullred){
			errorMessage( "background colour is null");
		}

		/* TABLEHEADERFOREGROUND */
		try {
			Integer thfi = new Integer(getParameter("tableheadercolour"));
			columnHeadColourFore = new Color(thfi.intValue());
		} catch(NumberFormatException numfe){
			errorMessage( "table header colour is not a number:###"+getParameter("tableheadcolour")+"###");
		} catch (NullPointerException nullred){
			errorMessage( "table header colour is null");
		}

		/* TABLEHEADERBACKGROUND */
		try {
			Integer thbi = new Integer(getParameter("tableheaderbackgroundcolour"));
			columnHeadColourBack = new Color(thbi.intValue());
		} catch(NumberFormatException numfe){
			errorMessage( "table header back colour is not a number:###"+getParameter("tableheaderbackgroundcolour")+"###");
		} catch (NullPointerException nullred){
			errorMessage( "table header back colour is null");
		}

		/* FILEEXTENSIONS */
		try {
			fileExtensions = getParameter("fileextensions").split(",");
		} catch(NullPointerException nullfileexts){
			errorMessage( "file extensions is null");
		}

		/* WARNINGMESSAGE */
		try {
			if (getParameter("warnmessage").toLowerCase() == "true")
				warnMessage = true;
			else
				warnMessage = false;
		} catch(NullPointerException nullwarnmessage){
			errorMessage( "warnmessage is null");
			warnMessage = false;
		}

		/* AUTOUPLOAD */
		try {
			if (getParameter("autoupload").toLowerCase().equals("true"))
				autoUpload = true;
			else
				autoUpload = false;
		} catch(NullPointerException nullwarnmessage){
			errorMessage( "autoUpload is null");
			autoUpload = false;
		}

		/* MAXTHREADS */
		try {
			Integer maxts = new Integer(getParameter("maxthreads"));
			maxThreads = maxts.intValue();
		} catch (NullPointerException nullmaxthreads){
			errorMessage( "maxthreads is null");
		} catch (NumberFormatException nummaxthreads){
			errorMessage( "maxthread is not a number");}

		/* ENDPAGE */
		try {
			endPageURL = new URL(getParameter("endpage"));
		} catch(java.net.MalformedURLException malurlex){
			errorMessage( "endpage is badly formed:###"+getParameter("endpage")+"###");
		} catch(java.lang.NullPointerException npe){
			errorMessage( "endpage is null");
		}

		/* HELPPAGE */
		try {
			helpPageURL = new URL(getParameter("helppage"));
		} catch(java.net.MalformedURLException malurlex){
			errorMessage( "helppage is badly formed:###"+getParameter("helppage")+"###");
		} catch(java.lang.NullPointerException npe){
			errorMessage( "helppage is null");
		}

		/* HELP BUTTON */
		try {
			if (getParameter("helpbutton").toLowerCase().trim().equals("true"))
				helpButton = true;
			else
				helpButton = false;
		} catch(NullPointerException nullwarnmessage){
			errorMessage( "helpbutton is null");
			helpButton = false;
		}

		/* ADD BUTTON */
		try {
			if (getParameter("addbutton").toLowerCase().trim().equals("false"))
				addButton = false;
			else
				addButton = true;
		} catch(NullPointerException nullwarnmessage){
			errorMessage( "addbutton is null");
			addButton = true;
		}

		/* REMOVE BUTTON */
		try {
			if (getParameter("removebutton").toLowerCase().trim().equals("false"))
				removeButton = false;
			else
				removeButton = true;
		} catch(NullPointerException nullwarnmessage){
			errorMessage( "removebutton is null");
			removeButton = true;
		}

		/* UPLOAD BUTTON */
		try {
			if (getParameter("uploadbutton").toLowerCase().trim().equals("false"))
				uploadButton = false;
			else
				uploadButton = true;
		} catch(NullPointerException nullwarnmessage){
			errorMessage( "uploadbutton is null");
			uploadButton = true;
		}
				
		/* REPLACE TABLE WITH "DROP" IMAGE */
		try {
			dropImage = getParameter("dropimage");
			if (dropImage!=null)
				dropImageURL = new URL(dropImage);
		} catch(MalformedURLException urlexception){
			try {
				URL codeBase = getCodeBase();
				dropImageURL = new URL(codeBase.getProtocol()+"://"+codeBase.getHost()+codeBase.getPath()+dropImage);
			} catch(MalformedURLException urlexception2){
				errorMessage("dropimage is not a valid reference");
			}
		}
		/* REPLACE TABLE WITH "DROP" IMAGE (UPLOAD IMAGE)*/
		try {
			dropImageUpload = getParameter("dropimageupload");
			if (dropImageUpload!=null)
				dropImageUploadURL = new URL(dropImageUpload);
		} catch(MalformedURLException urlexception){
			try {
				URL codeBase = getCodeBase();
				dropImageUploadURL = new URL(codeBase.getProtocol()+"://"+codeBase.getHost()+codeBase.getPath()+dropImageUpload);
			} catch(MalformedURLException urlexception2){
				errorMessage("dropimageupload is not a valid reference");
			}
		}
		/* REPLACE TABLE WITH "DROP" IMAGE (ADDED IMAGE)*/
		try {
			dropImageAdded = getParameter("dropimageadded");
			if (dropImageAdded!=null)
				dropImageAddedURL = new URL(dropImageAdded);
		} catch(MalformedURLException urlexception){
			try {
				URL codeBase = getCodeBase();
				dropImageAddedURL = new URL(codeBase.getProtocol()+"://"+codeBase.getHost()+codeBase.getPath()+dropImageAdded);
			} catch(MalformedURLException urlexception2){
				errorMessage("dropimageupload is not a valid reference");
			}
		}
		
		/* FAILED FILES WARNING */
		// This should be set to false if failed files are being handled in
		// javascript
		try {
			if (getParameter("failedfilesmessage").toLowerCase().trim().equals("true"))
				failedFileMessage = true;
			else
				failedFileMessage = false;
		} catch (NullPointerException nullfailedfilemessage){
			errorMessage( "failedfilemessage is null");
			failedFileMessage = false;
		}
		
		/* MAX PIXELS FOR AN UPLOADED IMAGE */
		// This supports PNG, GIF and JPEG images only.  All other images will
		// not be resized
		try {
			Integer maxps = new Integer(getParameter("maxpixels"));
			maxPixels = maxps.intValue();
		} catch (NullPointerException nullmaxpixels){
			errorMessage( "maxpixels is null");
		} catch (NumberFormatException nummaxpixels){
			errorMessage( "maxpixels is not a number");}
	}

	public int getMaxPixels(){
		return maxPixels;
	}
	
	public void setMaxPixels(int pixels){
		maxPixels = pixels;
	}
	
	public void removeClick() {
		if(table.getSelectedRowCount()>0) {
			File [] fileTemp = new File[files.length-table.getSelectedRowCount()];
			int [] selectedRows = table.getSelectedRows();
			Arrays.sort(selectedRows);
			int k=0;
			for (int i=0; i<files.length;i++){
				if (Arrays.binarySearch(selectedRows,i)<0){
					fileTemp[k]=files[i];
					k++;
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
			if (dropImageURL!=null && dropImageUploadURL!=null){
				iconLabel.setIcon(dropIconUpload);
				repaint();
			}
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
			// Upload is complete. Check for failed files.
			if (failedFiles.size()>0 && failedFileMessage){
				// There is at least one failed file. Show an error message
				String failedFilesString = "\r\n";
				for (int i=0; i<failedFiles.size(); i++){
					File tempFile = (File)failedFiles.elementAt(i);
					failedFilesString += tempFile.getName()+"\r\n";
				}
				JOptionPane.showMessageDialog(null, pLabels.getLabel(16)+":"+failedFilesString,pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
			}
			progCompletion.setText(pLabels.getLabel(2));
			if (endPageURL != null){
				getAppletContext().showDocument(endPageURL);
			} else {
				try {
					// Just ignore this error, as it is most likely from the endpage
					// not being set.
					// Attempt at calling Javascript after upload is complete.
					JSObject win = (JSObject) JSObject.getWindow(this);
					win.eval("postletFinished();");
				}
				catch (netscape.javascript.JSException jse){
					// Not to worry, just means the end page and a postletFinished
					// method aren't set. Just finish, and let the web page user
					// exit the page
					errorMessage("postletFinished, and End page unset");
				}
			}
			// Reset the applet
			progBar.setValue(0);
			files = new File[0];
			tableUpdate();
			add.setEnabled(true);
			help.setEnabled(true);
			if (dropImageURL!=null && dropImageUploadURL!=null){
				iconLabel.setIcon(dropIcon);
				repaint();
			}
			failedFiles.clear();
			uploadedFiles.clear();
		}
	}

	// Adds a file that HASN'T uploaded to an array. Once uploading is complete,
	// these can be listed with a popup box.
	public void addFailedFile(File f){
		failedFiles.add(f);
	}

	// Adds a file that HAS uploaded to an array. These are passed along with
	// failed files to a javascript method.
	public void addUploadedFile(File f){
		uploadedFiles.add(f);
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
		// FIXME - THIS SEEMS SILLY!********************************************
		String [][] rowData = new String[files.length][2];
		while(i<files.length) {
			rowData[i][0] = files[i].getName();
			rowData[i][1] = ""+files[i].length();
			i++;
		}
		// *********************************************************************
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
			if (dropImageURL!=null && dropImageAddedURL!=null){
				iconLabel.setIcon(dropIconAdded);
				repaint();
			}
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

	public String [] javascriptGetFailedFiles(){
		if (failedFiles.size()>0){
			String [] arrayFailedFiles = new String[failedFiles.size()];
			for (int i=0; i<failedFiles.size(); i++){
				File tempFile = (File)failedFiles.elementAt(i);
				arrayFailedFiles[i] = tempFile.getName();
			}
			return arrayFailedFiles;
		}
		else {
			return null;
		}
	}

	public String [] javascriptGetUploadedFiles(){
		if (uploadedFiles.size()>0){
			String [] arrayUploadedFiles = new String[uploadedFiles.size()];
			for (int i=0; i<uploadedFiles.size(); i++){
				File tempFile = (File)uploadedFiles.elementAt(i);
				arrayUploadedFiles[i] = tempFile.getName();
			}
			return arrayUploadedFiles;
		}
		else {
			return null;
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
		if(e.getSource()==add && add.isEnabled())		{addClick();}
		if(e.getSource()==upload && upload.isEnabled())	{uploadClick();}
		if(e.getSource()==remove && remove.isEnabled())	{removeClick();}
		if(e.getSource()==help && help.isEnabled())		{helpClick();}
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
					errorMessage("Windows D'n'D");
					List listOfFiles = (List)trans.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator iter = listOfFiles.iterator();
					while (iter.hasNext()) {
						File tempFile = (File) iter.next();
						filesFromDrop.add(tempFile);
					}
					filesFound = true;
				} else if (dataFlavour[i].equals(uriListFlavor)){
					// Linux
					errorMessage("Linux (Mac?) D'n'D");
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
			errorMessage("Enabling the upload and remove buttons");
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
