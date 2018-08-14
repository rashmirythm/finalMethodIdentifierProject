
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

public class extra {
	
	private static Git git;
	private static Repository repository;
	private static FileOutputStream fop = null;
	private static FileInputStream fin=null;
	private static byte[] contentInBytes;
	private static File file;
	private static File gitWorkDir = new File("C:/Users/I338008/git/JSParser");
	//private static HashSet<MethodNamesLines> funcDetails;
	
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
	
	private static String[] FindGitCommitFiles( String commitHash ) throws IOException 
	{
		Collection<String> pathnames = new ArrayList<>();
		Collection<String> pathnames1 = new ArrayList<>();
		Collection<String> pathnames2 = new ArrayList<>();
		String line;
		Process p1=Runtime.getRuntime().exec("git ls-tree -r "+commitHash+" --name-only", null, gitWorkDir);
		InputStream is = (InputStream) p1.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		while ((line = br.readLine()) != null) {
			pathnames1.add(line);
		}
		RevWalk revWalk = new RevWalk(repository);
		ObjectId treeId = ObjectId.fromString(commitHash);
		RevCommit commit = revWalk.parseCommit(treeId);
		RevCommit[] Parents = commit.getParents();
		for(RevCommit parent:Parents) {
			RevCommit ParentCommit = revWalk.parseCommit(parent.getId());
			String parentname = ParentCommit.getId().toString();
			String[] parentObjectDetailsSplit = parentname.split("\\s+");
			String Parentcommit = parentObjectDetailsSplit[1];
			Process p2=Runtime.getRuntime().exec("git ls-tree -r "+Parentcommit+" --name-only", null, gitWorkDir);
			InputStream is2 = (InputStream) p2.getInputStream();
			InputStreamReader isr2 = new InputStreamReader(is2);
			BufferedReader br2 = new BufferedReader(isr2);

			while ((line = br2.readLine()) != null) {
				pathnames2.add(line);
			}

		}
		Iterable<String> paths = CollectionUtils.union(pathnames1, pathnames2);
		return ( String[] )((Collection<String>) paths).toArray( new String[ pathnames.size() ] );
	}
	
	/*private static String[] FindGitCommitFiles( String commitHash ) throws IOException {

		Collection<String> pathnames = new ArrayList<>();
		//HashSet<String> pathnames = new HashSet<String>();
		String line;
		int i=0;
		RevWalk revWalk = new RevWalk(repository);
		ObjectId treeId = ObjectId.fromString(commitHash);
		RevCommit commit = revWalk.parseCommit(treeId);
		RevTree tree = commit.getTree();
		RevCommit[] Parents = commit.getParents();
		for(RevCommit parent:Parents) {
			RevCommit ParentCommit = revWalk.parseCommit(parent.getId());
			String parentname = ParentCommit.getId().toString();
			String[] parentObjectDetailsSplit = parentname.split("\\s+");
			String Parentcommit = parentObjectDetailsSplit[1];
			Process p1=Runtime.getRuntime().exec("git ls-tree -r "+Parentcommit+" --name-only", null, gitWorkDir);
			InputStream is = (InputStream) p1.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				//pathNames[]=String.format("%s"+"\n", line);
				pathnames.add(line);   
				i++;
			}

		}
		return ( String[] )pathnames.toArray( new String[ pathnames.size() ] );
		Collection<String> pathNames = new ArrayList<>();
                   TreeWalk treeWalk = new TreeWalk( repository );
                   treeWalk.setRecursive( true );
                   treeWalk.setPostOrderTraversal( true );
                   treeWalk.addTree( treeId );
                   while( treeWalk.next() ) {
                     pathNames.add( treeWalk.getPathString() );
                   }
		//return ( String[] )pathNames.toArray( new String[ pathNames.size() ] );
	}*/
	public static void createFile(ObjectId tree, String filename, String filechanged) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {

		File file = new File(filename);
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(filechanged));
		if (!treeWalk.next()) 
		{
			System.out.println("Nothing found!");
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
	
	public static HashSet<ChangesPerFile> DifferenceFile() throws IOException{                                                     
		HashSet<ChangesPerFile> dif=new HashSet<ChangesPerFile>();
		HashSet<ChangesPerFile> tempinitial = new HashSet<ChangesPerFile>();
		HashSet<ChangesPerFile> tempnew = new HashSet<ChangesPerFile>();
		ChangesPerFile c;
		Pattern pattFileName=Pattern.compile("[A-Za-z]+(.js)");
		Pattern pattinitial=Pattern.compile("(\\*\\*\\*) [0-9,]+");
		Pattern pattnew=Pattern.compile("(\\-\\-\\-) [0-9,]+");
		Pattern pattnum=Pattern.compile("-?\\d+");
		Pattern pattadd=Pattern.compile("^\\+\\s");
		Pattern pattsub=Pattern.compile("^\\-\\s");
		Pattern pattmod=Pattern.compile("^!\\s");
		BufferedReader r = new BufferedReader(new FileReader("C:\\Users\\I338008\\Documents\\GIT Documents\\Difference.txt"));
		String line,Filename="",type;
		int x,PStartingpoint=0,PEndingPoint=0,NStartingpoint=0,NEndingPoint=0,linenum=0,NoInitial=0,NoNew=0,start,end;
		Matcher mnum,m,minitial,mnew,madd,msub,mmod;
		while ((line = r.readLine()) != null) {
			m = pattFileName.matcher(line);
			minitial = pattinitial.matcher(line);
			mnew = pattnew.matcher(line);
			madd = pattadd.matcher(line);
			msub = pattsub.matcher(line);
			mmod = pattmod.matcher(line);
			if (m.find()) {
				if (tempnew.isEmpty()==true&&tempinitial.isEmpty()==false) {
					dif.addAll(tempinitial);
				}
				else if (tempnew.isEmpty()==false){
					dif.addAll(tempnew);
				}
				start = m.start(0);
				end = m.end(0);
				Filename=line.substring(start, end);
				continue;
			}
			if (minitial.find()) {

				NoNew=0;
				tempinitial=new HashSet<ChangesPerFile>();
				start = minitial.start(0);
				end = minitial.end(0);
				String Previousline=line.substring(start, end);

				mnum=pattnum.matcher(Previousline);
				x=1;
				while (mnum.find()) {
					if(x==1) {
						PStartingpoint=Integer.parseInt(mnum.group());
					}
					if(x==2) {
						PEndingPoint=Integer.parseInt(mnum.group());
					}
					x++;
				}
				if(x==2) {
					NoInitial=0;
					linenum++; 
					continue;
				}

				linenum=PStartingpoint-1;
				NoInitial=PEndingPoint-PStartingpoint;
				linenum++; 
				continue;
			}
			if (mnew.find()) {
				tempnew=new HashSet<ChangesPerFile>();
				start = mnew.start(0);
				end = mnew.end(0);
				String New=line.substring(start, end);

				mnum=pattnum.matcher(New);
				x=1;
				while (mnum.find()) {
					if(x==1) {
						NStartingpoint=Integer.parseInt(mnum.group());
					}
					if(x==2) {
						NEndingPoint=Integer.parseInt(mnum.group());
					}
					x++;
				}
				linenum=NStartingpoint-1;
				NoNew=NEndingPoint-NStartingpoint;
				linenum++; 
				continue;
			}
			if (madd.find()) {
				type="+";
				c=new ChangesPerFile(Filename,linenum,type); 
				dif.add(c);
				linenum++; 
				continue;
			}
			if (msub.find()) {
				type="-";
				c=new ChangesPerFile(Filename,linenum,type); 
				dif.add(c);
				linenum++; 
				continue;
			}
			if (mmod.find()) {

				if(NoNew>=NoInitial) {
					type="+";
					c=new ChangesPerFile(Filename,linenum,type); 
					tempnew.add(c);
				}
				else if(NoNew==0)
				{
					type="-";
					c=new ChangesPerFile(Filename,linenum,type); 
					tempinitial.add(c);
				}
			}
			linenum++; 
		}
		if (tempnew.isEmpty()==true&&tempinitial.isEmpty()==false) {
			dif.addAll(tempinitial);
		}
		else if (tempnew.isEmpty()==false){
			dif.addAll(tempnew);
		}
		return dif;       
	}
	
	/*public static HashSet<ChangesPerFile> DifferenceFile() throws IOException{

		int i = 0;
		HashSet<ChangesPerFile> dif=new HashSet<ChangesPerFile>();
		HashSet<ChangesPerFile> tempinitial = new HashSet<ChangesPerFile>();
		HashSet<ChangesPerFile> tempnew = new HashSet<ChangesPerFile>();
		ChangesPerFile c;
		List<String> mod=new ArrayList<String>();
		Pattern pattFileName=Pattern.compile("[A-Za-z]+(.js)");
		Pattern pattinitial=Pattern.compile("(\\*\\*\\*) [0-9,]+");
		Pattern pattnew=Pattern.compile("(\\-\\-\\-) [0-9,]+");
		Pattern pattnum=Pattern.compile("-?\\d+");
		Pattern pattadd=Pattern.compile("^\\+\\s");
		Pattern pattsub=Pattern.compile("^\\-\\s");
		Pattern pattmod=Pattern.compile("^!\\s");
		BufferedReader r = new BufferedReader(new FileReader("C:\\Users\\I338008\\Documents\\GIT Documents\\Difference.txt"));
		String line;
		String Filename="";
		int x,PStartingpoint=0,PEndingPoint=0,NStartingpoint=0,NEndingPoint=0,linenum=0,newfile=0,NoInitial=0,NoNew=0;
		String type;
		while ((line = r.readLine()) != null) {
			Matcher m = pattFileName.matcher(line);
			if (m.find()) {
				if (tempnew.isEmpty()==true&&tempinitial.isEmpty()==false) {
					dif.addAll(tempinitial);
				}
				else if (tempnew.isEmpty()==false){
					dif.addAll(tempnew);
				}
				int start = m.start(0);
				int end = m.end(0);
				Filename=line.substring(start, end);
			}
			Matcher minitial = pattinitial.matcher(line);
			if (minitial.find()) {

				NoNew=0;
				tempinitial=new HashSet<ChangesPerFile>();
				int start = minitial.start(0);
				int end = minitial.end(0);
				String Previousline=line.substring(start, end);

				Matcher mnum=pattnum.matcher(Previousline);
				x=1;
				while (mnum.find()) {
					if(x==1) {
						PStartingpoint=Integer.parseInt(mnum.group());
					}
					if(x==2) {
						PEndingPoint=Integer.parseInt(mnum.group());
					}
					x++;
				}
				linenum=PStartingpoint-1;
				//newfile=0;
				NoInitial=PEndingPoint-PStartingpoint;

			}

			Matcher mnew = pattnew.matcher(line);
			if (mnew.find()) {
				tempnew=new HashSet<ChangesPerFile>();
				int start = mnew.start(0);
				int end = mnew.end(0);
				String New=line.substring(start, end);

				Matcher mnum=pattnum.matcher(New);
				x=1;
				while (mnum.find()) {
					if(x==1) {
						NStartingpoint=Integer.parseInt(mnum.group());
					}
					if(x==2) {
						NEndingPoint=Integer.parseInt(mnum.group());
					}
					x++;
				}
				linenum=NStartingpoint-1;
				//newfile=1;
				NoNew=NEndingPoint-NStartingpoint;
			}
			Matcher madd = pattadd.matcher(line);
			if (madd.find()) {
				type="+";
				c=new ChangesPerFile(Filename,linenum,type); 
				dif.add(c);
			}
			Matcher msub = pattsub.matcher(line);
			if (msub.find()) {
				type="-";
				//System.out.println(line);
				c=new ChangesPerFile(Filename,linenum,type); 
				dif.add(c);
			}
			Matcher mmod = pattmod.matcher(line);
			if (mmod.find()) {

				if(NoNew>NoInitial) {
					type="+";
					c=new ChangesPerFile(Filename,linenum,type); 
					tempnew.add(c);
				}
				else if(NoNew==0)
				{
					type="-";
					c=new ChangesPerFile(Filename,linenum,type); 
					tempinitial.add(c);
				}
			}


			linenum++; 
		}
		if (tempnew.isEmpty()==true&&tempinitial.isEmpty()==false) {
			dif.addAll(tempinitial);
		}
		else if (tempnew.isEmpty()==false){
			dif.addAll(tempnew);
		}
		return dif;       
	}*/
	
	
	
	public static HashSet<ChangesPerFile> MethodofEachFile(String commitHash) throws MissingObjectException, IncorrectObjectTypeException, IOException, GitAPIException, ParseException {

		HashSet<ChangesPerFile> change=new HashSet<ChangesPerFile>();
		HashSet<ChangesPerFile> changesub=new HashSet<ChangesPerFile>();
		RevWalk revWalk = new RevWalk(repository);
		ObjectId treeId = ObjectId.fromString(commitHash);
		RevCommit commit = revWalk.parseCommit(treeId);
		RevTree tree = commit.getTree();
		RevCommit[] Parents = commit.getParents();
		for(RevCommit parent:Parents) {
			RevCommit ParentCommit = revWalk.parseCommit(parent.getId());
			String parentname = ParentCommit.getId().toString();
			String[] parentObjectDetailsSplit = parentname.split("\\s+");
			String Parentcommit = parentObjectDetailsSplit[1];
			Process p1=Runtime.getRuntime().exec("git difftool -y -x \"diff -c\" "+Parentcommit+" "+commitHash, null, gitWorkDir);
			readOutput(p1);
			changesub=DifferenceFile();
			change.addAll(changesub);              
		}
		return change;   
	}
	
	//========================================================================================
	public static HashSet<MethodNames> Comparison(HashSet<ChangesPerFile> dif,List<FunctionDetails> FDCurrentRev,List<FunctionDetails> FDParentRev,String Filename) {
		HashSet<MethodNames> MethodList=new HashSet<MethodNames>();
		MethodNames mn;
		for(ChangesPerFile ch:dif) {
			if(ch.getFilename().equals(Filename)) {
				if(ch.gettype()=="+") {
					for(FunctionDetails m:FDCurrentRev) {
						if(m.getFileName().equals(Filename))
						{
							if(ch.getLineNum()>=(int)m.getFunctionStartPos()&&ch.getLineNum()<=(int)m.getFunctionEndPos()) {
								mn=new MethodNames(ch.getFilename(),m.getFunctionName());
								MethodList.add(mn);
								break;
							}
						}
					}
				}else if(ch.gettype()=="-") {
					for(FunctionDetails m:FDParentRev) {
						if(m.getFileName().equals(Filename))
						{
							if(ch.getLineNum()>=(int)m.getFunctionStartPos()&&ch.getLineNum()<=(int)m.getFunctionEndPos()) {
								mn=new MethodNames(ch.getFilename(),m.getFunctionName());
								MethodList.add(mn);
								break;
							}
						}
					}
				}
			}
		}
		return MethodList;
	}
	
	public static HashSet<MethodNames> MethodFind(String commitHash,List<FunctionDetails> FDCurrentRev,List<FunctionDetails> FDParentRev) throws MissingObjectException, IncorrectObjectTypeException, IOException, GitAPIException, ParseException {

		git = Git.open(gitWorkDir);
		repository = git.getRepository();
		HashSet<MethodNames> changedMethods = new HashSet<MethodNames>();
		HashSet<MethodNames> changedMethodsub = new HashSet<MethodNames>();
		HashSet<ChangesPerFile> ChangesEachMethodsub = new HashSet<ChangesPerFile>();
		HashSet<ChangesPerFile> ChangesEachMethod = new HashSet<ChangesPerFile>();
		//HashSet<MethodNamesLines> PrevFuncDetails;
		//HashSet<MethodNamesLines> NewFuncDetails;
		ObjectId treeId = ObjectId.fromString(commitHash);
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(treeId);
		RevTree tree = commit.getTree();

		String file1 = "C:\\Users\\I338008\\Documents\\GIT Documents\\JSCurrentFile.txt";
		String file2 = "C:\\Users\\I338008\\Documents\\GIT Documents\\JSParentFile.txt";

		String[] Pathnames=MethodIdentifier.FilesChangedCurrent(commitHash);//FindGitCommitFiles(commitHash);
		
		System.out.println("\n\nLINE CHANGES:");
		System.out.println("***************");
		ChangesEachMethod=MethodofEachFile(commitHash);
		for(ChangesPerFile ccc: ChangesEachMethod)
		{
			if(ccc.getFilename() !="")
			{
				System.out.println(ccc.getFilename()+" "+ccc.getLineNum()+" "+ccc.gettype());
			}
			
		}

		for(String path:Pathnames) {
			/*createFile(tree,file1,path);
			NewFuncDetails=MethodLines(file1);
			RevCommit[] Parents = commit.getParents();
			for(RevCommit parent:Parents) {
				RevCommit ParentCommit = revWalk.parseCommit(parent.getId());
				RevTree ParentTree = ParentCommit.getTree();
				//                                                                        String parentname = ParentCommit.getId().toString();
				//                                                                        String[] parentObjectDetailsSplit = parentname.split("\\s+");
				//                                                                        String Parentcommit = parentObjectDetailsSplit[1];
				//                                                                        ChangesEachMethodsub=MethodofEachFile(commitHash,Parentcommit);
				//                                                                        ChangesEachMethod.addAll(ChangesEachMethodsub);
				createFile(ParentTree,file2,path);
				PrevFuncDetails=MethodLines(file2);*/
				changedMethodsub=Comparison(ChangesEachMethod,FDCurrentRev,FDParentRev,path);
				changedMethods.addAll(changedMethodsub);
			//
		}
		return changedMethods;
		//return ChangesEachMethod;
	}
	/*public static HashSet<MethodNamesLines> MethodLines(String Filename) {

		HashSet<MethodNamesLines> MethodDetails=new HashSet<MethodNamesLines>(); 
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
		Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(Filename);
		CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);

		JavacTask javacTask = (JavacTask) task;
		SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
		Iterable<? extends CompilationUnitTree> parseResult = null;
		try {
			parseResult = javacTask.parse();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		for (CompilationUnitTree compilationUnitTree : parseResult) {
			funcDetails=new HashSet<MethodNamesLines>();
			compilationUnitTree.accept(new MethodLineLogger(compilationUnitTree, sourcePositions), null);
			MethodDetails.addAll(funcDetails);

		}
		return MethodDetails;
	}

	private static class MethodLineLogger extends TreeScanner<Void, Void> {
		private final CompilationUnitTree compilationUnitTree;
		private final SourcePositions sourcePositions;
		private final LineMap lineMap;

		private MethodLineLogger(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {
			this.compilationUnitTree = compilationUnitTree;
			this.sourcePositions = sourcePositions;
			this.lineMap = compilationUnitTree.getLineMap();
		}

		public Void visitMethod(MethodTree arg0, Void arg1) {

			long startPosition = sourcePositions.getStartPosition(compilationUnitTree, arg0);
			long startLine = lineMap.getLineNumber(startPosition);
			long endPosition = sourcePositions.getEndPosition(compilationUnitTree, arg0);
			long endLine = lineMap.getLineNumber(endPosition);
			MethodNamesLines m=new MethodNamesLines(arg0.getName().toString(),startLine,endLine);
			funcDetails.add(m);

			return super.visitMethod(arg0, arg1);
		}*/


}
