import java.io.FileOutputStream;
import java.io.PrintWriter;

public class TitleCaser {

    public static void main(String[] args) throws Exception {
        FileOutputStream os = new FileOutputStream("target/temp.txt");
        PrintWriter out = new PrintWriter(os);
        String s = args[0];
        out.write(Character.toUpperCase(s.charAt(0)) + s.substring(1));
        out.close();
        os.close();
    }

}