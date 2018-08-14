
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

public class ChangesPerFile {
       private String Filename;
       private int line;
       private String type;
       ChangesPerFile(String File, int linenum,String Type){
             Filename=File;
             line=linenum;
             type=Type;   
       }
       public String getFilename() {
             return Filename;
       }
       public int getLineNum() {
             return line;
       }
       public String gettype() {
             return type;
       }      
}

