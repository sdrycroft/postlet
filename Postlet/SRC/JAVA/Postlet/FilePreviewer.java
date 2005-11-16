package Postlet;
import javax.swing.*; 
import javax.swing.border.*;   
import java.awt.*; 
import java.beans.*; 
import java.io.*; 

public class FilePreviewer extends JComponent implements PropertyChangeListener
{
     ImageIcon thumbnail;
     
     public FilePreviewer()
     {
         thumbnail = null; 
     }
     
     public FilePreviewer(JFileChooser fc) 
     { 
         setPreferredSize(new Dimension(100, 50)); 
         fc.addPropertyChangeListener(this);
         setBorder(new BevelBorder(BevelBorder.LOWERED)); 
     } 
      
     public void loadImage(File f) 
     {
        if (f == null) 
        { 
           thumbnail = null; 
        }
        else 
        { 
           ImageIcon tmpIcon = new ImageIcon(f.getPath()); 
           if(tmpIcon.getIconWidth() > 90) 
           {  		
              thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT)); 
           }
           else 
           { 
              thumbnail = tmpIcon; 
           } 
        } 
     } 
     
     public void propertyChange(PropertyChangeEvent e) 
     { 
        String prop = e.getPropertyName(); 
        if(prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) 
        { 
           if(isShowing()) 
           { 
              loadImage((File) e.getNewValue()); 
              repaint(); 
           } 
        } 
     } 
     
     public void paint(Graphics g) 
     { 
        super.paint(g); 
        if(thumbnail != null) 
        { 
           int x = getWidth()/2 - thumbnail.getIconWidth()/2; 
           int y = getHeight()/2 - thumbnail.getIconHeight()/2; 
           if(y < 0) 
           { 
              y = 0; 
           } 
           if(x < 5) 
           { 
              x = 5; 
           } 
           thumbnail.paintIcon(this, g, x, y); 
        } 
     } 
}