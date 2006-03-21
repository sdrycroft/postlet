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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.net.URL;
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

public class Main extends JApplet implements MouseListener {
	
	JTable table;
	JButton add, remove, upload, help;
	TableData tabledata;
	TableColumn sizeColumn;
	File [] files;
	JLabel progCompletion;
	JProgressBar progBar;
	int sentBytes,totalBytes,buttonClicked,red,green,blue;
	Font font;
	String destination;
	URL endpage, helppage;
	Color backgroundColour, columnHeadColourBack, columnHeadColourFore;
	PostletLabels pLabels;
	boolean javascript;
	Integer redInteger, greenInteger, blueInteger;
	
	public void init() {
		// First thing, output the version, for debugging purposes.
		System.out.println("POSTLET VERSION: 0.8.0");
		String date = "$Date$";
		System.out.println(date.substring(7,date.length()-1));

		// Set the javascript to false, and start listening for clicks
		javascript = false;
		JavascriptListener jsListen = new JavascriptListener(this);
		jsListen.start();
		buttonClicked = 0; // Default of add click.
		
		// Set the lanuage.
		if (getParameter("language")==null | getParameter("language")=="")
			pLabels = new PostletLabels("EN", null);
		else
			pLabels = new PostletLabels(getParameter("language"), getCodeBase());

		// Set the look of the applet
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException exc){;}
		catch (IllegalAccessException exc){;} 
		catch (ClassNotFoundException exc){;} 
		catch (InstantiationException exc){;}

		// Get the destination which is set by a parameter.
		try {
			URL dest = new URL(getParameter("destination"));
			destination = getParameter("destination");
		} catch(java.net.MalformedURLException malurlex){
			// Do something here for badly formed destination, which is ESENTIAL.
			JOptionPane.showMessageDialog(null, pLabels.getLabel(3),pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
		} catch(java.lang.NullPointerException npe){
			// Do something here for the missing destination, which is ESENTIAL.
			JOptionPane.showMessageDialog(null, pLabels.getLabel(4), pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
		}

		try {
			// Set the background color, which is set by a parameter.
			redInteger = new Integer(getParameter("red"));
			red = redInteger.intValue();
			greenInteger = new Integer(getParameter("green"));
			green = greenInteger.intValue();
			blueInteger = new Integer(getParameter("blue"));
			blue = blueInteger.intValue();
			backgroundColour = new Color(red, green, blue);
		}
		catch(java.lang.NullPointerException npered){;}
		catch(java.lang.NumberFormatException numfe){;}

		try {
			// Set the background colour of the table headers.
			redInteger = new Integer(getParameter("redheaderback"));
			red = redInteger.intValue();
			greenInteger = new Integer(getParameter("greenheaderback"));
			green = greenInteger.intValue();
			blueInteger = new Integer(getParameter("blueheaderback"));
			blue = blueInteger.intValue();
			columnHeadColourBack = new Color(red, green, blue);
		}
		catch(java.lang.NullPointerException npered){;}
		catch(java.lang.NumberFormatException numfe){;}

		try {
			// Set the foreground colour of the table headers.
			redInteger = new Integer(getParameter("redheader"));
			red = redInteger.intValue();
			greenInteger = new Integer(getParameter("greenheader"));
			green = greenInteger.intValue();
			blueInteger = new Integer(getParameter("blueheader"));
			blue = blueInteger.intValue();
			columnHeadColourFore = new Color(red, green, blue);

		}
		catch(java.lang.NullPointerException npered){;}
		catch(java.lang.NumberFormatException numfe){;}

		// Get the main pane to add content to.
		Container pane = getContentPane();

		// Table for the adding of Filenames and sizes to.
		tabledata = new TableData(pLabels.getLabel(0),pLabels.getLabel(1)+" -KB ");
		table = new JTable(tabledata);
		table.setColumnSelectionAllowed(false);
		//table.setDragEnabled(false); // This method is not available to Java 3!
		sizeColumn = table.getColumn(pLabels.getLabel(1)+" -KB ");
		sizeColumn.setMaxWidth(100);
		table.getColumn(pLabels.getLabel(1)+" -KB ").setMinWidth(100);
		if (columnHeadColourBack != null && backgroundColour != null){
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
			// Always set the table background colour as White.
			// May change this if required, only would require alot of Params!
			scrollPane.getViewport().setBackground(Color.white);
		}

		pane.add(progPanel,"South");

		// If the destination has not been set/isn't a proper URL
		// Then deactivate the buttons.
		if (destination == null)
			add.setEnabled(false);
	}

	public void removeClick() {
		if(table.getSelectedRowCount()>0) {
			File [] fileTemp = new File[files.length-table.getSelectedRowCount()];
			int k=0;
			for(int i=0; i<files.length-table.getSelectedRowCount(); i++) {
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
			try {
				UploadManager u;
				try {
					int maxThreads;
					Integer maxThreadsInteger = new Integer(getParameter("maxthreads"));
					maxThreads = maxThreadsInteger.intValue();
					u = new UploadManager(files, this, destination, maxThreads);
				}
				catch(java.lang.NullPointerException npered){
					u = new UploadManager(files, this, destination);
				}
				catch(java.lang.NumberFormatException numfe){
					u = new UploadManager(files, this, destination);
				}
				u.start();
			}
			catch (UnknownHostException uhe){;}
			catch (MalformedURLException mue){;}
		}
	}
	
	public synchronized void setProgress(int a) {
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
		UploaderFileFilter filter = new UploaderFileFilter();
		filter.addExtension("jpg");
		filter.addExtension("jpeg");
		filter.addExtension("gif");
		filter.addExtension("bmp");
		filter.addExtension("png");
		filter.addExtension("raw");
		filter.addExtension("tif");
		filter.setDescription(pLabels.getLabel(13));
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(chooser.getAcceptAllFileFilter());

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
		try {
			getAppletContext().showDocument(new URL(helpUrl), "_blank");
		} catch (MalformedURLException helpexception){
			// Show a popup with help instead!
			JOptionPane.showMessageDialog(null, pLabels.getLabel(14),pLabels.getLabel(5), JOptionPane.ERROR_MESSAGE);
		}
		
	}
    
    public String getCookie(){
    
        // Method reads the cookie in from the Browser using the LiveConnect object.
        // May also add an option to set the cookie using an applet parameter FIXME!
        JSObject win = (JSObject) JSObject.getWindow(this);
        String cookie = ""+(String)win.eval("document.cookie");
        return cookie;
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
	public int getButtonClicked(){
		
		return buttonClicked;
	}
	
	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==add && add.isEnabled())		   {addClick();}
		if(e.getSource()==upload && upload.isEnabled())	 {uploadClick();}
		if(e.getSource()==remove && remove.isEnabled())	 {removeClick();}
		if(e.getSource()==help && help.isEnabled())		 {helpClick();}
	}
	
	public void mouseEntered(MouseEvent e){;}
	public void mouseExited(MouseEvent e){;}
	public void mousePressed(MouseEvent e){;}
	public void mouseReleased(MouseEvent e){;}
	
}
