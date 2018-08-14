import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public class trydiffsplit {


	private static File file;
	private static FileOutputStream fop = null;
	private static FileInputStream fin=null;
	private static byte[] contentInBytes;
	public static Git git;
	public static Repository repository;
	public static File gitWorkDir = new File("C:/Users/I338008/git/JSParser");

	public static void difftool(String Parentcommit,String CurrentCommit,String[] CurrentCommitFilesChanged) throws IOException, ParseException
	{
		List<String> filesChanged = new ArrayList<String>();
		String FullDiffData;
		List<String> internalFileSplitdata = new ArrayList<String>();
		List<String> FileDiffData = new ArrayList<String>();
		for(int i=0;i<CurrentCommitFilesChanged.length;i++)
		{
			if(CurrentCommitFilesChanged[i].contains(".js"))
			{
				filesChanged.add(CurrentCommitFilesChanged[i]);
			}
		}

		Process p1=Runtime.getRuntime().exec("git difftool -y -x \"diff -c\" "+Parentcommit+" "+CurrentCommit, null, gitWorkDir);
		readOutput(p1);
		FullDiffData = diffDataFileSplit();
		FileDiffData = fileDiffDataSplit(FullDiffData,CurrentCommitFilesChanged);
		internalFileSplit(FileDiffData,filesChanged);



	}

	public static void readOutput(Process proc) throws IOException, ParseException {
		file = new File("C:\\Users\\I338008\\Documents\\GIT Documents\\Difference.txt");
		fop = new FileOutputStream(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		InputStream is = (InputStream) proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			contentInBytes=String.format("%s"+"\n", line).getBytes();
			fop.write(contentInBytes);    
		}
		fop.close();
	}

	public static String diffDataFileSplit() throws IOException
	{
		BufferedReader r = new BufferedReader(new FileReader("C:\\Users\\I338008\\Documents\\GIT Documents\\Difference.txt"));
		String line;
		String fullDiffData="";
		while ((line = r.readLine()) != null) 
		{
			fullDiffData = fullDiffData + line + "\n";
		}


		//System.out.println(fullDiffData);

		return fullDiffData;
	}

	public static List<String> fileDiffDataSplit(String FullDiffData,String[] CurrentCommitFilesChanged)
	{
		/*String[] FileDiffData;
		String[] SplitData;
		String currentcontent = FullDiffData;

		for(int i=0;i<CurrentCommitFilesChanged.length;i++)
		{
			if(i==CurrentCommitFilesChanged.length-1)
			{
				FileDiffData[i]=SplitData[1];
			}
			else
			{
				SplitData = currentcontent.split("(\\*\\*\\*) [A-Za-z]+("+CurrentCommitFilesChanged[i+1]+")",2);
				FileDiffData[i]=SplitData[0];
			}

		}*/
		String FileContent = FullDiffData;
		String[] splitFiledetails;
		String ProccessData;
		List<String> FileDiffData = new ArrayList<String>();
		int i=1;
		while(i<=CurrentCommitFilesChanged.length)
		{

			if(i==CurrentCommitFilesChanged.length)
			{
				//splitFiledetails = FileContent.split(Pathnames[i]);
				ProccessData = FileContent;
			}
			else
			{
				splitFiledetails = FileContent.split(CurrentCommitFilesChanged[i],2);
				ProccessData = splitFiledetails[0];
				FileContent = splitFiledetails[1];
			}


			if(CurrentCommitFilesChanged[i-1].contains(".js"))
			{
				//ProcessData contains Final file data
				//System.out.println("\n\n"+CurrentCommitFilesChanged[i-1]);
				//System.out.println(ProccessData);
				//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
				FileDiffData.add(ProccessData);
			}


			i++;
		}

		return FileDiffData;
	}

	public static void internalFileSplit(List<String> FileDiffData,List<String> filesChanged) throws IOException
	{

		String [] filediffdata = FileDiffData.toArray(new String[FileDiffData.size()]);
		String[] fileschanged = filesChanged.toArray(new String[filesChanged.size()]);
		List<String> internalFileSplitdata = new ArrayList<String>();
		List<String> internalFileSplitdata_filename = new ArrayList<String>();
		String[] mulstarsplit;


		for(int i=0;i<filediffdata.length;i++)
		{
			mulstarsplit = filediffdata[i].split("\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*");
			for(int j=0;j<mulstarsplit.length-1;j++)
			{
				internalFileSplitdata.add(mulstarsplit[j+1]);
				internalFileSplitdata_filename.add(fileschanged[i]);
				//System.out.println(mulstarsplit[j+1]);
				//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			}

			//RevisionSplit(internalFileSplitdata,fileschanged[i]);
		}
		RevisionSplit(internalFileSplitdata,internalFileSplitdata_filename);
	}
	public static void /*List<String>*/ RevisionSplit(List<String> internalFileSplitdata,List<String> internalFileSplitdata_filename) throws IOException
	{
		String[] internalfilesplitdata = internalFileSplitdata.toArray(new String[internalFileSplitdata.size()]);
		String[] filenames = internalFileSplitdata_filename.toArray(new String[internalFileSplitdata_filename.size()]);
		String[] RevSplitData;
		String oldRevSplitdata;
		String newRevSplitdata;
		String oldhunk="";
		String newhunk="";
		int oldhunkPrev = 0, oldhunkCurr=0, newhunkPrev=0, newhunkCurr=0;
		String[] oldsplit, newsplit;
		for(int i=0;i<internalFileSplitdata.size();i++)
		{
			//System.out.println(filenames[i]);
			//System.out.println(internalfilesplitdata[i]);
			//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

			//RevSplitData=internalfilesplitdata[i].split("\\*\\*\\*\\*",2);
			RevSplitData=internalfilesplitdata[i].split("---",2);
			oldRevSplitdata = RevSplitData[0];
			newRevSplitdata = RevSplitData[1];

			System.out.println("OLD data:");
			System.out.println(oldRevSplitdata);
			System.out.println("NEW data:");
			System.out.println(newRevSplitdata);
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");



			System.out.println(filenames[i]);

			Pattern oldhunkpattern = Pattern.compile("\\*\\*\\* (.*?) ");
			Pattern newwhunkpattern = Pattern.compile("(.*?) ----");

			Matcher oldhunkmatcher = oldhunkpattern.matcher(oldRevSplitdata);
			while (oldhunkmatcher.find()) {
				oldhunk = oldhunkmatcher.group(1);
				System.out.println("OLDHUNK:"+oldhunk);
			}

			Matcher newhunkmatcher = newwhunkpattern.matcher(newRevSplitdata);
			while (newhunkmatcher.find()) {
				newhunk = newhunkmatcher.group(1);
				System.out.println("NEWHUNK:"+newhunk);
			}
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

			

			if(oldhunk.contains(","))
			{
				oldsplit = (oldhunk.trim()).split(",",2);
				oldhunkPrev = Integer.parseInt(oldsplit[0]);
				oldhunkCurr = Integer.parseInt(oldsplit[1]);
				System.out.println(oldhunkPrev);
				System.out.println(oldhunkCurr);
			}
			else
			{
				if(Integer.parseInt(oldhunk.trim())==0)
				{
					oldhunkPrev=0;
					oldhunkCurr=-1;
				}
			}
				
			if(newhunk.contains(","))
			{
				newsplit = (newhunk.trim()).split(",",2);
				newhunkPrev = Integer.parseInt(newsplit[0]);
				newhunkCurr = Integer.parseInt(newsplit[1]);
				System.out.println(newhunkPrev);
				System.out.println(newhunkCurr);
			}
			else
			{
				if(Integer.parseInt(newhunk.trim())==1)
				{
					newhunkPrev=1;
					newhunkCurr=1;
				}
				else if(Integer.parseInt(newhunk.trim())==0)
				{
					newhunkPrev=0;
					newhunkCurr=-1;
				}
			}
			


			getlinedetails(oldRevSplitdata,newRevSplitdata,oldhunk,newhunk,oldhunkPrev, oldhunkCurr, newhunkPrev, newhunkCurr,filenames[i]);
		}

	}
	public static void getlinedetails(String oldRevSplitdata,String newRevSplitdata,String oldhunk,String newhunk,int oldhunkPrev,int oldhunkCurr,int newhunkPrev,int newhunkCurr,String filename) throws IOException
	{
		List<FileLineDetails> oldFileLineDetails = new ArrayList<FileLineDetails>();
		List<FileLineDetails> newFileLineDetails = new ArrayList<FileLineDetails>();
		
		Reader OldRevData = new StringReader(oldRevSplitdata);
		BufferedReader oldrevdata = new BufferedReader(OldRevData);
		Reader NewRevData = new StringReader(newRevSplitdata);
		BufferedReader newrevdata = new BufferedReader(NewRevData);

		Pattern oldhunkpatt=Pattern.compile(oldhunk);
		Pattern newhunkpatt=Pattern.compile(newhunk);
		
		Pattern pattadd=Pattern.compile("^\\+\\s");
		Pattern pattsub=Pattern.compile("^\\-\\s");
		Pattern pattmod=Pattern.compile("^!\\s");
		Matcher madd,msub,mmod,moldhunk,mnewhunk;
		String line;
		//int oldflag=0,newflag=0;
		
		while ((line = oldrevdata.readLine()) != null) 
		{
			moldhunk = oldhunkpatt.matcher(line);
			if(moldhunk.find())
			{
				//oldflag=1;
				line = oldrevdata.readLine();
				for(int a=oldhunkPrev;a<=oldhunkCurr;a++)
				{
					madd = pattadd.matcher(line);
					msub = pattsub.matcher(line);
					mmod = pattmod.matcher(line);
					if(madd.find())
					{
						FileLineDetails fld = new FileLineDetails();
						fld.filename = filename;
						fld.lineno = a;
						fld.changetype = "+";
						oldFileLineDetails.add(fld);
					}
					if(msub.find())
					{
						FileLineDetails fld = new FileLineDetails();
						fld.filename = filename;
						fld.lineno = a;
						fld.changetype = "-";
						oldFileLineDetails.add(fld);
					}
					if(mmod.find())
					{
						FileLineDetails fld = new FileLineDetails();
						fld.filename = filename;
						fld.lineno = a;
						fld.changetype = "!";
						oldFileLineDetails.add(fld);
					}
					
					
					line = oldrevdata.readLine();
				}
				break;
			}
			
		}

		while ((line = newrevdata.readLine()) != null) //&& newflag==0) 
		{

			mnewhunk = newhunkpatt.matcher(line);
			if(mnewhunk.find())
			{
				line = newrevdata.readLine();
				for(int a=newhunkPrev;a<=newhunkCurr;a++)
				{
					madd = pattadd.matcher(line);
					msub = pattsub.matcher(line);
					mmod = pattmod.matcher(line);
					if(madd.find())
					{
						FileLineDetails fld = new FileLineDetails();
						fld.filename = filename;
						fld.lineno = a;
						fld.changetype = "+";
						newFileLineDetails.add(fld);
					}
					if(msub.find())
					{
						FileLineDetails fld = new FileLineDetails();
						fld.filename = filename;
						fld.lineno = a;
						fld.changetype = "-";
						newFileLineDetails.add(fld);
					}
					if(mmod.find())
					{
						FileLineDetails fld = new FileLineDetails();
						fld.filename = filename;
						fld.lineno = a;
						fld.changetype = "!";
						newFileLineDetails.add(fld);
					}
					
					
					line = newrevdata.readLine();
				}
				break;
			}
			
		
		}
		
		
		FileLineDetails[] newfilelinedetails = newFileLineDetails.toArray(new FileLineDetails[newFileLineDetails.size()]);
		FileLineDetails[] oldfilelinedetails = oldFileLineDetails.toArray(new FileLineDetails[oldFileLineDetails.size()]);
		
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Old Revision Line Details:");
		for(int j=0;j<oldfilelinedetails.length;j++)
		{
			System.out.println(oldfilelinedetails[j].filename+"			"+oldfilelinedetails[j].lineno+"			"+oldfilelinedetails[j].changetype);
		}
		System.out.println("\nNew Revision Line Details:");
		for(int i=0;i<newfilelinedetails.length;i++)
		{
			System.out.println(newfilelinedetails[i].filename+"			"+newfilelinedetails[i].lineno+"			"+newfilelinedetails[i].changetype);
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
	}




	/*public static void List<String> RevisionSplit(List<String> internalFileSplitdata,String filename)
	{
		String[] internalfilesplitdata = internalFileSplitdata.toArray(new String[internalFileSplitdata.size()]);
		String[] RevSplitData;
		String oldRevSplitdata;
		String newRevSplitdata;
		for(int i=0;i<internalFileSplitdata.size();i++)
		{
			//RevSplitData=internalfilesplitdata[i].split("(\\-\\-\\-) [0-9,]+",2);
			RevSplitData=internalfilesplitdata[i].split("\\*\\*\\*\\*",2);
			oldRevSplitdata = RevSplitData[0];
			newRevSplitdata = RevSplitData[1];

			//System.out.println("OLD:");
			//System.out.println(oldRevSplitdata);
			//System.out.println("NEW:");
			//System.out.println(newRevSplitdata);
			//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			getHunk(oldRevSplitdata,newRevSplitdata);
		}

	}*/
	public static void getHunk(String oldRevSplitdata,String newRevSplitdata)
	{
		String oldhunk;
		String newhunk;
		Pattern oldhunkpattern = Pattern.compile("\\*\\*\\* (.*?) ");
		Pattern newwhunkpattern = Pattern.compile("--- (.*?) ----");

		Matcher oldhunkmatcher = oldhunkpattern.matcher(oldRevSplitdata);
		while (oldhunkmatcher.find()) {
			oldhunk = oldhunkmatcher.group(1);
			System.out.println("OLDHUNK:"+oldhunkmatcher.group(1));
		}

		Matcher newhunkmatcher = newwhunkpattern.matcher(newRevSplitdata);
		while (newhunkmatcher.find()) {
			newhunk = newhunkmatcher.group(1);
			System.out.println("NEWHUNK"+newhunkmatcher.group(1));
		}
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

	}

}
