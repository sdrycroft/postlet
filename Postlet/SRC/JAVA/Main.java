import java.awt.*;
import java.util.*;
import java.applet.Applet;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.net.*;
import javax.swing.JDialog;
import java.lang.*;
import javax.swing.JApplet;


public class Main extends JApplet implements MouseListener
{    
    JScrollPane scrollPane;
    JTable table;
    JPanel rightPanel;
    JButton add, remove, upload;
    String rowData [][];
    String filenames[];
    TableColumn left, middle, right;
    TableData tabledata;
    FileUploader fu;
    File [] file;
    JLabel progressLabel;
    JProgressBar progBar;
    int sentBytes;
    int fileSize[];
    int totalBytes;
    Font font;
    URL destination;
    Color backgroundColor, columnHeadColour;
    
    public void init()
    {       
        // Set the look of the applet to be the same as the system standard
        // of the computer that the applet is running on.
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException exc){;}
        catch (IllegalAccessException exc){;}
        catch (ClassNotFoundException exc){;}
        catch (InstantiationException exc){;}
        
        // Get the destination which is set by a parameter.
        try {
            destination = new URL(getParameter("destination"));
            System.out.println("Destination has been set to:"+destination);
        }
        catch(java.net.MalformedURLException malurlex){
            // Do something here for badly formed destination, which is ESENTIAL.
            JOptionPane message = new JOptionPane();
            JOptionPane.showMessageDialog(null, "The destination URL provided is not a valid one.","Postlet error.", JOptionPane.ERROR_MESSAGE);
            destination = null;
        }
        catch(java.lang.NullPointerException npe){
            // Do something here for the missing destination, which is ESENTIAL.
            JOptionPane message = new JOptionPane();
            JOptionPane.showMessageDialog(null, "You have not provided a destination URL.", "Postlet error.", JOptionPane.ERROR_MESSAGE);
            destination = null;
        }
        
        try {
            // Set the background color, which is set by a parameter.
            Integer redInteger = new Integer(getParameter("red"));
            int red = redInteger.intValue();
            Integer greenInteger = new Integer(getParameter("green"));
            int green = greenInteger.intValue();
            Integer blueInteger = new Integer(getParameter("blue"));
            int blue = blueInteger.intValue();
            backgroundColor = new Color(red, green, blue);
            System.out.println("Background colour has been set to:"+red+"/"+green+"/"+blue);
            blueInteger= null;
            greenInteger = null;            
            
            redInteger = new Integer(getParameter("redheader"));
            int redheader = redInteger.intValue();
            greenInteger = new Integer(getParameter("greenheader"));
            int greenheader = greenInteger.intValue();
            blueInteger = new Integer(getParameter("blueheader"));
            int blueheader = blueInteger.intValue();
            columnHeadColour = new Color(redheader, greenheader, blueheader);
            System.out.println("Background colour has been set to:"+redheader+"/"+greenheader+"/"+blueheader);
            redInteger = null;
            blueInteger= null;
            greenInteger = null;
        }
        catch(java.lang.NullPointerException npered){
            // Color isn't set.
            // Just ignore this, and set the background color to the 
            // default one.
            backgroundColor = null;
        }
        catch(java.lang.NumberFormatException numfe){
            // Color isn't set.
            // Just ignore this, and set the background color to the 
            // default one.
            backgroundColor = null;
        }
                
        // Get the main pane to add content to.
        Container pane = getContentPane();
        
        // Table for the adding of Filenames and sizes to.
        tabledata = new TableData();
        table = new JTable(tabledata);       
        table.setColumnSelectionAllowed(false);
        table.setDragEnabled(false);
        table.getColumn("Filename").setMinWidth(300);
        table.getTableHeader().setBackground(columnHeadColour);
        table.getTableHeader().setForeground(Color.RED);
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                
        // Add the scroll pane/table to the main pane
        pane.add(scrollPane, BorderLayout.CENTER);
        
        rightPanel = new JPanel(new GridLayout(4,1,10,10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        add = new JButton("Add");
        add.addMouseListener(this);
        rightPanel.add(add);
        
        remove = new JButton("Remove");
        remove.addMouseListener(this);
        rightPanel.add(remove);
        
        upload = new JButton("Upload");
        upload.addMouseListener(this);
        rightPanel.add(upload);
        pane.add(rightPanel,"East");   
        
        JPanel progPanel = new JPanel(new GridLayout(1, 3));
        progPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        JLabel progCompletion = new JLabel("Upload progress: ",SwingConstants.RIGHT);
        progPanel.add(progCompletion);
        
        progBar = new JProgressBar();
        progPanel.add(progBar);
        
        progressLabel = new JLabel("",SwingConstants.CENTER);
        progressLabel.setFont(font);
        //progressLabel.setForeground(blue);
        progPanel.add(progressLabel);
        
        
        if (backgroundColor != null){
            pane.setBackground(backgroundColor);
            rightPanel.setBackground(backgroundColor);
            scrollPane.setBackground(backgroundColor);
            progPanel.setBackground(backgroundColor);
            scrollPane.getViewport().setBackground(backgroundColor);
        }
        
        pane.add(progPanel,"South");
        
        // If the destination has not been set/isn't a proper URL
        // Then deactivate the buttons.
        if (destination == null)
        {
            remove.setEnabled(false);
            add.setEnabled(false);
            upload.setEnabled(false);
        }
    }
    
    public void removeClick()
    {
       if(table.getSelectedRowCount()>0)
       {
           File [] fileTemp = new File[file.length-table.getSelectedRowCount()];
           int k=0;
           for(int i=0; i<file.length-table.getSelectedRowCount(); i++)
           {
               for(int j=0; j<table.getSelectedRowCount(); j++)
               {
                   if(i==table.getSelectedRows()[j])
                   {
                       k++;
                   }
                   fileTemp[i] = file[i+k];
               }
           }
           file = fileTemp;
           tableUpdate();
       }
    }
    
    public void uploadClick()
    {
        if(filenames !=null)
        { 
            sentBytes = 0;
            progBar.setMaximum(totalBytes);
            progBar.setMinimum(0);
            Upload u = new Upload(filenames, fileSize, this, destination);
            u.start();       
        }
    }
    
    public void setProgress(int a)
    {
        sentBytes += a;
        progBar.setValue(sentBytes);
        if (sentBytes == totalBytes)
            progressLabel.setText("FINISHED");
    }
    
    public void tableUpdate()
    {
        totalBytes = 0;
        filenames = new String[file.length];
        fileSize = new int[file.length];
        for(int i=0; i<file.length; i++)
        {
            filenames[i] = file[i].getAbsolutePath();
            fileSize[i] = (int)file[i].length();
            totalBytes += (int)file[i].length();
        }
        int i=0;
        rowData = new String[255][2];
        while(i<file.length)
        {
            rowData[i][0] = file[i].getName();
            rowData[i][1] = ""+file[i].length();
            i++;
        }
        tabledata.formatTable(rowData,i);
        table.getColumn("Filename").setMinWidth(300);
        repaint();
    }
    
    public void addClick()
    {
        JFileChooser chooser = new JFileChooser(); 
        
        progressLabel.setText("");
        progBar.setValue(0);
        UploaderFileFilter filter = new UploaderFileFilter();
        filter.addExtension("jpg");
        filter.addExtension("jpeg");
        filter.addExtension("gif");
        filter.addExtension("bmp");
        filter.addExtension("png");
        filter.addExtension("raw");
        filter.addExtension("tif");
        filter.setDescription("Image files");        
        chooser.setFileFilter(filter);
        
        
        chooser.setAccessory(new FilePreviewer(chooser)); 
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(true);
        chooser.getSelectedFile();
        chooser.setDialogTitle("Select file for upload");
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            file = chooser.getSelectedFiles();            
            tableUpdate();
        }        
    }
    

    // Here for testing purposes
    public static void main(String args[]) 
    {      
        Frame f = new Frame("Uploader");
        Main main = new Main();
        
        main.init();
        
        f.add("Center", main);
        f.pack();
        f.setSize(600,200);
        f.setVisible(true);
    }    

    
    public void mouseClicked(MouseEvent e) 
    {
        if(e.getSource()==add && add.isEnabled())           {addClick();}
        if(e.getSource()==upload && upload.isEnabled())     {uploadClick();}
        if(e.getSource()==remove && remove.isEnabled())     {removeClick();}
    }
    
    public void mouseEntered(MouseEvent e){;}
    public void mouseExited(MouseEvent e){;}
    public void mousePressed(MouseEvent e){;}
    public void mouseReleased(MouseEvent e){;}
    
}