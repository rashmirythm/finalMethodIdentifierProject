import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

import org.eclipse.jgit.api.errors.GitAPIException;

public class Client {

	public static void main(String args[]) throws IOException, GitAPIException, ParseException
	{
		String CurrentCommitId;
		
		Scanner sc=new Scanner(System.in);  
		System.out.println("Enter the commit id:");  
		CurrentCommitId=sc.next(); 
		sc.close();
		Mediate.init(CurrentCommitId);
		
	}
}
