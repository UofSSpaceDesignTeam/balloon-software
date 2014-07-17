package usstv;

import java.io.IOException;

public class Noise
{
	private Noise()
	{}
		
	public static void main(String[] args) throws IOException
	{
		if(args.length != 2) // check correct number of args
		{
			System.err.println("Wrong number of arguments.");
			return;
		}
		
		double errorRate = Double.parseDouble(args[0]);
		int timeout = Integer.parseInt(args[1]);
		
		int current;
		long startTime = System.currentTimeMillis(); // reference for timeout check
		
		// listen while we have not timed out
		while(System.currentTimeMillis() - startTime < 1000*timeout)
		{
			current = System.in.read();
			if(Math.random() < errorRate)
			{
				current += (byte)(Math.random() * 255);
				if(current > 255)
					current -= 255;
			}
			System.out.write(current);
		}
	}
}