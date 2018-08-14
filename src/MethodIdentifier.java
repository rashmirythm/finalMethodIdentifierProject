import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.FunctionNode;
import org.mozilla.javascript.ScriptOrFnNode;


public class MethodIdentifier{

	public static int currentObjArrayPosition=0;
	private static final String currentfile = null;
	private static Git git;
	private static Repository repository;
	private static FileOutputStream fop = null;
	private static byte[] contentInBytes;
	
	//Getting the path names/file names
	//========================================================================================================
	public static String[] FindGitCommitFiles( String commitHash ) throws IOException {

		File gitWorkDir = new File("C:/Users/I338008/git/JSParser");
		git = Git.open(gitWorkDir);
		repository = git.getRepository();
		

		ObjectId treeId = ObjectId.fromString(commitHash);
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(treeId);
		RevTree tree = commit.getTree();


		Collection<String> pathNames = new ArrayList<>();
		TreeWalk treeWalk = new TreeWalk( repository );
		treeWalk.setRecursive( true );
		treeWalk.setPostOrderTraversal( true );
		treeWalk.addTree( tree );
		while( treeWalk.next() ) {
			pathNames.add( treeWalk.getPathString() );
		}

		String[] Pathnames= ( String[] )pathNames.toArray( new String[ pathNames.size() ] );

		return Pathnames;
	}
	//========================================================================================================

	public static String[] FilesChangedCurrent(String commitHash) throws IOException
	{
		File gitWorkDir = new File("C:/Users/I338008/git/JSParser");
		String line;
		List<String> FilesChanged = new ArrayList<String>();
		
		//git = Git.open(gitWorkDir);
		//repository = git.getRepository();
		Process p1=Runtime.getRuntime().exec("git diff-tree --no-commit-id --name-only -r "+commitHash, null, gitWorkDir);
		InputStream is = (InputStream) p1.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		while ((line = br.readLine()) != null) {
			//if(line.contains(".js"))
			//{
				FilesChanged.add(line);
			//}	
		}
		String[] Fileschanged= ( String[] )FilesChanged.toArray( new String[ FilesChanged.size() ] );

		return Fileschanged;
	}
	
	
	
	//Creating and copying the contents into a file
	//========================================================================================================
	public static void createFile(ObjectId tree, String filename, String filechanged) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {

		File gitWorkDir = new File("C:/Users/I338008/git/JSParser");
		git = Git.open(gitWorkDir);
		repository = git.getRepository();
		
		File file = new File(filename);
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(filechanged));
		if (!treeWalk.next()) 
		{
			//System.out.println("Nothing found!");
			return;
		}
		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		loader.copyTo(out);

		if (!file.exists()) {
			file.createNewFile();
		}
		fop = new FileOutputStream(file);
		contentInBytes = out.toString().getBytes();
		fop.write(contentInBytes);
	}
	//========================================================================================================

	//getting the ASTRoot 
	//========================================================================================================
	public static ScriptOrFnNode getASTnode(File ts) throws IOException
	{
		/*CompilerEnvirons compilerEnv = new CompilerEnvirons();
		ErrorReporter errorReporter = compilerEnv.getErrorReporter();

		File testScript = ts;
		String sourceURI;
		try {
			sourceURI = testScript.getCanonicalPath();
		} catch (IOException e) {
			sourceURI = testScript.toString();
		}
		Reader reader = new FileReader(testScript);

		org.mozilla.javascript.Parser p = new org.mozilla.javascript.Parser(compilerEnv, null);
		ScriptOrFnNode astRoot = p.parse(reader, sourceURI, 1);

		return astRoot;*/
		CompilerEnvirons compilerEnv = new CompilerEnvirons();
		ErrorReporter errorReporter = compilerEnv.getErrorReporter();

		ScriptOrFnNode astRoot;//=null;
		File testScript = ts;
		boolean empty = !testScript.exists() || testScript.length() == 0;
		String sourceURI;
		if(empty==false)
		{
			try {
				sourceURI = testScript.getCanonicalPath();
				//System.out.println(sourceURI);
			} catch (IOException e) {
				sourceURI = testScript.toString();
				//System.out.println(sourceURI);
			}
			Reader reader = new FileReader(testScript);

			org.mozilla.javascript.Parser p = new org.mozilla.javascript.Parser(compilerEnv, null);

			
			//int a = reader.read();
			
			//try
			//{
				astRoot = p.parse(reader, sourceURI, 1);
			//}
			//catch(NullPointerException e)
			//{
				//System.out.println("False");
			//}
			

			PrintWriter writer = new PrintWriter(ts);
			writer.print("");
			writer.close();
		}
		else
		{
			astRoot = null;
		}
		
		
		
		//===============================================================================================
		return astRoot;
	}
	//========================================================================================================


}
