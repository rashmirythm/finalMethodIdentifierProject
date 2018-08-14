
public class MethodNames {
	private String MethodName;
	private String Filename;
	//private int Startlineno;
	//private int Endlineno;
	
	MethodNames(String File,String methodname){
		Filename=File;
		MethodName=methodname;
		//Startlineno=start;
		//Endlineno=end;
	}
	public String getMethodName() {
		return MethodName;
	}
	public String getFilename() {
		return Filename;
	}
	/*public int getStartlineno()
	{
		return Startlineno;
	}
	public int getEndlineno()
	{
		return Endlineno;
	}*/
}
