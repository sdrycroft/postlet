import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class SplitFileInputStream extends FileInputStream
{
	private long offset;
	private long size;

	public SplitFileInputStream(SplitFile f, long chunk) throws FileNotFoundException, IOException
	{
		// Create the parent for this file
		super(f.getFile());

		// Set size
		size = f.getChunkSize(chunk);

		// Go to the chunk offset
		super.skip(f.getChunkOffset(chunk));
		super.mark((int)size);

		// Start at offset 0
		offset = 0;
	}

	public int available()
	{
		// Return available bytes
		return (int)(size - offset);
	}

	public int read() throws IOException
	{
		if(available() == 0)
			return -1;

		++offset;
		return super.read();
	}

	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		int available = available();
		if(available == 0)
			return -1;

		int read = Math.min(len, available);
		offset += read;
		return super.read(b, off, read);
	}

	public void reset() throws IOException
	{
		offset = 0;
		super.reset();
	}

	public long skip(long n) throws IOException
	{
		long skip = Math.min(n, (long)available());
		return super.skip(skip);
	}
}
