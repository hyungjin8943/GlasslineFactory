package factory.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
 
public class RedirectIO
{
 
	public static void main(String[] args)
	{
		PrintStream orgStream 	= null;
		PrintStream fileStream 	= null;
		try
		{
			// Saving the orginal stream
			orgStream = System.out;
			fileStream = new PrintStream(new FileOutputStream("out.txt",true));
			// Redirecting console output to file
			System.setOut(fileStream);
			// Redirecting runtime exceptions to file
			System.setErr(fileStream);
			throw new Exception("Test Exception");
 
		}
		catch (FileNotFoundException fnfEx)
		{
			System.out.println("Error in IO Redirection");
			fnfEx.printStackTrace();
		}
		catch (Exception ex)
		{
			//Gets printed in the file
			System.out.println("Redirecting output & exceptions to file");
			ex.printStackTrace();
		}
		finally
		{
			//Restoring back to console
			System.setOut(orgStream);
			//Gets printed in the console
			System.out.println("Redirecting file output back to console");
 
		}
 
	}
}