import java.awt.*;
import java.util.*;
import java.applet.Applet;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Main extends javax.swing.JApplet implements MouseListener
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
    Color blue;
    JLabel progressLabel;
    JProgressBar progBar;
    int sentBytes;
    int fileSize[];
    int totalBytes;
    Font font;
    
    public void init()
    {       
        try 
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (UnsupportedLookAndFeelException exc){;}
        catch (IllegalAccessException exc){;}
        catch (ClassNotFoundException exc){;}
        catch (InstantiationException exc){;}
        
        blue = new Color(0,111,221);
        Font font = new Font("Arial",Font.BOLD, 12);
        Container pane = getContentPane();
        pane.setBackground(Color.white);
        
        tabledata = new TableData();
        table = new JTable(tabledata);
        table.setBackground(Color.white);
        table.getTableHeader().setBackground(blue);
        table.getTableHeader().setForeground(Color.white);
        table.getTableHeader().setFont(font);
        table.setColumnSelectionAllowed(false);
        table.setDragEnabled(false);
        table.getColumn("Filename").setMinWidth(300);
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        scrollPane.setBackground(Color.white);
                
        pane.add(scrollPane, BorderLayout.CENTER);
        
        rightPanel = new JPanel(new GridLayout(4,1,10,10));
        rightPanel.setBackground(Color.white);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        add = new JButton("Add");
        add.setForeground(Color.white);
        add.setBackground(blue);
        add.addMouseListener(this);
        rightPanel.add(add);
        
        remove = new JButton("Remove");
        remove.setForeground(Color.white);
        remove.setBackground(blue);
        remove.addMouseListener(this);
        rightPanel.add(remove);
        
        upload = new JButton("Upload");
        upload.setBackground(blue);
        upload.setForeground(Color.white);
        upload.addMouseListener(this);
        rightPanel.add(upload);
        pane.add(rightPanel,"East");   
        
        JPanel progPanel = new JPanel(new GridLayout(1, 3));
        progPanel.setBackground(Color.white);
        progPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        JLabel progCompletion = new JLabel("Upload progress: ",SwingConstants.RIGHT);
        progCompletion.setFont(font);
        progCompletion.setForeground(blue);
        progPanel.add(progCompletion);
        
        progBar = new JProgressBar();
        progBar.setBackground(Color.white);
        progBar.setForeground(Color.BLACK);
        progPanel.add(progBar);
        
        progressLabel = new JLabel("",SwingConstants.CENTER);
        progressLabel.setFont(font);
        progressLabel.setForeground(blue);
        progPanel.add(progressLabel);
        
        pane.add(progPanel,"South");
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
            Upload u = new Upload(filenames, fileSize, this);
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
    
    public static void main(String args[]) 
    {      
        Frame f = new Frame("Uploader");
        Main main = new Main();
        
        main.init();
        
        f.add("Center", main);
        f.pack();
        f.setSize(600,200);
        f.show();
    }
    
    public void mouseClicked(MouseEvent e) 
    {
        if(e.getSource()==add)      {addClick();}
        if(e.getSource()==upload)   {uploadClick();}
        if(e.getSource()==remove)   {removeClick();}
    }
    
    public void mouseEntered(MouseEvent e){;}
    public void mouseExited(MouseEvent e){;}
    public void mousePressed(MouseEvent e){;}
    public void mouseReleased(MouseEvent e){;}
    
}