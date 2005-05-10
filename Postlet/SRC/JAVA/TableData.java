import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.*;

public class TableData extends AbstractTableModel 
{
	
	Vector myTable;  
  	int colCount;
        public final static String [] headers = {"Filename","Size - Kb"};
        int totalFileSize;

  	public TableData()  
	{
		myTable = new Vector();	
		colCount = 2;
                totalFileSize =0;
 	}	
   
	public String getColumnName(int i) 
	{     
                if(i==1 && totalFileSize !=0)
                {
                    double totalFileSizeMB = totalFileSize/ 10485.76;
                    totalFileSizeMB = (double)Math.round(totalFileSizeMB)/100;
                    return headers[i]+" ("+totalFileSizeMB+"Mb)";  
                }
                else
                    return headers[i];                    
        }
	 
 	public int getColumnCount()
        {
            return colCount; }
 	
	public int getRowCount()
        {
            return myTable.size();}
        
        public int getTotalFileSize()
        {
            return totalFileSize;}
        
	public Object getValueAt(int row, int col)
        {
            return ((Object[])myTable.elementAt(row))[col];}

        
	public void formatTable(String [][] data, int dataLength)
	{
            totalFileSize =0;
            myTable = new Vector();
            int j=0;
            while (j<dataLength)
            {
                Object[] row = new Object[colCount];
                for (int k=0; k < colCount; k++) 
                {
                    if(k==1)
                    {
                        try{
                            int thisFileSize = Integer.parseInt(data[j][k]);
                            totalFileSize += thisFileSize;
                            thisFileSize /=102.4;
                            double thisFileKb = (double)thisFileSize/10;
                            row[k] = new Double(thisFileKb);}
                        catch(NumberFormatException nfe){;}
                    }                        
                    else
                        row[k] = data[j][k];
                }	
                myTable.addElement(row);
                j++;
            }            
            fireTableChanged(null);          
        }
}
 

 	
