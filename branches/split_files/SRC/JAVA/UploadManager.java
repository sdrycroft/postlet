/*	Copyright (C) 2005 Simon David Rycroft

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA. */

import java.io.File;
import java.net.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

// Note, the upload manager extends Thread so that the GUI is
// still responsive, and updates.
public class UploadManager extends Thread {

	SplitFile [] files;
	Main main;
	URL destination;
	private int maxThreads = 5;
	ExecutorService threadpool = null;

	/** Creates a new instance of Upload */
	public UploadManager(SplitFile [] f, Main m, URL d){
		files = f;
		main = m;
		destination = d;
	}

	public UploadManager(SplitFile [] f, Main m, URL d, int max){
		try {
			if (max>5 || max < 1)
				max = 5;
			maxThreads = max;
		}
		catch (NullPointerException npe){
			maxThreads = 5;// Leave the maxThreads as default
		}
		files = f;
		main = m;
		destination = d;
	}
	
	public void cancelUpload(){
		// if there is a threadpool, shut it down
		if(threadpool != null)
			threadpool.shutdownNow();
	}

	public void run()
	{
		// create a threadpool to run these threads
		threadpool = Executors.newFixedThreadPool(maxThreads);
		try
		{
			// submit the tasks to this pool
			for(int i=0; i<files.length; i++)
			{
				// add one thread for each chunk
				for(int j=0; j<files[i].getChunkCount(); j++)
					threadpool.execute(new UploadTask(destination, files[i], j, main));
			}

			// do not accept more tasks
			threadpool.shutdown();

			// wait until all tasks are complete
			while(!threadpool.awaitTermination(1, TimeUnit.SECONDS));
		}
		catch(UnknownHostException uhe) {System.out.println("*** UnknownHostException: UploadManager ***");}
		catch(IOException ioe)          {System.out.println("*** IOException: UploadManager ***");}
		catch(InterruptedException ie) {
				// force a shutdown
				threadpool.shutdownNow();
		}
	}

	private void urlFailure(){
		// Output a message explaining that the URL has failed.
		// This should stop all the threads!
	}
}
