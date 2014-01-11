package usstv;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Decoder
{
	
	private Decoder() // no constructor, encoding is a static method
	{}
	
	public static void DecodeImage(int rows, int cols, int timeout) throws IOException
	{
		if(rows < 1 || cols < 1 || timeout < 1) // check argument sanity
		{
			System.err.println("Illegal argument.");
			return;
		}
		
		BufferedImage[] chunks = new BufferedImage[rows*cols]; // array for image pieces
		int chunkIndex = 0;
		char[] chunkRow = new char[rows*cols]; // arrays for grid position information 
		char[] chunkCol = new char[rows*cols];
		byte[] input = new byte[150000]; // buffer for input stream
		int inputIndex = 0;
		long startTime = System.currentTimeMillis(); // reference for timeout check
		
		while(System.in.available() > 0 && System.currentTimeMillis() - startTime < 1000*timeout) // listen while we have data and not timed out
		{
			if(chunkIndex > 0 && chunkCol[chunkIndex-1] == cols-1 && chunkRow[chunkIndex-1] == rows-1) // quit if we have all chunks
				break;
			if(inputIndex >= 150000) // quit if we fill the buffer and haven't found anything
				break;
			input[inputIndex] = (byte)System.in.read(); // read a byte from stdin
			if(inputIndex > 0 && input[inputIndex] == (byte)0xD8 && input[inputIndex-1] == (byte)0xFF) // found the start of a jpg
			{
				input[0] = (byte)0xFF; // write jpg start marker to buffer
				input[1] = (byte)0xD8;
				inputIndex = 2; // continue filling buffer from end of marker
			}
			else if(inputIndex > 0 && input[inputIndex] == (byte)0xD9 && input[inputIndex-1] == (byte)0xFF) // found the end of a jpg
			{
				chunks[chunkIndex] = ImageIO.read(new ByteArrayInputStream(input)); // read the jpg from the buffer
				if(chunks[chunkIndex] != null) // if it worked
				{
					chunkRow[chunkIndex] = (char)(System.in.read()*256 + System.in.read()); // read in grid position information
					chunkCol[chunkIndex] = (char)(System.in.read()*256 + System.in.read());
					chunkIndex++;
				}
				inputIndex = 0; // start filling buffer from beginning
			}
			else // we didn't find a jpg marker
				inputIndex++; // read into the next byte in the buffer
		}
		
		if(chunkIndex == 0) // timed out without finding any jpgs
		{
			System.out.println("timed out");
			return;
		}

		int chunkWidth = chunks[0].getWidth(); // get the dimensions of the jpg piece
		int chunkHeight = chunks[0].getHeight();
		BufferedImage output = new BufferedImage(cols*chunkWidth, rows*chunkHeight, chunks[0].getType()); // create the output image
		for(int i = 0; i < chunkIndex; i++) // paste each piece onto the output image in correct position
		{
			if(chunks[i] == null)
				continue;
			output.createGraphics().drawImage(chunks[i], chunkWidth*chunkCol[i], chunkHeight*chunkRow[i], null);
		}
		ImageIO.write(output, "jpg", System.out); // write composite image to stdout
	}
	
	public static void main(String[] args)
	{
		if(args.length != 3) // check correct number of args
		{
			System.err.println("Wrong number of arguments.");
			return;
		}
		
		int rows = Integer.parseInt(args[0]); // get arguments from command line
		int cols = Integer.parseInt(args[1]);
		int timeout = Integer.parseInt(args[2]);
		
		try
		{
			Decoder.DecodeImage(rows, cols, timeout);
		}
		catch(IOException e) // report problems with i/o to stderr
		{
			System.out.println("Got IOException: " + e.getMessage());
		}
	}
}
