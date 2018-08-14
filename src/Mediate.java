import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.mozilla.javascript.FunctionNode;
import org.mozilla.javascript.ScriptOrFnNode;

public class Mediate {

	public static String CurrentCommit;
	public static String ParentCommit;
	public static Git git;
	public static Repository repository;

	public static void init(String CurrentCommit) throws IOException, ParseException, GitAPIException
	{
		//GETTING CURRENT AND PARENT COMMIT ID
		//========================================================================================================
		//CurrentCommit = "53a58ca09cfdb77f53387ceec0cdd99cfd5eb447";
		File gitWorkDir = new File("C:/Users/I338008/git/JSParser");
		git = Git.open(gitWorkDir);
		Repository repo = git.getRepository();
		ObjectId commitId = ObjectId.fromString(CurrentCommit);
		RevWalk revWalk = new RevWalk(repo);
		RevCommit commit = revWalk.parseCommit(commitId);
		RevTree tree = commit.getTree();
		RevCommit pcommit = commit.getParent(0);
		String parent = pcommit.getId().toString();
		String[] parentObjectDetailsSplit = parent.split("\\s+");
		String Parentcommit = parentObjectDetailsSplit[1];
		ParentCommit = Parentcommit;

		System.out.println("Current Commit ID: "+CurrentCommit);
		System.out.println("Parent Commit ID: "+ParentCommit);
		//========================================================================================================



		//GETTING LIST OF ALL FILES IN GIT AT CURRENT COMMIT AND PARENT COMMIT
		//========================================================================================================
		List<String> CurrentAllFiles = new ArrayList<String>();
		List<String> ParentAllFiles = new ArrayList<String>();

		Process procCurrent=Runtime.getRuntime().exec("git ls-tree -r "+CurrentCommit+" --name-only", null, gitWorkDir);
		InputStream isCurr = (InputStream) procCurrent.getInputStream();
		InputStreamReader isrCurr = new InputStreamReader(isCurr);
		BufferedReader brCurr = new BufferedReader(isrCurr);
		String CurrFileline;
		while ((CurrFileline = brCurr.readLine()) != null) {
			CurrentAllFiles.add(CurrFileline);
		}

		Process procParent=Runtime.getRuntime().exec("git ls-tree -r "+ParentCommit+" --name-only", null, gitWorkDir);
		InputStream isPar = (InputStream) procParent.getInputStream();
		InputStreamReader isrPar = new InputStreamReader(isPar);
		BufferedReader brPar = new BufferedReader(isrPar);
		String ParFileline;
		while ((ParFileline = brPar.readLine()) != null) {
			ParentAllFiles.add(ParFileline);
		}

		System.out.println("\n\nLIST OF ALL FILES IN GIT AT CURRENT COMMIT:");
		System.out.println("*********************************************");
		for(int i=0;i<CurrentAllFiles.size();i++)
		{
			System.out.println(CurrentAllFiles.get(i));
		}
		System.out.println("\n\nLIST OF ALL FILES IN GIT AT PARENT COMMIT:");
		System.out.println("********************************************");
		for(int i=0;i<ParentAllFiles.size();i++)
		{
			System.out.println(ParentAllFiles.get(i));
		}
		//========================================================================================================


		//GETTING LIST OF FILES CHANGED IN CURRENT COMMIT AND PARENT COMMIT
		//========================================================================================================

		String[] CurrentCommitFilesChanged = MethodIdentifier.FilesChangedCurrent(CurrentCommit);
		//String[] CurrentCommitFilesChanged=MethodIdentifier.FindGitCommitFiles(CurrentCommit);
		System.out.println("\n\nLIST OF FILES CHANGED IN CURRENT COMMIT:");
		System.out.println("******************************************");
		for(String path:CurrentCommitFilesChanged) {
			System.out.println(path);
		}

		//this following information is not required
		/*String[] ParentCommitFilesChanged=MethodIdentifier.FindGitCommitFiles(ParentCommit);
				System.out.println("\nLIST OF FILES CHANGED IN CURRENT COMMIT:");
				for(String path:ParentCommitFilesChanged) {
					System.out.println(path);
				}
				System.out.println("\n");*/

		//========================================================================================================


		//GETTING LIST OF FUNCTION NAME + STARTLINE + ENDLINE FOR CURRENT COMMIT AND PARENT COMMIT
		//========================================================================================================

		Repository repository = git.getRepository();
		ObjectId treeId = ObjectId.fromString(CurrentCommit);
		RevWalk CurrentrevWalk = new RevWalk(repository);
		RevCommit Currentcommit = CurrentrevWalk.parseCommit(treeId);
		RevTree CurrentTree = Currentcommit.getTree();

		List<FunctionDetails> FDCurrentRev = new ArrayList<FunctionDetails>();
		List<FunctionDetails> FDParentRev = new ArrayList<FunctionDetails>();


		for (String filename:CurrentCommitFilesChanged) {

			if(filename.contains(".js"))
			{
				String file1 = "C:\\Users\\I338008\\Documents\\GIT Documents\\JSCurrentFile.txt";
				String file2 = "C:\\Users\\I338008\\Documents\\GIT Documents\\JSParentFile.txt";
				File CurrentTestScript = new File("C:\\Users\\I338008\\Documents\\GIT Documents\\JSCurrentFile.txt");
				File ParentTestScript = new File("C:\\Users\\I338008\\Documents\\GIT Documents\\JSParentFile.txt");

				MethodIdentifier.createFile(CurrentTree,file1,filename);
				RevCommit[] Parents = commit.getParents();
				for(RevCommit currentparent:Parents) {
					RevCommit ParentCommit = revWalk.parseCommit(currentparent.getId());
					RevTree ParentTree = ParentCommit.getTree();
					MethodIdentifier.createFile(ParentTree,file2,filename);
				}

				ScriptOrFnNode CurrentastRoot = MethodIdentifier.getASTnode(CurrentTestScript);
				ScriptOrFnNode ParentastRoot = MethodIdentifier.getASTnode(ParentTestScript);

				if(CurrentastRoot != null)
				{
					int Currentcount=CurrentastRoot.getFunctionCount();
					for(int i=0;i<Currentcount;i++)
					{
						FunctionDetails cfd = new FunctionDetails();
						FunctionNode cFN = CurrentastRoot.getFunctionNode(i);

						cfd.FileName = filename;
						cfd.FunctionName = cFN.getFunctionName();
						cfd.FunctionStartPos = cFN.getLineno();
						cfd.FunctionEndPos = cFN.getEndLineno();

						FDCurrentRev.add(cfd);
					}
				}

				if(ParentastRoot !=null)
				{
					int Parentcount=ParentastRoot.getFunctionCount();
					for(int i=0;i<Parentcount;i++)
					{
						FunctionDetails pfd = new FunctionDetails();
						FunctionNode pFN = ParentastRoot.getFunctionNode(i);

						pfd.FileName = filename;
						pfd.FunctionName = pFN.getFunctionName();
						pfd.FunctionStartPos = pFN.getLineno();
						pfd.FunctionEndPos = pFN.getEndLineno();

						FDParentRev.add(pfd);
					}
				}


			}


		}


		System.out.println("\n\nCURRENT REVISION FUNCTION DETAILS:");
		System.out.println("************************************");
		for(int i=0;i<FDCurrentRev.size();i++)
		{
			System.out.println("File Name= "+FDCurrentRev.get(i).FileName+":	Function "+FDCurrentRev.get(i).FunctionName+"() "+FDCurrentRev.get(i).FunctionStartPos+" - "+FDCurrentRev.get(i).FunctionEndPos);
		}
		System.out.println("\n\nPARENT REVISION FUNCTION DETAILS:");
		System.out.println("***********************************");
		for(int i=0;i<FDParentRev.size();i++)
		{
			System.out.println("File Name= "+FDParentRev.get(i).FileName+":	Function "+FDParentRev.get(i).FunctionName+"() "+FDParentRev.get(i).FunctionStartPos+" - "+FDParentRev.get(i).FunctionEndPos);
		}

		//========================================================================================================



		/*HashSet<ChangesPerFile> filediffdata=ExtractLinesChanges.DiffToolData(CurrentCommit);
		for(ChangesPerFile ch:filediffdata) {
			System.out.print(ch.getFilename()+"\t");
			System.out.println(ch.getLineNum());

			System.out.println(ch.gettype());
		}*/



		//my logic for splitting diff data
		//============================================================================
		trydiffsplit.difftool( Parentcommit, CurrentCommit,CurrentCommitFilesChanged);
		//=============================================================================


		/*HashSet<MethodNames> changedMethods = MethodFinder.MethodFind(CurrentCommit);
        System.out.println("Methods changed:");
        System.out.println("Filename\tMethods");
        for(MethodNames ch:changedMethods) {
        System.out.print(ch.getFilename()+"\t");
            System.out.println(ch.getMethodName());

        }*/



		HashSet<MethodNames> changedMethods = extra.MethodFind(CurrentCommit,FDCurrentRev,FDParentRev);
		System.out.println("\n\nMETHODS AFFECTED DUE TO CHANGES IN COMMIT:");
		System.out.println("********************************************");
		System.out.println("Methods changed:");
		System.out.println("Filename\tMethods");
		for(MethodNames ch:changedMethods) {
			System.out.print(ch.getFilename()+"\t");
			System.out.println(ch.getMethodName());
			//System.out.println(ch.getFilename());
			//System.out.println(ch.getLineNum());
			//System.out.println(ch.gettype());

		}
	}

}
