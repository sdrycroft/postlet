
import java.io.File;

// This class simply holds a file + its chunks
public class SplitFile {
	public static int STATUS_OK = 0;
	public static int STATUS_NOT_FOUND = 1;
	public static int STATUS_FAILED = 2;
	public static int STATUS_NOT_ALLOWED = 3;

	private File file;
	private long chunks, remaining, chunksize;
	private int status;

	public SplitFile(File f, long c)
	{
		// Assign
		file = f;
		chunksize = c;

		// Calculate the amount of chunks
		chunks = (long)Math.ceil((double)f.length() / (double)chunksize);
		remaining = chunks;

		// We assume this file is ok
		status = SplitFile.STATUS_OK;
	}

	public long getChunkCount()
	{
		return chunks;
	}

	// This is synced so no 2 threads ever decrease at the same time
	synchronized public void decRemaining()
	{
		--remaining;
	}

	public long getRemaining()
	{
		return remaining;
	}

	public long getChunkSize(long chunk)
	{
		// Size is full chunk size if it's not the last chunk
		if(chunk < chunks - 1)
			return chunksize;
		else if(chunk >= chunks)
			return -1; // fail
		else
			return file.length() % chunksize; // or the remaining file size
	}

	public long getChunkOffset(long chunk)
	{
		// Return the offset in the file for this chunk (which starts at 0)
		return chunk * chunksize;
	}

	public File getFile()
	{
		return file;
	}

	public void setStatus(int s)
	{
		status = s;
	}

	public int getStatus()
	{
		return status;
	}
}
